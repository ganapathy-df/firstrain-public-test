package com.firstrain.frapi.service;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;

@Service
public interface RestrictContentService extends FRService {

	public int hideContent(long userId, long entrpriseId, String articleId) throws Exception;

	public boolean checkIfHiddenContent(long enterpriseId, String articleId) throws Exception;

	public String getAllHiddenContent(long enterpriseId, String contentPrefix);
}
