<#ftl>
<#include "customutils.ftl"/>
<#if obj.result.data??>
	<#if showFR??>
		<div id="fr">
			<@docHtml data=obj.result.data/>
		</div>
	</#if>
	<#if showFT??>
		<div id="ft">
			<@tweetHtml data=obj.result.data />
		</div>
	</#if>
	<#if obj.result.htmlFrag??>
		<#if obj.result.htmlFrag.tt??>
			<div id="tt" class="ch-bdr">
				${obj.result.htmlFrag.tt}
			</div>
		</#if>
		<#if obj.result.htmlFrag.bi??>
			<div id="bi" class="ch-bdr">
				${obj.result.htmlFrag.bi}
			</div>
		</#if>
		<#if obj.result.htmlFrag.md??>
			<div id="md" class="ch-bdr">
				${obj.result.htmlFrag.md}
			</div>
		</#if>
		<#if obj.result.htmlFrag.twt??>
			<div id="twt" class="ch-bdr">
				${obj.result.htmlFrag.twt}
			</div>
		</#if>
		<#if obj.result.htmlFrag.gl??>
			<div id="gl" class="ch-bdr">
				${obj.result.htmlFrag.gl}
			</div>
		</#if>
		<#if obj.result.htmlFrag.rl??>
			<div id="rl" class="ch-bdr">
				${obj.result.htmlFrag.rl}
			</div>
		</#if>
	</#if>	
</#if>