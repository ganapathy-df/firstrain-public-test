package com.firstrain.frapi.service.impl;

import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.firstrain.db.obj.APIArticleHide;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.repository.RestrictContentRepository;
import com.firstrain.frapi.service.RestrictContentService;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;

@Service
public class RestrictContentServiceImpl implements RestrictContentService {

	private final Logger LOG = Logger.getLogger(RestrictContentServiceImpl.class);

	@Autowired
	private RestrictContentRepository restrictContentRepository;
	@Autowired
	@Qualifier("entityBaseServiceRepositoryImpl")
	private EntityBaseServiceRepository entityBaseServiceRepository;
	@Autowired(required = true)
	protected ThreadPoolTaskExecutor taskExecutor;
	@Value("${executor.timeout}")
	protected long executorTimeout;

	@Override
	public int hideContent(long userId, long enterpriseId, String articleId) throws Exception {

		long startTime = PerfMonitor.currentTime();

		try {
			restrictContentRepository.persistArticle(enterpriseId, articleId);
			return StatusCode.REQUEST_SUCCESS;
		} catch (PersistenceException e) {
			// API is catching com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
			LOG.debug(
					"Catching and successfully handling exception com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException");
			LOG.debug("Caused By : user is trying to hide same article again. Enterprise id " + enterpriseId + " articleId: " + articleId);
			return StatusCode.ARTICLE_ALREADY_HIDDEN;
		} catch (Exception e) {
			LOG.error("Error while hiding articleId: " + articleId + " for enterpriseId: " + enterpriseId, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "hideContent");
		}
	}

	@Override
	public boolean checkIfHiddenContent(long enterpriseId, String articleId) throws Exception {

		long startTime = PerfMonitor.currentTime();

		try {
			APIArticleHide article = restrictContentRepository.getArticle(enterpriseId, articleId);

			if (article != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			LOG.error("Error while checking hidden content. ArticleId: " + articleId + " for enterpriseId: " + enterpriseId, e);
			throw e;
		} finally {
			PerfMonitor.recordStats(startTime, PerfActivityType.ReqTime, "checkIfHiddenContent");
		}
	}

	@Override
	public String getAllHiddenContent(long enterpriseId, String contentPrefix) {

		List<APIArticleHide> articleList = restrictContentRepository.getAllHiddenArticles(enterpriseId, contentPrefix);
		String articleIdsSSV = "";

		if (articleList != null && !articleList.isEmpty()) {

			for (APIArticleHide article : articleList) {
				String articleId = article.getArticleId();
				String[] temp = articleId.split(":");
				if (temp.length == 2) {
					articleIdsSSV += " " + temp[1];
				}
			}
		}
		return articleIdsSSV.trim();
	}
}
