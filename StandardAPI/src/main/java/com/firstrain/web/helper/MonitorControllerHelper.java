package com.firstrain.web.helper;

import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.web.pojo.EntityDataHtml;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.service.core.FreemarkerTemplateService;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

public final class MonitorControllerHelper {

    private static final Logger log = Logger.getLogger(MonitorControllerHelper.class);
    private static final String SECTION_ID = "sectionId";

    private int errorCode;
    private RequestParsingService requestParsingService;
    private ResponseDecoratorService responseDecoratorService;
    private FreemarkerTemplateService ftlService;


    public MonitorControllerHelper(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public Set<SectionType> validateSectionsAndUpdateRequest(Map<SectionType, SectionSpec> sectionsMap,
            HttpServletRequest request, RequestParsingService requestParsingService, String sections) {

        return validateSectionsAndUpdateRequest(sectionsMap, request, requestParsingService, sections, null);
    }

    public Set<SectionType> validateSectionsAndUpdateRequest(Map<SectionType, SectionSpec> sectionsMap,
            HttpServletRequest request, RequestParsingService requestParsingService, String sections,
            Map<String, Object> ftlParams) {

        Set<SectionType> keySet = null;
        if (sectionsMap != null) {
            keySet = sectionsMap.keySet();
            String sectionId = null;
            if (keySet != null) {
                sectionId = keySet.toString();
            }
            if(ftlParams != null) {
                ftlParams.put(SECTION_ID, sectionId);
            }
            request.setAttribute(SECTION_ID, sectionId);
            if (requestParsingService
                    .intersectSets(keySet, requestParsingService.getAllSectionIDs())
                    .isEmpty()) {
                errorCode = StatusCode.ILLEGAL_ARGUMENT;
                log.warn("Invalid value passed in parameter <sections> : " + sections);
                throw new RuntimeException("Invalid value passed in parameter <sections> : " + sections);
            }
        }
        return keySet == null ? Collections.<SectionType>emptySet() : keySet;
    }

    public EnterprisePref getEnterprisePref(RequestParsingService requestParsingService, String sections) {
        EnterprisePref enterprisePref = null;
        if (sections != null) {
            try {
                enterprisePref = requestParsingService.getSectionsPageSpecMap(sections);
            } catch (Exception e) {
                log.warn("Error parsing parameter <sections>: " + sections + e.getMessage());
                errorCode = StatusCode.INVALID_SECTION;
                throw new RuntimeException(e);
            }
        } else {
            log.warn("Parameter <sections> not passed in the url.");
            errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
            throw new RuntimeException("Parameter <sections> not passed in the url.");
        }
        return enterprisePref;
    }

    public void updateEntityResponseData(HttpServletRequest request, HttpServletResponse response, String monitorId,
            String fq, boolean debug, String htmlFrag, String resultsCSV, EntityDataResponse entityDataResponse,
            Map<SectionType, SectionSpec> sectionsMap, Set<SectionType> keySet)
            throws IOException, TemplateException {
        if (resultsCSV.contains("H") || resultsCSV.contains("h")) {
            Map<String, Object> ftlParams = new HashMap<String, Object>();
            ftlParams.put("obj", entityDataResponse);
            if (htmlFrag.equalsIgnoreCase("classicFrame")) {
                ftlParams.put("showheading", "true");
            } else {
                ftlParams.put("showheading", "false");
            }
            entityDataResponse.getResult().setHtmlFrag(new EntityDataHtml());
            responseDecoratorService
                    .setChartDataForHtml(keySet, entityDataResponse, monitorId, sectionsMap, debug, fq, response);
            if (keySet.contains(SectionType.FR)) {
                entityDataResponse.getResult().getHtmlFrag().setFr(ftlService.getHtml("documents.ftl", ftlParams));
            }
            if (keySet.contains(SectionType.FT)) {
                ftlParams.put("reqScheme", requestParsingService.getRequestScheme(request));
                entityDataResponse.getResult().getHtmlFrag().setFt(ftlService.getHtml("tweets.ftl", ftlParams));
            }
        }
    }

    public static void getrackingRequestParams(HttpServletRequest request, String monitorId,
            String entity) {
        request.setAttribute("loadview", true);
        request.setAttribute("activityType", request.getMethod());
        request.setAttribute("target", "queryentity");
        request.setAttribute("targetId", monitorId);
        request.setAttribute("metaData", entity);
    }

    public void setRequestParsingService(RequestParsingService requestParsingService) {
        this.requestParsingService = requestParsingService;
    }

    public void setResponseDecoratorService(ResponseDecoratorService responseDecoratorService) {
        this.responseDecoratorService = responseDecoratorService;
    }

    public void setFtlService(FreemarkerTemplateService ftlService) {
        this.ftlService = ftlService;
    }
}
