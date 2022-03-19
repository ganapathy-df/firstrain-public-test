<#ftl>
<#include "customutils.ftl"/>
<#if obj.result.data?? && obj.result.data.document??>
	<#assign doc = obj.result.data.document>
	<style type="text/css">
		.context-view {font-size:13px; font-family:sans-serif;}
		.context-view a {
			color: #3476af;
			text-decoration: none;
		}
		.context-view a:hover, .context-view a:focus {
			color: #285b88
		}
		.article-details {
			padding-bottom: 16px;
		}
		.article-details h3 {margin: 0 0 15px;}
		.article-details h3 a {
			color: #444444;
			font-size: 25px;
			font-weight: 300;
			line-height: 1.2;
		}
		.article-details h3 a {text-decoration:none;}
		.article-details h3 a strong {font-weight:300;}
		.article-details ul {
			list-style: none;
			margin: 0;
			padding: 0;
			color: #999999;
		}
		.article-details li {
			display: inline-block;
			margin-right:10px;
			vertical-align: middle;
		}
		.smart-summary {
			font-size:16px;
			display: inline-block;
			border-bottom: 1px solid #979797;
			padding-bottom: 7px;
			margin-top: 16px;
			margin-bottom: 20px;
			text-transform: uppercase;
			font-weight: bold;
		}
		.em_summary {
		    font-size:13px;
			font-family: inherit;
		    font-weight: normal;
		    line-height: 1.6;
		    margin-bottom: 10px;
		}
		blockquote, blockquote p {
			line-height: 1.6;
			color: #333333;
			font-size:13px;
		}
		blockquote {
			position:relative;
			padding-left:40px;
			margin:0 0 30px;
		}
		blockquote:last-child {
			margin-bottom:0
		}
		blockquote:before {
		position: absolute;
		top: 38px;
		left: 0;
		color: #dedede;
		content: "\201C";
		font-size: 94px;
		line-height: 1px;
		margin-right: 4px;
		vertical-align: 6px;
		font-family: sans-serif;
		}
		
		q, blockquote p b {
			color: #0a81e0;
			font-style: italic; font-weight:normal;
		}
		.article-img {width:164px; height:auto; text-align: center; position:relative; float:right; margin-left:20px;}
		.article-img img {max-width:100%; max-height:125px;}
	</style>
	
	<div class="context-view">
		<div class="article-details">
			<h3>
				<a target="_blank" href="${doc.link}">${doc.title}</a>
			</h3>
			<ul>
				<li class="jq-em-sourceName">${doc.source.name}</li>
				<li>${doc.timeStamp?date("dd MMM yyyy HH:mm:ss z")?string("dd-MMM-yyyy")}</li>
				<li><a href="${doc.link}" target="_blank">Read Full Article</a></li>
			</ul>
		</div>
		<div class="smart-summary">Smart Summary</div>
		<p class="em_summary">
		<#if doc.image??>
			<span class="article-img"><img alt="Article Image" src="${doc.image}"></span>
		</#if>
		<#if doc.snippet??>
			${doc.snippet}
		</#if>
		<#if doc.quotes??>
			<blockquote>
		    	<p>${doc.quotes}</p>
		    </blockquote>
		</#if>
		
	</div>
</#if>