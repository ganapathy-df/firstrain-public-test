<#ftl>
<#include "customutilstheme.ftl"/>

<#if obj.result.data??>
<#assign data = obj.result.data />
<a class="jq-cs-cls fr-cls-btn"></a> 
<div class="jq-cs-slim-scr fr-cross-scroll">
	<div>
		<div class="fr-cross-srch jq-p-section" subsection="model.window">
			<div class="fr-cross-srch-tt">${name1?html} <span class="fr-cross-srch-tt-grey">FOCUSED ON</span> ${name2?html}</div>
			<#if data.cs?? && data.cs.documentBuckets[q1]??>
				<@csDocHtmltheme docs=data.cs.documentBuckets[q1] docCount=5 />
			<#else>
				<div class="dc-no-rslt">There are no recent web results to show.</div>
			</#if>
		</div>
		<div class="fr-cross-srch-web">
			<div class="fr-cross-srch-tt">Recent Web Intelligence on ${name2?html}</div>
			<#if data.cs?? && data.cs.documentBuckets[q2]??>
				<@csDocHtmltheme docs=data.cs.documentBuckets[q2] />
			<#else>
				<div class="dc-no-rslt">There are no recent web results to show.</div>
			</#if>
		</div>
	</div>
</div>
</#if>