package com.firstrain.web.helper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.web.pojo.EntityDataHtml;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.service.core.FreemarkerTemplateService;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.wrapper.EntityDataWrapper;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MonitorControllerHelperTest {

    @InjectMocks
    final private MonitorControllerHelper helper = new MonitorControllerHelper(-1);

    @Mock
    private HttpServletRequest request;
    @Mock
    private RequestParsingService requestParsingService;
    @Mock
    private ResponseDecoratorService responseDecoratorService;
    @Mock
    private FreemarkerTemplateService ftlService;
    @Mock
    private HttpServletResponse response;
    @Mock
    private EntityDataResponse entityDataResponse;
    @Mock
    private EntityDataWrapper resultWrapper;
    @Mock
    private EntityDataHtml dataHTML;
    @Captor
    private ArgumentCaptor<Map<String, Object>> ftlParamsCaptor;

    @Rule
    public final ErrorCollector collector = new ErrorCollector();


    @Test
    public void givenValidateSectionsAndUpdateRequestWhenValidThenKeySetReturned() throws Exception {
        //Arrange
        final Map<SectionType, SectionSpec> sectionsMap = new LinkedHashMap<SectionType, SectionSpec>();
        final SectionSpec sectionSpec = new SectionSpec();
        sectionsMap.put(SectionType.FT, sectionSpec);
        final String sections = "test";
        final Set<SectionType> keySet = new HashSet<SectionType>();
        keySet.add(SectionType.FT);
        when(requestParsingService.getAllSectionIDs()).thenReturn(keySet);
        when(requestParsingService.intersectSets(anySet(), anySet())).thenReturn(keySet);

        //Act
        Set<SectionType> result = helper.validateSectionsAndUpdateRequest(sectionsMap,
                request, requestParsingService, sections);

        //Assert
        assertNotNull(result);
    }

    @Test
    public void givenGetEnterprisePrefWhenSectionsNotNullThenValueReturned() throws Exception {
        //Arrange
        final String sections = "test";
        final EnterprisePref enterprisePref = new EnterprisePref();
        when(requestParsingService.getSectionsPageSpecMap(sections)).thenReturn(enterprisePref);

        //Act
        EnterprisePref result = helper.getEnterprisePref(requestParsingService, sections);

        //Assert
        assertNotNull(result);
    }

    @Test
    public void givenUpdateEntityResponseDataWhenValidParameterThenUpdated() throws IOException, TemplateException {
        //Arrange
        final String monitorId = "123:12";
        final Map<SectionType, SectionSpec> sectionsMap = new LinkedHashMap<SectionType, SectionSpec>();
        final SectionSpec sectionSpec = new SectionSpec();
        sectionsMap.put(SectionType.FT, sectionSpec);
        final Set<SectionType> keySet = new HashSet<SectionType>();
        keySet.add(SectionType.FT);
        final String resultsCSV = "H";
        when(entityDataResponse.getResult()).thenReturn(resultWrapper);
        when(resultWrapper.getHtmlFrag()).thenReturn(dataHTML);

        //Act
        helper.updateEntityResponseData(request, response, monitorId, monitorId, true, monitorId, resultsCSV,
                entityDataResponse, sectionsMap, keySet);

        verify(ftlService, atLeastOnce()).getHtml(eq("tweets.ftl"), ftlParamsCaptor.capture());
        Map<String, Object> ftlParams = ftlParamsCaptor.getValue();
        collector.checkThat(3, equalTo(ftlParams.size()));
    }
}