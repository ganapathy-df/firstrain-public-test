package com.firstrain.web.controller.core;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.ItemsDbAPI;
import com.firstrain.db.api.TagsDbAPI;
import com.firstrain.db.obj.BaseItem.FLAGS;
import com.firstrain.db.obj.EmailSchedule;
import com.firstrain.db.obj.Items;
import com.firstrain.db.obj.Items.Type;
import com.firstrain.db.obj.MailLog;
import com.firstrain.db.obj.Tags;
import com.firstrain.frapi.FRCompletionService;
import com.firstrain.frapi.domain.BaseSpec;
import com.firstrain.frapi.domain.EntityStatus;
import com.firstrain.frapi.domain.MonitorConfig;
import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.obj.MonitorBriefDomain;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.MonitorBriefDetail;
import com.firstrain.frapi.pojo.MonitorEmailAPIResponse;
import com.firstrain.frapi.pojo.wrapper.BaseSet;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.pojo.wrapper.DocumentSet;
import com.firstrain.frapi.pojo.wrapper.GetBulk;
import com.firstrain.frapi.repository.EmailServiceRepository;
import com.firstrain.frapi.repository.MonitorServiceRepository;
import com.firstrain.frapi.repository.impl.MonitorServiceRepositoryImpl;
import com.firstrain.frapi.service.MonitorBriefService;
import com.firstrain.frapi.service.MonitorService;
import com.firstrain.frapi.service.RestrictContentService;
import com.firstrain.frapi.service.impl.MonitorBriefServiceImpl;
import com.firstrain.frapi.util.MonitorAnalyticsUtil;
import com.firstrain.frapi.util.ServicesAPIUtil;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.frapi.util.TagsValidator;
import com.firstrain.frapi.util.UserMembership;
import com.firstrain.obj.IEntityInfoCache;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.helper.MonitorControllerHelper;
import com.firstrain.web.pojo.Content;
import com.firstrain.web.pojo.EntityData;
import com.firstrain.web.pojo.Tweet;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.response.EntityResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.MonitorConfigResponse;
import com.firstrain.web.response.MonitorInfoResponse;
import com.firstrain.web.response.MonitorWrapperResponse;
import com.firstrain.web.service.core.FreemarkerTemplateService;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.service.core.StorageService;
import com.firstrain.web.util.UserInfoThreadLocal;
import com.firstrain.web.wrapper.EntityDataWrapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType.VIZ;
import static com.firstrain.frapi.util.StatusCode.ILLEGAL_ARGUMENT;
import static com.firstrain.frapi.util.StatusCode.INSUFFICIENT_ARGUMENT;
import static com.firstrain.frapi.util.StatusCode.INVALID_SECTION;
import static com.firstrain.frapi.util.StatusCode.NO_EMAIL_SENT;
import static com.firstrain.frapi.util.StatusCode.NO_ITEMS;
import static com.firstrain.frapi.util.StatusCode.NO_MONITORS;
import static com.firstrain.frapi.util.StatusCode.REQUEST_SUCCESS;
import static com.firstrain.frapi.util.StatusCode.RSOURCE_NOT_FOUND;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
		UserInfoThreadLocal.class,
		PersistenceProvider.class,
		TagsDbAPI.class,
		MonitorBriefServiceImpl.class,
		MonitorController.class,
		MonitorControllerHelper.class,
		FRCompletionService.class,
		UserMembership.class,
		TagsValidator.class
})
@SuppressStaticInitializationFor("com.firstrain.common.db.jpa.PersistenceProvider")
public class MonitorControllerTest {

	private static final String ENDS_DATE = "2018-09-05";
	private static final String START_DATE = "2018-09-01";
	private static final String ENTITY_STRING = "entity";
	private static final String META_DATA = "metaData";
	private static final String VIEW_STR = "view";
	private static final String ERROR_MESSAGE = "error message";
	private static final String ATTRIBUTE_NAME = "errorMsg";
	private static final String INCORRECT_ID = "U:IncorrectId";
	private static final String RESULTS_CSV = "resultsCSVH";
	private static final String HTML_FRAG = "htmlFrag";
	private static final String FQ = "-null";
	private static final String SECTIONS = "io:sections";
	private static final long MONITOR_ID = 1L;
	private static final String USER_ID = "1";
	private static final String MONITOR_ID_STRING = "U:" + MONITOR_ID;
	private static final String REQUEST_BODY_FQ = "{\"fq\":[\"abc\",\"aaa\"]}";
	private static final String REQUEST_BODY_ENTITY = "{\"entity\":[\"abc\",\"aaa\"]}";
	private static final int STATUS_CODE_NOT_FOUND = 404;
	private static final String GET_METHOD = "get";
	private static final String ADD_FILTER = "add.filter";
	private static final String ADD_ENTITY = "add.entity";
	private static final String REMOVE_FILTER = "remove.filter";
	private static final String REMOVE_ENTITY = "remove.entity";
	private static final List<String> FILTER_LIST = unmodifiableList(Arrays.asList("abc", "aaa"));
	private static final SectionType FR_SECTION_TYPE = SectionType.valueOf("FR");
	private static final SectionType FT_SECTION_TYPE = SectionType.valueOf("FT");
	private static final SectionType E_SECTION_TYPE = SectionType.valueOf("E");
	private static final SectionType AC_SECTION_TYPE = SectionType.valueOf("AC");
	private static final String VIEW_FTL = "monitor-brief.ftl";
	private static final String TEST_WR_ID = "test-wr-id";
	private static final long TEST_ENTERPRISE_ID = 10L;
	private static final String HTML_FRAG_CLASSIC_FRAME = "classicFrame";
	private static final String SECTIONS_WITHOUT_IO = "sections";

	private JSONResponse jsonResponse;

	@Mock
	private MonitorService monitorService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private User user;
	@Mock
	private ResponseDecoratorService responseDecoratorService;
	@Mock
	private MonitorAPIResponse monitorAPIResponse;
	@Mock
	private MonitorInfoResponse monitorInfoResponse;
	@Mock
	private RequestParsingService requestParsingService;
	@Mock
	private EnterprisePref enterprisePref;
	@Mock
	private StorageService storageService;
	@Mock
	private FreemarkerTemplateService ftlService;
	@Mock
	private RestrictContentService restrictContentService;
	@Mock
	private MonitorAnalyticsUtil monitorAnalyticsUtil;
	@Mock
	private ThreadPoolTaskExecutor taskExecutor;
	@Mock
	private ThreadPoolExecutor executor;
	@Mock
	private EmailServiceRepository emailServiceRepository;
	@Mock
	private ServicesAPIUtil servicesAPIUtil;
	@Mock
	private Transaction txn;
	@Mock
	private Model model;
	@Mock
	private MonitorBriefService monitorBriefService;
	@InjectMocks
	private MonitorController controller;
	@Rule
	public final ErrorCollector collector = new ErrorCollector();
	@Captor
	private ArgumentCaptor<Integer> integerArgumentCaptor;

	private Set<SectionType> keySet;
	private Map<SectionType, SectionSpec> sectionsMap;
	private EntityDataResponse entityDataResponse;
	private MonitorControllerHelper monitorControllerHelper;
	private Map<String, GetBulk> docFieldFromStorageServiceMap;
	private DocumentSet documentSet;
	private EntityDataWrapper entityDataWrapper;

	@Before
	public void setUp() throws Exception {
		sectionsMap = new HashMap<>();
		entityDataResponse = new EntityDataResponse();
		mockStaticClasses();
		PowerMockito.when(UserInfoThreadLocal.class, GET_METHOD).thenReturn(user);
		when(user.getUserId()).thenReturn(USER_ID);
		when(monitorAPIResponse.getStatusCode()).thenReturn(StatusCode.REQUEST_SUCCESS);
		mockEnterprisePref();
		txn = PersistenceProvider.newTxn(PersistenceProvider.EMAIL_DATABASE);
		final Tags tags = new Tags();
		tags.setFlags(FLAGS.ACTIVE);
		tags.setEmailId(MONITOR_ID);
		when(TagsDbAPI.getTagById(eq(txn), anyLong())).thenReturn(tags);
		final List<Items> items = singletonList(new Items());
		when(ItemsDbAPI.getItemsByTagId(anyString(), anyLong(), Type.Comment, anyInt(), anyInt()))
				.thenReturn(items);
		final MonitorBriefDomain monitorBriefDomain = new MonitorBriefDomain();
		when(monitorAnalyticsUtil.getMonitorBriefDomainFromFolderId(anyList(), anyLong(),
				any(IEntityInfoCache.class),
				anyString(), anyString(), anyBoolean())).thenReturn(monitorBriefDomain);
		final EmailSchedule emailSchedule = new EmailSchedule();
		when(emailServiceRepository.getEmailSchedule(anyLong())).thenReturn(emailSchedule);
		final MailLog mailLog = new MailLog();
		final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		mailLog.setSentTime(timestamp);
		final List<MailLog> mailLogs = singletonList(mailLog);
		when(emailServiceRepository.getMailLog(anyLong(), any(java.sql.Timestamp.class), anyInt()))
				.thenReturn(mailLogs);
		final BaseSpec baseSpec = new BaseSpec();
		when(servicesAPIUtil.setSourceContent(anyBoolean(), anyBoolean(), any(BaseSpec.class),
				any(EnterprisePref.class))).thenReturn(baseSpec);
		setRequiredFields();
		FRCompletionService<BaseSet> completionService =
				(FRCompletionService<BaseSet>) mock(FRCompletionService.class);
		when(completionService.submit(Mockito.<Callable<BaseSet>>any())).thenReturn(null);
		when(completionService.getSubmissions()).thenReturn(1);
		when(completionService.poll(anyLong(), any(TimeUnit.class))).thenReturn(null);
		final EntityDataWrapper data = new EntityDataWrapper();
		entityDataResponse.setResult(data);
		when(responseDecoratorService.getEntityDataResponse(any(MonitorAPIResponse.class), anyString(), anyMap(),
				anyShort(), anyBoolean())).thenReturn(entityDataResponse);
		when(taskExecutor.getThreadPoolExecutor()).thenReturn(executor);
		whenNew(FRCompletionService.class).withAnyArguments().thenReturn(completionService);
		when(restrictContentService.getAllHiddenContent(anyLong(), anyString())).thenReturn(HTML_FRAG);
		when(requestParsingService.getDefaultSpec()).thenReturn(enterprisePref);
		when(requestParsingService.getSectionsPageSpecMap(anyString())).thenReturn(enterprisePref);
	}

	@Test
	public void givenFilterWhenAddFilterThenAdd() throws Exception {
		// Arrange
		when(monitorService.addFilters(MONITOR_ID, MONITOR_ID, FILTER_LIST)).thenReturn(monitorAPIResponse);
		when(responseDecoratorService.getMonitorInfoResponse(eq(monitorAPIResponse), eq(ADD_FILTER)))
				.thenReturn(monitorInfoResponse);
		mockStatic(MonitorControllerHelper.class);
		// Act
		jsonResponse = controller.addFilter(request, response, REQUEST_BODY_FQ, MONITOR_ID_STRING);
		// Assert
		assertEquals(monitorInfoResponse, jsonResponse);
		verifyStatic();
		MonitorControllerHelper.getrackingRequestParams(request, "M:" + MONITOR_ID, REQUEST_BODY_FQ);
	}

	@Test
	public void givenIncorrectMonitorIdWhenAddFilterThenReturnError() {
		// Act
		jsonResponse = controller.addFilter(request, response, REQUEST_BODY_FQ, INCORRECT_ID);
		// Assert
		assertErrResponseCode(ILLEGAL_ARGUMENT);
	}

	@Test
	public void givenEmptyReqBodyWhenAddFilterThenReturnErrorResponse() {
		// Act
		jsonResponse = controller.addFilter(request, response, "", MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(INSUFFICIENT_ARGUMENT);
	}

	@Test
	public void givenIncorrectReqBodyWhenAddFilterThenReturnErrorResponse() {
		// Act
		jsonResponse = controller.addFilter(request, response, "Incorrect JSON string", MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(ILLEGAL_ARGUMENT);
	}

	@Test
	public void givenEmptyFqParamWhenAddFilterThenReturnErrorResponse() {
		// Act
		jsonResponse = controller.addFilter(request, response, REQUEST_BODY_ENTITY, MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(INSUFFICIENT_ARGUMENT);
	}

	@Test
	public void givenStatusCodeNotOkWhenAddFilterThenReturnErrorResponse() throws Exception {
		// Arrange
		when(monitorService.addFilters(MONITOR_ID, MONITOR_ID, FILTER_LIST)).thenReturn(monitorAPIResponse);
		when(monitorAPIResponse.getStatusCode()).thenReturn(STATUS_CODE_NOT_FOUND);
		when(responseDecoratorService.getMonitorInfoResponse(eq(monitorAPIResponse), eq(ADD_FILTER)))
				.thenReturn(monitorInfoResponse);
		// Act
		jsonResponse = controller.addFilter(request, response, REQUEST_BODY_FQ, MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(RSOURCE_NOT_FOUND);
	}

	@Test
	public void removeFilterReturnCorrectResponse() throws Exception {
		// Arrange
		when(monitorService.removeFilters(MONITOR_ID, MONITOR_ID, FILTER_LIST)).thenReturn(monitorAPIResponse);
		when(responseDecoratorService.getMonitorInfoResponse(eq(monitorAPIResponse), eq(REMOVE_FILTER)))
				.thenReturn(monitorInfoResponse);
		mockStatic(MonitorControllerHelper.class);
		// Act
		jsonResponse = controller.removeFilter(request, response, REQUEST_BODY_FQ, MONITOR_ID_STRING);
		// Assert
		assertEquals(monitorInfoResponse, jsonResponse);
		verifyStatic();
		MonitorControllerHelper.getrackingRequestParams(request, "M:" + MONITOR_ID, REQUEST_BODY_FQ);
	}

	@Test
	public void removeFilterReturnErrorResponseMonitorIdIncorrect() {
		// Act
		jsonResponse = controller.removeFilter(request, response, REQUEST_BODY_FQ, INCORRECT_ID);
		// Assert
		assertErrResponseCode(ILLEGAL_ARGUMENT);
	}

	@Test
	public void removeFilterReturnErrorResponseEmptyRequestBody() {
		// Act
		jsonResponse = controller.removeFilter(request, response, "", MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(INSUFFICIENT_ARGUMENT);
	}

	@Test
	public void removeFilterReturnErrorResponseIncorrectRequestBody() {
		// Act
		jsonResponse = controller.removeFilter(request, response, "Incorrect JSON string", MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(ILLEGAL_ARGUMENT);
	}

	@Test
	public void removeFilterReturnErrorResponseEmptyFqParam() {
		// Act
		jsonResponse = controller.removeFilter(request, response, REQUEST_BODY_ENTITY, MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(INSUFFICIENT_ARGUMENT);
	}

	@Test
	public void removeFilterReturnErrorResponseStatusCodeNotOk() throws Exception {
		// Arrange
		when(monitorService.removeFilters(MONITOR_ID, MONITOR_ID, FILTER_LIST)).thenReturn(monitorAPIResponse);
		when(monitorAPIResponse.getStatusCode()).thenReturn(STATUS_CODE_NOT_FOUND);
		when(responseDecoratorService.getMonitorInfoResponse(eq(monitorAPIResponse), eq(REMOVE_FILTER)))
				.thenReturn(monitorInfoResponse);
		// Act
		jsonResponse = controller.removeFilter(request, response, REQUEST_BODY_FQ, MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(RSOURCE_NOT_FOUND);
	}

	@Test
	public void addEntityReturnCorrectResponse() throws Exception {
		// Arrange
		when(monitorService.addEntities(MONITOR_ID, MONITOR_ID, FILTER_LIST, enterprisePref))
				.thenReturn(monitorAPIResponse);
		when(responseDecoratorService.getAddRemoveEntityResponse(monitorAPIResponse, ADD_ENTITY))
				.thenReturn(monitorInfoResponse);
		mockStatic(MonitorControllerHelper.class);
		// Act
		jsonResponse = controller.addEntity(request, response, REQUEST_BODY_ENTITY, MONITOR_ID_STRING);
		// Assert
		assertEquals(monitorInfoResponse, jsonResponse);
		verifyStatic();
		MonitorControllerHelper.getrackingRequestParams(request, "M:" + MONITOR_ID, REQUEST_BODY_ENTITY);
	}

	@Test
	public void addEntityReturnErrorResponseMonitorIdIncorrect() {
		// Act
		jsonResponse = controller.addEntity(request, response, REQUEST_BODY_ENTITY, INCORRECT_ID);
		// Assert
		assertErrResponseCode(ILLEGAL_ARGUMENT);
	}

	@Test
	public void addEntityReturnErrorResponseEmptyRequestBody() {
		// Act
		jsonResponse = controller.addEntity(request, response, "", MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(INSUFFICIENT_ARGUMENT);
	}

	@Test
	public void addEntityReturnErrorResponseIncorrectRequestBody() {
		// Act
		jsonResponse = controller.addEntity(request, response, "Incorrect JSON string", MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(ILLEGAL_ARGUMENT);
	}

	@Test
	public void addEntityReturnErrorResponseEmptyFqParam() {
		// Act
		jsonResponse = controller.addEntity(request, response, REQUEST_BODY_FQ, MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(INSUFFICIENT_ARGUMENT);
	}

	@Test
	public void addEntityReturnErrorResponseStatusCodeNotOk() throws Exception {
		// Arrange
		when(monitorService.addEntities(MONITOR_ID, MONITOR_ID, FILTER_LIST, enterprisePref))
				.thenReturn(monitorAPIResponse);
		when(monitorAPIResponse.getStatusCode()).thenReturn(STATUS_CODE_NOT_FOUND);
		when(responseDecoratorService.getAddRemoveEntityResponse(eq(monitorAPIResponse), eq(ADD_ENTITY)))
				.thenReturn(monitorInfoResponse);
		// Act
		jsonResponse = controller.addEntity(request, response, REQUEST_BODY_ENTITY, MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(RSOURCE_NOT_FOUND);
	}

	@Test
	public void removeEntityReturnCorrectResponse() throws Exception {
		// Arrange
		when(monitorService.removeEntities(MONITOR_ID, MONITOR_ID, FILTER_LIST)).thenReturn(monitorAPIResponse);
		when(responseDecoratorService.getAddRemoveEntityResponse(eq(monitorAPIResponse), eq(REMOVE_ENTITY)))
				.thenReturn(monitorInfoResponse);
		when(monitorAPIResponse.getStatusCode()).thenReturn(StatusCode.NO_ITEMS_IN_MONITOR);
		mockStatic(MonitorControllerHelper.class);
		// Act
		jsonResponse = controller.removeEntity(request, response, REQUEST_BODY_ENTITY, MONITOR_ID_STRING);
		// Assert
		assertEquals(monitorInfoResponse, jsonResponse);
		verifyStatic();
		MonitorControllerHelper.getrackingRequestParams(request, "M:" + MONITOR_ID, REQUEST_BODY_ENTITY);
	}

	@Test
	public void removeEntityReturnErrorResponseMonitorIdIncorrect() {
		// Act
		jsonResponse = controller.removeEntity(request, response, REQUEST_BODY_ENTITY, INCORRECT_ID);
		// Assert
		assertErrResponseCode(ILLEGAL_ARGUMENT);
	}

	@Test
	public void removeEntityReturnErrorResponseEmptyRequestBody() {
		// Act
		jsonResponse = controller.removeEntity(request, response, "", MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(INSUFFICIENT_ARGUMENT);
	}

	@Test
	public void removeEntityReturnErrorResponseIncorrectRequestBody() throws Exception {
		// Act
		jsonResponse = controller.removeEntity(request, response, "Incorrect JSON string", MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(ILLEGAL_ARGUMENT);
	}

	@Test
	public void removeEntityReturnErrorResponseEmptyFqParam() throws Exception {
		// Act
		jsonResponse = controller.removeEntity(request, response, REQUEST_BODY_FQ, MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(INSUFFICIENT_ARGUMENT);
	}

	@Test
	public void removeEntityReturnErrorResponseStatusCodeNotOk() throws Exception {
		// Arrange
		when(monitorService.removeEntities(anyLong(), eq(MONITOR_ID), eq(FILTER_LIST)))
				.thenReturn(monitorAPIResponse);
		when(monitorAPIResponse.getStatusCode()).thenReturn(STATUS_CODE_NOT_FOUND);
		when(responseDecoratorService.getAddRemoveEntityResponse(eq(monitorAPIResponse), eq(REMOVE_ENTITY)))
				.thenReturn(monitorInfoResponse);
		// Act
		jsonResponse = controller.removeEntity(request, response, REQUEST_BODY_ENTITY, MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(RSOURCE_NOT_FOUND);
	}

	@Test
	public void givenEmptySectionMapFromHelperWhenGetResponseInAllFormatThenError() {
		// Arrange
		Map<SectionType, SectionSpec> emptySectionsMap = new HashMap<>();
		when(enterprisePref.getSectionsMap()).thenReturn(emptySectionsMap);
		// Act
		jsonResponse = controller.getResponseInAllFormat(request, response, MONITOR_ID_STRING, SECTIONS, FQ, true,
				HTML_FRAG, RESULTS_CSV, true, true);
		// Assert
		assertErrResponseCode(ILLEGAL_ARGUMENT);
	}

	@Test
	public void givenIncorrectIdWhenGetResponseInAllFormatThenError() {
		// Act
		jsonResponse = controller.getResponseInAllFormat(request, response, INCORRECT_ID,
				SECTIONS, FQ, true, HTML_FRAG, RESULTS_CSV, true, true);
		// Assert
		assertErrResponseCode(ILLEGAL_ARGUMENT);
	}

	@Test
	public void givenEntityDataResponseWhenGetResponseInAllFormatThenGetResponse() throws Exception {
		// Arrange
		arrangeGetResponseInAllFormatCommon(FQ);
		// Act
		jsonResponse = controller.getResponseInAllFormat(request, response, MONITOR_ID_STRING, SECTIONS, FQ,
				true, HTML_FRAG, RESULTS_CSV, true, true);
		// Assert
		collector.checkThat((EntityDataResponse) jsonResponse, equalTo(entityDataResponse));
		collector.checkThat(sectionsMap.size(), equalTo(5));
		verify(storageService).populateFieldInDocSet(docFieldFromStorageServiceMap,
				documentSet);
		verify(enterprisePref).setApplyMinNodeCheckInVisualization(false);
		verify(monitorControllerHelper).setFtlService(ftlService);
		verify(monitorControllerHelper).setRequestParsingService(requestParsingService);
		verify(monitorControllerHelper).setResponseDecoratorService(responseDecoratorService);
		verify(monitorControllerHelper).updateEntityResponseData(request, response, MONITOR_ID_STRING,
				FQ, true, HTML_FRAG, RESULTS_CSV, entityDataResponse, sectionsMap, keySet);
		collector.checkThat(entityDataWrapper.getData(), nullValue());
		collector.checkThat(entityDataWrapper.getMetaData(), nullValue());
		verify(request).setAttribute("loadview", true);
		verify(request).setAttribute("activityType", "GET");
		verify(request).setAttribute("targetId", MONITOR_ID_STRING);
		verify(request).setAttribute("target", "monitorbriefwithmultipleresponsetype");
	}

	@Test
	public void givenResponseCodeNotOkWhenGetResponseInAllFormatThenReturnErrorResponse() throws Exception {
		// Arrange
		when(monitorBriefService.getMonitorBriefDetails(user, MONITOR_ID, enterprisePref, FQ))
				.thenReturn(monitorAPIResponse);
		when(monitorAPIResponse.getStatusCode()).thenReturn(NO_MONITORS);
		// Act
		jsonResponse = controller.getResponseInAllFormat(request, response, MONITOR_ID_STRING, SECTIONS, FQ,
				true, HTML_FRAG, RESULTS_CSV, true, true);
		// Assert
		assertNull(jsonResponse);
		assertErrResponseCode(NO_MONITORS);
	}

	@Test
	public void givenAPIReturnedNullWhenGetResponseInAllFormatThenReturnErrorResponse() throws Exception {
		// Arrange
		when(monitorBriefService.getMonitorBriefDetails(user, MONITOR_ID, enterprisePref, FQ))
				.thenReturn(null);
		// Act
		jsonResponse = controller.getResponseInAllFormat(request, response, MONITOR_ID_STRING, SECTIONS, FQ,
				true, HTML_FRAG, "resultsCSV_D_M", true, true);
		// Assert
		assertNull(jsonResponse);
		assertErrResponseCode(-1);
	}

	@Test
	public void givenFQWhenGetResponseInAllFormatThenGet() throws Exception {
		// Arrange
		arrangeGetResponseInAllFormatCommon(FQ);
		// Act
		jsonResponse = controller.getResponseInAllFormat(request, response, MONITOR_ID_STRING, SECTIONS, FQ,
				true, HTML_FRAG, null, true, true);
		// Assert
		assertEquals(entityDataResponse, jsonResponse);
		assertNull(entityDataWrapper.getMetaData());
		verify(request).setAttribute("target", "monitorbriefwithfilteringsupport");
		verify(request).setAttribute("target", "monitorbriefwithpagingandfilteringsupport");
	}

	@Test
	public void givenIsPaginationWhenGetResponseInAllFormatThenGet() throws Exception {
		// Arrange
		arrangeGetResponseInAllFormatCommon(null);
		// Act
		jsonResponse = controller.getResponseInAllFormat(request, response, MONITOR_ID_STRING, SECTIONS, null,
				true, HTML_FRAG, null, true, true);
		// Assert
		assertEquals(entityDataResponse, jsonResponse);
		assertNull(entityDataWrapper.getMetaData());
		verify(request).setAttribute("target", "monitorbriefwithpagingsupport");
	}

	@Test
	public void givenSectionsNullWhenGetResponseInAllFormatThenGet() throws Exception {
		// Arrange
		monitorControllerHelper = mock(MonitorControllerHelper.class);
		whenNew(MonitorControllerHelper.class)
				.withArguments(-1)
				.thenReturn(monitorControllerHelper);
		when(monitorControllerHelper.getEnterprisePref(requestParsingService, null))
				.thenReturn(enterprisePref);
		when(monitorControllerHelper
				.validateSectionsAndUpdateRequest(sectionsMap, request, requestParsingService,
						null))
				.thenReturn(keySet);
		entityDataWrapper = new EntityDataWrapper();
		entityDataResponse.setResult(entityDataWrapper);
		when(request.getMethod()).thenReturn("GET");
		documentSet = new DocumentSet();
		final MonitorBriefDetail monitorBriefDetail = new MonitorBriefDetail();
		monitorBriefDetail.setWebResults(documentSet);
		when(monitorAPIResponse.getMonitorBriefDetail()).thenReturn(monitorBriefDetail);
		when(monitorBriefService.getMonitorBriefDetails(user, MONITOR_ID, enterprisePref, null))
				.thenReturn(monitorAPIResponse);
		final List<String> wrIdList = singletonList(TEST_WR_ID);
		when(storageService.getListOfIdsfromDocumentSet(documentSet)).thenReturn(wrIdList);
		final GetBulk getBulk = new GetBulk();
		docFieldFromStorageServiceMap =
				singletonMap("test-key", getBulk);
		when(storageService.getDocFieldsFromStorageService(anyList(), eq(true), eq(true)))
				.thenReturn(docFieldFromStorageServiceMap);
		when(requestParsingService.getSerializedMetadata(null, null, null, null, HTML_FRAG,
				null, true, true, false))
				.thenReturn(META_DATA);
		// Act
		jsonResponse = controller.getResponseInAllFormat(request, response, MONITOR_ID_STRING, null, null,
				true, HTML_FRAG, null, true, true);
		// Assert
		assertEquals(entityDataResponse, jsonResponse);
		assertNull(entityDataWrapper.getMetaData());
		verify(request).setAttribute("target", "monitorbrief");
		verify(request).setAttribute("metaData", META_DATA);
	}

	@Test
	public void givenResultsCSVHasDataAndMetadataWhenGetResponseInAllFormatThenReturnErrorResponse() throws Exception {
		// Arrange
		arrangeGetResponseInAllFormatCommon(FQ);
		when(user.getOwnedBy()).thenReturn(TEST_ENTERPRISE_ID);
		when(responseDecoratorService.excludeTweetInfo(TEST_ENTERPRISE_ID)).thenReturn(true);
		final EntityData entityData = new EntityData();
		final Content content = new Content();
		final Tweet tweet = new Tweet();
		final List<Tweet> tweetList = singletonList(tweet);
		content.setTweets(tweetList);
		entityData.setFt(content);
		entityDataWrapper.setData(entityData);
		// Act
		jsonResponse = controller.getResponseInAllFormat(request, response, MONITOR_ID_STRING, SECTIONS, FQ,
				true, HTML_FRAG, "resultsCSV_D_M", true, true);
		// Assert
		verify(responseDecoratorService).makeTweetsFieldsNullable(tweetList);
	}

	@Test
	public void givenIncorrectIdWhenGetListEmailsThenGetErrorResponse() {
		// Arrange
		entityDataResponse = new EntityDataResponse();
		final EntityDataWrapper data = new EntityDataWrapper();
		entityDataResponse.setResult(data);
		when(responseDecoratorService.getEntityDataResponse(any(MonitorEmailAPIResponse.class), anyString()))
				.thenReturn(entityDataResponse);
		// Act
		jsonResponse = controller.getListEmails(request, response, INCORRECT_ID,
				START_DATE, ENDS_DATE, true);
		// Assert
		assertNull(jsonResponse);
		assertErrResponseCode(ILLEGAL_ARGUMENT);
	}

	@Test
	public void givenValidIdWhenGetListEmailsThenGetResponse() throws Exception {
		// Arrange
		entityDataResponse = new EntityDataResponse();
		final EntityDataWrapper data = new EntityDataWrapper();
		entityDataResponse.setResult(data);
		when(responseDecoratorService.getEntityDataResponse(any(MonitorEmailAPIResponse.class), anyString()))
				.thenReturn(entityDataResponse);
		when(request.getMethod()).thenReturn("GET");
		final MonitorEmailAPIResponse monitorEmailAPIResponse = mock(MonitorEmailAPIResponse.class);
		when(monitorEmailAPIResponse.getStatusCode()).thenReturn(REQUEST_SUCCESS);
		when(monitorBriefService.getMonitorEmailList(user, MONITOR_ID, START_DATE, ENDS_DATE))
				.thenReturn(monitorEmailAPIResponse);
		// Act
		jsonResponse = controller.getListEmails(request, response, MONITOR_ID_STRING,
				START_DATE, ENDS_DATE, true);
		// Assert
		assertEquals(entityDataResponse, jsonResponse);
		verify(request).setAttribute("loadview", true);
		verify(request).setAttribute("activityType", "GET");
		verify(request).setAttribute("targetId", MONITOR_ID_STRING);
		verify(request).setAttribute("target", "listEmail");
	}

	@Test
	public void givenStatusNotOkWhenGetListEmailsThenReturnErrorResponse() throws Exception {
		// Arrange
		final MonitorEmailAPIResponse monitorEmailAPIResponse = mock(MonitorEmailAPIResponse.class);
		when(monitorEmailAPIResponse.getStatusCode()).thenReturn(REQUEST_SUCCESS);
		when(monitorBriefService.getMonitorEmailList(user, MONITOR_ID, START_DATE, ENDS_DATE))
				.thenReturn(monitorEmailAPIResponse);
		when(monitorEmailAPIResponse.getStatusCode()).thenReturn(NO_ITEMS);
		// Act
		jsonResponse = controller.getListEmails(request, response, MONITOR_ID_STRING,
				START_DATE, ENDS_DATE, true);
		// Assert
		assertNull(jsonResponse);
		assertErrResponseCode(NO_ITEMS);
	}

	@Test
	public void givenAPIReturnedNullWhenGetListEmailsThenReturnErrorResponse() throws Exception {
		// Arrange
		when(monitorBriefService.getMonitorEmailList(user, MONITOR_ID, START_DATE, ENDS_DATE))
				.thenReturn(null);
		// Act
		jsonResponse = controller.getListEmails(request, response, MONITOR_ID_STRING,
				START_DATE, ENDS_DATE, true);
		// Assert
		assertNull(jsonResponse);
		assertErrResponseCode(-1);
	}

	@Test
	public void givenNullSectionWhenGetResponseInHtmlThenErrorResponse() {
		// Arrange
		when(requestParsingService.getErrorHtmlResponse(INSUFFICIENT_ARGUMENT)).thenReturn(ERROR_MESSAGE);
		// Act
		final String actual = controller.getResponseInHtml(model, request, response, null, FQ, true, HTML_FRAG,
				MONITOR_ID_STRING);
		// Assert
		verify(requestParsingService).getErrorHtmlResponse(integerArgumentCaptor.capture());
		collector.checkThat(integerArgumentCaptor.getValue(), is(INSUFFICIENT_ARGUMENT));
		final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(model).addAttribute(captor.capture(), captor.capture());
		final List<String> args = captor.getAllValues();
		collector.checkThat(args.get(0), is(ATTRIBUTE_NAME));
		collector.checkThat(args.get(1), is(ERROR_MESSAGE));
		collector.checkThat(actual, is(VIEW_STR));
	}

	@Test
	public void givenSectionNotFoundWhenGetResponseInHtmlThenErrorResponse() throws Exception {
		// Arrange
		final Exception throwException = new Exception();
		when(requestParsingService.getErrorHtmlResponse(INVALID_SECTION)).thenReturn(ERROR_MESSAGE);
		when(requestParsingService.getSectionsPageSpecMap(SECTIONS)).thenThrow(throwException);
		// Act
		final String actual = controller.getResponseInHtml(model, request, response, SECTIONS, FQ, true, HTML_FRAG,
				MONITOR_ID_STRING);
		// Assert
		final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(model).addAttribute(captor.capture(), captor.capture());
		final List<String> args = captor.getAllValues();
		collector.checkThat(args.get(0), is(ATTRIBUTE_NAME));
		collector.checkThat(args.get(1), is(ERROR_MESSAGE));
		collector.checkThat(actual, is(VIEW_STR));
	}

	@Test
	public void givenIncorrectIdWhenGetResponseInHtmlThenErrorResponse() throws Exception {
		// Arrange
		when(requestParsingService.getErrorHtmlResponse(ILLEGAL_ARGUMENT)).thenReturn(ERROR_MESSAGE);
		when(requestParsingService.getSectionsPageSpecMap(SECTIONS)).thenReturn(enterprisePref);
		// Act
		final String actual = controller.getResponseInHtml(model, request, response, SECTIONS, FQ, true, HTML_FRAG,
				INCORRECT_ID);
		// Assert
		final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(model).addAttribute(captor.capture(), captor.capture());
		final List<String> args = captor.getAllValues();
		collector.checkThat(args.get(0), is(ATTRIBUTE_NAME));
		collector.checkThat(args.get(1), is(ERROR_MESSAGE));
		collector.checkThat(actual, is(VIEW_STR));
	}

	@Test
	public void givenEmptyIntersectSetsWhenGetResponseInHtmlThenErrorResponse() throws Exception {
		// Arrange
		when(requestParsingService.intersectSets(keySet, keySet))
				.thenReturn(Collections.<SectionType>emptySet());
		when(requestParsingService.getErrorHtmlResponse(ILLEGAL_ARGUMENT)).thenReturn(ERROR_MESSAGE);
		when(requestParsingService.getSectionsPageSpecMap(SECTIONS)).thenReturn(enterprisePref);
		// Act
		String actual = controller.getResponseInHtml(model, request, response, SECTIONS, FQ, true, HTML_FRAG,
				MONITOR_ID_STRING);
		// Assert
		verify(request).setAttribute("sectionId", keySet.toString());
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(model).addAttribute(captor.capture(), captor.capture());
		List<String> args = captor.getAllValues();
		collector.checkThat(args.get(0), is(ATTRIBUTE_NAME));
		collector.checkThat(args.get(1), is(ERROR_MESSAGE));
		collector.checkThat(actual, is(VIEW_STR));
	}

	@Test
	public void givenIntersectSetsWhenGetResponseInHtmlThenGet() throws Exception {
		// Arrange
		when(requestParsingService.getSectionsPageSpecMap(SECTIONS)).thenReturn(enterprisePref);
		when(requestParsingService.getSerializedMetadata(SECTIONS, null, FQ, null,
				HTML_FRAG)).thenReturn(META_DATA);
		when(requestParsingService.getRequestScheme(request)).thenReturn("https");
		when(ftlService.getHtml(eq(VIEW_FTL), anyMap())).thenReturn(HTML_FRAG);
		when(request.getMethod()).thenReturn("GET");
		when(monitorBriefService.getMonitorBriefDetails(user, MONITOR_ID, enterprisePref, FQ))
				.thenReturn(monitorAPIResponse);
		// Act
		final String actual = controller.getResponseInHtml(model, request, response, SECTIONS, FQ,
				true, HTML_FRAG, MONITOR_ID_STRING);
		// Assert
		assertGetResponseInHTMLCommon(actual, "false", FQ);
		verify(request).setAttribute("target", "monitorbriefwithfilteringsupport");
		verify(request).setAttribute("target", "monitorbriefwithpagingandfilteringsupport");
		verify(request).setAttribute("metaData", META_DATA);
	}

	@Test
	public void givenIntersectSetsAndHTMLFragClassicFrameWhenGetResponseInHtmlThenGet() throws Exception {
		// Arrange
		when(requestParsingService.getSectionsPageSpecMap(SECTIONS)).thenReturn(enterprisePref);
		when(requestParsingService.getRequestScheme(request)).thenReturn("https");
		when(ftlService.getHtml(eq(VIEW_FTL), anyMap())).thenReturn(HTML_FRAG);
		when(request.getMethod()).thenReturn("GET");
		when(monitorBriefService.getMonitorBriefDetails(user, MONITOR_ID, enterprisePref, null))
				.thenReturn(monitorAPIResponse);
		// Act
		final String actual = controller.getResponseInHtml(model, request, response, SECTIONS, null,
				true, HTML_FRAG_CLASSIC_FRAME, MONITOR_ID_STRING);
		// Assert
		assertGetResponseInHTMLCommon(actual, "true", null);
		verify(request).setAttribute("target", "monitorbriefwithpagingsupport");
	}

	@Test
	public void givenSectionsNullWhenGetResponseInHtmlThenGet() throws Exception {
		// Arrange
		when(monitorBriefService.getMonitorBriefDetails(user, MONITOR_ID, enterprisePref, null))
				.thenReturn(monitorAPIResponse);
		when(requestParsingService.getSectionsPageSpecMap(SECTIONS_WITHOUT_IO)).thenReturn(enterprisePref);
		// Act
		controller.getResponseInHtml(model, request, response, SECTIONS_WITHOUT_IO, null,
				true, HTML_FRAG_CLASSIC_FRAME, MONITOR_ID_STRING);
		// Assert
		verify(request).setAttribute("target", "monitorbrief");
	}

	@Test
	public void givenResponseStatusNotOkWhenGetResponseInHtmlThenReturnErrorResponse() throws Exception {
		// Arrange
		when(monitorBriefService.getMonitorBriefDetails(user, MONITOR_ID, enterprisePref, FQ))
				.thenReturn(monitorAPIResponse);
		when(monitorAPIResponse.getStatusCode()).thenReturn(NO_EMAIL_SENT);
		when(requestParsingService.getErrorHtmlResponse(NO_EMAIL_SENT))
				.thenReturn(ERROR_MESSAGE);
		// Act
		final String result = controller.getResponseInHtml(model, request, response, SECTIONS, FQ,
				true, HTML_FRAG, MONITOR_ID_STRING);
		// Assert
		assertEquals(VIEW_STR, result);
		verify(model).addAttribute("errorMsg", ERROR_MESSAGE);
	}

	@Test
	public void givenCreateMonitorAPIReturnedNullWhenGetResponseInHtmlThenReturnErrorResponse() throws Exception {
		// Arrange
		when(monitorBriefService.getMonitorBriefDetails(user, MONITOR_ID, enterprisePref, FQ))
				.thenReturn(null);
		when(requestParsingService.getErrorHtmlResponse(-1))
				.thenReturn(ERROR_MESSAGE);
		// Act
		final String result = controller.getResponseInHtml(model, request, response, SECTIONS, FQ,
				true, HTML_FRAG, MONITOR_ID_STRING);
		// Assert
		assertEquals(VIEW_STR, result);
		verify(model).addAttribute("errorMsg", ERROR_MESSAGE);
	}

	@Test
	public void givenNullBodyWhenCreateThenErrorResponse() {
		// Act
		jsonResponse = controller.create(request, response, null);
		// Assert
		assertErrResponseCode(INSUFFICIENT_ARGUMENT);
	}

	@Test
	public void givenResponseStatusNotOkWhenCreateThenErrorResponse() throws Exception {
		// Arrange
		final Map<String, Object> bodyMap = arrangeValidBodyMap();
		when(monitorService.createMonitor(MONITOR_ID, "name", (List<String>) bodyMap.get(ENTITY_STRING),
				(List<String>) bodyMap.get("fq"), enterprisePref))
				.thenReturn(monitorAPIResponse);
		when(monitorAPIResponse.getStatusCode()).thenReturn(NO_ITEMS);
		// Act
		jsonResponse = controller.create(request, response, JSONUtility.serialize(bodyMap));
		// Assert
		assertErrResponseCode(NO_ITEMS);
	}

	@Test
	public void givenMonitorNameNullWhenCreateThenErrorResponse() {
		// Act
		jsonResponse = controller.create(request, response, REQUEST_BODY_ENTITY);
		// Assert
		assertErrResponseCode(INSUFFICIENT_ARGUMENT);
	}

	@Test
	public void givenIncorrectBodyWhenCreateThenErrorResponse() {
		// Act
		jsonResponse = controller.create(request, response, "incorrect");
		// Assert
		assertErrResponseCode(ILLEGAL_ARGUMENT);
	}

	@Test
	public void givenNullFromCreateMonitorWhenCreateThenErrorResponse() throws Exception {
		// Arrange
		final Map<String, Object> bodyMap = arrangeValidBodyMap();
		// Act
		jsonResponse = controller.create(request, response, JSONUtility.serialize(bodyMap));
		// Assert
		assertErrResponseCode(-1);
	}

	@Test
	public void givenMonitorCreateMonitorWhenCreateThenResponse() throws Exception {
		// Arrange
		when(monitorService.createMonitor(eq(MONITOR_ID), eq("name"), anyList(), anyList(), eq(enterprisePref)))
				.thenReturn(monitorAPIResponse);
		MonitorWrapperResponse expected = new MonitorWrapperResponse();
		when(responseDecoratorService.getMonitorWrapperResponse(any(monitorAPIResponse.getClass()), anyString()))
				.thenReturn(expected);
		when(request.getMethod()).thenReturn("GET");
		final Map<String, Object> bodyMap = new HashMap<>();
		bodyMap.put("name", "name");
		bodyMap.put(ENTITY_STRING, singletonList(ENTITY_STRING));
		bodyMap.put("fq", singletonList("fq"));
		final String reqBody = JSONUtility.serialize(bodyMap);
		// Act
		jsonResponse = controller.create(request, response, reqBody);
		// Assert
		assertEquals(expected, jsonResponse);
		verify(request).setAttribute("loadview", true);
		verify(request).setAttribute("activityType", "GET");
		verify(request).setAttribute("target", "createmonitor");
		verify(request).setAttribute("metaData", reqBody);
	}

	@Test
	public void givenResponseStatusNotOkWhenRemoveThenErrorResponse() throws Exception {
		// Arrange
		when(monitorService.removeMonitor(user, MONITOR_ID))
				.thenReturn(monitorAPIResponse);
		when(monitorAPIResponse.getStatusCode()).thenReturn(NO_ITEMS);
		// Act
		jsonResponse = controller.remove(request, response, MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(NO_ITEMS);
	}

	@Test
	public void givenIncorrectWhenRemoveThenErrorResponse() {
		// Act
		jsonResponse = controller.remove(request, response, INCORRECT_ID);
		// Assert
		assertErrResponseCode(ILLEGAL_ARGUMENT);
	}

	@Test
	public void givenNullFromRemoveMonitorWhenRemoveThenErrorResponse() {
		// Act
		jsonResponse = controller.remove(request, response, MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(-1);
	}

	@Test
	public void givenValidIdWhenRemoveThenResponse() throws Exception {
		// Arrange
		when(monitorService.removeMonitor(user, MONITOR_ID)).thenReturn(monitorAPIResponse);
		final MonitorWrapperResponse expected = new MonitorWrapperResponse();
		when(responseDecoratorService.getMonitorWrapperResponse(monitorAPIResponse, "remove.monitor.succ"))
				.thenReturn(expected);
		when(request.getMethod()).thenReturn("GET");
		// Act
		jsonResponse = controller.remove(request, response, MONITOR_ID_STRING);
		// Assert
		assertEquals(expected, jsonResponse);
		verify(request).setAttribute("loadview", true);
		verify(request).setAttribute("activityType", "GET");
		verify(request).setAttribute("target", "removemonitor");
		verify(request).setAttribute("targetId", MONITOR_ID_STRING);
	}

	@Test
	public void givenRespStatusNotOkWhenQueryEntityThenErrorResponse() throws Exception {
		// Arrange
		when(monitorAPIResponse.getStatusCode()).thenReturn(NO_ITEMS);
		when(monitorService.getEntityStatus(user, MONITOR_ID, ENTITY_STRING))
				.thenReturn(monitorAPIResponse);
		// Act
		jsonResponse = controller.queryEntity(request, response, MONITOR_ID_STRING, ENTITY_STRING);
		// Assert
		assertErrResponseCode(NO_ITEMS);
	}

	@Test
	public void givenEntityStatusNullWhenQueryEntityThenErrorResponse() throws Exception {
		// Arrange
		when(monitorAPIResponse.getStatusCode()).thenReturn(REQUEST_SUCCESS);
		when(monitorAPIResponse.getEntityStatus()).thenReturn(null);
		when(monitorService.getEntityStatus(user, MONITOR_ID, ENTITY_STRING))
				.thenReturn(monitorAPIResponse);
		// Act
		jsonResponse = controller.queryEntity(request, response, MONITOR_ID_STRING, ENTITY_STRING);
		// Assert
		assertErrResponseCode(REQUEST_SUCCESS);
	}

	@Test
	public void givenIncorrectIdWhenQueryEntityThenErrorResponse() {
		// Act
		jsonResponse = controller.queryEntity(request, response, INCORRECT_ID, ENTITY_STRING);
		// Assert
		assertErrResponseCode(ILLEGAL_ARGUMENT);
	}

	@Test
	public void givenValidIdAndNullFromMonitorServiceWhenQueryEntityThenErrorResponse() {
		// Act
		jsonResponse = controller.queryEntity(request, response, MONITOR_ID_STRING, ENTITY_STRING);
		// Assert
		assertErrResponseCode(-1);
	}

	@Test
	public void givenValidIdAndMonitorFromMonitorServiceWhenQueryEntityThenResponse() throws Exception {
		// Arrange
		final EntityStatus entityStatus = new EntityStatus();
		when(monitorAPIResponse.getEntityStatus()).thenReturn(entityStatus);
		when(monitorService.getEntityStatus(user, MONITOR_ID, ENTITY_STRING)).thenReturn(monitorAPIResponse);
		final EntityResponse expected = new EntityResponse();
		when(responseDecoratorService.getMonitorEntityResponse(entityStatus)).thenReturn(expected);
		mockStatic(MonitorControllerHelper.class);
		// Act
		jsonResponse = controller.queryEntity(request, response, MONITOR_ID_STRING, ENTITY_STRING);
		// Assert
		assertEquals(expected, jsonResponse);
		verifyStatic();
		MonitorControllerHelper.getrackingRequestParams(request, MONITOR_ID_STRING,
				"{\"entity\":\"" + ENTITY_STRING + "\"}");
	}

	@Test
	public void givenRespStatusNotOkWhenGetThenErrorResponse() throws Exception {
		// Arrange
		when(monitorAPIResponse.getStatusCode()).thenReturn(NO_ITEMS);
		final MonitorConfig monitorConfig = new MonitorConfig();
		when(monitorAPIResponse.getMonitorConfig()).thenReturn(monitorConfig);
		when(monitorService.getMonitorDetails(user, MONITOR_ID))
				.thenReturn(monitorAPIResponse);
		// Act
		jsonResponse = controller.get(request, response, MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(NO_ITEMS);
	}

	@Test
	public void givenEntityStatusNullWhenGetThenErrorResponse() throws Exception {
		// Arrange
		when(monitorAPIResponse.getStatusCode()).thenReturn(REQUEST_SUCCESS);
		when(monitorAPIResponse.getEntityStatus()).thenReturn(null);
		when(monitorService.getMonitorDetails(user, MONITOR_ID))
				.thenReturn(monitorAPIResponse);
		// Act
		jsonResponse = controller.get(request, response, MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(REQUEST_SUCCESS);
	}

	@Test
	public void givenIncorrectIdWhenGetThenErrorResponse() {
		// Act
		jsonResponse = controller.get(request, response, INCORRECT_ID);
		// Assert
		assertErrResponseCode(ILLEGAL_ARGUMENT);
	}

	@Test
	public void givenValidIdAndNullFromMonitorServiceWhenGetThenErrorResponse() {
		// Act
		jsonResponse = controller.get(request, response, MONITOR_ID_STRING);
		// Assert
		assertErrResponseCode(-1);
	}

	@Test
	public void givenValidIdAndEntityFromMonitorServiceWhenGetThenResponse() throws Exception {
		// Arrange
		final MonitorConfig config = new MonitorConfig();
		when(monitorAPIResponse.getMonitorConfig()).thenReturn(config);
		when(monitorService.getMonitorDetails(any(user.getClass()), anyLong())).thenReturn(monitorAPIResponse);
		MonitorConfigResponse expected = new MonitorConfigResponse();
		when(responseDecoratorService.getMonitorConfigResponse(config)).thenReturn(expected);
		when(request.getMethod()).thenReturn("GET");
		// Act
		jsonResponse = controller.get(request, response, MONITOR_ID_STRING);
		// Assert
		assertEquals(expected, jsonResponse);
		verify(request).setAttribute("loadview", true);
		verify(request).setAttribute("activityType", "GET");
		verify(request).setAttribute("target", "monitorconfig");
		verify(request).setAttribute("targetId", "M:" + MONITOR_ID);
	}

	private void setRequiredFields() {
		setInternalState(controller, "monitorBriefService", monitorBriefService);
		setInternalState(controller, "storageService", storageService);
	}

	private void mockStaticClasses() {
		mockStatic(UserInfoThreadLocal.class);
		mockStatic(PersistenceProvider.class);
		mockStatic(TagsDbAPI.class);
		mockStatic(UserMembership.class);
		mockStatic(TagsValidator.class);
	}

	private void mockEnterprisePref() throws Exception {
		sectionsMap.put(FR_SECTION_TYPE, new SectionSpec());
		sectionsMap.put(FT_SECTION_TYPE, new SectionSpec());
		sectionsMap.put(E_SECTION_TYPE, new SectionSpec());
		sectionsMap.put(AC_SECTION_TYPE, new SectionSpec());
		keySet = sectionsMap.keySet();
		when(requestParsingService.getSectionsPageSpecMap(SECTIONS, true, true)).thenReturn(enterprisePref);
		when(enterprisePref.getSectionsMap()).thenReturn(sectionsMap);
		when(requestParsingService.getAllSectionIDs()).thenReturn(keySet);
		when(requestParsingService.intersectSets(keySet, keySet)).thenReturn(keySet);
	}

	private void arrangeGetResponseInAllFormatCommon(String fqString) throws Exception {
		monitorControllerHelper = mock(MonitorControllerHelper.class);
		whenNew(MonitorControllerHelper.class)
				.withArguments(-1)
				.thenReturn(monitorControllerHelper);
		when(monitorControllerHelper.getEnterprisePref(requestParsingService, SECTIONS))
				.thenReturn(enterprisePref);
		when(monitorControllerHelper
				.validateSectionsAndUpdateRequest(sectionsMap, request, requestParsingService,
						SECTIONS))
				.thenReturn(keySet);
		entityDataWrapper = new EntityDataWrapper();
		entityDataResponse.setResult(entityDataWrapper);
		when(request.getMethod()).thenReturn("GET");
		documentSet = new DocumentSet();
		final MonitorBriefDetail monitorBriefDetail = new MonitorBriefDetail();
		monitorBriefDetail.setWebResults(documentSet);
		when(monitorAPIResponse.getMonitorBriefDetail()).thenReturn(monitorBriefDetail);
		when(monitorBriefService.getMonitorBriefDetails(user, MONITOR_ID, enterprisePref, fqString))
				.thenReturn(monitorAPIResponse);
		final List<String> wrIdList = singletonList(TEST_WR_ID);
		when(storageService.getListOfIdsfromDocumentSet(documentSet)).thenReturn(wrIdList);
		final GetBulk getBulk = new GetBulk();
		docFieldFromStorageServiceMap =
				singletonMap("test-key", getBulk);
		when(storageService.getDocFieldsFromStorageService(anyList(), eq(true), eq(true)))
				.thenReturn(docFieldFromStorageServiceMap);
	}

	private void assertErrResponseCode(int expectedCode) {
		verify(requestParsingService).getErrorResponse(integerArgumentCaptor.capture());
		assertEquals(expectedCode, (int) integerArgumentCaptor.getValue());
	}

	private void assertGetResponseInHTMLCommon(String actual, String showHeading, String fqString)
			throws Exception {
		collector.checkThat(actual, is(VIEW_STR));
		final ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		verify(request).setAttribute(eq("sectionId"), stringCaptor.capture());
		final String sectionIdsString = stringCaptor.getValue();
		final String[] sectionIds = sectionIdsString.split(",");
		collector.checkThat(sectionIds.length, equalTo(4));
		collector.checkThat(sectionIdsString, containsString("FT"));
		collector.checkThat(sectionIdsString, containsString("FR"));
		collector.checkThat(sectionIdsString, containsString("E"));
		collector.checkThat(sectionIdsString, containsString("AC"));
		verify(responseDecoratorService).setChartDataForHtml(keySet, entityDataResponse,
				MONITOR_ID_STRING, sectionsMap, true, fqString, response);
		final ArgumentCaptor<Map<String, Object>> ftlParamsCaptor = createMapCaptor();
		verify(ftlService).getHtml(eq(VIEW_FTL), ftlParamsCaptor.capture());
		final Map<String, Object> ftlParams = ftlParamsCaptor.getValue();
		collector.checkThat(ftlParams.size(), equalTo(5));
		collector.checkThat((boolean) ftlParams.get("showFR"), equalTo(true));
		collector.checkThat((boolean) ftlParams.get("showFT"), equalTo(true));
		collector.checkThat((EntityDataResponse) ftlParams.get("obj"),
				equalTo(entityDataResponse));
		collector.checkThat((String) ftlParams.get("showheading"), equalTo(showHeading));
		collector.checkThat((String) ftlParams.get("reqScheme"), equalTo("https"));
		collector.checkThat(sectionsMap.get(VIZ), nullValue());
		verify(model).addAttribute("htmlContent", HTML_FRAG);
		verify(request).setAttribute("loadview", true);
		verify(request).setAttribute("activityType", "GET");
		verify(request).setAttribute("targetId", MONITOR_ID_STRING);
	}

	private static Map<String, Object> arrangeValidBodyMap() {
		final Map<String, Object> bodyMap = new HashMap<>(3);
		bodyMap.put("name", "name");
		bodyMap.put(ENTITY_STRING, singletonList(ENTITY_STRING));
		bodyMap.put("fq", singletonList("fq"));
		return bodyMap;
	}

	private static ArgumentCaptor<Map<String, Object>> createMapCaptor() {
		return (ArgumentCaptor<Map<String, Object>>) ArgumentCaptor.forClass((Class) Map.class);
	}
}
