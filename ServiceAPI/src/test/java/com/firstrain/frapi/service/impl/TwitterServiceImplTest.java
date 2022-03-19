package com.firstrain.frapi.service.impl;

import com.aurea.unittest.commons.jdbc.QueryResult;
import com.firstrain.frapi.domain.Tweet;
import com.firstrain.frapi.domain.TwitterSpec;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.NotableDetails;
import com.firstrain.frapi.pojo.wrapper.TweetSet;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.repository.impl.EntityBaseServiceRepositoryImpl;
import com.firstrain.frapi.repository.impl.IndustryClassificationMap;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.DateBucketUtils;
import com.firstrain.frapi.util.DefaultEnums;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.util.SolrServerReader;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        TwitterServiceImpl.class,
        SolrServerReader.class,
        QueryRequest.class
})
public class TwitterServiceImplTest {

    private static final String CAT_ID = "12";
    private static final int SPEC_ROWS = 10;
    private static final long TWEET_ID = 12;
    private static final String ENT_INFO_ID = "123";
    private static final long DOC_GROUP_ID = 12L;
    private static final String USR_IMAGE = "img";
    private static final String TWEET_CLASS = "tweetClass";
    private static final String CORE_TWEET = "coreTweet";
    private static final String DOC_TWEET = "docTweet";
    private static final String SCREEN_NAME = "screenName";
    private static final String DOC_NAME = "docName";
    private static final String DOC_LINKS = "docLinks";
    private static final String DOC_DESC = "docDesc";
    private static final int LINK_ID = 22;
    private static final String DOC_SOURCE = "source";
    private static final int TOPIC_ID_CORE_TWEET = 34;
    private static final String HOME_PAGE = "home";
    private static final int SPEC_START = 40;
    private static final String EXC_ARTICLE_IDS_SSV = "879 34 23 2342 8P";
    private static final int GROUP_SIZE = 44;
    private static final int COMBO_SCORE = 483;
    @InjectMocks
    @Spy
    private final TwitterServiceImpl twitterService = new TwitterServiceImpl();
    @Spy
    private final EntityBaseServiceRepositoryImpl entityBaseServiceRepository = new EntityBaseServiceRepositoryImpl();
    @Spy
    private final ConvertUtil convertUtil = new ConvertUtil();
    @Spy
    private final ServicesAPIUtil servicesAPIUtil = new ServicesAPIUtil();
    @Spy
    private final DateBucketUtils dateBucketUtils = new DateBucketUtils();
    @Spy
    private final IndustryClassificationMap industryClassificationMap = new IndustryClassificationMap();
    @Mock
    private IEntityInfoCache entityInfoCache;
    @Mock
    private IEntityInfo entityInfo;
    @Mock
    private QueryRequest queryRequest;
    @Mock
    private QueryResponse queryResponse;
    @Mock
    private NamedList<Object> objectNamedList;
    @Rule
    public final ErrorCollector errorCollector = new ErrorCollector();

    @Before
    public void setUp() throws Exception {
        mockStatic(SolrServerReader.class, QueryRequest.class);
        NamedList<Object> list = new NamedList();
        when(objectNamedList.get("tweetId"))
                .thenReturn(TWEET_ID);
        when(objectNamedList.get("roundrobin"))
                .thenReturn(objectNamedList)
                .thenReturn(Collections.singletonList(objectNamedList));
        when(objectNamedList.get("resultScope")).thenReturn(1);
        when(objectNamedList.get("maxPagingRows")).thenReturn(100);
        list.add("topTweets", objectNamedList);
        list.add("summary", objectNamedList);
        when(queryResponse.getResponse()).thenReturn(list);
        when(queryRequest.process(any(SolrServer.class)))
                .thenReturn(queryResponse);
        whenNew(QueryRequest.class).withAnyArguments().thenReturn(queryRequest);
        when(entityInfo.getId()).thenReturn(ENT_INFO_ID);
        when(entityInfoCache.catIdToEntity(anyString())).thenReturn(entityInfo);
        when(entityInfoCache.companyIdToEntity(anyInt())).thenReturn(entityInfo);
        doReturn(entityInfoCache).when(entityBaseServiceRepository).getEntityInfoCache();
    }

    @Test
    public void getTweets() throws Exception {
        //Arrange
        Entity ent = createEntity();
        doReturn(ent).when(convertUtil).convertEntityInfo(any(IEntityInfo.class));
        when(SolrServerReader.retrieveNSolrDocs(any(SolrServer.class), anyString(),
                anyInt(), anyInt(), (String[]) anyVararg()))
                .thenReturn(createSolrDocuments());
        //Act
        TweetSet tweetSetResult = twitterService.getTweets(new String[]{"1", "2"},
                true,
                createTwitterSpec(), TWEET_ID);
        //Assert
        errorCollector.checkThat(tweetSetResult.getTweets().size(), is(1));
    }

    @Test
    public void getTweetUsers() throws Exception {
        //Arrange
        when(SolrServerReader.retrieveNSolrDocs(any(SolrServer.class), anyString(),
                anyInt(), anyInt(), anyString(), anyBoolean(), (String[]) anyVararg()))
                .thenReturn(createSolrDocuments());
        //Act
        List<Tweet> tweetList = twitterService.getTweetUsers(DOC_GROUP_ID, TWEET_ID);
        //Assert
        Tweet tweetResult = tweetList.get(0);
        errorCollector.checkThat(tweetResult.getUserImage(), is(USR_IMAGE));
        errorCollector.checkThat(tweetResult.getScreenName(), is(SCREEN_NAME));
    }

    @Test
    public void getTopTweets() throws Exception {
        //Arrange
        Entity ent = createEntity();
        doReturn(ent).when(convertUtil).convertEntityInfo(any(IEntityInfo.class));
        when(SolrServerReader.retrieveNSolrDocs(any(SolrServer.class), anyString(),
                anyInt(), anyInt(), (String[]) anyVararg()))
                .thenReturn(createSolrDocuments());
        when(SolrServerReader.retrieveSolrDocsInBatches(any(SolrServer.class), anyString(),
                anyInt(), (String[]) anyVararg()))
                .thenReturn(createSolrDocuments());
        //Act
        TweetSet setResult = twitterService.getTopTweets(createTwitterSpec());
        //Assert
        errorCollector.checkThat(setResult.getTotal(), is(1));
        errorCollector.checkThat(setResult.getTweets().size(), is(1));
        errorCollector.checkThat(setResult.getBucketMode(), is(DefaultEnums.DateBucketingMode.DATE));
    }

    @Test
    public void getNotableDetails() throws Exception {
        //Arrange
        when(SolrServerReader.retrieveNSolrDocs(any(SolrServer.class), anyString(),
                anyInt(), anyInt(), anyString(), anyBoolean(), (String[]) anyVararg()))
                .thenReturn(createSolrDocuments());
        when(SolrServerReader.retrieveSolrDocsInBatches(any(SolrServer.class), anyString(),
                anyInt(), (String[]) anyVararg()))
                .thenReturn(createSolrDocuments());

        //Act
        NotableDetails details = twitterService.getNotableDetails(TWEET_ID, DOC_GROUP_ID, true
        );

        //Assert
        errorCollector.checkThat(details.getTweetId(), is("TW:" + TWEET_ID));
        errorCollector.checkThat(details.getNotableDetails().size(), is(1));
    }

    private static TwitterSpec createTwitterSpec() {
        TwitterSpec spec = new TwitterSpec();
        spec.setCatIds(new String[]{CAT_ID});
        spec.setListView(true);
        spec.setNeedBucket(true);
        spec.setStart((short) SPEC_START);
        spec.setRows(SPEC_ROWS);
        spec.setExcludeArticleIdsSSV(EXC_ARTICLE_IDS_SSV);
        spec.setBucketMode(DefaultEnums.DateBucketingMode.DATE);
        String[] strArr = new String[300];
        strArr[0] = CAT_ID;
        spec.setCatIds(strArr);
        return spec;
    }

    private static SolrDocumentList createSolrDocuments() {
        SolrDocument document = new SolrDocument();
        document.setField("tweetId", TWEET_ID);
        document.setField("companyId", Collections.singletonList(123));
        document.setField("description", DOC_DESC);
        document.setField("expandedLinks", Collections.singletonList(LINK_ID));
        document.setField("tweetCreationDate", new Date());
        document.setField("links", DOC_LINKS);
        document.setField("name", DOC_NAME);
        document.setField("screenName", SCREEN_NAME);
        document.setField("tweet", DOC_TWEET);
        document.setField("coreTweet", CORE_TWEET);
        document.setField("tweetClass", TWEET_CLASS);
        document.setField("userImage", USR_IMAGE);
        document.setField("source", Collections.singletonList(DOC_SOURCE));
        document.setField("groupId", DOC_GROUP_ID);
        document.setField("groupLead", true);
        document.setField("scope", new Byte("1"));
        document.setField("comboScore", COMBO_SCORE);
        document.setField("creationTime", new Date());
        document.setField("affinityScope", Collections.singletonList(89));
        document.setField("groupSize", GROUP_SIZE);
        document.setField("catId", CAT_ID);
        document.setField("affinityScoreNormalized", (short) 199);
        document.setField("topicIdCoreTweet", Collections.singletonList(TOPIC_ID_CORE_TWEET));
        SolrDocumentList documents = new SolrDocumentList();
        documents.add(document);
        return documents;
    }

    private static Entity createEntity() {
        Entity ent = new Entity();
        ent.setId(String.valueOf(TWEET_ID));
        ent.setAdded(true);
        ent.setHomePage(HOME_PAGE);
        ent.setThirtyOneDaysDocCount(31);
        return ent;
    }
}
