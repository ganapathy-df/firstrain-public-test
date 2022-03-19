(function($) {
	$(function() {
		FREnv.pageName = "entitybrief";
		var frCommonUtils = new FRCommonUtils($);
		frCommonUtils.addProximaNova(document);
		frCommonUtils.initiateClickTracking();
		FREnv.frUserId = $("#jqi-env-frUserId").html();
		FREnv.authKey = $("#jqi-env-authKey").html();
		//send height of firstrain content to the parent iframe
		if($('#jq-fr-container').length > 0) {
			var sectionStr = frCommonUtils.getSectionStr();
			var json = {"height":$('#jq-fr-container').height(), "section" : sectionStr};
			parent.postMessage(JSON.stringify(json), "*");
		}
		
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
		
		frCommonUtils.bindMouseEventToTweetNotables(".jq-tw-notable");
		
		function bindLoadMoreClick(_this) {
			var section = _this.attr("type");
			var ldngelem = $('.jq-' + section + '-ldng');
			var secelem = $('.jq-ldmr-'+section);
			secelem.find('.jq-a-ld-mr').hide();
			ldngelem.show();
			var requrl = secelem.find('.jq-a-ld-mr').attr('url');
			requrl = frCommonUtils.constructActivityURL(_this, requrl);
			$.ajax({
				url: requrl,
				type: "GET",
				dataType: "json",
				success: function(data) {
					$('.jq-cnt-' + section).last().append(data.result);
					ldngelem.hide();
					secelem.find('.jq-a-ld-mr').show();
					var elem = $('#ld-mr-url-' + section);
					if(elem.attr('url')){
						secelem.find('.jq-a-ld-mr').attr('url', elem.attr('url'));
					} else {
						secelem.remove();
					}
					elem.remove();
				}
			});
		};
	});
})(FREnv.$); // We call our anonymous function immediately