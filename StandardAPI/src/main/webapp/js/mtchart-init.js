if(typeof FREnv == 'undefined') {
	FREnv = {};
}
(function() {

	//Localize jQuery variable
	var $, envConf, servicePath;
	
	var reqDefaults = {"dim": "200x90"};
	
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
			$(".jq-fr-mtchart").each(function(index) {
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
			var cid = paramMap.cid || ("jq-mc-canvas");
			var html = '<canvas id="'+cid+'" width="'+width+'" height="'+height+'">[Please wait...]</canvas><div class="fr-mgmtchrt-lgnd">'
          		+ '<div class="fr-keylabel"><img class="fr-key-hire" src="'+envConf.imgCssURL+'/images/spacer.gif"> hire </div>'
          		+ '<div class="fr-keylabel"><img class="fr-key-depart" src="'+envConf.imgCssURL+'/images/spacer.gif"> departure </div>'
          		+ '<div class="fr-keylabel"><img class="fr-key-intmoves" src="'+envConf.imgCssURL+'/images/spacer.gif"> internal move </div></div>';
			
			$(scriptTag).after(html);
			
			turnoverChart(cid, width, height, 0, paramMap.id);
		}
		
		function turnoverChart(domId, width, height, searchId, searchToken, companyId) {
			var _width = $("#"+domId).parent().width() - 30;
			if(_width > width) {
				_width = Math.min(_width, height * 3.5);
				$("#jq-mc-canvas").attr("width", _width)
			}
			var file = FREnv.debug == "true" ? "excanvas" : "excanvas.min";
			if(navigator.appVersion.indexOf("MSIE 7") !=-1 || navigator.appVersion.indexOf("MSIE 8") != -1){
				includeJS(file, null);
			}
			
			file = FREnv.debug == "true" ? "rgraph" : "rgraph.min";
			includeJS(file, function() {
				loadMTChartData(domId, searchToken);
			});
		}
		
		function loadMTChartData(domId, searchToken) {
			
			//$("#" + domId).removeClass("chart").addClass("charth5");
			$.ajax({
				url: servicePath + "/chart/mtchartdata?q="+searchToken,
				type: "GET",
				dataType: "json",
				cache: false,
				success: function(data) {
					if(data.status == "SUCCESS") {
						var options = {"showToolTip": true};
						var mgmtChartObj = data.result;
						renderMgmtRGraphChart(domId, JSON.parse(mgmtChartObj.valuesAsString), JSON.parse(mgmtChartObj.labelAsString), mgmtChartObj.max, options);
					}
				}
			});
		}
		
		/*
		 * options set of key/value pairs to configure the management chart RGraph.
		 * showtooltip
		 * 	default- false, if required then pass showtooltip:true
		 */
		function renderMgmtRGraphChart (id, data, labels, max, options) {
			var showTooltip = function(index) {
				var i1 = Math.floor( index / 3 );
				var i2 = index % 3;
				var d = Math.abs(data[i1][i2]);
				if(d == 0) {
					return null;
				}
				var msg = d;
				if(i2 == 0) {
					msg += " Departures";
				} else if(i2 == 1) {
					msg += " Hires";
				} else {
					msg += " Internal Moves";
				}
				return msg;
			};
			
			//increment max by 1
			max = max + 1;
		    
			if (typeof(G_vmlCanvasManager) != 'undefined') {
				G_vmlCanvasManager.initElement( $("#"+ id)[0] );
			}
		    // Create the br chart. The arguments are the ID of the canvas tag and the data
		    var bar = new RGraph.Bar(id, data)
		        // Now configure the chart to appear as wanted by using the .Set() method.
		        // All available properties are listed below.
		        .Set('chart.labels', labels)
		       	.Set('chart.gutter.top', 8)
		       	.Set('chart.gutter.right', 25)
		       	.Set('chart.gutter.bottom', 16)
		       	.Set('chart.gutter.left', 2)
		        //.Set('chart.background.barcolor1', '#333')
		        //.Set('chart.background.barcolor2', '#3ff')
		        .Set('chart.background.grid', true)
		        .Set('chart.background.grid.vlines', true)
		        .Set('chart.background.grid.hlines', false)
		        .Set('chart.background.grid.autofit', true)
		        .Set('chart.background.grid.autofit.numvlines', 12)
		        .Set('chart.background.grid.autofit.numhlines', 0)
		        .Set('chart.background.grid.color', '#ddd')
		        //.Set('chart.background.color', '#FAFAFA')
		        .Set('chart.background.image', envConf.imgCssURL + '/images/chart_gradient.png')
		        
		        .Set('chart.shadow', false)
		        .Set('chart.numxticks', 0)
		    	.Set('chart.numyticks', 2)
		        
		        .Set('chart.ylabels', true)
		       	.Set('chart.ylabels.specific', [max, ''])
		       	.Set('chart.ylabels.count', 2)
		        .Set('chart.yaxispos', 'right')
		        .Set('chart.axis.color', '#ddd')
		        
		        .Set('chart.colors', ['#DD3333', '#77BB11', '#0066cc'])
		        .Set('chart.ymax', max)
		        //.Set('chart.ymin', 0)
		        .Set('chart.xaxispos', "center")
		        .Set('chart.noendxtick', true)
		        
		        .Set('chart.text.color', '#A4A4A4')
		        .Set('chart.text.size', 8)
		        .Set('chart.hmargin', 0)
		        .Set('chart.hmargin.grouped', 1)
		        
		        // Now call the .Draw() method to draw the chart
		        .Draw();
		    
		  //on-off tooltip
		    if (options.showToolTip) {
		    	bar.Set('chart.tooltips', showTooltip)
		        .Set('chart.tooltips.event', 'onmousemove')
		        .canvas.onmouseout = function (e) {
		            RGraph.HideTooltip();
		            RGraph.Redraw();
		        };
		    }
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