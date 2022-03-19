<#ftl>
<div class="fr-ntbl">
    <div class="fr-ntbl-hd"><img src="${imgCssURL}/images/spacer.gif" class="fr-tw-icon16"> Also Tweeted by</div>
    <div class="fr-cl"></div>
    <#if data.notableDetails??>
	    <#list data.notableDetails as nd>
			<div class="fr-ntbl-itm">
				<img class="fr-ntbl-img" height="16" src="${nd.userImage}">
				<a class="jq_clicktrack" section="tweet.notable.tooltip" viewid="<#if viewId??>${viewId}</#if>" sectionid="<#if sectionId??>${sectionId}</#if>" act="title.click" item="tweet" itemid="${nd.screenName}" subSection="first.tweets" subSectionId="TW:${tweetId}" href="${reqScheme}://twitter.com/${nd.screenName}" target="_blank" metadata="${metadata?html}">${nd.screenName}</a>
			</div>
		</#list>
	</#if>
    <div class="fr-cl"></div>
</div>