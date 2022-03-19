package com.firstrain.frapi.service.impl;

import com.firstrain.common.db.jpa.PersistenceProvider;
import com.firstrain.common.db.jpa.Transaction;
import com.firstrain.db.api.ItemsDbAPI;
import com.firstrain.db.api.MailDocsDbAPI;
import com.firstrain.db.api.MailLogDbAPI;
import com.firstrain.db.obj.EMailScheduleLog;
import com.firstrain.db.obj.EmailSchedule;
import com.firstrain.db.obj.Items;
import com.firstrain.db.obj.MailDocs;
import com.firstrain.db.obj.MailLog;
import com.firstrain.db.obj.Template;
import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.EmailResponse;
import com.firstrain.frapi.repository.EmailServiceRepository;
import com.firstrain.frapi.repository.MonitorServiceRepository;
import com.firstrain.frapi.repository.impl.EmailServiceRepositoryImpl;
import com.firstrain.frapi.repository.impl.MonitorServiceRepositoryImpl;
import com.firstrain.frapi.service.VisualizationService;
import com.firstrain.frapi.util.ConvertUtil;
import com.firstrain.frapi.util.DefaultEnums;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.solr.client.DocEntry;
import com.firstrain.solr.client.SolrSearcher;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({
        EmailServiceImpl.class,
        EmailServiceRepositoryImpl.class,
        PersistenceProvider.class,
        MailLogDbAPI.class,
        ItemsDbAPI.class,
        MailDocsDbAPI.class
})
public class EmailServiceImplTest {

    private static final String USER_ID = "2432";
    private static final long OWNED_BY = 32L;
    private static final long FR_MONITOR_ID = 98L;
    private static final String FIRST_NAME = "user";
    private static final String USER_DOMAIN = "domain";
    private static final long MONITOR_ID_EMAIL_DL = 987L;
    private static final int SENT_STATUS = 21;
    private static final long SCHEDULE_ID = 98L;
    private static final String MAIL_SUBJECT = "subject";
    private static final long MAIL_ID = 68L;
    private static final long MAIL_USER_ID = 53L;
    private static final String MAIL_COMMENT = "comment";
    private static final long TEMPLATE_ID = 24L;
    private static final String DISPLAY_NAME = "template_display";
    private static final long GROUP_ID = 1L;
    private static final long SITE_DOC_ID = 868L;
    private static final String ENTRY_DOC_BODY = "doc_body";
    
    private static final int RESULT_SIZE = 546;
    private static final long DOC_GROUP_ID = 67L;
    private static final String ITEMS_DATA = "items_data&fq452352";
    @Spy
    private final ConvertUtil convertUtil = new ConvertUtil();
    @Spy
    private final VisualizationService visualizationService = new VisualizationServiceImpl();
    @Spy
    private final EmailServiceRepository emailServiceRepository = new EmailServiceRepositoryImpl();
    @Spy
    private final MonitorServiceRepository monitorServiceRepository = new MonitorServiceRepositoryImpl();
    @Mock
    protected ThreadPoolTaskExecutor executorService;
    @InjectMocks
    private final EmailServiceImpl emailService = new EmailServiceImpl();
    @Mock
    private Transaction transaction;
    @Mock
    private SolrSearcher searcher;
    private final ErrorCollector errorCollector;
    private final ExpectedException expectedException;
    @Rule
    public final RuleChain ruleChain = RuleChain.outerRule(errorCollector = new ErrorCollector())
            .around(expectedException = ExpectedException.none());

    @Before
    public void setUp() throws Exception {
        mockStatic(PersistenceProvider.class,
                MailLogDbAPI.class,
                ItemsDbAPI.class,
                MailDocsDbAPI.class);
        when(searcher.fetch(any(long[].class)))
                .thenReturn(Collections.singletonList(createDocEntry()));
        doReturn(searcher).when(monitorServiceRepository)
                .getSearcher();
        when(PersistenceProvider.newTxn(anyString())).thenReturn(transaction);
    }

    @Test
    public void givenGetEmailDetailsWhenMailLogIsNullThenResponse() throws Exception {
        //Arrange
        doReturn(null).when(emailServiceRepository).getEmailLogById(anyLong());
        //Act
        EmailResponse response = emailService.getEmailDetails(createUser(), MAIL_ID);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(StatusCode.ENTITY_NOT_FOUND));

    }

    @Test
    public void givenGetEmailDetailsThenThrowsException() throws Exception {
        //Arrange
        doThrow(new RuntimeException("MESSAGE"))
                .when(emailServiceRepository).getEmailLogById(anyLong());
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("MESSAGE");
        //Act
        emailService.getEmailDetails(createUser(), MAIL_ID);
    }

    @Test
    public void getEmailDetails() throws Exception {
        //Arrange
        when(ItemsDbAPI.getItemById(any(Transaction.class), anyLong()))
                .thenReturn(null).thenReturn(createItems());
        MailLog mailLog = createMailLog();
        when(MailLogDbAPI.fetchUserEmailLogById(any(Transaction.class), anyLong()))
                .thenReturn(null).thenReturn(mailLog);
        when(MailDocsDbAPI.fetchEmailDocLogByMailId(anyLong()))
                .thenReturn(null).thenReturn(Collections.singletonList(createMailDocs()));
        EmailSchedule schedule = createEmailSchedule();
        Template template = new Template();
        template.setDisplayName(DISPLAY_NAME);
        doReturn(schedule).when(emailServiceRepository).getEmailSchedule(anyLong());
        doReturn(template).when(emailServiceRepository).getTemplate(anyLong());
        //Act
        EmailResponse response = emailService.getEmailDetails(createUser(), MONITOR_ID_EMAIL_DL);
        //Assert
        errorCollector.checkThat(response.getStatusCode(), is(200));
        errorCollector.checkThat(response.getEmailName(), is(MAIL_SUBJECT));
        errorCollector.checkThat(response.getEmailTemplate(), is(DISPLAY_NAME));
    }


    private static User createUser() {
        User user = new User();
        user.setUserId(USER_ID);
        user.setOwnedBy(OWNED_BY);
        user.setStatus(DefaultEnums.UserValidationStatus.ACTIVATION_MAIL_FAILURE);
        user.setFrMonitorId(FR_MONITOR_ID);
        user.setFirstName(FIRST_NAME);
        user.setDomain(USER_DOMAIN);
        return user;
    }

    private static MailLog createMailLog() {
        MailLog mailLog = new MailLog();
        mailLog.setComment(MAIL_COMMENT);
        mailLog.setUserId(MAIL_USER_ID);
        mailLog.setMailId(MAIL_ID);
        mailLog.setMailSubject(MAIL_SUBJECT);
        mailLog.setScheduleId(SCHEDULE_ID);
        mailLog.setSentStatus(SENT_STATUS);
        return mailLog;
    }

    private static EmailSchedule createEmailSchedule() {
        EmailSchedule schedule = new EmailSchedule();
        schedule.setTemplateID(TEMPLATE_ID);
        schedule.setUserId(MAIL_USER_ID);
        schedule.setStatus(EmailSchedule.EmailScheduleEnums.NEW);
        return schedule;
    }

    private static MailDocs createMailDocs() {
        MailDocs mailDocs = new MailDocs();
        mailDocs.setGroupId(GROUP_ID);
        mailDocs.setMailId(MAIL_ID);
        mailDocs.setScheduleId(SCHEDULE_ID);
        mailDocs.setSiteDocId(SITE_DOC_ID);
        return mailDocs;
    }

    private static DocEntry createDocEntry() {
        DocEntry entry = new DocEntry();
        entry.setDocBody(ENTRY_DOC_BODY);
        entry.sitedocId = String.valueOf(SITE_DOC_ID);
        return entry;
    }

    private static Items createItems() {
        Items items = new Items();
        items.setResultSize(RESULT_SIZE);
        items.setDocGroupId(DOC_GROUP_ID);
        items.setData(ITEMS_DATA);
        return items;
    }

}
