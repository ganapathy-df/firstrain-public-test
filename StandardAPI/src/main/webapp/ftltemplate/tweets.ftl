<#ftl>
<#include "customutils.ftl"/>
<#if obj.result.data??>
	<div id="ft" class="fr">
		<@tweetHtml data=obj.result.data />
	</div>
</#if>