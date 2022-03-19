<#ftl>
<#include "customutilstheme.ftl"/>

<#if obj.result.data??>
<#assign data = obj.result.data />
<div id="jq-fr-container" class="fr jq-p-viewid jq-p-p-section" str1="${str1?html}" sectionId="${sectionId?html}" viewid="${viewId}" metadata="${metadata?html}">
	<div class="fr-ov-ly jq-ov-ly"></div>
	<div class="fr-ov-mdl jq-p-p-section" sectionId="${sectionId?html}" section="cross.section" id="crossSearch"></div>
	<div id="jq-hid-val" entityName="${obj.result.name}" entityId="${searchToken}" <#if fq??>fq="${fq?html}"</#if>></div>
	<#if obj.result.htmlFrag?? && obj.result.htmlFrag.wv??>
		<div class="fr-dc-chart jq-p-section" subsection="web.volume.chart">
			<div class="fr-spnr1 jq-wvc-spnr">
				<img class="fr-spinner" src="${imgCssURL}/images/spacer.gif"> Loading
			</div>
			${obj.result.htmlFrag.wv}
		</div>
	</#if>
	<#if obj.result.htmlFrag?? && obj.result.htmlFrag.analyticsRibbon??>
		<div class="fr-ribbon jq-p-section" subsection="analytics">
			<div class="jq-ribbon fr-spnr">
				<img class="fr-spinner" src="${imgCssURL}/images/spacer.gif"> Loading
			</div>
			<#if obj.result.htmlFrag.analyticsRibbon??>
				<div id="analyticsRibbon">
					${obj.result.htmlFrag.analyticsRibbon}
				</div>
			</#if>
			<div class="fr-cl"></div>
		</div>
	</#if> 
	<div class="fr-lft">
		<div class="fr-lft-i">
			<#if showFR??>
				<div class="fr-dc jq-p-section" subsection="web.results">
					<div class="fr-dc-hd"> <a class="jq_clicktrack fr-dc-mr jq-hlp-tt" act="help" item="notification" divId="jqi-hlp-News"></a> Latest News
						<div class="fr-tltp-help" id="jqi-hlp-News">
							<div class="fr-tltp-help-close"><a class="jq-hlp-c-tt" divId="jqi-hlp-News"></a>
								<div class="fr-cl"></div>
							</div>
							<div class="fr-tltp-help-c">
								<div class="fr-tltp-help-i"><strong>Latest News</strong> shows the most significant and relevant recent news stories about the company from across the global web.
								</div>
							</div>
						</div>
					</div>
					<div class="fr-dc-in">
						<div id="fr" class="jq-slim-scr mCSB_hgt">
							<@docHtmltheme data=data />
						</div>
						<#if data.fr?? && data.fr.documents??>
							<#if showmore?string == "true">
								<#assign start =  data.fr.itemOffset + data.fr.itemCount>
								<#if start lt fr_paginationcount && start lt data.fr.totalItemCount>
									<div class="jq-ldmr jq-ldmr-fr fr-floatr fr-load-mr-inr" act="load.more.click" item="load.more" type="fr">
										<span class="fr-load-mr jq-a-ld-mr" url="../loadMore?q=${obj.result.id}&sectiontype=FR&start=${start}&count=${data.fr.itemCount}&layout=classicBrief<#if fq??>&fq=${fq?html}</#if><#if data.scopeDirective??>&scopeDirective=${data.scopeDirective}</#if>">Load More</span>
										<span class="fr-spnr jq-fr-ldng fr-hide"><img src="${imgCssURL}/images/spacer.gif" class='fr-spinner'> Loading</span>
									</div>
									<div class="fr-cl"></div>
								</#if>
							</#if>
						</#if>
					</div>
				</div>
			</#if>
			<#if showE??>
				<div class="fr-dc fr-dc-event jq-p-section" subsection="event.timeline">
					<div class="fr-dc-hd"> <a class="jq_clicktrack fr-dc-mr jq-hlp-tt" act="help" item="notification" divId="jqi-hlp-Event"></a> 
						<div class="fr-tltp-help" id="jqi-hlp-Event">
							<div class="fr-tltp-help-close"><a class="jq-hlp-c-tt" divId="jqi-hlp-Event"></a>
								<div class="fr-cl"></div>
							</div>
							<div class="fr-tltp-help-c">
								<div class="fr-tltp-help-i"><strong>Event Timeline</strong> provides a timeline view of up to 20 of the most significant developments and events affecting the company. 
								</div>
							</div>
						</div>  
						Event Timeline 
					</div>
					<div class="fr-dc-in">					
						<@eventsTimelineHtmltheme data=data />
					</div>
				</div>
			</#if>
			<#if showAC??>
				<div class="fr-dc jq-p-section" subsection="analyst.comments">
					<div class="fr-dc-hd"> <a class="jq_clicktrack fr-dc-mr jq-hlp-tt" act="help" item="notification" divId="jqi-hlp-Analyst"></a> 
						<div class="fr-tltp-help" id="jqi-hlp-Analyst">
							<div class="fr-tltp-help-close"><a class="jq-hlp-c-tt" divId="jqi-hlp-Analyst"></a>
								<div class="fr-cl"></div>
							</div>
							<div class="fr-tltp-help-c">
								<div class="fr-tltp-help-i">
									<strong>Analyst Commentary</strong> provides ratings and commentary about the company from financial and industry analysts across the global web.
								</div>
							</div>
						</div> Analyst Commentary
					</div>
					<div class="fr-dc-in">
						<div class="jq-slim-scr mCSB_hgt1">
							<@analystCommentsHtmltheme data=data />
						</div>
						<#if data.ac?? && data.ac.documents??>
							<#if showmore?string == "true">
								<#assign start =  data.ac.itemOffset + data.ac.itemCount>
								<#if start lt ac_paginationcount && start lt data.ac.totalItemCount>
									<div class="jq-ldmr jq-ldmr-ac fr-floatr fr-load-mr-inr" act="load.more.click" item="load.more" type="ac">
										<span class="fr-load-mr jq-a-ld-mr" url="../loadMore?q=${obj.result.id}&sectiontype=AC&start=${start}&count=${data.ac.itemCount}&layout=classicBrief<#if fq??>&fq=${fq?html}</#if>">Load More</span>
										<span class="fr-spnr jq-ac-ldng fr-hide"><img src="${imgCssURL}/images/spacer.gif" class='fr-spinner'> Loading</span>
									</div>
									<div class="fr-cl"></div>
								</#if>
							</#if>
						</#if>
					</div>
				</div>
			</#if>
		</div>
	</div>
	<div class="fr-rgt">
		<div class="fr-rgt-i">
			<#if showFT??>
				<div class="fr-dc fr-tw jq-p-section" subsection="first.tweets">
					<div class="fr-dc-hd"> <a class="jq_clicktrack fr-dc-mr jq-hlp-tt" act="help" item="notification" divId="jqi-hlp-Tweets"></a> 
						<div class="fr-tltp-help" id="jqi-hlp-Tweets">
							<div class="fr-tltp-help-close"><a class="jq-hlp-c-tt" divId="jqi-hlp-Tweets"></a>
								<div class="fr-cl"></div>
							</div>
							<div class="fr-tltp-help-c">
								<div class="fr-tltp-help-i">
									<strong>Top Business Tweets</strong> shows the most relevant, recent, business-focused tweets about the company.
								</div>
							</div>
						</div>Top Business Tweets
					</div>
					<div id="ft" class="jq-slim-scr mCSB_hgt3">
						<@tweetHtmltheme data=data />
					</div>
					<#if data.ft?? && data.ft.tweets??>
						<div class="fr-tw-lt fr-tw-bdr fr-pd-10"> 
							<#if showmore?string == "true">
								<#assign start =  data.ft.itemOffset + data.ft.itemCount>
								<#if start lt ft_paginationcount && start lt data.ft.totalItemCount> 	
									<div class="jq-ldmr jq-ldmr-ft fr-floatr fr-load-mr-inr" act="load.more.click" item="load.more" type="ft">
										<span class="fr-load-mr jq-a-ld-mr" url="../loadMore?q=${obj.result.id}&sectiontype=FT&start=${start}&count=${data.ft.itemCount}&layout=classicBrief<#if fq??>&fq=${fq?html}</#if>">Load More</span>
										<span class="fr-spnr jq-ft-ldng fr-hide"><img src="${imgCssURL}/images/spacer.gif" class='fr-spinner'> Loading</span>
									</div>
									<div class="fr-cl"></div>
								</#if>	
							</#if>
						</div>
						<div class="fr-tw-lt fr-pd-10"> <span class="fr-floatr"><span class="fr-tw-tm">from</span><img src="${imgCssURL}/images/spacer.gif" class="tw-icon"></span>
							<div class="fr-cl"></div>
						</div>
					</#if>
				</div>
			</#if>
			<#if showTE??>
				<div class="fr-dc fr-dc-mgmt jq-p-section" subsection="management.changes">
					<div class="fr-dc-hd"> <a class="jq_clicktrack fr-dc-mr jq-hlp-tt" act="help" item="notification" divId="jqi-hlp-Management"></a> 
						<div class="fr-tltp-help" id="jqi-hlp-Management">
							<div class="fr-tltp-help-close"><a class="jq-hlp-c-tt" divId="jqi-hlp-Management"></a>
								<div class="fr-cl"></div>
							</div>
							<div class="fr-tltp-help-c">
								<div class="fr-tltp-help-i">
									<strong>Management Changes</strong> shows movement and job changes for a company's key management personnel.  
								</div>
							</div>
						</div> Management Changes
					</div>
					<div class="fr-dc-in">
						<#if data.te?? && data.te.events?? && obj.result.htmlFrag.mgmtTurnoverChart??>
							<div class="fr-spnr1 jq-wvc-spnr">
								<img class="fr-spinner" src="${imgCssURL}/images/spacer.gif">
								Loading
							</div>
							${obj.result.htmlFrag.mgmtTurnoverChart}
						</#if>
						<div class="fr-cl"></div>
						<div class="jq-slim-scr mCSB_hgt2">
							<@turnoverEventsHtmltheme data=data />
						</div>
						<#if data.te?? && data.te.events??>
							<#if showmore?string == "true">
								<#assign start =  data.te.itemOffset + data.te.itemCount>
								<#if start lt te_paginationcount && start lt data.te.totalItemCount> 
									<div class="jq-ldmr jq-ldmr-te fr-floatr fr-load-mr-inr" act="load.more.click" item="load.more"  type="te">
										<span class="fr-load-mr jq-a-ld-mr" url="../loadMore?q=${obj.result.id}&sectiontype=TE&start=${start}&count=${data.te.itemCount}&layout=classicBrief<#if fq??>&fq=${fq?html}</#if>">Load More</span>
										<span class="fr-spnr jq-te-ldng fr-hide"><img src="${imgCssURL}/images/spacer.gif" class='fr-spinner'> Loading</span>
									</div>
									<div class="fr-cl"></div>
								</#if>	
							</#if>
						</#if>
					</div>
				</div>
			</#if>
		</div>
	</div>
	<div class="fr-cl"></div>
</div>
</#if>