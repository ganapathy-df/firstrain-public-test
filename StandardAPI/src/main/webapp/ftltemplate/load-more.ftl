<#ftl>
<#include "customutils.ftl"/>
<#if obj.result.data??>
	<#assign data =  obj.result.data>
	<#if data.fr?? && data.fr.documents??>
		<#list data.fr.documents as doc>
			<div class="fr-dc-lt <#if doc_index %2 != 0 >even</#if>">
				<span class="${contentTypeCSS(doc.contentType)}" title="${doc.contentType}"></span>
					<div class="fr-dc-inner">
						<a target="_blank" href="${doc.link}">${doc.title}</a>
						<span class="fr-dc-src">${doc.source.name}</span> <span class="fr-dc-dt">${doc.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span><#if doc.image??><br class="cl"></#if>
						<#if doc.primaryDUNSMatchStr??>
							<div class="fr">
								<span style="color:#7d7d7d;">${doc.primaryDUNSMatchStr}<#if doc.additionalMatchQualifierStr??>${doc.additionalMatchQualifierStr}</#if></span>
							</div>
						</#if>
					</div>	
			</div>
		</#list>
		<#assign start =  data.fr.itemOffset + data.fr.itemCount>
		<#assign totalItemCount =  data.fr.totalItemCount>
		<#if totalItemCount gt fr_paginationcount>
			<#assign totalItemCount = fr_paginationcount>
		</#if>
		<#if start lt totalItemCount>
			<span id="ld-mr-url-fr" class="fr-hide" url="../loadMore?q=${token?html}&sectiontype=FR&start=${start}&count=${data.fr.itemCount}<#if fq??>&fq=${fq?html}</#if><#if data.scopeDirective??>&scopeDirective=${data.scopeDirective}</#if>"></span>
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
				<span id="ld-mr-url-te" class="fr-hide" url="../loadMore?q=${token?html}&sectiontype=TE&start=${start}&count=${data.te.itemCount}<#if fq??>&fq=${fq?html}</#if>"></span>
			</#if>
		</#if>
	</#if>
	<#if E??>
		<#if data.bucketedEvents?? && data.bucketedEvents.eventBuckets??>
			<#list data.bucketedEvents.eventBuckets?keys as key>
				<#if lasteventdate?? && key != lasteventdate>
					<div class="fr-dc-dtbkt-hd">${key}</div>
				</#if>					
		   		<#list data.bucketedEvents.eventBuckets[key] as event>
					<div class="fr-evnt-lt"> 
						<span class="${eventTypeCSS(event.eventType)}" title="${contentTypeIconTooltip(event.eventType)}"></span>
						<div class="fr-dc-inner">
							<#if event.link??>
								<a itemid="${event.id}" act="title.click" target="_blank" href="${event.link}" class="jq_clicktrack">${event.title}</a>
							<#else>
								${event.title}
							</#if>
							<span class="fr-dc-dt">${event.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span>
						</div>
					</div>
		   		</#list>
		   		<#assign lastdate = key>
			</#list>
			<#assign start =  data.bucketedEvents.itemOffset + data.bucketedEvents.itemCount>
			<#if start lt e_paginationcount && start lt data.bucketedEvents.totalItemCount>
				<span id="ld-mr-url-e" class="fr-hide" url="../loadMore?q=${token?html}&sectiontype=E&lasteventdate=${lastdate}&start=${start}&count=${data.bucketedEvents.itemCount}<#if fq??>&fq=${fq?html}</#if>"></span>
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
						<a target="_blank" href="${reqScheme}://twitter.com/${tweet.authorName}/status/${getNumericId(tweet.tweetId)}">${tweet.tweetHtml}</a>
						<span class="fr-tw-dt">${tweet.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span>
						<div class="fr-cl"></div>
					</div>
				</div>
			</#list>
			<#assign start =  data.ft.itemOffset + data.ft.itemCount>
			<#if start lt ft_paginationcount && start lt data.ft.totalItemCount>
				<span id="ld-mr-url-ft" class="fr-hide" url="../loadMore?q=${token?html}&sectiontype=FT&start=${start}&count=${data.ft.itemCount}<#if fq??>&fq=${fq?html}</#if>"></span>
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
				<span id="ld-mr-url-ac" class="fr-hide" url="../loadMore?q=${obj.result.id}&sectiontype=AC&start=${start}&count=${data.ac.itemCount}<#if fq??>&fq=${fq?html}</#if>"></span>
			</#if>
		</#if>
	</#if>
</#if>