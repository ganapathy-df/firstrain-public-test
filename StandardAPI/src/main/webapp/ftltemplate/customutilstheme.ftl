<#macro docHtmltheme data>
	<div class="jq-cnt-fr">
		<#if data.fr?? && data.fr.documents??>
			<#list data.fr.documents as doc>
				<div class="fr-dc-lt"> 
					<span class="${contentTypeCSS(doc.contentType)}" title="${doc.contentType}"></span>
					<div class="fr-dc-inner">
						<a class="jq_clicktrack" itemid="${doc.id}" act="title.click" target="_blank" href="${doc.link}">${doc.title}</a>
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
		<#else>
			<div class="fr-dc-no-rslt">There are no recent web results to show.</div>
		</#if>
	</div>
</#macro>

<#macro csDocHtmltheme docs docCount=10>
	<#if docs??>
		<#list docs as doc>
			<#if doc_index < docCount>
				<div class="fr-dc-lt"> 
					<span class="${contentTypeCSS(doc.contentType)}" title="${doc.contentType}"></span>
					<div class="fr-dc-inner">
						<a class="jq_clicktrack" itemid="${doc.id}" act="title.click" target="_blank" href="${doc.link}">${doc.title}</a>
						<span class="fr-dc-src">${doc.source.name}</span> <span class="fr-dc-dt">${doc.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span>
					</div>
				</div>
			</#if>
		</#list>
	<#else>
		<div class="fr-dc-no-rslt">There are no recent web results to show.</div>
	</#if>
</#macro>

<#macro turnoverEventsHtmltheme data>
	<#if data.te?? && data.te.events??>
	   	<div class="jq-cnt-te">
		   	<#list data.te.events as event>
	   			<div class="fr-mgmt-lt"> 
	   				<span class="${eventTypeCSS(event.eventType)}" title="${contentTypeIconTooltip(event.eventType)}"></span>
					<div class="fr-dc-inner">
						<a class="jq_clicktrack" itemid="${event.id}" act="title.click" href="${event.link}" target="_blank">${event.title}</a>
						<span class="fr-dc-dt">${event.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span>
					</div>
					<div class="fr-cl"></div>
				</div>
			</#list>
		</div>
	<#else>
		<div class="fr-dc-no-rslt">There are no management changes to show.</div>
	</#if>
</#macro>

<#macro eventsTimelineHtmltheme data>
	<#if data.bucketedEvents?? && data.bucketedEvents.eventBuckets??>
		<div class="fr-dc-dtbkt">
			<#list data.bucketedEvents.eventBuckets?keys as key>
				<div class="fr-dc-dtbkt-hd">${key}</div>					
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
			</#list>
		</div>
	<#else>
		<div class="fr-dc-no-rslt">There is no event timeline to show.</div>
	</#if>
</#macro>

<#macro analystCommentsHtmltheme data>
	<div class="jq-cnt-ac">
		<#if data.ac?? && data.ac.documents??>
			<#list data.ac.documents as doc>
				<div class="fr-dc-lt"> 
					<span class="${contentTypeCSS(doc.contentType)}" title="${doc.contentType}"></span>
					<div class="fr-dc-inner">
						<a class="jq_clicktrack" itemid="${doc.id}" act="title.click" target="_blank" href="${doc.link}">${doc.title}</a>
						<span class="fr-dc-src">${doc.source.name}</span> <span class="fr-dc-dt">${doc.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span>
					</div>
				</div>	
			</#list>
		<#else>
			<div class="fr-dc-no-rslt">There is no analyst commentary to show.</div>
		</#if>
	</div>
</#macro>

<#macro tweetHtmltheme data>
	<#if data.ft?? && data.ft.tweets??>
		<div class="jq-cnt-ft">
			<#list data.ft.tweets as tweet>
				<div class="fr-tw-lt">
					<#if showNotable?? && tweet.groupSize gt 2>
						<div class="fr-ntbl-bsbl jq-tw-notable" act="mouseover" item="tweet.notable" itemid="${tweet.tweetId}" tweetId="${getNumericId(tweet.tweetId)}" groupId="${tweet.groupId}"></div>
					</#if>
					<div class="fr-tw-lt-in <#if showNotable??>${tweetNotable(tweet.groupSize)}</#if>">
						<img width="26" src="${tweet.authorAvatar}" class="fr-tw-img">
						<span class="fr-tw-dt">${tweet.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span>
						<a class="jq_clicktrack" itemid="${tweet.tweetId}" act="title.click" target="_blank" href="${reqScheme}://twitter.com/${tweet.authorName}/status/${getNumericId(tweet.tweetId)}">${tweet.tweetHtml}</a>
						<div class="fr-cl"></div>
					</div>
				</div>
			</#list>
		</div>
	<#else>
		<div class="fr-dc-in"><div class="fr-dc-no-rslt">There are no recent tweets to show.</div></div>
	</#if>
</#macro>

<#macro webVolumeChart index width=900 height=115 searchId="" searchToken="">
	<div class="fr-chart" id="chart-c-${index}">
		<div class="fr-spnr" style="line-height: ${height}px; width: ${width?number+5}px;">
			<img class="fr-spinner" src="${imgCssURL}/images/spacer.gif"> Loading
		</div>
	</div>
	<div class="fr-web-lgnd">
		<span><img src="${imgCssURL}/images/spacer.gif" class="fr-web-daily"> Daily Closing Price</span>
		<span><img src="${imgCssURL}/images/spacer.gif" class="fr-web-sec"> SEC Filing</span>
		<span><img src="${imgCssURL}/images/spacer.gif" class="fr-web-price"> Price Change</span>
		<span><img src="${imgCssURL}/images/spacer.gif" class="fr-web-news"> News Event</span>
		<span><img src="${imgCssURL}/images/spacer.gif" class="fr-web-management"> Management Change</span>
	</div>
	<div class="fr-cl"></div>
	<script>
		function webVolumeChart(domId, width, height, searchId, searchToken, companyId) {
			var obj;
			if(companyId)  {
				obj = {"n": "companyId", "v": companyId};
			} else if (searchId) {
				obj = {"n": "searchId", "v": searchId};
			} else {
				obj = {"n": "searchToken", "v": searchToken};
			}
			
			var file = "webvolume";
			includeJS(file, function() {
				var options = {
					showVolume:true,
					showPrice:true,
					showSecFiling:true,
					showPriceChange:true,
					showNewsEvents:true,
					showManagementChange:true,
					showLegend:false,
					allowHover:true,
					allowClick:true,
					callback:function(plot) {
						//frchart=plot;
					}
				};
				//width = width - 20;
				//height = height - 15;
				$("#" + domId).removeClass("chart").addClass("charth5");
				var url = "/${appName}/${version}/staticdatafiles/webvolumechart.html";
				$.frchart(domId, url, width, height, options);
			});
		}
		function includeJS(file, callback) {
			$.ajax({
				url: "${jsURL}/js/${appName}/${version}/" + file + ".js",
				cache: true,
				crossDomain: true,
				dataType: "script" //dataType: "script" works for cross domain also
			}).done(function() {
				  if(callback) {
					  callback();
				  }
			});
		}
		$(function() {
			webVolumeChart("chart-c-${index}", ${width}, ${height}, "${searchId}", "${searchToken}");
		});
	</script>   				
</#macro>

<#function getNumericId absId>
	<#if absId?is_number>
		<#return absId>
	</#if>
	<#assign arr = absId?split(":")>
	<#return arr[(arr?size - 1)]>
</#function>

<#function eventTypeCSS eventType>
	<#switch eventType>
	  	<#case "MT_HIRE">
	     	<#return "mt-hire">
	     	<#break>
	  	<#case "MT_DEPARTURE">
	     	<#return "mt-dept">
	     	<#break>
	  	<#case "MT_MOVE">
	     	<#return "mt-move">
	     	<#break>
	   	<#case "PRICE_UP">
	     	<#return "price-up">
	     	<#break>
	    <#case "PRICE_DOWN">
	     	<#return "price-down">
	     	<#break>
	     <#case "WEB_VOLUME">
	     	<#return "web-volume">
	     	<#break>
	     <#case "SEC">
	     	<#return "sec">
	     	<#break>
	  	<#default>
	     	<#return "">
	</#switch>
</#function>

<#function contentTypeIconTooltip eventType>
 <#switch eventType>
    <#case "MT_HIRE">
       <#return "HIRE">
    <#case "MT_DEPARTURE">
       <#return "DEPARTURE">
    <#case "MT_MOVE">
       <#return "INTERNAL MOVE">
    <#case "10-K">
    <#case "10-K/A">
       <#return "10-K Filings">
    <#case "10-Q">
    <#case "10-Q/A">
       <#return "10-Q Filings">
    <#case "8-K">
    <#case "8-K/A">
       <#return "8-K Filings">
    <#default>
       <#return eventType?replace("_", " ")>
 </#switch>
</#function>

<#function contentTypeCSS label>
	<#switch label>
	  	<#case "Call Transcripts">
	     	<#return "calltranscripts">
	     	<#break>
	  	<#case "Press Releases">
	     	<#return "pressrelease">
	     	<#break>
	  	<#case "Blogs">
	     	<#return "blogs">
	     	<#break>
	  	<#case "SEC Form 3,4,5">
	  	<#case "8-K Filings">
	  	<#case "10-Q Filings">
	  	<#case "10-K Filings">
	     	<#return "sec">
	     	<#break>
	   	<#case "Medical Journals">
	     	<#return "medicaljournals">
	     	<#break>
	    <#case "Industry Sources">
	     	<#return "industry">
	     	<#break>
	     <#case "News Wires">
	     	<#return "newsWire">
	     	<#break>
	     <#case "News and Web">
	     	<#return "news">
	     	<#break>
	  	<#default>
	     	<#return "news">
	</#switch>
	<#return "news">
</#function>

<#function tweetNotable groupSize>
	<#if groupSize gt 100>
		<#return "fr-ntbl100">
	</#if>
	<#if groupSize gt 50>
		<#return "fr-ntbl50">
	</#if>
	<#if groupSize gt 40>
		<#return "fr-ntbl40">
	</#if>
	<#if groupSize gt 30>
		<#return "fr-ntbl30">
	</#if>
	<#if groupSize gt 20>
		<#return "fr-ntbl20">
	</#if>
	<#if groupSize gt 10>
		<#return "fr-ntbl10">
	</#if>
	<#if groupSize gt 5>
		<#return "fr-ntbl05">
	</#if>
	<#if groupSize gt 2>
		<#return "fr-ntbl02">
	</#if>
	<#return "">
</#function>