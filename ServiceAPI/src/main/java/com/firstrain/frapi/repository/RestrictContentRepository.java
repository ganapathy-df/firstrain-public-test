package com.firstrain.frapi.repository;

import java.util.List;

import org.springframework.stereotype.Service;

import com.firstrain.db.obj.APIArticleHide;
import com.firstrain.db.obj.APIArticleTakeDown;

@Service
public interface RestrictContentRepository {

	public APIArticleHide getArticle(long enterpriseId, String articleId);

	public void persistArticle(long enterpriseId, String articleId) throws Exception;

	public APIArticleTakeDown getTakeDownArticle(long articleId);

	public void persistTakeDownArticle(long articleId) throws Exception;

	public String fetchSourceIdsCSVByEnterpriseId(long enterpriseId);

	public List<APIArticleHide> getAllHiddenArticles(long enterpriseId, String articlePrefix);
}
