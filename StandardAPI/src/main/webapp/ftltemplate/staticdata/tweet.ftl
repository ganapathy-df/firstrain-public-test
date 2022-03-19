<#ftl>
<#include "customutils.ftl"/>
<#if obj.result.data?? && obj.result.data.tweet??>
	<#assign tweet = obj.result.data.tweet>
	<style type="text/css">
	.colorGreen {color:#659A1F;}
	.colorBlue {color:#005596;}
	.lightGrey {color:#7d7d7d;}
	.bold {font-weight:bold;}
	.context-view {
		font-size: 13px;
		font-family: sans-serif;
	}
	.context-view a {
		color: #3476af;
		text-decoration: none;
	}
	.context-view a:hover, .context-view a:focus {
		color: #285b88
	}
	.cl {clear:both; height:0; font-size:0; overflow:hidden;}
	.article-details {
		padding-bottom: 16px;
	}
	.article-details h3 {
		margin: 0 0 15px;
		color: #444444;
		font-size: 25px;
		font-weight: 300;
		line-height: 1.2;
	}
	.article-details h3 strong {
		font-weight: 300;
	}
	.article-details ul {
		list-style: none;
		margin: 0;
		padding: 0;
		color: #999999;
	}
	.article-details li {
		display: inline-block;
		margin-right: 10px;
		vertical-align: middle;
	}
	.smart-summary {
		font-size: 16px;
		display: inline-block;
		border-bottom: 1px solid #979797;
		padding-bottom: 7px;
		margin-top: 16px;
		margin-bottom: 20px;
		text-transform: uppercase;
		font-weight: bold;
	}
	/* -- tweet user info -- */
	.media {
		margin-bottom: 1.25em;
		overflow: hidden;
		color:#333333;
	}
	.media h6 {
		color: #444;
		font-size: 1.124rem;
		font-weight: 600;
	}
	.media .media-obj {
		margin-right: .9375em;
		float: left
	}
	.media .media-obj img {
		display: block
	}
	.media .media-obj-ext {
		margin-left: .9375em;
		float: right
	}
	.media-bd {
		overflow: hidden
	}
	.media-bd :first-child {
		margin-top: 0
	}
	.media-bd :last-child {
		margin-bottom: 0
	}
	.ft-alsotw {margin-bottom:1.250em; padding:1.250em; background-color:#D6EDF5;}
	/* -- notable tooltip -- */
	.ntbl-hd {font-weight:600; font-size:13px;}
	.ntbl-itm {width:47%; float:left; margin-right:2%; margin-top:2px;}
	.ntbl-itm a {display:block; padding:3px 0; line-height: 16px; margin-right: 21px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size:14px;}
	.ntbl-img {float:right; margin-left:5px; margin-top:2px; width:16px;}
	</style>
	
	<div class="context-view">
		<div class="article-details">
			<h3 class="ft-container"><span>${tweet.tweetHtml}</span></h3>
			<ul>
				<li>${tweet.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</li>
				<li><a href="${tweet.link[0]}" target="_blank">Read Full Article</a></li>
			</ul>
		</div>
		<div class="smart-summary">Tweeted By</div>
		<div class="media">
			<div class="media-obj"> <img alt="Avatar" src="${tweet.authorAvatar}" class="avatar"> </div>
			<div class="media-bd">
				<p><strong>${tweet.authorName}</strong> <a href="https://twitter.com/${tweet.userName}" target="_blank">@${tweet.userName}</a> <br>
					${tweet.authorDescription}
				</p>
			</div>
		</div>
	</div>
</#if>