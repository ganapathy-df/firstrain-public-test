package com.firstrain.web.controller.core;

import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.web.exception.ControllerOperationException;
import com.firstrain.web.response.MonitorInfoResponse;
import com.firstrain.web.util.UserInfoThreadLocal;
import java.util.List;

enum OperationType {
    ADD_ENTITY("addentity", "entity") {
        @Override
        MonitorInfoResponse executeOperation(MonitorController monitorController, long frmonitorId,
                List<String> list) throws Exception {
            MonitorInfoResponse response;

            EnterprisePref enterprisePref = monitorController.getRequestParsingService().getDefaultSpec();
            MonitorAPIResponse apiRes =
                    monitorController.getMonitorService()
                            .addEntities(Long.parseLong(UserInfoThreadLocal.get().getUserId()), frmonitorId, list,
                                    enterprisePref);
            if (apiRes != null && apiRes.getStatusCode() != 200) {
                throw new ControllerOperationException(
                        "Error in addEntities api , error code: " + apiRes.getStatusCode());
            } else if (apiRes != null) {
                response = monitorController.getResponseDecoratorService()
                        .getAddRemoveEntityResponse(apiRes, "add.entity");
            } else {
                throw new Exception("addEntities api returned null.");
            }

            return response;
        }
    },
    REMOVE_ENTITY("removeentity", "entity") {
        @Override
        MonitorInfoResponse executeOperation(MonitorController monitorController, long frmonitorId,
                List<String> list) throws Exception {
            MonitorInfoResponse response;

            MonitorAPIResponse apiRes =
                    monitorController.getMonitorService()
                            .removeEntities(Long.parseLong(UserInfoThreadLocal.get().getUserId()), frmonitorId,
                                    list);
            if (apiRes != null && apiRes.getStatusCode() != 200
                    && apiRes.getStatusCode() != StatusCode.NO_ITEMS_IN_MONITOR) {
                throw new ControllerOperationException(
                        "Error in removeEntities api , error code: " + apiRes.getStatusCode());
            } else if (apiRes != null) {
                response = monitorController.getResponseDecoratorService()
                        .getAddRemoveEntityResponse(apiRes, "remove.entity");
            } else {
                throw new Exception("removeEntities api returned null.");
            }

            return response;
        }
    },
    ADD_FILTER("addfilter", "fq") {
        @Override
        MonitorInfoResponse executeOperation(MonitorController monitorController, long frmonitorId,
                List<String> list) throws Exception {
            MonitorInfoResponse response;

            MonitorAPIResponse apiRes = monitorController.getMonitorService()
                    .addFilters(Long.parseLong(UserInfoThreadLocal.get().getUserId()), frmonitorId, list);
            if (apiRes != null && apiRes.getStatusCode() != 200) {
                throw new ControllerOperationException(
                        "Error in addFilters api , error code: " + apiRes.getStatusCode());
            } else if (apiRes != null) {
                response = monitorController.getResponseDecoratorService()
                        .getMonitorInfoResponse(apiRes, "add.filter");
            } else {
                throw new Exception("addFilters api returned null.");
            }

            return response;
        }
    },
    REMOVE_FILTER("removefilter", "fq") {
        @Override
        MonitorInfoResponse executeOperation(MonitorController monitorController, long frmonitorId,
                List<String> list) throws Exception {
            MonitorInfoResponse response;
            MonitorAPIResponse apiRes =
                    monitorController.getMonitorService()
                            .removeFilters(Long.parseLong(UserInfoThreadLocal.get().getUserId()), frmonitorId,
                                    list);
            if (apiRes != null && apiRes.getStatusCode() != 200) {
                throw new ControllerOperationException(
                        "Error in removeFilters api , error code: " + apiRes.getStatusCode());
            } else if (apiRes != null) {
                response = monitorController.getResponseDecoratorService()
                        .getMonitorInfoResponse(apiRes, "remove.filter");
            } else {
                throw new Exception("removeFilters api returned null.");
            }

            return response;
        }
    };

    private final String attribute;
    private final String requestParam;

    OperationType(String attribute, String requestParam) {
        this.attribute = attribute;
        this.requestParam = requestParam;
    }

    String getAttribute() {
        return attribute;
    }

    String getRequestParam() {
        return requestParam;
    }

    abstract MonitorInfoResponse executeOperation(MonitorController monitorController, long frmonitorId,
            List<String> list) throws Exception;
}
