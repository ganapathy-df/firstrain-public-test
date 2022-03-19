<%@page import="com.firstrain.web.service.core.Constant"%>
<%@page import="java.util.ResourceBundle"%>
<%@ page language="java" session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import = "org.apache.log4j.Logger" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%! ResourceBundle resource = ResourceBundle.getBundle("resource/message"); %>
<%
int errorCode = 0;
try {
	if(pageContext.getErrorData() != null) {
		errorCode = pageContext.getErrorData().getStatusCode();
	}
} catch(Exception e) {}
pageContext.setAttribute("errorCode", errorCode);
%>

<c:choose>
	<c:when test="${header['Accept'] eq 'application/json'}">
		<% response.setContentType("application/json"); %>
		<c:choose>
			<c:when test="${invalidIP}">
				{"message": "<%=resource.getString("errorcode.403")%>", "status": "ERROR", "errorCode": 403, "version": "<%=Constant.getVersion()%>"}
			</c:when>
			<c:when test="${invalidVersion}">
				{"message": "<%=resource.getString("errorcode.410")%>", "status": "ERROR", "errorCode": 410, "version": "<%=Constant.getVersion()%>"}
			</c:when>
			<c:when test="${invalidAPI}">
				{"message": "<%=resource.getString("errorcode.411")%>", "status": "ERROR", "errorCode": 411, "version": "<%=Constant.getVersion()%>"}
			</c:when>
			<c:when test="${unauthorized}">
				{"message": "<%=resource.getString("errorcode.105")%>", "status": "ERROR", "errorCode": 105, "version": "<%=Constant.getVersion()%>"}
			</c:when>
			<c:when test="${errorCode eq 500}">
				{"message": "<%=resource.getString("errorcode.500")%>", "status": "ERROR", "errorCode": 500, "version": "<%=Constant.getVersion()%>"}
			</c:when>
			<c:when test="${errorCode eq 404}">
				{"message": "<%=resource.getString("errorcode.404")%>", "status": "ERROR", "errorCode": 404, "version": "<%=Constant.getVersion()%>"}
			</c:when>
			<c:when test="${errorCode eq 400}">
				{"message": "<%=resource.getString("errorcode.400")%>", "status": "ERROR", "errorCode": 400, "version": "<%=Constant.getVersion()%>"}
			</c:when>
			<c:when test="${errorCode eq 405}">
				{"message": "<%=resource.getString("errorcode.405")%>", "status": "ERROR", "errorCode": 405, "version": "<%=Constant.getVersion()%>"}
			</c:when>
			<c:otherwise>
				{"message": "<%=resource.getString("errorcode.500")%>", "status": "ERROR", "errorCode": 500, "version": "<%=Constant.getVersion()%>"}
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<!DOCTYPE html>
		<html>
		<head>
		<style type="text/css">
		.err-msg-w {position: absolute; top: 0; bottom: 0; left:0; right:0;}
		.err-msg {position:absolute; top:50%; left:0; right:0; padding:0px; margin:0 auto; margin-top:-22px; font:bold 14px Arial, Helvetica, sans-serif; color:#666666; text-align:center; text-shadow: 0px 1px 0px rgba(255,255,255,0.5);}
		.err-msg p {font-size:14px; line-height:19px; padding:0; margin:0;}
		.err-msg p.pd {padding-bottom:5px;}
		.err-msg a {font:bold 14px Arial, Helvetica, sans-serif; color:#005596; text-decoration:underline;}
		</style>
		</head>
		<body>
			<div class="err-msg">
				<p class="pd">
					<% pageContext.setAttribute("errorCode", request.getAttribute("errorCode")); %>
					<c:choose>
						<c:when test="${invalidIP}">
							<%=resource.getString("errorcode.403")%>
						</c:when>
						<c:when test="${invalidVersion}">
							<%=resource.getString("errorcode.410")%>
						</c:when>
						<c:when test="${unauthorized}">
							<%=resource.getString("errorcode.105")%>
						</c:when>
						<c:when test="${errorCode eq 500}">
							<%=resource.getString("errorcode.500")%>
						</c:when>
						<c:when test="${errorCode eq 404}">
							<%=resource.getString("errorcode.404")%>
						</c:when>
						<c:when test="${errorCode eq 400}">
							<%=resource.getString("errorcode.400")%>
						</c:when>
						<c:when test="${errorCode eq 405}">
							<%=resource.getString("errorcode.405")%>
						</c:when>
						<c:otherwise>
							<%=resource.getString("errorcode.500")%>
						</c:otherwise>
					</c:choose>
				</p>
			</div>
			</div>
		</body>
		</html>
	</c:otherwise>
</c:choose>