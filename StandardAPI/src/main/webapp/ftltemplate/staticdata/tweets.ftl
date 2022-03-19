<#ftl>
<#include "customutils.ftl"/>
<#if obj.result.data??>
	<div id="ft">
		<@tweetHtml data=obj.result.data />
	</div>
</#if>