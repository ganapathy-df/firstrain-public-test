<#macro docHtmltheme data>
	<ul class="dc-lt">
		<#if data.fr?? && data.fr.documents??>
			<#list data.fr.documents as doc>
				<li>
					<a class="dc-tt" target="_blank" href="${doc.link}">${doc.title}</a>
					<span class="dc-src">${doc.source.name}</span> <span class="dc-dt">${doc.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span><#if doc.image??><br class="cl"></#if>
				</li>	
			</#list>
		<#else>
			<li class="dc-no-rslt">There are no recent web results to show.</li>
		</#if>
	</ul>
</#macro>

<#macro tweetHtmltheme data>
	<#if data.ft?? && data.ft.tweets??>
		<#list data.ft.tweets as tweet>
			<div class="tw-wrp">
				<div class="tw-cnt">
					<img width="26" src="${tweet.authorAvatar}" class="fr-tw-img">
					<span class="tw-dt">${tweet.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span>
					<a class="tw-tt" target="_blank" href="${reqScheme}://twitter.com/${tweet.authorName}/status/${getNumericId(tweet.tweetId)}">${tweet.tweetHtml}</a>
					<br class="cl">
				</div>
			</div>
		</#list>	
	<#else>
		<div class="tw-wrp">
			<div class="tw-cnt dc-no-rslt">There are no recent tweets to show.</div>
		</div>
	</#if>
</#macro>

<#macro webVolumeChart index width=900 height=115 searchId="" searchToken="">
	<div class="vol-chrt">
		<div class="chart" id="chart-c-${index}">
			<div class="spnr" style="line-height: ${height}px; width: ${width?number+5}px;">
				<img class="spinner" src="${imgCssURL}/images/spacer.gif">
				Loading
			</div>
		</div>
	</div>
	<div class="lgnd">
		<h6>LEGEND</h6>
		<ul>
			<li><img src="${imgCssURL}/images/spacer.gif" id="daily"> Daily Closing Price</li>
			<li><img src="${imgCssURL}/images/spacer.gif" id="sec"> SEC Filing</li>
			<li><img src="${imgCssURL}/images/spacer.gif" id="price"> Price Change</li>
			<li><img src="${imgCssURL}/images/spacer.gif" id="news"> News Event</li>
			<li><img src="${imgCssURL}/images/spacer.gif" id="management"> Management Change</li>
	    </ul>
	</div>
	<div class="cl"></div>
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