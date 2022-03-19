package com.firstrain.frapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;

@Service
public interface RehydrateService extends FRService {

	public DocumentSet getDocumentView(long frUserId, List<Long> docIds, short industryClassificationId) throws Exception;

	public TweetSet getTweetView(long frUserId, List<Long> tweetIds) throws Exception;
}
