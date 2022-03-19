<#ftl>
<#include "customutils.ftl"/>
<#if obj.result.data??>
	<#assign data = obj.result.data />
	<div id="jq-fr-container" class="jq-p-viewid jq-p-p-section" str1="${str1?html}" sectionId="${sectionId?html}" viewid="${viewId}" metadata="${metadata?html}">
		<#if showFR??>
			<div id="fr" class="fr jq-p-section" subsection="web.results">
				<@docHtml data=obj.result.data token=token?html/>
			</div>
		</#if>
		<#if showFT??>
			<div id="ft" class="fr">
				<@tweetHtml data=data />
			</div>
		</#if>
		<#if obj.result.htmlFrag.wv??>
			<div id="wv" class="fr">
				<div class="fr-dc-chart-sin">
					<div class="fr-spnr1 jq-wvc-spnr">
						<img class="fr-spinner" src="${imgCssURL}/images/spacer.gif"> Loading
					</div>
					${obj.result.htmlFrag.wv}
				</div>
			</div>
		</#if>
		
		<#if showTE??>
			<div id="te" subsection="management.changes" class="fr fr-dc-mgmt jq-p-section fr-dc-bg<#if showheading == "true"> fr-dc</#if>">
				<#if showheading == "true">
					<div class="fr-dc-hd">Management Changes</div>
				</#if>
				<div class="mgmt-inr<#if showheading == "true"> fr-dc-in</#if>">
					<#if data.te?? && data.te.events?? && obj.result.htmlFrag.mgmtTurnoverChart??>
						<div class="fr-spnr1 jq-wvc-spnr">
							<img class="fr-spinner" src="${imgCssURL}/images/spacer.gif">
							Loading
						</div>
						${obj.result.htmlFrag.mgmtTurnoverChart}
					</#if>
					<div class="fr-cl"></div>
					<div class="jq-slim-scr mCSB_hgt2">
						<@turnoverEvents data=data />
					</div>
					<#if data.te?? && data.te.events??>
						<#if showmore??>
							<#assign start =  data.te.itemOffset + data.te.itemCount>
							<#if start lt te_paginationcount && start lt data.te.totalItemCount> 
								<div class="jq-ldmr jq-ldmr-te fr-floatr fr-load-mr-inr" act="load.more.click" item="load.more"  type="te">
									<span class="fr-load-mr jq-a-ld-mr" url="../loadMore?q=${token?html}&sectiontype=TE&start=${start}&count=${data.te.itemCount}<#if fq??>&fq=${fq?html}</#if>">Load More</span>
									<span class="fr-spnr jq-te-ldng fr-hide"><img src="${imgCssURL}/images/spacer.gif" class='fr-spinner'> Loading</span>
								</div>
								<div class="fr-cl"></div>
							</#if>	
						</#if>
					</#if>
				</div>
			</div>
		</#if>
		
		<#if showE??>
			<div id="e" class="fr fr-dc-bg<#if showheading == "true"> fr-dc</#if> fr-dc-event jq-p-section" subsection="event.timeline">
				<#if showheading == "true">
					<div class="fr-dc-hd">Event Timeline</div>
					<div class="fr-dc-in">
				</#if>				
					<@eventsTimeline data=data />
				<#if showheading == "true">
					</div>
				</#if>
			</div>
		</#if>
		
		<#if showAC??>
			<div id="ac" class="fr <#if showheading == "true"> fr-dc</#if> jq-p-section" subsection="analyst.comments">
				<#if showheading == "true">
					<div class="fr-dc-hd">Analyst Commentary</div>
					<div class="fr-dc-in">
				</#if>				
					<@analystComments data=data />
					<#if data.ac?? && data.ac.documents??>
						<#if showmore??>
							<#assign start =  data.ac.itemOffset + data.ac.itemCount>
							<#if start lt ac_paginationcount && start lt data.ac.totalItemCount> 
								<div class="jq-ldmr jq-ldmr-ac fr-floatr fr-load-mr-inr" act="load.more.click" item="load.more"  type="ac">
									<span class="fr-load-mr jq-a-ld-mr" url="../loadMore?q=${token?html}&sectiontype=AC&start=${start}&count=${data.ac.itemCount}<#if fq??>&fq=${fq?html}</#if>">Load More</span>
									<span class="fr-spnr jq-ac-ldng fr-hide"><img src="${imgCssURL}/images/spacer.gif" class='fr-spinner'> Loading</span>
								</div>
								<div class="fr-cl"></div>
							</#if>	
						</#if>
					</#if>
				<#if showheading == "true">
					</div>
				</#if>
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
	</div>	
</#if>