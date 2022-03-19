/**
 * options set of key/value pairs to configure the scroll.
 * toLoadSectionSelector
 * 	default- ".jq_toload", dom selectors relative to container for which we have to load data on scroll.
 *  section must have id and url attribute e.g. <div id="data1" url="data1.jsp?id=1" class="jq_toload"></div> 
 * 
 * load
 * 	default- "visible", load data only for visible slides. "all"- load data for all slide. "none" - if data already loaded  
 * 
 * scrollOffsetToLoadNext,
 *  default- 80, offset when it will load next section when user scroll down  
 * 
 * scrollContainerSelector
 *  default- ".jq_vscroll_container", will use to find total scroll height
 *  
 * loadDataCallback
 * 	default- null, must be provided (if load != 'none')- slides will load data using this callback(url, container_id) return jqXHR
 * 
 * loadingImgCallback
 * 	default- Loading... , slides to show loading image/text callback(container_id)
 * 
 * zoom
 *  default- true, set zoom as false if need to disable zoom-in zoom-out on touch devices
 * 
 */

(function( $ ) {
	
	var defaults = {
		toLoadSectionSelector: ".jq_toload",
		load: "visible", /*all, none*/
		scrollOffsetToLoadNext: 80,
		scrollContainerSelector: ".jq_vscroll_container",
		loadDataCallback: null,
		onScrollMoveFunc: null,
		zoom: true,
		loadingImgCallback: function(id){ $("#" + id).html("Loading ...");}
	};
	
	$.fn.frvscroll = function( options ) {

		return this.each(function() {
			var _this = $(this);
			options = $.extend({}, defaults, options);
			if(options.load != 'none' && !options.loadDataCallback) {
				throw "loadDataCallback method is not defined";
			}
			new ScrollerVer(_this, options);
		});
	};
	
	function ScrollerVer(root, options) {
		var self = this;
		self.inProgress = false;
		self.touch = 'ontouchstart' in document.documentElement;
		self.root = root;
		self.options = options;
		
		self.toLoadArr = [];
		if(self.options.load != 'none') {
		  	self.root.find(self.options.toLoadSectionSelector).each(function() {
				var _this = $(this);
				self.toLoadArr.push({"id": _this.attr("id"), "url": _this.attr("url")});
			});
		}
      	if(self.touch) {
      		touchScroll();
      	} else {
      		mouseKeyboardScroll();
      	}
      	if(self.options.load == 'all') {
      		for(var i = 0; i < self.toLoadArr.length; i++) {
      			var o = self.toLoadArr[i];
      			self.options.loadingImgCallback(o.id);
				self.options.loadDataCallback(o.url, o.id);
      		}
      	} else {
      		if(self.toLoadArr.length > 0) {
      			self.options.loadingImgCallback( self.toLoadArr[0].id );
      		}
      		loadNextIfRequired();
      	}
      	
      	function touchScroll() {
      		var iscroll = new iScroll(self.root[0],  {hScroll: false, hScrollbar: false, vScroll: true, vScrollbar: true, checkDOMChanges: true,
         		onScrollEnd: (self.options.load == 'visible' ? onScrollEnd : null), onScrollMove: self.options.onScrollMoveFunc, useTransform: true, zoom: self.options.zoom,
    			onBeforeScrollStart: function (e) {
					var target = e.target;
					while (target.nodeType != 1) { 
						target = target.parentNode; 
					}
					if (target.tagName != 'SELECT' && target.tagName != 'INPUT' && target.tagName != 'TEXTAREA') {
						e.preventDefault();
					}
				}
      		});
      		self.root.data("iscroll", iscroll);
      	}
      	
      	function mouseKeyboardScroll() {
      		self.root.mCustomScrollbar({
	            horizontalScroll: false,
	            scrollInertia: 0,
	            callbacks:{
					onTotalScroll: onScroll,
					onTotalScrollOffset: self.options.scrollOffsetToLoadNext,
					whileScrolling: self.options.onScrollMoveFunc
				},
				advanced:{
				    updateOnBrowserResize:true, 
				    updateOnContentResize:true 
				}
	        });
      	}
      	
      	function onScrollEnd () {
 			var _this = this;
			if( (_this.y - self.options.scrollOffsetToLoadNext) < _this.maxScrollY) {
				loadData();
			}
 		}
      	
      	//can be case if there are no content or less content in slide compare to visible window
      	function loadNextIfRequired() {
      		if (self.toLoadArr.length > 0) {
      			var contHeight = self.root.find(self.options.scrollContainerSelector).height();
      			var toLoadSlideHeight = $("#" + self.toLoadArr[0].id).height();
      			if(contHeight && contHeight > 0 && (contHeight - toLoadSlideHeight) <= self.root.height() ) {
      				loadData();
      			}
  			}
      	}
      	
      	//for clicktracking
      	function onScroll() {
      		self.root.vscroll = 'v';
      		loadData();
      	}
      	
      	function loadData() {
      		if(self.inProgress || self.options.load != 'visible' || self.toLoadArr.length == 0) {
      			return;
      		}
      		self.inProgress = true; 
      		var section = self.toLoadArr[0];
      		self.toLoadArr.splice(0, 1); //remove first section
      		var jqXhr = self.options.loadDataCallback(section.url, section.id, self.root.vscroll);
      		if(self.toLoadArr.length > 0) {
      			if(jqXhr) {
      				var fn = function() {
      					self.inProgress = false;
      					//put loading sign for advance slide if there
      	  				self.options.loadingImgCallback( self.toLoadArr[0].id );
      					loadNextIfRequired();
      				};
      				jqXhr.complete(fn);
      			} else {
      				self.inProgress = false;
      				//put loading sign for advance slide if there
      				self.options.loadingImgCallback( self.toLoadArr[0].id );
      			}
      		}
      	}
	}
	
})( FREnv.$ );
