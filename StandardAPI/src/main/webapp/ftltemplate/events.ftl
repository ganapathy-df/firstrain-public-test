<#ftl>
<#include "customutils.ftl"/>
<#if obj.result.data??>
	<div id="e" class="fr">
		<@eventsTimelineFlatListing data=obj.result.data />
	</div>
</#if>