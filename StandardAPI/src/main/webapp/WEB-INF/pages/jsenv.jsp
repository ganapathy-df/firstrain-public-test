<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<span id="jqi-env-frUserId" style="display: none;"><c:out value="${param['frUserId']}"/></span>
<span id="jqi-env-authKey" style="display: none;"><c:out value="${param['authKey']}"/></span>
<span id="jqi-env-appname" style="display: none;"><c:out value="${appName}"/></span>
<span id="jqi-env-appbaseurl" style="display: none;"><c:out value="${appBaseUrl}"/></span>
<span id="jqi-env-version" style="display: none;"><c:out value="${version}"/></span>
<span id="jqi-env-imgcssurl" style="display: none;"><c:out value="${imgCssURL}"/></span>
<span id="jqi-env-jsurl" style="display: none;"><c:out value="${jsURL}"/></span>
<script>
	if(typeof FREnv == 'undefined') {
		FREnv = {"debug":"<c:out value="${param['debug']}"/>"};
	}
</script>