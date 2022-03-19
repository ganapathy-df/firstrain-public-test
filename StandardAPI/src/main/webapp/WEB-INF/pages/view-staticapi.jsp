<%@ page language="java" session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<c:if test="${empty param['layout']}">
	<link rel="stylesheet" type="text/css" href="${imgCssURL}/css/${appName}/${version}/fr.css" />
</c:if>
<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script type="text/javascript" src="${jsURL}/js/${appName}/${version}/jquery-migrate-1.1.1.js"></script>

${htmlContent}
${errorMsg}
<script>
	function callback0md(graph, label) {
	   $(".jq-ov-ly").show();
	   $('#crossSearchTopic').show();
	};
	
	function callback0bi(graph, label) {
		 $(".jq-ov-ly").show();
		 $('#crossSearchCompany').show();
	};
	if(typeof FREnv == 'undefined') {
		FREnv = {"debug":"true"};
	} else {
		FREnv.debug = "true";
	}
</script>