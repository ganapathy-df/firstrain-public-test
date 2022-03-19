<#macro docHtml data>
	<style type="text/css">
		.fr-dc-lt {font-family:sans-serif; font-size:12px; margin-bottom:10px; border:1px solid #e5e5e5; -moz-border-radius:3px; -webkit-border-radius:3px; border-radius:3px;}
		.fr-dc-lt h3 {
		 background:#e5e5e5;
		 background:-moz-linear-gradient(top, #f6f6f6, #e5e5e5);
		 background:-webkit-gradient(linear, center top, center bottom, from(#f6f6f6), to(#e5e5e5));
		 background: -webkit-linear-gradient(top, #f6f6f6, #e5e5e5);
		 background:-o-linear-gradient(top, #f6f6f6, #e5e5e5);
		 background:-ms-linear-gradient(top, #f6f6f6, #e5e5e5);
		 background: linear-gradient(to bottom, #f6f6f6, #e5e5e5);
		 height:26px;
		 padding-left:10px;
		 padding-right:10px;
		 font-weight:600;
		 font-size:11px;
		 text-transform:uppercase;
		 color:#810501;
		 line-height:26px;
		 margin:0;
		}
		.fr-dc-lt ul {list-style:none; margin:0; padding:0 10px;}
		.fr-dc-lt-ul {font-family:sans-serif; font-size:12px; list-style:none; margin:0;}
		.fr-dc-lt-ul li {line-height:19px; padding:5px 0;}
		.fr-dc-lt-ul a {display:inline; color:#000000; font-weight:600;  text-decoration:none;}
		.fr-dc-lt-ul a:hover {text-decoration:underline;}
		.fr-dc-src, .fr-dc-dt {font-size:11px; color:#666666; margin-left:5px;}
		.fr-dc-src {color:#368ae8;}
		.fr-dc-img {float:right; padding-left:10px;}
		.fr-pb-10 {padding-bottom:10px;}
	</style>
		<#if showheading == "true">
			<div class="fr-dc-lt">
				<h3>FirstReads</h3>
		</#if>
		<#if data.fr?? && data.fr.documents??>
			<ul class="fr-dc-lt-ul">
				<#list data.fr.documents as doc>
					<li>
						<#-- if doc.image??>
							<img width="26" src="${doc.image}" class="fr-dc-img">
						</#if -->
						<a target="_blank" href="${doc.link}">${doc.title}</a>
						<span class="fr-dc-src">${doc.source.name}</span> <span class="fr-dc-dt">${doc.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</span><#if doc.image??><br class="cl"></#if>
					</li>	
				</#list>
			</ul>
			<div class="fr fr-pb-10"><span class="load-mr">Load More</span></div>
			<div class="cl"></div>
		<#else>
			<ul class="fr-dc-lt-ul"><li>There are no recent web results to show.</li></ul>
		</#if>
	</div>
	<#if showheading == "true">
		</div>
	</#if>
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
	<#if showheading == "true">
		<style type="text/css">
			.fr-web {font-family:sans-serif; font-size:12px; margin-bottom:10px; border:1px solid #e5e5e5; -moz-border-radius:3px; -webkit-border-radius:3px; border-radius:3px;}
			.fr-web h3 {
			 background:#e5e5e5;
			 background:-moz-linear-gradient(top, #f6f6f6, #e5e5e5);
			 background:-webkit-gradient(linear, center top, center bottom, from(#f6f6f6), to(#e5e5e5));
			 background: -webkit-linear-gradient(top, #f6f6f6, #e5e5e5);
			 background:-o-linear-gradient(top, #f6f6f6, #e5e5e5);
			 background:-ms-linear-gradient(top, #f6f6f6, #e5e5e5);
			 background: linear-gradient(to bottom, #f6f6f6, #e5e5e5);
			 height:26px;
			 padding-left:10px;
			 padding-right:10px;
			 font-weight:600;
			 font-size:11px;
			 text-transform:uppercase;
			 color:#810501;
			 line-height:26px;
			 margin:0;
			}
		.fr-web-in {padding:15px;}
		</style>
		<div class="fr-web">
			<h3>Web Volume Chart with Annotated Events</h3>
			<div class="fr-web-in">
	</#if>
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