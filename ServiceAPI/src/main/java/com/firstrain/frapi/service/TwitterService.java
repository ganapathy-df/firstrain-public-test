package com.firstrain.frapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.firstrain.frapi.FRService;
import com.firstrain.frapi.domain.Tweet;
import com.firstrain.frapi.domain.TwitterSpec;
import com.firstrain.frapi.pojo.NotableDetails;
import com.firstrain.frapi.pojo.wrapper.TweetSet;

@Service
public interface TwitterService extends FRService {

	public TweetSet getTweets(String[] catIds, boolean attachGroupInfo, TwitterSpec tSpec, long... tweetIds) throws Exception;

	public List<Tweet> getTweetUsers(long groupId, long tweetId) throws Exception;

	public TweetSet getTopTweets(TwitterSpec tSpec) throws Exception;

	public NotableDetails getNotableDetails(long tweetId, long groupId, boolean isIpad) throws Exception;

}
