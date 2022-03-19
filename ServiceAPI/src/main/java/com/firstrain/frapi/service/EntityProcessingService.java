package com.firstrain.frapi.service;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.util.DateBucketUtils.BucketSpec;

@Service
public interface EntityProcessingService extends FRService {

	/**
	 * Get Metadata For Documents
	 * 
	 * @param documentSet
	 * @return documentSet
	 */
	public DocumentSet getDocumentSetWithId(DocumentSet documentSet) throws Exception;

	/**
	 * Get Metadata For Tweets
	 * 
	 * @param tweetSet
	 * @return tweetSet
	 */
	public TweetSet getTweetsWithId(TweetSet tweetSet) throws Exception;

	/**
	 * Get Metadata For Events
	 * 
	 * @param eventSet
	 * @return eventSet
	 */
	public EventSet getEventSetWithId(EventSet eventSet) throws Exception;

	/**
	 * Get Date Bucketed Event List From The EventSet
	 * 
	 * @param eventSet
	 * @param bucketSpec
	 * @return
	 */
	public EventSet getEventSetWithDateBucketing(EventSet eventSet, BucketSpec bucketSpec) throws Exception;
}
