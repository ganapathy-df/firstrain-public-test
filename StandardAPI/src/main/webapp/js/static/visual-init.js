if(typeof FREnv == 'undefined') {
	FREnv = {};
}
(function() {

	//Localize jQuery variable
	var $, frVisual1, envConf, servicePath;
	
	var CH_TYPES = {"tt": "TREE_MONITOR_SEARCH","bi": "TREE_COMPANY","md": "TREE_TOPICS","twt": "ACC_METER","gl": "GEO_WORLD","rl": "GEO_US"};
	var reqDefaults = {"dim": "1300x200", "chDims": "200x150", "chTypes": "tt,bi,md,twt,gl,rl"};
	
	/******** Load jQuery if not present *********/
	if(!FREnv.$) {
		var script_tag = document.createElement('script');
		script_tag.setAttribute("type","text/javascript");
		var file = FREnv.debug == "true" ? "jquery.js" : "jquery.min.js";
		script_tag.setAttribute("src", "//ajax.googleapis.com/ajax/libs/jquery/1.9.1/" + file);
		
		
		if (script_tag.readyState) {
			script_tag.onreadystatechange = function () { // For old versions of IE
				if (this.readyState == 'complete' || this.readyState == 'loaded') {
					scriptLoadHandler();
				}
			};
		} else { // Other browsers
			script_tag.onload = scriptLoadHandler;
		}
		
		// Try to find the head, otherwise default to the documentElement
		(document.getElementsByTagName("head")[0] || document.documentElement).appendChild(script_tag);
	} else {
		$ = FREnv.$;
		// Call our main function
		main(); 
	}
	
	/******** Called once jQuery has loaded ******/
	function scriptLoadHandler() {
		// Restore $ and window.jQuery to their previous values and store the
		// new jQuery in our local jQuery variable
		if(!FREnv.$) {
			FREnv.$ = window.jQuery.noConflict(true);
		}
		$ = FREnv.$;
		// Call our main function
		main(); 
	}

	/******** Our main function ********/
	function main() { 
		$(function() {
			$(".jq-fr-visualization").each(function(index) {
				var scriptTag = $(this);
				if("true" != scriptTag.attr("rendered")) {
					scriptTag.attr("rendered", "true");
					
					var _src = scriptTag.attr("src");
					var paramMap = urlToParamsMap(_src);
					//console.log( paramMap );
					paramMap = $.extend({}, reqDefaults, paramMap);
					servicePath = paramMap.sp || _src.substring(0, _src.indexOf("/js/")) 
					//if config already loaded
					if(FREnv.imgcssurl) {
						envConf = {};
						envConf.imgCssURL = FREnv.imgcssurl;
						envConf.appName = FREnv.appname;
						envConf.version = FREnv.version;
						envConf.jsURL = FREnv.jsurl;
						
						$(".jq-ribbon").remove();
						prepareForVisualization(scriptTag, paramMap, index);
					} else {
						$.ajax({
							url: servicePath + "/widget/config",
							data: {"authInReqParam": true, "authKey": paramMap.authKey, "frUserId": paramMap.frUserId},
							dataType: "jsonp",
							cache: false,
							success: function(res) {
								if(res.status == "SUCCESS") {
									$(".jq-ribbon").remove();
									envConf = res.result;
									prepareForVisualization(scriptTag, paramMap, index);
								} else {
									throw "Error while loading environment configurations";
								}
							}
						});
					}
				}
			});
		});
	}
	
	function prepareForVisualization(scriptTag, paramMap, index) {
		var cssUrl = envConf.imgCssURL + "/css/" + envConf.appName + "/" + envConf.version;
		var cssfile = FREnv.debug == "true" ? "visualization.css" : "visualization.min.css";
		var styleURL =  paramMap.styleURL || cssUrl + "/" + cssfile;
		$link = $("<link />").attr("rel", "stylesheet").attr("type", "text/css").attr("href", styleURL);
		$("head").append($link);
	
		var cid = paramMap.cid || ("jq-visual-data-cont_" + index);
		var cont = $("#" + cid);
		if(cont.length == 0) {
			cont = $("<div />").attr("id", cid);
			$(scriptTag).after(cont);
		}
		var dims = paramMap.dim.split("x");
		if(paramMap.chTypes == 'twt') {
			cont.css({"min-width": (dims[0] + "px"), "height": ((parseInt(dims[1]) + 18) + "px")});
		} else {
			cont.css({"width": (dims[0] + "px"), "height": ((parseInt(dims[1]) + 18) + "px")});
		}
		var jsUrl = envConf.jsURL + "/js/" + envConf.appName + "/" + envConf.version;
		var file = FREnv.debug == "true" ? "visualization.js" : "visualization.min.js";
		$.ajax({
            url: jsUrl + "/" + file,
            dataType: "script",
            cache: true,
            success: function() {
                FRVisual($);
                //console.log("visual-" + $().treemap);
                file = FREnv.debug == "true" ? "graph.js" : "graph.min.js";
                $.ajax({
    	            url: jsUrl + "/" + file,
    	            dataType: "script",
    	            cache: true,
    	            success: function() {
    	            	frVisual1 = new FRVisual1($);
    	            	//console.log(frVisual1);
    	            	
    	            	var chTypes = paramMap.chTypes.split(",");
    	            	paramMap.chTypes = [];
    	            	$.each(chTypes, function() {
    	            		paramMap.chTypes.push(CH_TYPES[this]);
    	        		});
    	            	
    	            	$.ajax({
    	            		url: servicePath + "/widget/graph",
    	            		data: {"authInReqParam": true, "id": paramMap.id, "chTypes": paramMap.chTypes.join(","), "authKey": paramMap.authKey, "frUserId": paramMap.frUserId},
    	            		dataType: "jsonp",
    	            		cache: false,
    	            		success: function(res) {
    	            			var options = getGraphOptions(paramMap, index);
    	            			if(chTypes.length > 1) {
    	    	            		options.isRibbon = true;
    	    	        		}
    	            			renderAnalyticsRibbon(cont, res, options);
    	            		}
    	            	});
    	            	
    	            }
                });
            }
        });
	}
	
	function urlToParamsMap(url){
		var map = {};
		var parts = url.split(/(\?|&|#)/);
		for(var part in parts){
			var keyVal = parts[part].split("=");
			if(keyVal.length == 2){
				//must be 2 since we are chopping xx=yy
				var key = $.trim(keyVal[0]);
				var value = decodeURIComponent($.trim(keyVal[1]));
				if(key && key != "")
					map[key] = value;
			}
		}
		return map;
	}
	
	function getGraphOptions(paramMap, index) {
		var chDims = paramMap.chDim.split("x");
		var options = {"index": index, "width": parseInt(chDims[0]), "height": parseInt(chDims[1]), "accelboxwidth": 85, "imgBaseURL": envConf.imgCssURL + "/images/"};
		options.data = {};
		options.chTypes = paramMap.chTypes;
		options.events = {};
		if(paramMap.events) {
			//events = "click:callback1,mouseenter:callback1,mouseleave:callback2
			$.each(paramMap.events.split(","), function() {
				var arr = this.split(":");
				if(arr.length == 2) {
					options.events[arr[0]] = arr[1];
				}
			});
		} else {
			options.events["click"] = "frdefaultClick";
			window["frdefaultClick"] = function() {
				console.log(this);
				console.log("frdefaultClick");
			};
		}
		return options;
	}
	
	function renderAnalyticsRibbon(cont, res, options) { //options={"q"="...", "fq"="...", "id"="..."}
		cont.html("");
		var totalWidth = 0, graphCount = 0;
		var charts = false;
		
		if(res && res.result && res.result.graphs) {
			var graphs = res.result.graphs;
			
			for(var chartType in graphs) {
				var graph = graphs[chartType];
				if(!graph.nodes || graph.nodes.length == 0 || $.inArray(chartType, options.chTypes) == -1) {
					continue;
				}
				graphCount++;
				if(!charts) {
					charts = true;
				}
				if(chartType == "TREE_MONITOR_SEARCH" || chartType == "TREE_COMPANY" || chartType == "TREE_TOPICS") {
					totalWidth += options.width + 10 /*margin*/;
					frVisual1.buildTreemapGraph(cont, graph, options);
				} else if (chartType == "GEO_WORLD" || chartType == "GEO_US") {
					totalWidth += options.width + 10 /*margin*/;
					frVisual1.buildGeoGraph(cont, graph, options);
				} else if (chartType == "ACC_METER") {
					//var w = frVisual1.buildAccelerometerGraph(cont, graph, options);
					var w = frVisual1.buildAccelerometerGraphForEntity(cont, graph, options);
					totalWidth += w + 10 /*margin*/;
				} else {
					throw ("unsupported chart type: " + chartType);
				}
			}
		}
		if(!charts) {
			var name = "";
			if(graph.chartType == 'TREE_MONITOR_SEARCH') {
				name = "MONITOR ACTIVITY TRENDS";
			} else if(graph.chartType == 'TREE_COMPANY') {
				name = "BUSINESS INFLUENCERS";
			} else if(graph.chartType == 'TREE_TOPICS') {
				name = "MARKET DRIVERS";
			} else if(graph.chartType == 'GEO_WORLD') {
				name = "REGIONAL LENS: GLOBAL";
			} else if(graph.chartType == 'GEO_US') {
				name = "REGIONAL LENS: U.S.";
			} else if (chartType == "ACC_METER") {
				name = "Business Twitter Trends";
			}
			var html = "<div class='ch-lbl'>"+name+"</div>";
			if(options.isRibbon) {
				cont.css("height", 50);
				html = "";
			} else {
				cont.css("position", "relative");
			}
			html += "<div class='ch-msg'>There are no recent analytics to show.</div>";
			cont.append(html);
			if (chartType == "ACC_METER") {
				cont.css("width", cont.css('min-width'));
			} else {
				cont.css("width", cont.width() - 20);
			}
		} else {
			cont.css("width", totalWidth + 5);
		}
	};
	

})(); // We call our anonymous function immediately