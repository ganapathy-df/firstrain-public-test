function FRCommonUtils($) {
	var MSG = {
		"email.help.text": "Your email address here",
		"name.required" : "Please enter your name.",
		"email.invalid" : "Please enter a valid email address",
		"email.in.use" : "Email already in use",
		"accountname.required" : "Do not modify input account name. Please provide additional inputs in the next line.",
		"invalid.group.map" : "Group mapped to your account is not valid.",
		"email.invalid.domain" : "Sorry, this address could not be verified. Please make sure you are using your corporate email address",
		"email.invalid.group" : "Sorry, this is an invalid account",
		"err.msg.unable.to.perform.opr" : "<div class='font14'><table width='100%' cellpadding='0' cellspacing='0'><tr height='220'><td valign='middle' align='center'>The system was unable to perform the requested operation.<br />Contact Administrator.</td></tr></table></div>",
		"err.msg.create.task" : "<h3>Create a Task</h3><div class='font14'><table width='100%' cellpadding='0' cellspacing='0'><tr height='180'><td valign='middle' align='center'> Could not <strong>create task</strong>.<br>Try again later.</td></tr></table><div class='bt-msg'><em>Please contact <a href='mailto:support@firstrain.com'>support@firstrain.com</a> or your company's FirstRain Administrator with any questions.</em></div></div>",
		"company" : "company",
		"title.click" : "titleclick",
		"entity.click" : "entityclick",
		"more.click" : "moreclick",
		"search" : "search",
		"create.monitor" : "createmonitor",
		"go.click" : "goclick",
		"notification" : "notification",
		"chat.this" : "chatthis",
		"note" : "addnote",
		"create.task" : "createtask",
		"email.this" : "emailthis",
		"feedback.open" : "feedbackopen",
		"feedback.form" : "feedbackform",
		"feedback.email" : "feedbackemail",
		"bookmark" : "bookmark",
		"unbookmark" : "unbookmark",
		"search" : "search",
		"tab.click" : "tabclick",
		"feedback.click" : "feedbackclick",
		"feedback.via.email.click" : "feedbackviaemailclick",
		"support.link" : "supportlink",
		"monitor.this.vai.email" : "monitorthisvaiemail/mobile",
		"apply.button" : "applybutton",
		"submit.button" : "submitbutton",
		"load.more" : "loadmore",
		"load.more.click" : "loadmoreclick",
		"mail" : "mail",
		"bookmark.view" : "bookmarkview",
		"feedback" : "feedback",
		"quote" : "quote",
		"model.window" : "modelwindow",
		"monitor.summary" : "monitorsummary",
		"monitor.analytics" : "monitoranalytics",
		"monitor.searches" : "monitorsearch",
		"favourite.monitors" : "favouritemonitors",
		"my.monitors" : "mymonitors",
		"group.monitor" : "groupmonitor",
		"header" : "header",
		"footer" : "footer",
		"generate.token" : "generatetoken",
		"first.tweets" : "tweets",
		"tweet" : "tweet",
		"mark.default" : "markdefault",
		"unmark.default" : "unmarkdefault",
		"management.changes" : "managementchanges",
		"trending.companies" : "trendingcompanies",
		"trending.topics" : "trendingtopics",
		"recent.web.intelligence" : "firstreads",
		"event.timeline" : "eventtimelines",
		"major.stock.financial.events" : "stock&financialevents",
		"analyst.comments" : "analystcomments",
		"industry.health.indicators" : "industryhealthindicator",
		"corporate.governance.issues" : "corporategovernance",
		"industry.transaction" : "industrytransactions",
		"setup.email.delivery" : "setupemaildelivery",
		"iphone" : "iphone",
		"ipad" : "ipad",
		"web.volume.chart" : "webvolumechart",
		"windows.phone.app" : "windowsphoneapp",
		"android" : "android",
		"firstrain.window" : "firstrainwindow",
		"firstrain.click" : "firstrainclick",
		"analytics" : "analyticschart",
		"form.submit" : "formsubmit",
		"submit.feedback" : "submitfeedback",
		"click" : "click",
		"powered.by.firstrain" : "poweredbyfirstrain",
		"firstrain" : "firstrain",
		"firstrain.learning.centre" : "firstrainlearningcentre",
		"heading.click" : "headingclick",
		"click.here" : "clickhere",
		"portal.link" : "portallink",
		"load.view" : "loadview",
		"monitor.compact" : "monitorcompact",
		"trending.entities" : "trendingentities",
		"select.monitors.to.display" : "selectmonitorstodisplay",
		"done" : "done",
		"expando" : "expando",
		"monitor.view" : "monitorview",
		"MONITOR ACTIVITY TRENDS" : "monitoractivitytrends",
		"BUSINESS INFLUENCERS" : "businessinfluencers",
		"MARKET DRIVERS" : "marketdrivers",
		"Business Twitter Trends" : "businesstwittertrends",
		"REGIONAL LENS: GLOBAL" : "regionallensglobal",
		"REGIONAL LENS: U.S." : "regionallensus",
		"management" : "management",
		"challengers" : "challengers",
		"industry" : "industry",
		"facts" : "facts",
		"more" :"more",
		"monitor.this.company" : "monitorthiscompany",
		"add" : "add",
		"is.this.wrong.company" : "isthiswrongcompany",
		"significant.events" : "significantevents",
		"lhs" : "lhs",
		"rhs" : "rhs",
		"my.bookmark" : "mybookmark",
		"business.challengers" : "businesschallengers",
		"business.lines" : "businesslines",
		"recently.viewed" : "recentlyviewed",
		"hscroll" : "hscroll",
		"vscroll" : "vscroll",
		"favourite" : "favourite",
		"unfavourite" : "unfavourite",
		"companyurl" : "companyurl",
		"company.matches" : "companymatches",
		"firstrain.select.company" : "firstrainselectcompany",
		"web.results" : "webresults",
		"cross.section" : "crosssection",
		"request.coverage" : "requestcoverage",
		"baseballcard.window" : "baseballcardwindow",
		"tweet.notable" : "tweetnotable",
		"tweet.notable.tooltip" : "tweetnotabletooltip",
		"topic" : "topic",
		"business.basics" : "businessbasics",
		"apply" : "apply",
		"cancel" : "cancel",
		"back" : "back",
		"view" : "view",
		"help" :"help",
		"section" : "section",
		"mouseover" : "mouseover",
		"intelligence.trends" : "intelligencetrends",
		"link" : "link",
		"opportunity" : "opportunity",
		"availableviews" : "availableviews",
		"yourgame" : "yourgame",
		"info" : "info",
		"tweet.this" : "tweetthis",
		"share.on.linkedin" : "shareonlinkedin",
		"sec.fillings" : "secfillings",
		"welcome.form" : "welcomeform",
		"welcome.user" : "welcomeuser",
		"thank.you" : "thankyou",
		"confirm" : "confirm",
		"yammer.this" : "yammerthis",
		"top.news" : "topnews",
		"heat.map" : "heatmap",
		"refresh" : "refresh",
		"subview" : "subview",
		"first.reads" : "firstreads",
		"monitor" :"monitor",
		"close" : "close",
		"industry.peers" : "industrypeers",
		"opportunities":"opportunities",
		"intelligence.analytics" : "intelligenceanalytics",
		"starthere" : "starthere",
		"privacy.policy": "privacypolicy",
		"competitor.events":"competitorevents",
		"industry.topic.events" : "industrytopicevents",
		"reason.to.call" : "reasontocall"
	};
	
	FRCommonUtils.prototype.constructActivityURL =  function (_this, url) {
		var params = {};
		FRCommonUtils.prototype.constructActivityParams(params, _this);
		var symb = url.indexOf("?") == -1 ? "?" : "&";
		var i = 0;
		for (var key in params) {
			if (i == 0) {
				url += symb + key + '=' + params[key];
			} else {
				url += '&' + key + '=' + params[key];
			}
			i += 1;
		}
		return url;
	};
	
	FRCommonUtils.prototype.bindMouseEventToTweetNotables = function (selector, addLeftPos) {
		var jqXHR;
		$(document).on("mouseenter click", selector, function(e) {
			var delayTimer = $("#fr-tooltip").data('delayTimer');
			if(delayTimer) {
				clearTimeout(delayTimer);
			}
			var _this = $(this);
			var fn1 = function() {
				if(jqXHR) {
					jqXHR.abort();
				}
				var pos = _this.offset();
				if (addLeftPos != null && addLeftPos != undefined) {
					pos.left = pos.left + addLeftPos;
				} else {
					pos.left = pos.left + 10;
				}
				pos.top = pos.top + 10;
				pos.width = _this.width();
				FRCommonUtils.prototype.showTooltip(pos, "", false, true);
				FRCommonUtils.prototype.addLoadingImg("#fr-tooltip .jq-tt-data", "fr-spnr-ntbl");
				var groupId = _this.attr("groupId");
				var tweetId = _this.attr("tweetId");
				
				var url = "../../common/tweetNotable?tweetId=" + tweetId;
				var _url = FRCommonUtils.prototype.constructActivityURL(_this, url);
				if (groupId != undefined) {
					_url += "&groupId=" + groupId;
				}
				jqXHR = $.get(_url)
				.success(function(res) {
					FRCommonUtils.prototype.showTooltip(pos, res.result, false, true);
				});
			};
			$("#fr-tooltip").data('delayTimer', setTimeout(fn1, 200));
		})
		.on("mouseleave", function(e) {
			if(FRCommonUtils.prototype.isTouchDevice()) {
				return;
			}
			if(jqXHR) {
				jqXHR.abort();
			}
			var delayTimer = $("#fr-tooltip").data('delayTimer');
			if(delayTimer) {
				clearTimeout(delayTimer);
			}
			$("#fr-tooltip").data('hideTimer', setTimeout(FRCommonUtils.prototype.hideTooltip, 300));
		});
	}
	
	FRCommonUtils.prototype.initiateClickTracking = function (appendCode) {
		$(document).on("click", ".jq_clicktrack", function() {
			var _this = $(this);
			var data = {};
			FRCommonUtils.prototype.constructActivityParams(data, _this, appendCode);
			FRCommonUtils.prototype.ajxCallToLogAction (data);
		});
		
		$(document).on("mouseover", ".jq_mouseovertrack", function() {
			var _this = $(this);
			var data = {};
			FRCommonUtils.prototype.constructActivityParams(data, _this);
			FRCommonUtils.prototype.ajxCallToLogAction (data);
		});
	}
	
	FRCommonUtils.prototype.ajxCallToLogAction = function (data, requestType) {
		$.ajax({
			url : FREnv.appBaseUrl + "/" + FREnv.appname + "/" + FREnv.version + "/activity/logaction",
			cache:false,
			type : requestType ? requestType : "GET",
			dataType : "html",
			data : data
		});
	}
	
	FRCommonUtils.prototype.constructActivityParams = function (params, _this, appendCode, isSerializeData, track) {
		var data = {};
		if(appendCode) {
			data.code = FREnv.CODE;
		}
		data.activity = MSG[_this.parents('.jq-p-p-section').attr("act")];
		if(data.activity == undefined) {
			data.activity = MSG[_this.attr("act")];
		}
		data.target = MSG[_this.parents('.jq-p-p-section').attr("item")];
		if (data.target == undefined) {
			data.target = MSG[_this.attr("item")];
		}
		data.targetId = MSG[_this.parents('.jq-p-p-section').attr("itemid")];
		if(data.targetId == undefined) {
			data.targetId = _this.attr("itemid");
		}
		
		data.viewId = _this.parents('.jq-p-p-section').attr("viewid");
		if (data.viewId == undefined) {
			data.viewId = _this.attr("viewid");
		}
		if (data.viewId == undefined) {
			data.viewId = _this.parents('.jq-p-viewid').attr("viewid");
		}
		
		data.section = MSG[_this.parents('.jq-p-p-section').attr("section")];
		if (data.section == undefined) {
			data.section = MSG[_this.attr("section")];
		}
		
		data.sectionId = _this.parents('.jq-p-p-section').attr("sectionid");
		if (data.sectionId == undefined) {
			data.sectionId = _this.attr("sectionid");
		}
		
		data.subSection = MSG[_this.parents('.jq-p-section').attr("subsection")];
		if (data.subSection == undefined) {
			data.subSection = MSG[_this.attr("subsection")];
		}
		
		data.subSectionId = _this.parents('.jq-p-section').attr("subsectionid");
		if (data.subSectionId == undefined) {
			data.subSectionId = _this.attr("subsectionid");
		}
		data.metadata = _this.attr("metadata");
		var activityView = _this.attr("activityView");
		if (activityView) {
			FREnv.pageName = activityView;
		}
		
		if (data.metadata == undefined) {
			data.metadata = _this.parents('.jq-p-viewid').attr("metadata");
		}
		
		//first prefrence in tag str1 otherwise pick root str1  
		var str1 = _this.attr("str1");
		if (str1) {
			data.str1 = str1;
		} else {
			str1 = $(".jq-p-viewid").attr("str1");
			if (str1) {
				data.str1 = str1;
			}
		}
		
		//first prefrence in tag str2 otherwise pick root str2  
		var str2 = _this.attr("str2");
		if (str2) {
			data.str2 = str2;
		} else {
			str2 = $(".jq-p-viewid").attr("str2");
			if (str2) {
				data.str2 = str2;
			}
		}
		
		if(isSerializeData) {
			FRCommonUtils.prototype.constructActivityParamsInSerializeData(data, params, track);
		} else {
			FRCommonUtils.prototype.constructActivityParamsInAjaxCall(data,params);
		}
	}

	FRCommonUtils.prototype.constructActivityParamsInAjaxCall = function  (data,params) {
		if (data.activity != undefined && data.activity != '') {
			params.activityType = data.activity;
		}
		if (data.target != undefined && data.target != '') {
			params.target = data.target;
		}
		if (data.targetId != undefined && data.targetId != '') {
			params.targetId = data.targetId;
		}
		if (FREnv.pageName == "baseballcard") {
			params.activityView = "accountsnapshot";
		} else {
			params.activityView = FREnv.pageName;
		}
		if(FREnv.activityChannel) {
			params.activityChannel = FREnv.activityChannel;
		}
		if (data.viewId != undefined && data.viewId != '') {
			params.viewId = data.viewId;
		}
		//if section is not defined and subsection is present there then subsection becomes section 
		if (data.section != undefined && data.section != '') {
			params.section = data.section;
		} else if (data.subSection != undefined && data.subSection != '') {
			params.section = data.subSection;
			data.subSection = '';
		}
		if (data.sectionId != undefined && data.sectionId != '') {
			params.sectionId = data.sectionId;
		} else if (data.subSectionId != undefined && data.subSectionId != '' && (data.section == undefined || data.section == '')) {
			params.sectionId = data.subSectionId;
			data.subSectionId = '';
		}
		if (data.subSection != undefined && data.subSection != '') {
			params.subSection = data.subSection;
		}
		if (data.subSectionId != undefined && data.subSectionId != '') {
			params.subSectionId = data.subSectionId;
		}
		if(data.code != undefined && data.code != '') {
			params.code = data.code;
		}
		if (data.metadata != undefined && data.metadata != '') {
			params.metaData = data.metadata;
		}
		if (data.str1 != undefined && data.str1 != '') {
			params.str1 = data.str1;
		}
		if (data.str2 != undefined && data.str2 != '') {
			params.str2 = data.str2;
		}
		params.track = "true";
	}
	
	FRCommonUtils.prototype.addProximaNova = function(d) {
	  var config = {
	    kitId: 'odo4zng',
	    scriptTimeout: 3000
	  },
	  h=d.documentElement,t=setTimeout(function(){h.className=h.className.replace(/\bwf-loading\b/g,"")+" wf-inactive";},config.scriptTimeout),tk=d.createElement("script"),f=false,s=d.getElementsByTagName("script")[0],a;h.className+=" wf-loading";tk.src='//use.typekit.net/'+config.kitId+'.js';tk.async=true;tk.onload=tk.onreadystatechange=function(){a=this.readyState;if(f||a&&a!="complete"&&a!="loaded")return;f=true;clearTimeout(t);try{Typekit.load(config)}catch(e){}};s.parentNode.insertBefore(tk,s)
	}
	
	FRCommonUtils.prototype.showTooltip = function(pos, html, isTreeMapOrTweetGauge, isTweetNotable) {
		var adjustHeightForIE10Win8 = 0;
		var userAgent = navigator.userAgent.toLowerCase();
		if( userAgent.indexOf("touch") == -1 &&  userAgent.indexOf("windows nt 6.2") > 0 && (navigator.userAgent).indexOf("msie 10") > 0) {
			if(typeof pageYOffset != "undefined") {
				adjustHeightForIE10Win8 = pageYOffset;
			} else {
				adjustHeightForIE10Win8 = document.body.scrollTop;
			}
		}
		var tt = $("#fr-tooltip");
		if(tt.length == 0) { //if no placeholder add that
				tt =
					$("<div id='fr-tooltip' class='fr-tltp'>" +
							"<div class='fr-tltp-ar-l jq-arrow'></div>" +
							"<div class='fr-tltp-w jq-tt-st'>" +
								"<div class='fr-tltp-c'>" +
									"<div class='jq-tt-data'></div>" +
								"</div>" +
							"</div>" +
						"</div>");
			tt.appendTo(document.body).hide();
			
			tt.on("mouseenter", function() {
				var hideTimer = $("#fr-tooltip").data('hideTimer');
				var containerTimer = $("#fr-tooltip").data('containerTimer');
				if(hideTimer) {
					clearTimeout(hideTimer);
				}
				if(containerTimer) {
					clearTimeout(containerTimer);
				}
			}).on('mouseleave', function() {
				FRCommonUtils.prototype.hideTooltip();
			});
		}
		if(isTweetNotable) {
			tt.find(".jq-tt-st").css({"max-width": 280, "min-width": 280, "width": 280});	
		} else {
			tt.find(".jq-tt-st").css({"max-width": 190, "min-width": 170});		
		}
		tt.show().find(".jq-tt-data").html(html);
		var winWidth = FRCommonUtils.prototype.getContainerWidth();
		var wrapperWidth = FRCommonUtils.prototype.getWrapperWidth();
		var wrapperLeft = (winWidth - wrapperWidth)/2;
		var elem = tt.find(".jq-tt-st");
		var detailsWidth = elem.outerWidth();
		var detailsHeight = elem.outerHeight();
		var winHeight =  $(window).height();
		
		var boxHeight = 0;
		if(isTreeMapOrTweetGauge) {
			boxHeight = pos.height;
		}
		var scrTop = $(window).scrollTop();
		//show up
		if((pos.top - scrTop + boxHeight + detailsHeight + 15) > winHeight) {
			var left = pos.left - 60;
			var toppos = pos.top - (detailsHeight + 15);
			if(isTreeMapOrTweetGauge) {
				left = left + (pos.width/2);
				toppos = toppos - (pos.height/2) + 5;
			} else if(isTweetNotable) {
				left = pos.left - 20;
			}
			if(toppos < 0 )
			{
				toppos = 5; 
			}
			var arrleft = 50;
			if(isTweetNotable) {
				arrleft = 13;
			} else if((left + detailsWidth + 10) > winWidth) {
				left = winWidth - (detailsWidth  + 40);
				arrleft = pos.left - left - 10;
				if(isTreeMapOrTweetGauge) {
					arrleft = arrleft + (pos.width/2);
				}
			}
			tt.find(".jq-arrow").attr("class","jq-arrow fr-tltp-ar-b").css({"top": (detailsHeight - 1),"left": arrleft});
			tt.css({"left": left , "top": toppos});
		} else if((pos.left + pos.width + detailsWidth + 10) > (wrapperWidth + wrapperLeft)) {	// show left or down	
				// not enough space to show in left
				if((detailsWidth + 5) > pos.left) {
					//show down
					var left = pos.left  - 60;
					var toppos = pos.top  + 15;
					if(isTreeMapOrTweetGauge) {
						left = left + (pos.width/2);
						toppos = toppos + (pos.height/2) - 5;
					} else if(isTweetNotable) {
						left = pos.left - 20;
					}
					var arrleft = 50;
					if(isTweetNotable) {
						arrleft = 13;
					} else if((left + detailsWidth + 10) > winWidth) {
						left = winWidth - (detailsWidth  + 40);
						arrleft = pos.left - (left + 10);
						if(isTreeMapOrTweetGauge) {
							arrleft = arrleft + (pos.width/2);
						}
					}
					tt.find(".jq-arrow").attr("class","jq-arrow fr-tltp-ar-t").css({"top": -11, "left": arrleft});
					tt.css({"left": left , "top": toppos});
				} else { // show left
					tt.find(".jq-arrow").attr("class","jq-arrow fr-tltp-ar-r").css({"top": "auto", "left": (detailsWidth + 10)});
					tt.css({"left": (pos.left - (detailsWidth + 22)) , "top": (pos.top - 10 + adjustHeightForIE10Win8)});
				}
		} else { //show right
				tt.find(".jq-arrow").attr("class","jq-arrow fr-tltp-ar-l").css({"top": "auto", "left": 0});
				tt.css({"left": (pos.left + pos.width), "top": (pos.top - 10 + adjustHeightForIE10Win8) });
		}
		
		if(FRCommonUtils.prototype.isTouchDevice()) {
			setTimeout(FRCommonUtils.prototype.hideTooltip, 3000);
		}
	}
	
	FRCommonUtils.prototype.isTouchDevice = function() {
		/*
		return !!('ontouchstart' in window) // works on most browsers 
	      || !!('onmsgesturechange' in window); // works on ie10
	  	*/
		return ('ontouchstart' in window || window.navigator.msMaxTouchPoints > 0);
	}
	
	FRCommonUtils.prototype.getWrapperWidth = function() {
		return $("body").width();	
	}

	FRCommonUtils.prototype.getContainerWidth = function() {
		return $(window).width();	
	}

	FRCommonUtils.prototype.setTooltipHtml = function(html) {
		$("#fr-tooltip .jq-tt-data").html(html);
	}

	FRCommonUtils.prototype.hideToolTipForTouch = function() {
		FRCommonUtils.prototype.hideTooltip();
		$(".treemap-node").removeClass('treeNodeHover');
	}

	FRCommonUtils.prototype.hideTooltip = function() {
		var delayTimer = $("#fr-tooltip").data('delayTimer');
		if(delayTimer) {
			clearTimeout(delayTimer);
		}
		$("#fr-tooltip").hide();
	}
	
	FRCommonUtils.prototype.addLoadingImg = function(selector, className) {
		selector = FRCommonUtils.prototype.wrapAsjQueryObj(selector);
		if(className) {
			selector.html("<div class=\"fr-spnr " + className + "\"><img class=\"fr-spinner\" src=\"" + FREnv.imgcssurl + "/images/spacer.gif\">Loading</div>");
		} else {
			selector.html("<center><div class=\"fr-spnr\"><img class=\"fr-spinner\" src=\"" + FREnv.imgcssurl + "/images/spacer.gif\">Loading</div></center>");		
		}
	}
	
	FRCommonUtils.prototype.wrapAsjQueryObj = function(obj) {
		if ( !(obj instanceof $) ) {
			obj = $(obj);
		}
		return obj;
	}
	
	FRCommonUtils.prototype.getSectionStr = function() {
		var sectionStr = '';
		if($('#fr').length > 0) {
			sectionStr = 'fr';
		}
		if($('#ft').length > 0) {
			if(sectionStr.length > 0) {
				sectionStr += '-';
			}
			sectionStr += 'ft';
		}
		if($('#te').length > 0) {
			if(sectionStr.length > 0) {
				sectionStr += '-';
			}
			sectionStr += 'te'
		}
		if($('#e').length > 0) {
			if(sectionStr.length > 0) {
				sectionStr += '-';
			}
			sectionStr += 'e'
		}
		if($('#ac').length > 0) {
			if(sectionStr.length > 0) {
				sectionStr += '-';
			}
			sectionStr += 'ac'
		}
		return sectionStr;
	}
}