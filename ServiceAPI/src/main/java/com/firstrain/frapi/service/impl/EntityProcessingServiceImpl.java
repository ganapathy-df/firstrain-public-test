package com.firstrain.frapi.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firstrain.frapi.domain.Document;
import com.firstrain.frapi.domain.Tweet;
import com.firstrain.frapi.pojo.Event;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.EventSet;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.service.EntityProcessingService;
import com.firstrain.frapi.util.DateBucketUtils;
import com.firstrain.frapi.util.DateBucketUtils.BucketSpec;
import com.firstrain.frapi.util.DefaultEnums.EventInformationEnum;
import com.firstrain.frapi.util.EntityHandler;
import com.firstrain.frapi.util.FRAPIConstant;
import com.firstrain.utils.TitleUtils;

@Service
public class EntityProcessingServiceImpl implements EntityProcessingService {

	private final Logger LOG = Logger.getLogger(EntityProcessingServiceImpl.class);

	@Autowired
	private DateBucketUtils dateBucketUtils;

	@Override
	public DocumentSet getDocumentSetWithId(DocumentSet documentSet) throws Exception {
		if (documentSet == null) {
			return null;
		}
		try {
			if (documentSet.isFiling()) {
				doAddIdToFilings(documentSet);
			} else {
				populateDocumentIds(documentSet);
			}
		} catch (Exception e) {
			LOG.error("Error Processing Request For Documents !", e);
			throw e;
		}
		return documentSet;
	}

	private void populateDocumentIds(final DocumentSet documentSet) {
		List<Document> docs = documentSet.getDocuments();
		if (docs != null) {
			for (Document doc : docs) {
				String id = FRAPIConstant.DOCUMENT_PREFIX + doc.getId();
				doc.setId(id);
			}
		} else if (documentSet.getDocumentBucket() != null) {
			for (Map.Entry<String, List<Document>> entry : documentSet.getDocumentBucket().entrySet()) {
				for (Document doc : entry.getValue()) {
					String id = FRAPIConstant.DOCUMENT_PREFIX + doc.getId();
					doc.setId(id);
				}
			}
		}
	}

	private void doAddIdToFilings(final DocumentSet documentSet) {
		List<Document> docFillings = documentSet.getDocuments();
		if (docFillings != null) {
			for (Document doc : docFillings) {
				doc = this.addIdToFilings(doc);
			}
		}
	}

	@Override
	public TweetSet getTweetsWithId(TweetSet tweetSet) throws Exception {

		if (tweetSet == null) {
			return null;
		}
		try {
			if (tweetSet.getTweets() != null) {
				for (Tweet tweet : tweetSet.getTweets()) {
					String tweetId = FRAPIConstant.TWEET_PREFIX + tweet.getTweetId();
					tweet.setTweetId(tweetId);
				}
			}
			if (tweetSet.getBucketedTweets() != null) {
				for (Map.Entry<String, List<Tweet>> entry : tweetSet.getBucketedTweets().entrySet()) {
					for (Tweet tweet : entry.getValue()) {
						String tweetId = FRAPIConstant.TWEET_PREFIX + tweet.getTweetId();
						tweet.setTweetId(tweetId);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error Processing Request For Tweets !", e);
			throw e;
		}
		return tweetSet;
	}

	private Document addIdToFilings(Document docFiling) {
		try {
			if (docFiling != null) {

				docFiling.setId(FRAPIConstant.SEC_FILING_PREFIX + docFiling.getId());

				String secFormType = docFiling.getSecFormType();
				String title = docFiling.getTitle();
				if (secFormType != null) {
					title = TitleUtils.getSecTitle(null, title, secFormType);
				}
				docFiling.setTitle(title);
			}
		} catch (Exception e) {
			LOG.error("Error Processing Request!", e);
		}
		return docFiling;
	}

	@Override
	public EventSet getEventSetWithId(EventSet eventSet) {
		if (eventSet == null || (eventSet.getEvents() == null && eventSet.getEventBuckets() == null)) {
			return null;
		}
		if (eventSet.getEvents() != null) {
			for (Event eventPojo : eventSet.getEvents()) {
				EntityHandler.addId(eventPojo);
			}
		} else if (eventSet.getEventBuckets() != null) {
			for (Map.Entry<String, List<Event>> entry : eventSet.getEventBuckets().entrySet()) {
				for (Event eventPojo : entry.getValue()) {
					EntityHandler.addId(eventPojo);
				}
			}
		}
		return eventSet;
	}

	@Override
	public EventSet getEventSetWithDateBucketing(EventSet eventSet, BucketSpec bucketSpec) throws Exception {
		Map<String, List<Event>> bucketedEvents = null;
		try {
			bucketedEvents = dateBucketUtils.getListGroupByDate(eventSet.getEvents(), bucketSpec);
			eventSet.setEventBuckets(bucketedEvents);
		} catch (Exception e) {
			LOG.error("Error Processing Request!", e);
			throw e;
		}
		return eventSet;
	}
}
