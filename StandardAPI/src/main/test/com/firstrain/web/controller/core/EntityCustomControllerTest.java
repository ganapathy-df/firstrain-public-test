package com.firstrain.web.controller.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.content.similarity.DocumentSimilarityUtilV3;
import com.firstrain.db.api.FRAPIAuthDbAPI;
import com.firstrain.db.obj.APIAccount;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.BlendDunsInput;
import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.repository.EntityBaseServiceRepository;
import com.firstrain.frapi.service.EntityBaseService;
import com.firstrain.frapi.service.EntityBriefCustomService;
import com.firstrain.frapi.service.impl.EntityBreifCustomServiceImpl;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.obj.IEntityInfo;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.DocListBucket;
import com.firstrain.solr.client.EntityEntry;
import com.firstrain.solr.client.HotListBucket;
import com.firstrain.solr.client.HotListEntry;
import com.firstrain.solr.client.SearchResult;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.solr.client.SolrSearcher;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.pojo.Entity;
import com.firstrain.web.pojo.SearchResultInputBean;
import com.firstrain.web.pojo.SearchResultWeb;
import com.firstrain.web.response.AuthKeyResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.service.core.AuthKeyCacheManager;
import com.firstrain.web.service.core.Constant;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
	Constant.class,
	FRAPIAuthDbAPI.class,
	PersistenceProvider.class
})
public class EntityCustomControllerTest {

	private static final String AUTH_PASSWORD = "authPassword";
	private static final String AUTH_NAME = "authName";
	private static final String ENCRYPTED_PASSWD = "wtgrKBCpdcBp4R+7XaMy6VMywBhugVuT2+OEB8w46bg=";
	private static final String SALT_KEY = "saltKey";
	private static final String EMPTY_REQ_BODY = "";
	private static final String INCORRECT_REQ_BODY = "Incorrect";

	private final JSONResponse jsonResponse = new JSONResponse();
	private final AuthKeyResponse authKeyResponse = new AuthKeyResponse();
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private RequestParsingService requestParsingService;
	@Mock
	private Transaction txn;
	@Mock
	private ResponseDecoratorService responseDecoratorService;
	@Mock
	private AuthKeyCacheManager authKeyCacheManager;
	@Mock
	private IEntityInfoCache entityInfoCache;
	@Mock
	private IEntityInfo entityInfo;
	@Mock
	private SolrSearcher searcher;
	@Mock
	private EntityBaseServiceRepository entityBaseServiceRepository;
	@Mock
	private EntityBaseService entityBaseService;

	@InjectMocks
	private EntityCustomController controller;

	private EntityBriefCustomService entityBriefCustomService = new EntityBreifCustomServiceImpl();
	private ServicesAPIUtil servicesAPIUtil = new ServicesAPIUtil();
	private ConvertUtil convertUtil = new ConvertUtil();
	private DocumentSimilarityUtilV3 dsutil;
	@Rule
	public final ErrorCollector collector = new ErrorCollector();

	@Captor
	private ArgumentCaptor<Integer> integerArgumentCaptor;
	@Mock
	private EntityEntry entity;
	@Mock
	private DocEntry docEntry;

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(FRAPIAuthDbAPI.class);
		PowerMockito.mockStatic(Constant.class);
		PowerMockito.mockStatic(PersistenceProvider.class);
		mockForSearchServiceRepository();
		APIAccount apiAccount = new APIAccount();
		apiAccount.setSalt(SALT_KEY);
		apiAccount.setAuthPassword(ENCRYPTED_PASSWD);
		when(FRAPIAuthDbAPI.getAccountDetails(anyString(), anyString())).thenReturn(apiAccount);
		when(PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE)).thenReturn(txn);
		when(requestParsingService.getErrorResponse(anyInt())).thenReturn(jsonResponse);
		when(requestParsingService.getRefinedReqVal(AUTH_NAME)).thenReturn("userName");
		when(requestParsingService.getRefinedReqVal(AUTH_PASSWORD)).thenReturn(AUTH_PASSWORD);
		when(responseDecoratorService.getAuthKeyResponse(any(AuthAPIResponse.class))).thenReturn(authKeyResponse);
		setRequiredFields();

	}

	@Test
	public void givenEmptyRequestBodyWhenCoMentionsThenErrorResponse() {
		// Act
		controller.coMentions(EMPTY_REQ_BODY);
		// Assert
		verify(requestParsingService).getErrorResponse(integerArgumentCaptor.capture());
		collector.checkThat(integerArgumentCaptor.getValue(), is(StatusCode.INSUFFICIENT_ARGUMENT));
	}

	@Test
	public void givenIncorrectRequestBodyWhenCoMentionsThenErrorResponse() {
		// Act
		controller.coMentions(INCORRECT_REQ_BODY);
		// Assert
		verify(requestParsingService).getErrorResponse(integerArgumentCaptor.capture());
		collector.checkThat(integerArgumentCaptor.getValue(), is(StatusCode.ILLEGAL_ARGUMENT));
	}

	@Test
	public void givenSearchResultInputBeanWhenCoMentionsThenResponse() throws Exception {
		// Arrange
		SearchResultInputBean inputBean = new SearchResultInputBean();
		List<Long> catIds = Collections.singletonList(1L);
		inputBean.setSecondaryCatIds(catIds);
		inputBean.setScope(1);
		inputBean.setCount(1);
		inputBean.setDaysCount(1);
		// Act
		JSONResponse<List<Entity>> actual = controller.coMentions(JSONUtility.serialize(inputBean));
		// Assert
		verify(requestParsingService, never()).getErrorResponse(anyInt());
		assertEquals(1, actual.getResult().size());
	}

	@Test
	public void givenEmptyRequestBodyWhenSearchResultThenErrorResponse() {
		// Act
		controller.searchResult(EMPTY_REQ_BODY);
		// Assert
		verify(requestParsingService).getErrorResponse(integerArgumentCaptor.capture());
		collector.checkThat(integerArgumentCaptor.getValue(), is(StatusCode.INSUFFICIENT_ARGUMENT));
	}

	@Test
	public void givenIncorrectRequestBodyWhenSearchResultThenErrorResponse() {
		// Act
		controller.searchResult(INCORRECT_REQ_BODY);
		// Assert
		verify(requestParsingService).getErrorResponse(integerArgumentCaptor.capture());
		collector.checkThat(integerArgumentCaptor.getValue(), is(StatusCode.ILLEGAL_ARGUMENT));
	}

	@Test
	public void givenSearchResultInputBeanTypeFRWhenSearchResultThenResponse() throws Exception {
		// Arrange
		SearchResultInputBean inputBean = new SearchResultInputBean();
		List<Long> catIds = Collections.singletonList(1L);
		inputBean.setSecondaryCatIds(catIds);
		inputBean.setScope(1);
		inputBean.setCount(1);
		inputBean.setDaysCount(1);
		inputBean.setType(SectionType.FR.name());
		DocumentSet webResultsForSearch = new DocumentSet();
		when(entityBaseService.getWebResultsForSearch(anyString(), anyString(), any(BaseSpec.class),
				any(BlendDunsInput.class))).thenReturn(webResultsForSearch);
		// Act
		JSONResponse<SearchResultWeb> actual = controller.searchResult(JSONUtility.serialize(inputBean));
		// Assert
		verify(requestParsingService, never()).getErrorResponse(anyInt());
		assertEquals(webResultsForSearch, actual.getResult().getDocumentSet());
	}

	@Test
	public void givenSearchResultInputBeanTypeNotFRWhenSearchResultThenResponse() throws Exception {
		// Arrange
		SearchResultInputBean inputBean = new SearchResultInputBean();
		List<Long> catIds = Collections.singletonList(1L);
		inputBean.setSecondaryCatIds(catIds);
		inputBean.setPrimaryCatId(1L);
		inputBean.setScope(1);
		inputBean.setCount(1);
		inputBean.setDaysCount(1);
		inputBean.setAdvanceSort(true);
		// Act
		JSONResponse<SearchResultWeb> actual = controller.searchResult(JSONUtility.serialize(inputBean));
		// Assert
		verify(requestParsingService, never()).getErrorResponse(anyInt());
		assertFalse(actual.getResult().getDocumentSet().getDocuments().isEmpty());
	}

	@Test
	public void givenEmptyRequestBodyWhenDocCountForNDaysThenErrorResponse() {
		// Act
		controller.docCountForNDays(EMPTY_REQ_BODY);
		// Assert
		verify(requestParsingService).getErrorResponse(integerArgumentCaptor.capture());
		collector.checkThat(integerArgumentCaptor.getValue(), is(StatusCode.INSUFFICIENT_ARGUMENT));
	}

	@Test
	public void givenEmptySecondaryCatIdsWhenDocCountForNDaysThenErrorResponse() throws Exception {
		SearchResultInputBean inputBean = new SearchResultInputBean();
		// Act
		controller.docCountForNDays(JSONUtility.serialize(inputBean));
		// Assert
		verify(requestParsingService).getErrorResponse(integerArgumentCaptor.capture());
		collector.checkThat(integerArgumentCaptor.getValue(), is(StatusCode.INSUFFICIENT_ARGUMENT));
	}

	@Test
	public void givenSecondaryCatIdsWhenDocCountForNDaysThenResponse() throws Exception {
		SearchResultInputBean inputBean = new SearchResultInputBean();
		List<Long> catIds = Collections.singletonList(1L);
		inputBean.setSecondaryCatIds(catIds);
		// Act
		JSONResponse<Map<Long, Integer>> actual = controller.docCountForNDays(JSONUtility.serialize(inputBean));
		// Assert
		verify(requestParsingService, never()).getErrorResponse(anyInt());
		assertFalse(actual.getResult().isEmpty());
	}

	@Test
	public void givenIncorrectRequestBodyWhenDocCountForNDaysThenErrorResponse() {
		// Act
		controller.docCountForNDays(INCORRECT_REQ_BODY);
		// Assert
		verify(requestParsingService).getErrorResponse(integerArgumentCaptor.capture());
		collector.checkThat(integerArgumentCaptor.getValue(), is(StatusCode.ILLEGAL_ARGUMENT));
	}

	private void mockForSearchServiceRepository() throws Exception {
		when(entityInfo.getId()).thenReturn("1");
		when(entityInfoCache.catIdToEntity(anyString())).thenReturn(entityInfo);
		when(entityInfoCache.companyIdToEntity(anyInt())).thenReturn(entityInfo);
		SearchResult result = arrangeData();

		when(searcher.search(any(SearchSpec.class))).thenReturn(result);
	}

	private SearchResult arrangeData() {
	    when(entityBaseServiceRepository.getEntityInfoCache()).thenReturn(entityInfoCache);
	    when(entityBaseServiceRepository.getSearcher()).thenReturn(searcher);
	    SearchResult result = new SearchResult();
	    HotListBucket hotListBucket = new HotListBucket("bucket");
	    List<HotListEntry> entries = new ArrayList<HotListEntry>();
	    HotListEntry hotListEntry = new HotListEntry();
	    hotListEntry.docCount = 2;
	    hotListEntry.entity = entity;
	    entries.add(hotListEntry);
	    hotListBucket.entries = entries;
	    result.facetCompanies = hotListBucket;
	    List<DocListBucket> buckets = new ArrayList<DocListBucket>();
	    List<DocEntry> docs = new ArrayList<DocEntry>();
	    when(docEntry.getTitle()).thenReturn("title");
	    docs.add(docEntry);
	    docs.add(docEntry);
	    DocListBucket docListBucket = new DocListBucket(entity, docs, 0, 0);
	    docListBucket.matches = 1;
	    buckets.add(docListBucket);
	    buckets.add(docListBucket);
	    result.buckets = buckets;
	    return result;
	}
	
	private void setRequiredFields() throws Exception {
		Whitebox.setInternalState(servicesAPIUtil, "entityBaseServiceRepository", entityBaseServiceRepository);
		Whitebox.setInternalState(entityBriefCustomService, "entityBaseServiceRepository", entityBaseServiceRepository);
		Whitebox.setInternalState(entityBriefCustomService, "entityBaseService", entityBaseService);
		dsutil = new DocumentSimilarityUtilV3("");
		Whitebox.setInternalState(entityBriefCustomService, "dsutil", dsutil);
		Whitebox.setInternalState(entityBriefCustomService, "convertUtil", convertUtil);
		Whitebox.setInternalState(controller, "entityBriefCustomService", entityBriefCustomService);
		Whitebox.setInternalState(controller, "servicesAPIUtil", servicesAPIUtil);
	}
}
