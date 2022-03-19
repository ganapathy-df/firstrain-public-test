package com.firstrain.frapi.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.domain.EntityDetailSpec;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.service.EntityProcessingService;
import com.firstrain.frapi.service.RehydrateService;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;

@Service
public class RehydrateServiceImpl implements RehydrateService {

	private final Logger LOG = Logger.getLogger(RehydrateServiceImpl.class);

	@Autowired
	private EntityBaseService entityBaseService;
	@Autowired
	private EntityProcessingService entityProcessingService;

	@Override
	public DocumentSet getDocumentView(long frUserId, List<Long> docIds, short industryClassificationId) throws Exception {

		long startTime = PerfMonitor.currentTime();
		try {
			EntityDetailSpec spec = new EntityDetailSpec();

			DocumentSet docSet = entityBaseService.getDocDetails(docIds, spec, null, industryClassificationId);
			entityProcessingService.getDocumentSetWithId(docSet);
			return docSet;
		} catch (Exception e) {
			LOG.error("Error while getting document view for docIds:" + docIds, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getDocumentView");
		}
	}

	@Override
	public TweetSet getTweetView(long frUserId, List<Long> tweetIds) throws Exception {

		long startTime = PerfMonitor.currentTime();
		try {
			EntityDetailSpec spec = new EntityDetailSpec();
			spec.attachGroupInfo = true;
			TweetSet tweetSet = entityBaseService.getTweetDetails(tweetIds, spec);
			entityProcessingService.getTweetsWithId(tweetSet);
			return tweetSet;
		} catch (Exception e) {
			LOG.error("Error while getting tweet view for tweetIds:" + tweetIds, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "getTweetView");
		}
	}
}
