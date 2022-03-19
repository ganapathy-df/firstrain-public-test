
$.fn.getCDATA = function() {
	if($.browser.msie)
		return this[0].childNodes[0].nodeValue;

	// Other browsers do this
	return this[0].childNodes[1].nodeValue;
};

(function($) {
	$.frchart = function(placeholder,dataSource,width,height,options,isxml) {
		if(placeholder==null || dataSource==null || width==null || height==null) {
			alert('frchart error: required parameters not set');
			return false;
		}
		var _default_options = {
			showVolume:true,
			showPrice:true,
			showSecFiling:true,
			showPriceChange:true,
			showNewsEvents:true,
			showManagementChange:true,
			showLegend:true,
			allowHover:true,
			allowClick:true
		};
		var _options = {};
		$.extend(_options,_default_options,options);
		if(_options.showVolume==false && _options.showPrice==false) {
			alert('frchart error: You must show at least one data series of "Price" or "Volume"');
			return false;
		}
		
		var plot;
		var data_price = [];
		var data_price_high = 0;
		var data_price_low = 100000000000;
		var data_price_max;
		var data_price_min;
		var data_volume = [];
		var data_volume_high = 0;
		var data_volume_max;
		var data_sec_filing = [];
		var data_sec_filing_info = {};
		var data_price_change = [];
		var data_price_change_info = {};
		var data_news_event = [];
		var data_news_event_info = {};
		var data_management_change = [];
		var data_management_change_info = {};
		var month_names = ["January","February","March","April","May","June","July","August","September","October","November","December"];

		var legendWidth = 110;
		var chartWidth = _options.showLegend?width-legendWidth:width;
		var chartHeight = height;
		
		var priceLabelPositionX =($.browser.msie)? 
			_options.showVolume?chartWidth:chartWidth-13  :
			_options.showVolume?chartWidth-10:chartWidth-20;
		
		var priceLabelPositionY =($.browser.msie)?(height/2)-20:(height/2)-10;
		var volumeLabelPositionX = 0;
		var volumeLabelPositionY = ($.browser.msie)?(height/2)-30:(height/2)+10;
		var yAxisRHSLabels = [];
		
		var flot_chart_id = placeholder+'_flotchart';
		if(isxml) {
			processXML();
		} else {
			//Load up the XML and create the chart
			loadXML();
		}
		
		
		function loadXML() {
			$.ajax({
				type:"GET",
				url:dataSource,
				dataType: "xml",
				success: function(res) {
					var chartdata = $(res);
					if(_options.showPrice) {
						_options.showPrice = chartdata.find("lineSet[seriesname='Price']").length > 0;
					}
					generateBaseHtml(placeholder, flot_chart_id, _options);
					parseXML(chartdata);
				}
			});
		}
		
		function processXML() {
			var chartdata = $(dataSource);
			if(_options.showPrice) {
				_options.showPrice = chartdata.find("lineSet[seriesname='Price']").length > 0;
			}
			generateBaseHtml(placeholder, flot_chart_id, _options);
			parseXML(chartdata);
		}
		
		function generateBaseHtml(placeholder, flot_chart_id, _options) {
			var html = '';
			if(_options.showVolume)
				html += '<div id="web_volume_label">Web Volume</div>';
			html += '<div id="'+flot_chart_id+'" class="flot_chart" style="width:'+chartWidth+'px;height:'+chartHeight+'px"></div>';
			if(_options.showPrice)
				html+='<div id="price_label" class="jq-price-label" style="left:'+priceLabelPositionX+'px;">Price</div>';
			if(_options.showLegend) {
				html += '<div id="legend"><h6>LEGEND</h6><ul>';
				if(_options.showPrice)
					html += '<li><img id="daily" src="/images/spacer.gif" alt=""><span></span> Daily Closing Price</li>';
				if(_options.showSecFiling)
					html += '<li><img id="sec" src="/images/spacer.gif" alt=""><span></span> SEC Filing</li>';
				if(_options.showPriceChange)
					html += '<li><img id="dapriceily" src="/images/spacer.gif" alt=""><span></span> Price Change</li>';
				if(_options.showNewsEvents)
					html += '<li><img id="news" src="/images/spacer.gif" alt=""><span></span> News Event</li>';
				if(_options.showManagementChange)
					html += '<li><img id="management" src="/images/spacer.gif" alt=""><span></span> Management Change</li>';
				html += '</ul></div>';
			};
			//populate the placeholder with the html 
			$('#'+placeholder).html(html);	
			$('#'+placeholder).addClass('frchart_wrap');
		}
		
		function parseXML(chartdata) {
			//get date data
			var dates = [];
			var date_ref = {};
			chartdata.find('categories').each(function(){
				$(this).find('category').each(function(i){
					var date_string = $(this).attr('label');
					var date_stamp = Date.parse(date_string);
					dates.push(date_stamp);
					date_ref[date_stamp]=i;
				});
			});
			
			//get volume data
			if(_options.showVolume) {
				chartdata.find('dataSet').each(function(){
					series = $(this).attr('seriesName');
					if(series=='all') {
						$(this).find('set').each(function(i){
							var val = parseFloat($(this).attr('value'));
							data_volume_high = val>data_volume_high?val:data_volume_high;
							data_volume.push([dates[i],$(this).attr('value')]);	
						});
						data_volume_max = data_volume_high + (data_volume_high * .15);
					}//end if series==all
				});
			}

			//get price data
			if(_options.showPrice) {
				chartdata.find('lineSet').each(function(){
					series = $(this).attr('seriesname');
					if(series=='Price') {
						$(this).find('set').each(function(i){
							var val = parseFloat($(this).attr('value'));
							data_price_high = val>data_price_high?val:data_price_high;
							data_price_low = val<data_price_low?val:data_price_low;
							data_price.push([dates[i],val]);
						});
						data_price_max = Math.ceil(data_price_high + (data_price_high*.03));
						data_price_min = Math.floor(data_price_low - (data_price_low*.05));
					}
				});
			}

			//get event marker data
			chartdata.find('eventSet').each(function(){
				var series = $(this).attr('seriesname');
				//Management Change
				if(_options.showManagementChange) {
					if(series=='Management Change') {
						$(this).find('set').each(function(i){
							date_string = $(this).attr('date');
							year = date_string.substr(0,4);
							month_num = parseInt(date_string.substr(4,2), 10);	
							month_string = month_names[month_num-1];
							day = date_string.substr(6,2);
							date_string_formatted = month_string+" "+day+", "+year;	
							date_stamp = Date.parse(date_string_formatted);
							date_i = date_ref[date_stamp];
							if(_options.showVolume)
								y = data_volume[date_i][1];
							else
								y = data_price[date_i][1];
							y = fixScaleForWebVolumeChart(y);
							data_management_change.push([date_stamp,y]);

							var headline = $(this).attr('headline');
							var supplink = $(this).attr('supplink');
							var morelink = $(this).attr('morelink');
							data_management_change_info[date_stamp] = {};
							data_management_change_info[date_stamp].headline = headline;
							data_management_change_info[date_stamp].supplink = unescape(supplink.replace(/\+/g, " "));
							data_management_change_info[date_stamp].morelink = morelink;
						});
					}//end Management Change
				}

				//News Event
				if(_options.showNewsEvents) {
					if(series=='Web Event') {
						$(this).find('set').each(function(i){
							date_string = $(this).attr('date');
							year = date_string.substr(0,4);
							month_num = parseInt(date_string.substr(4,2), 10);	
							month_string = month_names[month_num-1];
							day = date_string.substr(6,2);
							date_string_formatted = month_string+" "+day+", "+year;	
							date_stamp = Date.parse(date_string_formatted);
							date_i = date_ref[date_stamp];
							if(_options.showVolume)
								y = data_volume[date_i][1];
							else
								y = data_price[date_i][1];
							y = fixScaleForWebVolumeChart(y);
							data_news_event.push([date_stamp,y]);

							var headline = $(this).attr('headline');
							var supplink = $(this).attr('supplink');
							var morelink = $(this).attr('morelink');
							data_news_event_info[date_stamp] = {};
							data_news_event_info[date_stamp].headline = headline;
							data_news_event_info[date_stamp].supplink = unescape(supplink.replace(/\+/g, " "));
							data_news_event_info[date_stamp].morelink = morelink;
						});
					}//end News Event
				}

				//SEC filing
				if(_options.showSecFiling) {
					if(series=='SEC Filing') {
						$(this).find('set').each(function(i){
							date_string = $(this).attr('date');
							year = date_string.substr(0,4);
							month_num = parseInt(date_string.substr(4,2), 10);	
							month_string = month_names[month_num-1];
							day = date_string.substr(6,2);
							date_string_formatted = month_string+" "+day+", "+year;	
							date_stamp = Date.parse(date_string_formatted);
							date_i = date_ref[date_stamp];
							if(_options.showVolume)
								y = data_volume[date_i][1];
							else
								y = data_price[date_i][1];
							y = fixScaleForWebVolumeChart(y);
							data_sec_filing.push([date_stamp,y]);

							var headline = $(this).attr('headline');
							var supplink = $(this).attr('supplink');
							var morelink = $(this).attr('morelink');
							data_sec_filing_info[date_stamp] = {};
							data_sec_filing_info[date_stamp].headline = headline;
							data_sec_filing_info[date_stamp].supplink = unescape(supplink.replace(/\+/g, " "));
							data_sec_filing_info[date_stamp].morelink = morelink;
						});
					}//end SEC Filing
				}

				//Price Change
				if(_options.showPriceChange) {
					if(series=='Stock Price') {
						$(this).find('set').each(function(i){
							date_string = $(this).attr('date');
							year = date_string.substr(0,4);
							month_num = parseInt(date_string.substr(4,2), 10);	
							month_string = month_names[month_num-1];
							day = date_string.substr(6,2);
							date_string_formatted = month_string+" "+day+", "+year;	
							date_stamp = Date.parse(date_string_formatted);
							date_i = date_ref[date_stamp];
							if(_options.showPrice) {
								if(data_price[date_i]) {
									y = data_price[date_i][1];
								} else {
									y = 0;
								}
								y = y+(y/100)*1.5;
								data_price_change.push([date_stamp,y]);
							} else {
								y = data_volume[date_i][1];
								y = fixScaleForWebVolumeChart(y);
								data_price_change.push([date_stamp,y]);
							}
							var headline = $(this).attr('headline');
							var supplink = $(this).attr('supplink');
							var morelink = $(this).attr('morelink');
							data_price_change_info[date_stamp] = {};
							data_price_change_info[date_stamp].headline = headline;
							data_price_change_info[date_stamp].supplink = unescape(supplink.replace(/\+/g, " "));
							data_price_change_info[date_stamp].morelink = morelink;
						});
					}//end Price Change
				}

			});//end chartdata loop
				
			load_chart();
		}//end function parseXML()

		////////////////////
		// Plot the Flot!
		///////////////////
		function load_chart() {
			//define the series
			var volume_axis = 1;
			var price_axis = 2;
			if(!_options.showVolume && _options.showPrice) {
				price_axis = 1;
			}
			var series_volume =  {
				data:data_volume,
				bars: { show:true, fillColor:"#A6CAE1",lineWidth:chartWidth*.0035},
				color:"#A6CAE1",
				label:"volume",
				yaxis:volume_axis
			};
			var series_sec_filing = {
				data:data_sec_filing,
				points: {show:true,symbol:"square",fillColor:"#7C3392",lineWidth:0,radius:4},
				color:"#7C3392",
				label:"sec_filing",
				yaxis:volume_axis
			};
			var series_price_change = {
				data:data_price_change,
				points: {show:true,symbol:"square",fillColor:"#D9B200",lineWidth:3},
				color:"#D9B200",
				label:"price_change",
				yaxis:price_axis
			};
			var series_news_events = {
				data:data_news_event,
				points: {show:true,symbol:"square",fillColor:"#0099CC",lineWidth:3},
				color:"#0099CC",
				label:"news_event",
				yaxis:volume_axis
			};
			var series_management_change = {
				data:data_management_change,
				points: {show:true,symbol:"square",fillColor:"#CD660B",lineWidth:3},
				color:"#CD660B",
				label:"management_change",
				yaxis:volume_axis
			};
			var series_price =  {
				data:data_price,
				points: { show:true,radius:1,lineWidth:.5},
				lines: { show:true,color:"#BD250E",lineWidth:2},
				color:"#BD250E",
				label:"price",
				yaxis:price_axis,
				shadowSize:0
			};

			var series = [];
			if(_options.showVolume)
				series.push(series_volume);
			if(_options.showSecFiling)
				series.push(series_sec_filing);
			if(_options.showPriceChange)
				series.push(series_price_change);
			if(_options.showNewsEvents)
				series.push(series_news_events);
			if(_options.showManagementChange)
				series.push(series_management_change);
			if(_options.showPrice)
				series.push(series_price);

			var label_margin = !_options.price?0:10;
				
			var plot_options = {
				grid: {
					borderWidth:2,
					borderColor:"#DDDDDD",
					hoverable: _options.allowHover,
					clickable: _options.allowClick,
					labelMargin:0,
					axisMargin:0,
					backgroundColor:"#ffffff",
					autoHighlight: false
				},
				xaxes:[{
					mode:"time",
					position:"bottom"
				}],
				legend:{
					show:false
				}
			};

			var volume_axis_options = {
				min:0,
				max:data_volume_max,
				ticks:0,
				position:"left"
			};
			var price_axis_options = {
				min:data_price_min,
				max:data_price_max,
				ticks:[data_price_min,data_price_max],
				tickFormatter:dollarFormatter,
				position:"right"
			};
			if(_options.showVolume && _options.showPrice) {
				var yaxes = [];
				yaxes.push(volume_axis_options);
				yaxes.push(price_axis_options);
				plot_options.yaxes = yaxes;
			}else if(_options.showVolume) {
				plot_options.yaxis = volume_axis_options;
			}else if(_options.showPrice) {
				var yaxes=[];
				//yaxes.push(volume_axis_options);
				yaxes.push(price_axis_options);
				plot_options.yaxes = yaxes;
			}

			plot = $.plot($("#"+flot_chart_id), series, plot_options);
			chartRenderedCallback();
			if(_options.callback) {
				_options.callback.apply(null,[plot]);
			}

		}//end load_chart

		function dollarFormatter(v, axis) {
			var lbl = "$"+v;
			yAxisRHSLabels.push(lbl);
			return lbl;
		}

		//Tooltip
		function showTooltip(pos,item) {
			var pageX = item.pageX;
			var pageY = item.pageY;
			var content='';
			var top_offset = pageY - 60;
			var left_offset = pageX - 40;

			if(item.series.label == 'price') {
				var date_stamp = item.datapoint[0];
				var price = item.datapoint[1].toFixed(2);
				var d = new Date(date_stamp);
				var date_string = month_names[d.getMonth()]+' '+d.getDate()+', '+d.getFullYear();
				content = date_string+"<br/>Price: $"+price;	
				/*
				top_offset=pageY - 15;
				left_offset=pageX + 40;
				*/
			}//end if series==price

			if(item.series.label == 'management_change') {
				var date_stamp = item.datapoint[0];
				var d = new Date(date_stamp);
				var date_string = month_names[d.getMonth()]+' '+d.getDate()+', '+d.getFullYear();
				var headline = data_management_change_info[date_stamp].headline;
				content = date_string+"<br/>"+headline;	
				/*
				top_offset=headline.length>65?pageY-30:pageY - 15;
				left_offset=pageX + 40;
				*/
			}//end if series==Management Change

			if(item.series.label == 'news_event') {
				var date_stamp = item.datapoint[0];
				var d = new Date(date_stamp);
				var date_string = month_names[d.getMonth()]+' '+d.getDate()+', '+d.getFullYear();
				var headline = data_news_event_info[date_stamp].headline;
				content = date_string+"<br/>"+headline;	
				/*
				top_offset=headline.length>65?pageY-30:pageY - 15;
				left_offset=pageX + 40;
				*/
			}//end if series==news_event

			if(item.series.label == 'sec_filing') {
				var date_stamp = item.datapoint[0];
				var d = new Date(date_stamp);
				var date_string = month_names[d.getMonth()]+' '+d.getDate()+', '+d.getFullYear();
				var headline = data_sec_filing_info[date_stamp].headline;
				content = date_string+"<br/>"+headline;	
				/*
				top_offset=headline.length>65?pageY-30:pageY - 15;
				left_offset=pageX + 40;
				*/
			}//end if series==sec_filing

			if(item.series.label == 'price_change') {
				var date_stamp = item.datapoint[0];
				var d = new Date(date_stamp);
				var date_string = month_names[d.getMonth()]+' '+d.getDate()+', '+d.getFullYear();
				var headline = data_price_change_info[date_stamp].headline;
				content = date_string+"<br/>"+headline;	
				/*
				top_offset=headline.length>65?pageY-50:pageY - 15;
				left_offset=pageX + 40;
				*/
			}//end if series==price_change

			var ttDiv = $('<div id="frchart_tooltip" class="web-v-tltp">' + content + '</div>').appendTo("body");
			var ttLeft = ttDiv.outerWidth(true) + left_offset - $(window).width();
			if( ttLeft > 0 ) {
				left_offset -= ttLeft;
			}
			if( top_offset < 0 ) {
				//showToolTip in bottom
				top_offset = 80 + top_offset;
			}
			
			if( left_offset < 0 ) {
				left_offset = 0;
			}
			ttDiv.css({
				top: top_offset,
				left: left_offset
			}).fadeIn(200);

		}

		function fixScaleForWebVolumeChart(v) {
			//hack to show events upper than bottom line
			if(data_volume_max == 0) {
				return 0.06;
			}
			v = parseFloat(v);
			if(v > 0) {
				v += data_volume_max / 13;
			} else {
				v = data_volume_max / 20;
			}
			return v;
		}
		
		function chartRenderedCallback() {
			
			if(yAxisRHSLabels.length > 0) {
				var lbl = "";
				$.each(yAxisRHSLabels, function() {
					if( this.length > lbl.length ) {
						lbl = this;
					}
				});
				if(lbl.length <= 3 || lbl.length <= 4 && lbl.indexOf(".") != -1) {
					var pl = $('#'+placeholder).find(".jq-price-label");
					var _left = parseInt( pl.css("left") );
					pl.css("left", _left + 5);
				}
			}
			//Hover Hook
			if(_options.allowHover) {
				var previousPoint = null;
				$("#"+flot_chart_id).bind("plothover", function (event, pos, item) {
					var _this = $(this);
						if (item) {
							_this.css("cursor","pointer","important");
							if (previousPoint != item.dataIndex) {
								previousPoint = item.dataIndex;
								$("#frchart_tooltip").remove();
	
								//showTooltip(item.pageX, item.pageY, item.series.label + " of " + x + " = " + y,item.series);
								//showTooltip(item.pageX, item.pageY,content,item.series);
								showTooltip(pos,item);
								
								if(typeof isTouchDevice != 'undefined' && isTouchDevice()) {
									setTimeout(function() {
										$("#frchart_tooltip").remove();
									}, 3000);
								}
							}
						}
						else {
							_this.css("cursor","default","important");
								$("#frchart_tooltip").remove();
								previousPoint = null;            
						}
				});
			}
	
			//Click Hook
			if(_options.allowClick) {
				$("#"+flot_chart_id).bind("plotclick", function (event, pos, item) {
					if (item) {
						//alert("You clicked point " + item.dataIndex + " in " + item.series.label + ".");
						if(item.series.label == 'management_change') {
							var date_stamp = item.datapoint[0];
							var link = data_management_change_info[date_stamp].supplink;
						}
						if(item.series.label == 'news_event') {
							var date_stamp = item.datapoint[0];
							var link = data_news_event_info[date_stamp].supplink;
						}
						if(item.series.label == 'sec_filing') {
							var date_stamp = item.datapoint[0];
							var link = data_sec_filing_info[date_stamp].supplink;
						}
						if(item.series.label == 'price_change') {
							var date_stamp = item.datapoint[0];
							var link = data_price_change_info[date_stamp].supplink;
						}
	
						if(link) {
							var newWindow = window.open(link,'_blank');	
							newWindow.focus();
							return false;
						}
					}
				});
			}
		}

	}//end frchart()

})(jQuery);


function randomFromTo(from, to){
	return Math.floor(Math.random() * (to - from + 1) + from);
}