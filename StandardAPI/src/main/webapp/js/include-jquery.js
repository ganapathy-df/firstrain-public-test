if(typeof FREnv == 'undefined') {
	FREnv = {};
}
(function() {
	var $;
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
		main();
	}
	
	function main() {
		FREnv.appname = $("#jqi-env-appname").html();
		FREnv.appBaseUrl = $("#jqi-env-appbaseurl").html();
		FREnv.version = $("#jqi-env-version").html();
		FREnv.jsurl = $("#jqi-env-jsurl").html();
		FREnv.imgcssurl = $("#jqi-env-imgcssurl").html();
		var file = FREnv.debug == "true" ? "custom.js" : "custom.min.js";
		loadjs(file);
	}
	
	function loadjs(filename) {
		var jsUrl = FREnv.jsurl + "/js/" + FREnv.appname + "/" + FREnv.version;
		var file = FREnv.debug == "true" ? "common-utils.js" : "common-utils.min.js";
		$.ajax({
	   		 url: jsUrl + "/" + file,
	   		 dataType: "script",
	   		 cache: true,
	   		 success: function () {
	   			$.ajax({
		   	         url: jsUrl + "/" + filename,
		   	         dataType: "script",
		   	         cache: true
		   		 }); 
	   		 }
   	 	});
	}
})(); // We call our anonymous function immediately