function FRVisual1($) {

	var addGraphLabel = function  (graph, label) {
	    graph.wrap("<div class='ch'></div>")
	    .parent().prepend("<div class='ch-lbl'>"+label+"</div>");
	};

	FRVisual1.prototype.buildTreemapGraph =  function (cont, graph, options) {
		
		var chartId = graph.chartId + "_" + graph.chartType + "_" + options.index;
		var graphDom = $("<div class='"+ graph.chartType +"'></div>").attr("id", chartId).css( {"width": options.width, "height": options.height} );
		graphDom.appendTo(cont);
		var name = "Tree map";
		if(graph.chartType == 'TREE_MONITOR_SEARCH') {
			name = "MONITOR ACTIVITY TRENDS";
		} else if(graph.chartType == 'TREE_COMPANY') {
			name = "BUSINESS INFLUENCERS";
		} else if(graph.chartType == 'TREE_TOPICS') {
			name = "MARKET DRIVERS";
		} else if(graph.chartType == 'TREE_COMPANY_GESOCIAL') {
			name = "";
		}
		addGraphLabel(graphDom, name);
		var nodes = graph.nodes;
		var totalSize = 0;
		for(var i = 0; i < nodes.length; i++) {
			totalSize += nodes[i].value;
		}
		var assignUniqueIdAndPartToNodes = function(nodes, nodesToOrder) {
			for(var i = 0; i < nodes.length; i++) {
				var node = nodes[i];
				node.id = chartId + "_" + node.id ; //make id unique across tree
				var part = (node.value / totalSize) * 100;
				node.part = Math.round(part * 100) / 100; //round part to two decimal places
				node.header = name;
				if(node.subtree) {
					assignUniqueIdAndPartToNodes(node.subtree, nodesToOrder);
				} else {
					nodesToOrder.push(node);
				}
			}
		};
		var nodesToOrder = [];
		assignUniqueIdAndPartToNodes(nodes, nodesToOrder);
		nodesToOrder.sort(function(o1, o2) {return -(o1.value - o2.value);});
		var colorRanges = [90, 100, 124, 149, 199, 399];
		if (options.changedColorRanges) {
			colorRanges = options.changedColorRanges;
		} 
		var fonts = [{s: 11, w: "normal"}, {s: 11, w: "bold"}, {s: 13, w: "bold"}, {s: 15, w: "bold"}, {s: 18, w: "bold"}];
		if(options.changedFonts) {
			fonts = options.changedFonts; 
		}
		var mapOptions = {
			sync: true,	
			fonts: fonts,
			nodeClass: function(node) {
				for(var j = 0; j < colorRanges.length; j++) {
					if(node.intensity <= colorRanges[j]) {
						return "jq_treemap treemapNode" + j; 
					}
				}
				return "jq_treemap treemapNode" + colorRanges.length;
			},
			ready: function() {
				$(".treemap-label").parent().bind("click", function() {
					var _this = $(this); 
					_this.addClass("treeNodeHover");
				});
				$(".treemap-label").parent().bind("click", function() {
					$(".treemap-node").removeClass('treeNodeHover');
				});  
			}
		};
		$('#' + chartId).treemap(nodes, mapOptions);
		
		var chNodes = $('#' + chartId).find(".jq_treemap");
		$.each(options.events, function( key, value ) {
			chNodes.on(key, function(event) {
				var id = $(this).attr("id");
				var node;
				$.each(nodesToOrder, function() {
					if(this.id == id) {
						node = this;
						return;
					}
				});
				if(node && window[value]) {
					window[value].call(this, event, node);
				}
			});
		});
		
	};

	FRVisual1.prototype.buildGeoGraph = function (cont, graph, options) {
		
		var chartId = graph.chartId + "_" + graph.chartType + "_" + options.index;
		var graphDom = $("<div class=''></div>").attr("id", chartId).css( {"width": options.width, "height": options.height} );
		graphDom.appendTo(cont);
		var name = "REGIONAL LENS: GLOBAL";
		if(graph.chartType == 'GEO_US') {
			name = "REGIONAL LENS: U.S.";
		}
		
		addGraphLabel(graphDom, name);
		var jsonData = graph.nodes;
		var colors = {}, ccVsNode = {}, ccVsCount = {};
		var rangesVsColor = [{"r":30, "c":"#b6d6ff"}, {"r":99, "c":"#2E93DB"}, {"r":499, "c":"#83b3f1"}, {"r":999, "c":"#2E93DB"}, {"r":Number.MAX_VALUE, "c":"#005ace"}];
		$.each(jsonData, function() {
			var cc = this.cc;
			ccVsNode[cc] = this;
			if (this.value > 0) {
				ccVsCount[cc] = this.value;
				ccVsCount[cc+"_1"] = name;
				for(var j = 0; j < rangesVsColor.length; j++) {
					if(this.value <= rangesVsColor[j].r) {
						colors[cc] = rangesVsColor[j].c;
						break;
					}
				}
		    }
		});
		
		var mapOptions = {
		    values: ccVsCount,
		    colors: colors,
		    color: '#D5CFC3',
		    normalizeFunction: 'polynomial',
		    hoverOpacity: 0.7,
		    hoverColor: '#b6d6ff',
		    
		    onLabelShow: function(e, label, code){
		    	e.preventDefault();
		    },
		    onRegionOver: function(e, code){
		    	e.preventDefault();
		    },
		    onRegionOut: function(e, code){
		    	e.preventDefault();
		    },
		     onRegionClick: function(e, code){
		    	e.preventDefault();
		    }
		};
		if(graph.chartType == "GEO_US") {
			mapOptions.map = 'usa_en';
		}
		try {
			$('#' + chartId).vectorMap(mapOptions);
			var mapObject = $('div#' + chartId).data("mapObject");
			var pathId = "jvectormap" + mapObject.index +"_";
			for (var cc in ccVsCount) {
				$("#"+ pathId + cc).attr("fill", colors[cc]);
			}
		} catch(e) { 
			//alert(e);
		}
		
		var chNodes = $('#' + chartId).find("path");
		if(chNodes.length == 0) {
			chNodes = $('#' + chartId).find("shape");
		}
		$.each(options.events, function( key, value ) {
			chNodes.on(key, function(event) {
				var cc = this.id.split("_")[1];//jvectormap2_tx
				var node = ccVsNode[cc];
				
				if(node && node.value > 0 && window[value]) {
					window[value].call(this, event, node);
				}
			});
		});
		
	};

	FRVisual1.prototype.buildAccelerometerGraphForEntity = function (cont, graph, options) {
		var chartId = graph.chartId + "_" + graph.chartType;
		var graphDom = $("<div class=''></div>").attr("id", chartId).css( {"height": options.height, "background": "#FFFFFF"} );
		graphDom.appendTo(cont);
		var name = "Business Twitter Trends";
		addGraphLabel(graphDom, name);
		
		var target = 'target=\"Firstrain_window\"';
		if(options.data.target) {
			target = 'target=\"' + options.data.target + '\"';
		}
		var jsonData = graph.nodes;
		var singleEntity = jsonData.length == 1;
		if(singleEntity) {
			var node = jsonData[0]; 
			var html = '<div class="acl-ch" style="height:' + options.height + 'px;">'+ 
				'<div class="acl-ch-img">'+
					'<img _id="'+ node.id +'" class="jq-b-a-tt" src="'+options.imgBaseURL+node.imageName+'" width="83" />'+
				'</div>'+
				'<div class="hidden">'+
					'<div class="tltp-hd">' + node.name + '</div>' +
					'<div class="tltp-txt"><div class="lightGrey">Web Volume:</div>' + node.smartText +
						'<div class="cl"></div><a '+ target +' act="go.click" itemid="'+node.searchToken+'" class="tltp-btn jq_clicktrack jq_anchor_add_code" href="redirect.do?rt=portal.twitter.page&opt=1&q='+node.searchToken+'">'+
							'Go &raquo;'+
							'</a><div class="cl"></div>'+
					'</div>'+
				'</div>'+
				'<div class="acl-lbl">'+node.label+'</div>'+
				'<div class="acl-ch-txtn"><div class="lightGrey">Web Volume:</div>'+node.smartText+'</div>'+
			'</div>';
			graphDom.append(html);
		} else {
			//case of 6 gauges is broken because it's taking wrong outer width while rendering
			if(jsonData.length == 6) {
				graphDom.css( {"width": 338} );
			}
			
			var html = '<div class="fl">';
			$.each(jsonData, function(i, node) {
				html +=
					'<div class="acl-ch-t" style="width:'+(jsonData.length == 2 ? 155 : 110)+'px;">'+
					'<div class="acl-img"><img _id="'+ node.id +'" class="jq-b-a-tt" src="'+options.imgBaseURL+node.imageName+'" width="83" /></div>'+
					'<div class="hidden">'+
						'<div class="tltp-hd">' + node.name + '</div>' +
						'<div class="tltp-txt"><div class="lightGrey">Web Volume:</div>' + node.smartText +
							'<div class="cl"></div><a '+ target +' act="go.click" itemid="'+node.searchToken+'" class="tltp-btn jq_clicktrack jq_anchor_add_code" href="redirect.do?rt=portal.twitter.page&opt=1&q='+node.searchToken+'">'+						
								'Go &raquo;'+
								'</a><div class="cl"></div>'+
						'</div>'+
					'</div>'+
					'<div class="acl-lbl">'+node.label+'</div>'+
				'</div>';
				if( (i + 1) % 2 == 0) {
					html += '</div><div class="fl">';
				}
			});
			html += "</div>";
			graphDom.append(html);
		}
		
		var chNodes = $('#' + chartId).find(".jq-b-a-tt");
		$.each(options.events, function( key, value ) {
			chNodes.on(key, function(event) {
				var id = $(this).attr("_id");
				var node;
				$.each(jsonData, function() {
					if(this.id == id) {
						node = this;
						return;
					}
				});
				if(node && window[value]) {
					window[value].call(this, event, node);
				}
			});
		});
		
		return graphDom.outerWidth(true);
	}

}