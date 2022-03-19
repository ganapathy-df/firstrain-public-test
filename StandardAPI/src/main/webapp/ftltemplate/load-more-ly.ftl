<#ftl>
<#include "customutilstheme.ftl"/>
<#if obj.result.data??>
	<#assign data =  obj.result.data>
	<#if FR??>
		<#if data.fr?? && data.fr.documents??>
			<#list data.fr.documents as doc>
				
				<div class="fr-dc-lt"> 
					<span class="${contentTypeCSS(doc.contentType)}" title="${doc.contentType}"></span>
					<div class="fr-dc-inner">
						<a target="_blank" href="${doc.link}">${doc.title}</a>
						<span class="fr-dc-src">${doc.source.name}</span> <span class="fr-dc-dt">${doc.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span>
						<div class="fr-cl"></div>
						<#if doc.primaryDUNSMatchStr??>
							<div class="fr">
								<span style="color:#7d7d7d;">${doc.primaryDUNSMatchStr}<#if doc.additionalMatchQualifierStr??>${doc.additionalMatchQualifierStr}</#if></span>
							</div>
						</#if>
					</div>
				</div>	
			</#list>
			<#assign start =  data.fr.itemOffset + data.fr.itemCount>
			<#if start lt fr_paginationcount && start lt data.fr.totalItemCount>
				<span id="ld-mr-url-fr" class="fr-hide" url="../loadMore?q=${obj.result.id}&sectiontype=FR&start=${start}&count=${data.fr.itemCount}&layout=classicBrief<#if fq??>&fq=${fq?html}</#if><#if data.scopeDirective??>&scopeDirective=${data.scopeDirective}</#if>"></span>
			</#if>
		</#if>
	</#if>
	<#if AC??>
		<#if data.ac?? && data.ac.documents??>
			<#list data.ac.documents as doc>
				<div class="fr-dc-lt">
					<span class="${contentTypeCSS(doc.contentType)}" title="${doc.contentType}"></span>
					<div class="fr-dc-inner">
						<a target="_blank" href="${doc.link}">${doc.title}</a>
						<span class="fr-dc-src">${doc.source.name}</span> <span class="fr-dc-dt">${doc.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span>
					</div>	
				</div>	
			</#list>
			<#assign start =  data.ac.itemOffset + data.ac.itemCount>
			<#if start lt ac_paginationcount && start lt data.ac.totalItemCount>
				<span id="ld-mr-url-ac" class="fr-hide" url="../loadMore?q=${obj.result.id}&sectiontype=AC&start=${start}&count=${data.ac.itemCount}&layout=classicBrief<#if fq??>&fq=${fq?html}</#if>"></span>
			</#if>
		</#if>
	</#if>
	<#if TE??>
		<#if data.te?? && data.te.events??>
			<#list data.te.events as event>
	   			<div class="fr-mgmt-lt"> 
	   				<span class="${eventTypeCSS(event.eventType)}" title="${contentTypeIconTooltip(event.eventType)}"></span>
					<div class="fr-dc-inner">
						<a href="${event.link}" target="_blank">${event.title}</a>
						<span class="fr-dc-dt">${event.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span>
					</div>
					<div class="fr-cl"></div>
				</div>
			</#list>
			<#assign start =  data.te.itemOffset + data.te.itemCount>
			<#if start lt te_paginationcount && start lt data.te.totalItemCount>
				<span id="ld-mr-url-te" class="fr-hide" url="../loadMore?q=${obj.result.id}&sectiontype=TE&start=${start}&count=${data.te.itemCount}&layout=classicBrief<#if fq??>&fq=${fq?html}</#if>"></span>
			</#if>
		</#if>
	</#if>
	<#if FT??>
		<#if data.ft?? && data.ft.tweets??>
			<#list data.ft.tweets as tweet>
				<div class="fr-tw-lt">
					<#if showNotable?? && tweet.groupSize gt 2>
						<div class="fr-ntbl-bsbl jq-tw-notable" act="mouseover" item="tweet.notable" itemid="${tweet.tweetId}" tweetId="${getNumericId(tweet.tweetId)}" groupId="${tweet.groupId}"></div>
					</#if>
					<div class="fr-tw-lt-in <#if showNotable??>${tweetNotable(tweet.groupSize)}</#if>">
						<img width="26" src="${tweet.authorAvatar}" class="fr-tw-img">
						<span class="fr-tw-dt">${tweet.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span>
						<a target="_blank" href="${reqScheme}://twitter.com/${tweet.authorName}/status/${getNumericId(tweet.tweetId)}">${tweet.tweetHtml}</a>
						<div class="fr-cl"></div>
					</div>
				</div>
			</#list>
			<#assign start =  data.ft.itemOffset + data.ft.itemCount>
			<#if start lt ft_paginationcount && start lt data.ft.totalItemCount>
				<span id="ld-mr-url-ft" class="fr-hide" url="../loadMore?q=${obj.result.id}&sectiontype=FT&start=${start}&count=${data.ft.itemCount}&layout=classicBrief<#if fq??>&fq=${fq?html}</#if>"></span>
			</#if>
		</#if>
	</#if>
</#if>