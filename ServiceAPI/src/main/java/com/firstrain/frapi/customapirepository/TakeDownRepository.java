package com.firstrain.frapi.customapirepository;

import org.springframework.stereotype.Service;

import com.firstrain.db.obj.APIArticleTakeDown;

@Service
public interface TakeDownRepository {

	public APIArticleTakeDown getTakeDownArticle(long articleId);

	public void persistTakeDownArticle(long articleId) throws Exception;

	public String fetchSourceIdsCSVByEnterpriseId(long enterpriseId);

}
