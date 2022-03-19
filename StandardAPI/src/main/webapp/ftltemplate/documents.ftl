<#ftl>
<#include "customutils.ftl"/>
<#if obj.result.data??>
	<div id="fr" class="fr">
		<@docHtml data=obj.result.data/>
	</div>
</#if>