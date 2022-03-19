package com.firstrain.frapi.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.firstrain.db.obj.EmailSchedule;
import com.firstrain.db.obj.Items;
import com.firstrain.db.obj.MailDocs;
import com.firstrain.db.obj.MailLog;
import com.firstrain.db.obj.Template;
import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.Search;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.EmailDetail;
import com.firstrain.frapi.pojo.EmailResponse;
import com.firstrain.frapi.repository.EmailServiceRepository;
import com.firstrain.frapi.repository.MonitorServiceRepository;
import com.firstrain.frapi.service.EmailService;
import com.firstrain.frapi.service.VisualizationService;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private VisualizationService visualizationService;
	@Autowired
	private EmailServiceRepository emailServiceRepository;
	@Autowired
	private MonitorServiceRepository monitorServiceRepository;
	@Autowired(required = true)
	protected ThreadPoolTaskExecutor executorService;
	@Autowired
	private ConvertUtil convertUtil;
	@Value("${executor.timeout}")
	protected long executorTimeout;
	@Value("${content.filter.token}")
	protected String contentFilterToken;

	private final Logger LOG = Logger.getLogger(EmailServiceImpl.class);

	@Override
	public EmailResponse getEmailDetails(User user, long emailId) throws Exception {
		EmailDetail emailDetail = new EmailDetail();

		long startTime = PerfMonitor.currentTime();
		try {
			EmailResponse res = new EmailResponse();
			MailLog mailLog = emailServiceRepository.getEmailLogById(emailId);
			if (mailLog == null) {
				res.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
				return res;
			}
			res.setEmailName(mailLog.getMailSubject());
			res.setEmailId(mailLog.getId());

			EmailSchedule emailSchedule = emailServiceRepository.getEmailSchedule(mailLog.getScheduleId());
			if (emailSchedule != null) {
				Template template = emailServiceRepository.getTemplate(emailSchedule.getTemplateID());
				if (template != null) {
					res.setEmailTemplate(template.getDisplayName());
				}
			}

			List<MailDocs> mailDocsList = emailServiceRepository.getEmailById(emailId);

			if (mailDocsList == null || mailDocsList.isEmpty()) {
				res.setStatusCode(StatusCode.ENTITY_NOT_FOUND);
				return res;
			}
			Map<String, Search> searchMap = new HashMap<String, Search>();
			long[] siteDocIdList = new long[mailDocsList.size()];
			for (int i = 0; i < mailDocsList.size(); i++) {
				siteDocIdList[i] = mailDocsList.get(i).getSiteDocId();
			}
			List<DocEntry> docEntries = monitorServiceRepository.getSearcher().fetch(siteDocIdList);
			Map<String, DocEntry> docMap = new HashMap<String, DocEntry>();
			for (DocEntry docEntry : docEntries)
				docMap.put(docEntry.getSitedocId(), docEntry);

			for (MailDocs mailDocs : mailDocsList) {
				String searchId = Long.toString(mailDocs.getSearchId());
				Search search = searchMap.get(searchId);
				if (search == null) {
					search = new Search();
					search.setSearchId(searchId);
					Items items = emailServiceRepository.getSearchById(mailDocs.getSearchId());
					search.setSearchName(items.getName());
					search.setSearchQuery(items.getData().indexOf("&fq") > -1 ? items.getData().substring(0, items.getData().indexOf("&fq"))
							: items.getData());
					search.setSearchFilter(
							items.getData().indexOf("&fq") > -1 ? items.getData().substring(items.getData().indexOf("&fq") + 4) : null);
				}
				List<Document> docs = search.getDocuments();
				if (docs == null || docs.isEmpty()) {
					docs = new ArrayList<Document>();
				}
				DocEntry docEntry = docMap.get(new Long(mailDocs.getSiteDocId()).toString());
				if (docEntry != null) {
					Document document = convertUtil.convertDocumentPOJOFromDocEntry(docEntry);
					docs.add(document);
				}
				search.setDocuments(docs);
				searchMap.put(searchId, search);
			}

			List<Search> searches = new ArrayList<Search>(searchMap.values());
			emailDetail.setSearches(searches);

			res.setEmailDetail(emailDetail);
			res.setStatusCode(StatusCode.REQUEST_SUCCESS);
			return res;
		} catch (Exception e) {
			LOG.error("Error while getting email detail for emailId :" + emailId, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getEmailDetails");
		}
	}

}
