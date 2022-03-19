package com.firstrain.frapi.repository.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.FRAPIArticleDbAPI;
import com.firstrain.db.api.TakeDownDbAPI;
import com.firstrain.db.obj.APIArticleHide;
import com.firstrain.db.obj.APIArticleTakeDown;
import com.firstrain.db.obj.APIArticleTakeDown.Status;
import com.firstrain.frapi.repository.RestrictContentRepository;

@Service
public class RestrictContentRepositoryImpl implements RestrictContentRepository {

	@Override
	public APIArticleHide getArticle(long enterpriseId, String articleId) {
		return FRAPIArticleDbAPI.getArticle(enterpriseId, articleId);
	}

	@Override
	public String fetchSourceIdsCSVByEnterpriseId(long enterpriseId) {
		return TakeDownDbAPI.fetchSourceIdsCSVByEnterpriseId(enterpriseId);
	}

	@Override
	public void persistArticle(long enterpriseId, String articleId) throws Exception {

		APIArticleHide article = new APIArticleHide();
		article.setEnterpriseId(enterpriseId);
		article.setArticleId(articleId);

		Transaction txn = null;

		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
			FRAPIArticleDbAPI.persistArticle(txn, article);
			txn.commit();
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}

	@Override
	public List<APIArticleHide> getAllHiddenArticles(long enterpriseId, String articlePrefix) {
		return FRAPIArticleDbAPI.getAllHiddenArticles(enterpriseId, articlePrefix);
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
			txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
			TakeDownDbAPI.persistTakeDownArticle(txn, article);
			txn.commit();
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}
}
