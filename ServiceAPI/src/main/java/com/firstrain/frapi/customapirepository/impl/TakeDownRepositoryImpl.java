package com.firstrain.frapi.customapirepository.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.TakeDownDbAPI;
import com.firstrain.db.obj.APIArticleTakeDown;
import com.firstrain.db.obj.APIArticleTakeDown.Status;
import com.firstrain.frapi.customapirepository.TakeDownRepository;

@Service
public class TakeDownRepositoryImpl implements TakeDownRepository {

	private final Logger LOG = Logger.getLogger(TakeDownRepositoryImpl.class);

	@Override
	public String fetchSourceIdsCSVByEnterpriseId(long enterpriseId) {
		return TakeDownDbAPI.fetchSourceIdsCSVByEnterpriseId(enterpriseId);
	}

	@Override
	public APIArticleTakeDown getTakeDownArticle(long articleId) {
		return TakeDownDbAPI.getTakeDownArticle(articleId);
	}

	@Override
	public void persistTakeDownArticle(long articleId) throws Exception {
		APIArticleTakeDown article = new APIArticleTakeDown();
		article.setArticleId(Long.toString(articleId));
		article.setStatus(Status.NEW);

		Transaction txn = null;

		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.SPY_DB);
			TakeDownDbAPI.persistTakeDownArticle(txn, article);
			txn.commit();
		} catch (Exception e) {
			LOG.error("Error while persisting take down articleId: " + articleId, e);
			throw e;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}
}
