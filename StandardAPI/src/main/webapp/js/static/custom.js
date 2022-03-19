(function($) {
	$(function() {
		FREnv.pageName = "entitybrief";
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
		$(".jq-ldmr-fr").on("click", function() {
			bindLoadMoreClick("fr", $(this));
		});
		
		function bindLoadMoreClick(section, _this) {
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
					$('.jq-cnt-fr').last().append(data.result);
					ldngelem.hide();
					secelem.find('.jq-a-ld-mr').show();
					var elem = $('#ld-mr-url-fr');
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
})(jQuery); // We call our anonymous function immediately