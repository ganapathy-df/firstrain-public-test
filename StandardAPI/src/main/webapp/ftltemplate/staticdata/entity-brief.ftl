<#ftl>
<#include "customutils.ftl"/>
<#if obj.result.data??>
	<#if showFR??>
		<div id="fr">
			<@docHtml data=obj.result.data />
		</div>
	</#if>
	
			
	<#if showFT??>
		<div id="ft">
			<#if showheading == "true">
				<div class="dc-hd pos-r">FirstTweets</div>
				<div class="dc-tw">
			</#if>
				<div class="tw-wrp tw-cnt">
					<div class="acl-sn">
						<div class="acl-img"><img width="83" src="${imgCssURL}/images/Accelerometer_22_F.png" class="jq-tw-s-tt"></div>
						<div class="hidden">
							<div class="tltp-hd">Microsoft Corporation</div>
							<div class="tltp-txt"> <span class="floatleft"><span class="bold"><span class="colorRed"><span class="font14">61%</span></span></span></span><span class="analytic_tooltip_smartText"><span class="bold"><span class="colorRed">below average</span></span><br>
								and <span class="bold"><span class="colorGreen">rising</span></span></span>
								<div class="cl"></div>
								<a target="Firstrain_window" itemid="C:MicrosoftCorporation" act="go.click" subsection="" sectionid="" viewid="" section="" class="tltp-btn jq_clicktrack jq_anchor_add_code">Go »</a>
								<div class="cl"></div>
							</div>
						</div>
						<div class="acl-lbl">Microsoft</div>
					</div>
					<div class="acl-sn-txt">
						<div class="acl-sn-txtn"> <span class="floatleft"><span class="bold"><span class="colorRed"><span class="font14">61%</span></span></span></span><span class="analytic_tooltip_smartText"><span class="bold"><span class="colorRed">below average</span></span><br>
							and <span class="bold"><span class="colorGreen">rising</span></span></span> </div>
					</div>
					<div class="cl"></div>
				</div>
					<div id="ft">
						<@tweetHtmltheme data=obj.result.data />
					</div>
				<div class="tw-wrp tw-cnt">
					<div class="fr"><a class="load-mr">Load More</a></div>
					<div class="cl"></div>
				</div>
				<#if showheading == "true">
						<div class="tw-tm"> <span class="fr"><span class="tw-tm-r">from</span><img src="${imgCssURL}/images/spacer.gif" class="tw-icon"></span>
							<div class="cl"></div>
						</div>
					</div>
				</#if>
		</div>
	</#if>
	
	<#if showTE??>
		<#if showheading == "true">
			<div class="dc-hd pos-r">Management Changes</div>
			<div class="dc-cnt">
		</#if>
				<div class="mgmt-chrt-inln"> <img src="${imgCssURL}/images/6938_mgmt.png">
					<div class="mgmt-chrt-lgnd">
						<div class="keylabel"><img src="${imgCssURL}/images/spacer.gif" class="key-hire"> hire</div>
						<div class="keylabel"><img src="${imgCssURL}/images/spacer.gif" class="key-depart"> departure</div>
						<div class="keylabel"><img src="${imgCssURL}/images/spacer.gif" class="key-intmoves"> internal move</div>
					</div>
					<div class="cl"></div>
				</div>
				<ul class="dc-lt">
					<li> <span title="DEPARTURE" class="mt-dept"></span>
						<div> <a class="dc-evnt-tt" href="http://gematsu.com/2014/11/xbox-japan-boss-takashi-sensui-resigns" target="_blank"><span class="bold">Takashi Sensui</span>, General Manager of Xbox, Japan <span class="bold"><span class="colorRed">LEFT</span></span> </a> <span class="dc-dt">30-Nov-2014</span> </div>
						<div class="cl"></div>
					</li>
					<li> <span title="DEPARTURE" class="mt-dept"></span>
						<div> <a class="dc-evnt-tt" href="http://www.zdnet.com/microsoft-hr-chief-and-senior-leadership-team-member-lisa-brummel-to-retire-7000035627/" target="_blank"><span class="bold">Lisa Brummel</span>, Executive Vice President of Human Resources <span class="bold"><span class="colorRed">LEFT</span></span> </a> <span class="dc-dt">31-Dec-2014</span> </div>
						<div class="cl"></div>
					</li>
					<li> <span title="INTERNAL MOVE" class="mt-move"></span>
						<div> <a class="dc-evnt-tt" href="http://www.zdnet.com/microsoft-hr-chief-and-senior-leadership-team-member-lisa-brummel-to-retire-7000035627/" target="_blank"><span class="bold">Kathleen Hogan</span>, Head of Microsoft Services <span class="bold"><span class="colorBlue">MOVED</span></span> to Executive Vice President of Human Resources</a> <span class="dc-dt">31-Dec-2014</span> </div>
						<div class="cl"></div>
					</li>
					<li> <span title="DEPARTURE" class="mt-dept"></span>
						<div> <a class="dc-evnt-tt" href="http://rcpmag.com/articles/2014/11/06/sap-hires-former-microsoft-vp.aspx" target="_blank"><span class="bold">Quentin Clark</span>, Vice President of Corporate - Data Platform Group <span class="bold"><span class="colorRed">LEFT</span></span> for SAP SE</a> <span class="dc-dt">06-Nov-2014</span> </div>
						<div class="cl"></div>
					</li>
					<li> <span title="DEPARTURE" class="mt-dept"></span>
						<div> <a class="dc-evnt-tt" href="http://www.geekwire.com/2014/microsoft-startup-guru-rahul-sood-leaving-build-new-startup/" target="_blank"><span class="bold">Rahul Sood</span>, General Manager of Microsoft Ventures <span class="bold"><span class="colorRed">LEFT</span></span> </a> <span class="dc-dt">03-Nov-2014</span> </div>
						<div class="cl"></div>
					</li>
				</ul>
				<div class="fr"><a class="load-mr">Load More</a></div>
				<div class="cl"></div>
			<#if showheading == "true">
				</div>
			</#if>
	</#if>
	
	<#if showE??>
		<#if showheading == "true">
			<div class="dc-hd pos-r">Event Timeline </div>
			<div class="dc-cnt">
		</#if>
			<div class="dc-dtbkt">
				<div class="dc-dtbkt-hd">Week of 09-Nov-2014</div>
				<ul class="dc-lt">
					<li> 
					<div> <a target="_blank" href="http://www.pinfofeed.com/article/51561/microsoft-buys-israeli-hybrid-cloud-security-startup-aorato-in-200m-deal/" class="dc-evnt-tt">microsoft buys israeli hybrid cloud security startup aorato in 200m deal</a> <span class="dc-dt">13-Nov-2014</span> </div>
						<div class="cl"></div>
					</li>
					<li> 
					<div> <a target="_blank" href="http://www.marketwatch.com/story/xamarin-announces-integration-with-microsoft-visual-studio-2015-and-new-customer-programs-onstage-at-microsoft-connect-developer-event-2014-11-12" class="dc-evnt-tt">Xamarin Announces Integration With Microsoft Visual Studio 2015 and New Customer Programs Onstage at Microsoft Connect Developer Event</a> <span class="dc-dt">12-Nov-2014</span> </div>
						<div class="cl"></div>
					</li>
					<li> 
						<div> <a target="_blank" href="http://www.windowscentral.com/microsoft-human-resources-head-lisa-brummel-retiring" class="dc-evnt-tt">Microsoft human resources head Lisa Brummel retiring</a> <span class="dc-dt">10-Nov-2014</span> </div>
						<div class="cl"></div>
					</li>
				</ul>
				<div class="dc-dtbkt-hd">Week of 02-Nov-2014</div>
				<ul class="dc-lt">
					<li> 
						<div> <a target="_blank" href="http://www.cultofmac.com/302227/microsoft-brings-word-excel-powerpoint-iphone-free/" class="dc-evnt-tt">Microsoft brings Word, Excel and Powerpoint to iPhone for free</a> <span class="dc-dt">06-Nov-2014</span> </div>
						<div class="cl"></div>
					</li>
					<li> 
						<div> <a target="_blank" href="http://www.fiercecmo.com/story/microsoft-advertising-job-cuts-could-negatively-affect-its-relationship-cmo/2014-11-03?utm_medium=rss&amp;utm_source=rss&amp;utm_campaign=rss" class="dc-evnt-tt">Microsoft advertising job cuts could negatively affect its relationship with CMOs</a> <span class="dc-dt">03-Nov-2014</span> </div>
						<div class="cl"></div>
					</li>
				</ul>
				<div class="dc-dtbkt-hd">Week of 26-Oct-2014</div>
				<ul class="dc-lt">
					<li> 
						<div> <a target="_blank" href="http://www.n3rdabl3.co.uk/2014/10/microsoft-annouces-microsoft-health-microsoft-band/" class="dc-evnt-tt">Microsoft Annouces Microsoft Health and the Microsoft Band</a> <span class="dc-dt">30-Oct-2014</span> </div>
						<div class="cl"></div>
					</li>
				</ul>
				<div class="dc-dtbkt-hd">Oct-2014</div>
				<ul class="dc-lt">
					<li> 
						<div> <a target="_blank" href="http://www.techienews.co.uk/9719580/microsoft-lumia-brand-name-unveiled-nokia-lumia-branding-culled/" class="dc-evnt-tt">Microsoft Lumia brand name unveiled; Nokia Lumia branding culled</a> <span class="dc-dt">24-Oct-2014</span> </div>
						<div class="cl"></div>
					</li>
					<li> 
						<div> <a target="_blank" href="http://www.siliconrepublic.com/digital-life/item/38898-microsoft-to-begin-killing" class="dc-evnt-tt">Microsoft to begin killing off Nokia brand, replacing it with Microsoft Lumia</a> <span class="dc-dt">22-Oct-2014</span> </div>
						<div class="cl"></div>
					</li>
					<li> 
						<div> <a target="_blank" href="http://www.v3.co.uk/v3-uk/news/2376821/microsoft-and-dell-offer-cloud-in-a-box-with-cloud-platform-system" class="dc-evnt-tt">Microsoft and Dell offer cloud in a box with Cloud Platform System</a> <span class="dc-dt">21-Oct-2014</span> </div>
						<div class="cl"></div>
					</li>
					<li> 
						<div> <a target="_blank" href="http://www.sitepronews.com/2014/10/21/microsoft-expands-hybrid-cloud-presence/" class="dc-evnt-tt">Microsoft Expands Hybrid Cloud Presence - Announces Partnership With Dell to Offer Microsoft Cloud Platform System</a> <span class="dc-dt">20-Oct-2014</span> </div>
						<div class="cl"></div>
					</li>
				</ul>
				<div class="fr"><a class="load-mr">Load More</a></div>
				<div class="cl"></div>
			</div>
		<#if showheading == "true">
			</div>
		</#if>
	</#if>
	
	<#if showWV??>
		<div class="vol-chrt-in jq-vol-lgnd">
			<@webVolumeChart index=0 searchId=0 />
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