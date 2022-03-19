if(typeof FREnv == 'undefined') {
	FREnv = {};
}
(function() {

	//Localize jQuery variable
	var $, envConf, servicePath;
	
	var reqDefaults = {"dim": "850x115"};
	
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
		$.migrateMute = true;
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
		$.migrateMute = true;
		// Call our main function
		main(); 
	}

	/******** Our main function ********/
	function main() { 
		$(function() {
			$(".jq-fr-wvchart").each(function(index) {
				var scriptTag = $(this);
				if("true" != scriptTag.attr("rendered")) {
					scriptTag.attr("rendered", "true");
					var _src = scriptTag.attr("src");
					var paramMap = urlToParamsMap(_src);
					//console.log( paramMap );
					paramMap = $.extend({}, reqDefaults, paramMap);
					servicePath = paramMap.sp || _src.substring(0, _src.indexOf("/js/")) 
					
					$.ajaxSetup({
						beforeSend: function(xhrObj) {
						 	xhrObj.setRequestHeader("Accept","application/json");
						 	xhrObj.setRequestHeader("frUserId",paramMap.frUserId);
						 	xhrObj.setRequestHeader("authKey",paramMap.authKey);
						}
					});
					
					var dims = paramMap.dim.split("x");
					//if config already loaded
					if(FREnv.imgcssurl) {
						envConf = {};
						envConf.imgCssURL = FREnv.imgcssurl;
						envConf.appName = FREnv.appname;
						envConf.version = FREnv.version;
						envConf.jsURL = FREnv.jsurl;
						
						processChart(paramMap, dims, scriptTag);
					} else {
						$.ajax({
							url: servicePath + "/widget/config",
							data: {"authInReqParam": true, "authKey": paramMap.authKey, "frUserId": paramMap.frUserId},
							dataType: "jsonp",
							cache: false,
							success: function(res) {
								if(res.status == "SUCCESS") {
									envConf = res.result;
									processChart(paramMap, dims, scriptTag);
								} else {
									throw "Error while loading environment configurations";
								}
							}
						});
					}
				}
			});
		});
		
		function processChart(paramMap, dims, scriptTag) {
			$('.jq-wvc-spnr').remove();
			
			var width = parseInt(dims[0]);
			var height = parseInt(dims[1]);
			
			var cid = paramMap.cid || ("chart-c-0");
			var html = '<div class="fr-chart" id="'+cid+'">'
                + '<div class="fr-spnr" style="line-height: '+height+'px; width: '+(width+5)+'px;">'
                + '<img class="fr-spinner" src="'+envConf.imgCssURL+'/images/spacer.gif"> Loading</div></div>'
            + '<div class="fr-web-lgnd">'
                + '<span><img src="'+envConf.imgCssURL+'/images/spacer.gif" class="fr-web-news"> News Event</span>'
            + '<span><img src="'+envConf.imgCssURL+'/images/spacer.gif" class="fr-web-management"> Management Change</span>'
            + '</div><div class="fr-cl"></div>';

			$(scriptTag).after(html);
			
			webVolumeChart(cid, width, height, 0, paramMap);
		}
		
		function webVolumeChart(domId, width, height, searchId, paramMap, companyId) {
			var obj;
			if(companyId)  {
				obj = {"n": "companyId", "v": companyId};
			} else if (searchId) {
				obj = {"n": "searchId", "v": searchId};
			} else {
				obj = {"n": "searchToken", "v": paramMap.id};
			}
			var file = FREnv.debug == "true" ? "jquery-migrate-1.1.1" : "jquery-migrate-1.1.1.min";
			includeJS(file, null);
			
			if(navigator.appVersion.indexOf("MSIE 7") !=-1 || navigator.appVersion.indexOf("MSIE 8") != -1){
				file = FREnv.debug == "true" ? "excanvas" : "excanvas.min";
				includeJS(file, null);
			}
			
			file = FREnv.debug == "true" ? "webvolume" : "webvolume.min";
			includeJS(file, function() {
				var options = {
					showVolume:true,
					showPrice:false,
					showSecFiling:false,
					showPriceChange:false,
					showNewsEvents:true,
					showManagementChange:true,
					showLegend:false,
					allowHover:true,
					allowClick:true,
					callback:function(plot) {
						//frchart=plot;
					}
				};
				loadXMLData(domId, width, height, searchId, paramMap, companyId, options);
			});
		}
		
		function loadXMLData(domId, width, height, searchId, paramMap, companyId, options) {
			
			//$("#" + domId).removeClass("chart").addClass("charth5");
			$.ajax({
				 beforeSend: function(xhrObj){
					xhrObj.setRequestHeader("frUserId",paramMap.frUserId);
				 	xhrObj.setRequestHeader("authKey",paramMap.authKey);
				 },
				url: servicePath + "/chart/wvchartdata?q="+paramMap.id,
				type: "GET",
				dataType: "xml",
				cache: false,
				success: function(data) {
					if(data) {
						$.frchart(domId, data, width, height, options, true);
					}
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
		
		function includeJS(file, callback) {
			$.ajax({
				url: envConf.jsURL + "/js/" + envConf.appName + "/" + envConf.version + "/" + file + ".js",
				cache: true,
				crossDomain: true,
				dataType: "script" //dataType: "script" works for cross domain also
			}).done(function() {
				  if(callback) {
					  callback();
				  }
			});
		}
	}
})(); // We call our anonymous function immediately