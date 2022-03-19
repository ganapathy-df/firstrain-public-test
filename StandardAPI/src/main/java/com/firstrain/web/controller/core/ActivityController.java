/**
 * 
 */
package com.firstrain.web.controller.core;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author vgoyal
 *
 */

@Controller
@RequestMapping(value = "/activity")
public class ActivityController {

	@RequestMapping(value = "/logaction", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String logAction() {
		return "";
	}
}
