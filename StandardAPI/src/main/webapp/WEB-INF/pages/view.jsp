<%@ page language="java" session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty param['debug']}">
	<c:set var="minVer" value=".min" />
</c:if>

<c:set var="cssName" value="fr" />
<c:if test="${not empty cssfilename}">
	<c:set var="cssName" value="${cssfilename}" />
</c:if>

<c:choose> 
  <c:when test="${empty param['layout']}">
  	 <%@ include file="jsenv.jsp" %>
  	 <script type="text/javascript" src="${jsURL}/js/${appName}/${version}/include-jquery${minVer}.js"></script>
  	 <link rel="stylesheet" type="text/css" href="${imgCssURL}/css/${appName}/${version}/${cssName}${minVer}.css" />
  	 ${htmlContent}
	${errorMsg}
  </c:when>
  <c:otherwise>
  	<!DOCTYPE html>
	<html>
	<head>
		<%@ include file="jsenv.jsp" %>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<link rel="stylesheet" type="text/css" href="${imgCssURL}/css/${appName}/${version}/${cssName}${minVer}.css" />
	  	<link rel="stylesheet" type="text/css" href="${imgCssURL}/css/${appName}/${version}/mcustomscroll${minVer}.css" />
	  	<link rel="stylesheet" type="text/css" href="${imgCssURL}/css/${appName}/${version}/fr-layout${minVer}.css" />
	  	<script type="text/javascript" src="${jsURL}/js/${appName}/${version}/fr-ly-include-jquery${minVer}.js"></script> 
	</head>
	<body>
		${htmlContent}
		${errorMsg}
	</body>
	</html>
  </c:otherwise>
</c:choose>