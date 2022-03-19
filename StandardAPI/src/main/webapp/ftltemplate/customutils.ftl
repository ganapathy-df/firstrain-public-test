<#macro docHtml data token="">
	<div class="fr-dc-bg<#if showheading == "true"> fr-dc</#if>">
		<#if showheading == "true">
				<div class="fr-dc-hd">FirstReads</div>
				<div class="fr-dc-in">
		</#if>
	<#if data.fr?? && data.fr.documents??>
		<div class="jq-cnt-fr">
			<#list data.fr.documents as doc>
				<div class="fr-dc-lt <#if doc_index %2 != 0 >even</#if>">
					<#-- if doc.image??>
						<img width="26" src="${doc.image}" class="fr-dc-img">
					</#if -->
					<span class="${contentTypeCSS(doc.contentType)}" title="${doc.contentType}"></span>
					<div class="fr-dc-inner">
						<a class="jq_clicktrack" itemid="${doc.id}" act="title.click" target="_blank" href="${doc.link}">${doc.title}</a>
						<span class="fr-dc-src">${doc.source.name}</span> <span class="fr-dc-dt">${doc.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span><#if doc.image??><div class="fr-cl"></div></#if>
						<#if doc.primaryDUNSMatchStr??>
							<div class="fr">
								<span style="color:#7d7d7d;">${doc.primaryDUNSMatchStr}<#if doc.additionalMatchQualifierStr??>${doc.additionalMatchQualifierStr}</#if></span>
							</div>
						</#if>
					</div>
				</div>
			</#list>
		</div>
		<#if showmore??>
			<#assign start =  data.fr.itemOffset + data.fr.itemCount>
			<#assign totalItemCount =  data.fr.totalItemCount>
			<#if totalItemCount gt fr_paginationcount>
				<#assign totalItemCount = fr_paginationcount>
			</#if>
			<#if start lt totalItemCount>
				<div class="jq-ldmr jq-ldmr-fr fr-floatr fr-load-mr-inr" act="load.more.click" item="load.more" type="fr">
					<span class="fr-load-mr jq-a-ld-mr" url="../loadMore?q=${token}&sectiontype=FR&start=${start}&count=${data.fr.itemCount}<#if fq??>&fq=${fq?html}</#if><#if data.scopeDirective??>&scopeDirective=${data.scopeDirective}</#if>">Load More</span>
					<span class="fr-spnr jq-fr-ldng fr-hide"><img src="${imgCssURL}/images/spacer.gif" class='fr-spinner'> Loading</span>
				</div>
				<div class="fr-cl"></div>
			</#if>
		</#if>
	<#else>
		<div class="fr-dc-no-rslt">There are no recent web results to show.</div>
	</#if>
		<#if showheading == "true">
				</div>
		</#if>
	</div>
</#macro>

<#macro turnoverEvents data>
	<#if data.te?? && data.te.events??>
	   	<div class="jq-cnt-te ">
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

<#macro tweetHtml data>
	<div class="fr-dc-bg<#if showheading == "true"> fr-dc fr-tw</#if>">
	<#if showheading == "true">
		<div class="fr-dc-hd">FirstTweets</div>
	</#if>
	<#if data.ft?? && data.ft.tweets??>
		<div class="jq-cnt-ft">
			<#if data.ft?? && data.ft.tweetAccelerometer?? && data.ft.tweetAccelerometer.graphNodeList?? && data.ft.tweetAccelerometer.graphNodeList?size gt 0>
				<div class="fr-tw-lt fr-twt-acl">
					<#if data.ft.tweetAccelerometer.graphNodeList?size gt 1>
						<@twoColumnAccelerometer accNodeList = data.ft.tweetAccelerometer.graphNodeList section="tweets" sectionId="${sectionId?html}" viewId="${viewId}"/>
					<#else>
						<@entityAccelerometer accNode=data.ft.tweetAccelerometer.graphNodeList[0] inline=true section="tweets" sectionId="${sectionId?html}" subSection="first.tweets" viewId="${viewId}"/>					
					</#if>
				</div>
			</#if>
			<#list data.ft.tweets as tweet>
				<div class="fr-tw-lt">
					<#if showNotable?? && tweet.groupSize gt 2>
						<div class="fr-ntbl-bsbl jq-tw-notable" act="mouseover" item="tweet.notable" itemid="${tweet.tweetId}" tweetId="${getNumericId(tweet.tweetId)}" groupId="${tweet.groupId}"></div>
					</#if>
					<div class="fr-tw-lt-in <#if showNotable??>${tweetNotable(tweet.groupSize)}</#if>">
						<img width="26" src="${tweet.authorAvatar}" class="fr-tw-img">
						<a target="_blank" href="${reqScheme}://twitter.com/${tweet.authorName}/status/${getNumericId(tweet.tweetId)}">${tweet.tweetHtml}</a>
						<span class="fr-tw-dt">${tweet.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span><div class="fr-cl"></div>
					</div>
				</div>
			</#list>
		</div>	
		<div class="fr-tw-lt <#if showheading == "true">fr-tw-bdr</#if>"> 
			<#if showmore??>
				<#assign start =  data.ft.itemOffset + data.ft.itemCount>
				<#if start lt ft_paginationcount && start lt data.ft.totalItemCount> 	
					<div class="jq-ldmr jq-ldmr-ft fr-floatr fr-load-mr-inr" act="load.more.click" item="load.more" type="ft">
						<span class="fr-load-mr jq-a-ld-mr" url="../loadMore?q=${token?html}&sectiontype=FT&start=${start}&count=${data.ft.itemCount}<#if fq??>&fq=${fq?html}</#if>">Load More</span>
						<span class="fr-spnr jq-ft-ldng fr-hide"><img src="${imgCssURL}/images/spacer.gif" class='fr-spinner'> Loading</span>
					</div>
					<div class="fr-cl"></div>
				</#if>	
			</#if>
		</div>
		<#if showheading == "true">
			<div class="fr-tw-lt fr-pd-10"> <span class="fr-floatr"><span class="fr-tw-tm">from</span><img src="${imgCssURL}/images/spacer.gif" class="tw-icon"></span>
				<div class="fr-cl"></div>
			</div>
		</#if>
	<#else>
		<div class="fr-dc-no-rslt">There are no recent tweets to show.</div>
	</#if>
	</div>
</#macro>

<#macro eventsTimeline data>
	<#if data.bucketedEvents?? && data.bucketedEvents.eventBuckets??>
		<div class="fr-dc-dtbkt jq-cnt-e">
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
		   		<#assign lastdate = key>
			</#list>
		</div>
		<#if data.bucketedEvents?? && data.bucketedEvents.eventBuckets??>
			<#if showmore??>
				<#assign start =  data.bucketedEvents.itemOffset + data.bucketedEvents.itemCount>
				<#if start lt e_paginationcount && start lt data.bucketedEvents.totalItemCount> 
					<div class="jq-ldmr jq-ldmr-e fr-floatr fr-load-mr-inr" act="load.more.click" item="load.more"  type="e">
						<span class="fr-load-mr jq-a-ld-mr" url="../loadMore?q=${token?html}&lasteventdate=${lastdate}&sectiontype=E&start=${start}&count=${data.bucketedEvents.itemCount}<#if fq??>&fq=${fq?html}</#if>">Load More</span>
						<span class="fr-spnr jq-e-ldng fr-hide"><img src="${imgCssURL}/images/spacer.gif" class='fr-spinner'> Loading</span>
					</div>
					<div class="fr-cl"></div>
				</#if>
			</#if>
		</#if>
	<#else>
		<div class="fr-dc-no-rslt">There is no event timeline to show.</div>
	</#if>
</#macro>

<#macro eventsTimelineFlatListing data>
	<#if data.e?? && data.e.events??>
		<div class="jq-cnt-e">
			<#list data.e.events as event>
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
		</div>
	<#else>
		<div class="fr-dc-no-rslt">There is no event timeline to show.</div>
	</#if>
</#macro>

<#macro analystComments data>
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

<#macro entityAccelerometer accNode inline section="" sectionId="" subSection="" viewId="">
	<div class="fr-acl-sn">
		<div class="fr-acl-img"><img class="jq-tw-s-tt" width="83" <#if !inline>src="${imgCssURL}/images/spacer.gif" _</#if>src="${imgCssURL}/images/${accNode.imageName}"></div>
		<div class="fr-acl-lbl">${accNode.label}</div>
	</div>
	<div class="fr-acl-sn-txt">
		<div class="fr-acl-sn-txtn">
			<#if accNode.smartText??>
				${accNode.smartText}
			</#if>
		</div>
	</div>
	<div class="fr-cl"></div>
</#macro>

<#macro twoColumnAccelerometer accNodeList section="" sectionId="" viewId="">
	<div class=fr-tw-wrp>
		<div class="fr-acl">
			<#list accNodeList as accNode><div class="fr-acl-in"><div class="fr-acl-itm">
				<div class="fr-acl-img"><img class="jq-tw-s-tt" width="83" src="${imgCssURL}/images/${accNode.imageName}"></div>
				<div class="fr-acl-lbl">${accNode.label}</div>
			</div></div></#list>
		</div>
	</div>
</#macro>

<#macro webVolumeChart index width=900 height=115 searchId="" searchToken="">
	<#if showheading == "true">
		<div class="fr-dc">
			<div class="fr-dc-hd">Web Volume Chart with Annotated Events</div>
			<div class="fr-dc-in">
	</#if>
		<div class="fr-chart" id="chart-c-${index}">
			<div class="fr-spnr" style="line-height: ${height}px; width: ${width?number+5}px;">
				<img class="fr-spinner" src="${imgCssURL}/images/spacer.gif"> Loading
			</div>
		</div>
		<div class="fr-web-lgnd">
			<ul>
				<li><img src="${imgCssURL}/images/spacer.gif" class="fr-web-daily"> Daily Closing Price</li>
				<li><img src="${imgCssURL}/images/spacer.gif" class="fr-web-sec"> SEC Filing</li>
				<li><img src="${imgCssURL}/images/spacer.gif" class="fr-web-price"> Price Change</li>
				<li><img src="${imgCssURL}/images/spacer.gif" class="fr-web-news"> News Event</li>
				<li><img src="${imgCssURL}/images/spacer.gif" class="fr-web-management"> Management Change</li>
		    </ul>
		</div>
		<div class="fr-cl"></div>
	<#if showheading == "true">
			</div>
		</div>
	</#if>
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