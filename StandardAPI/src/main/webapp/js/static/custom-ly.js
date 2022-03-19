(function($) {
	$(function() {
		FREnv.pageName = "entitybrieflayout";
		var frCommonUtils = new FRCommonUtils($);
		frCommonUtils.addProximaNova(document);
		frCommonUtils.initiateClickTracking();
		FREnv.frUserId = $("#jqi-env-frUserId").html();
		FREnv.authKey = $("#jqi-env-authKey").html();
		$.ajaxSetup({
			beforeSend: function(xhrObj) {
			 	xhrObj.setRequestHeader("Accept","application/json");
			 	xhrObj.setRequestHeader("frUserId",FREnv.frUserId);
			 	xhrObj.setRequestHeader("authKey",FREnv.authKey);
			}
		});
		$(".jq-ldmr").on("click", function() {
			bindLoadMoreClick($(this));
		});
		
		function bindLoadMoreClick(_this) {
			var section = _this.attr("type");
			var ldngelem = $('.jq-' + section + '-ldng');
			var _sel = $('.jq-ldmr-' + section).find('.jq-a-ld-mr');
			_sel.hide();
			ldngelem.show();
			var requrl = _sel.attr('url');
			requrl = frCommonUtils.constructActivityURL(_this, requrl);
			$.ajax({
				url: requrl,
				type: "GET",
				dataType: "json",
				success: function(data) {
					$('.jq-cnt-' + section).last().append(data.result);
					ldngelem.hide();
					_sel.show();
					var elem = $('#ld-mr-url-' + section);
					if(elem.attr('url')){
						_sel.attr('url', elem.attr('url'));
					} else {
						_sel.remove();
					}
					elem.remove();
				}
			});
		};
		
		function crossSectionReady() {
			var fn = function() {
				$(".jq-cs-slim-scr").frvscroll({"load": "none"});
			};
			setTimeout(fn, 100);
			
			$(".jq-cs-cls").on("click", function() {
				$(".jq-ov-ly").hide();
				$('#crossSearch').html("").hide();
			});
		}
		
		//slim scroll on fr
		var fn = function() {
			$(".jq-slim-scr").each(function() {
				$(this).frvscroll({"load": "none"});
			});
		 };
		setTimeout(fn, 1);
		
		$(".jq-hlp-tt").on("click", function() {
			$('#' + $(this).attr("divId")).toggle();
		});
		
		$(".jq-hlp-c-tt").on("click", function() {
			$('#' + $(this).attr("divId")).hide();
		});
		
		window.callback0 = function(graph, label) {
			var elem = graph.target;
			//disable twitter click
			if($(elem).attr('class') != 'jq-b-a-tt') {
				$(".jq-ov-ly").show();
				$('#crossSearch').show().html("<div class=\"fr-spnr\"><img class=\"fr-spinner\" src=\"" + FREnv.imgcssurl + "/images/spacer.gif\">Loading</div>");
				var _sel = $("#jq-hid-val");
				var eST1 = _sel.attr("entityId");
				var compname = _sel.attr("entityName");
				var fq = _sel.attr("fq");
				var reqData = {};
				reqData.q1 = eST1;
				reqData.q2 = label.searchToken;
				reqData.name = compname;
				reqData.fq = fq;
				//usagetracking
				frCommonUtils.constructActivityParams(reqData, $(this));
				reqData.activityType = "click";
				reqData.targetId = label.searchToken;
				$.ajax({
					url: "../crossSection",
					type: "GET",
					data: reqData,
					success: function(res) {
						$('#crossSearch').html(res.result);
						crossSectionReady();
					}
				});
			}
		}
	});
})(jQuery); // We call our anonymous function immediately