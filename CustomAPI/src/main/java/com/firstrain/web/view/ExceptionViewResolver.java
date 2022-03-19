package com.firstrain.web.view;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.firstrain.web.exception.PermissionException;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;

public class ExceptionViewResolver extends SimpleMappingExceptionResolver {
	private static final Logger LOG = Logger.getLogger(ExceptionViewResolver.class);
	private static final String NL = String.format("%n");

	@Autowired
	private ResourceBundleMessageSource messageSource;
	@Autowired
	private ServletContext servletContext;

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {

		ModelAndView modelAndView = super.doResolveException(request, response, handler, e);
		if (handler != null) {
			try {
				Map<String, Object> model = modelAndView.getModel();
				if (e instanceof PermissionException) {
					LOG.info("PermissionException for handler " + handler.getClass() + " :: " + e.getMessage(), e);
				} else if (e instanceof MissingServletRequestParameterException) {
					updateError(request, handler, e, response, 400, model);
				} else if (e instanceof HttpRequestMethodNotSupportedException) {
					updateError(request, handler, e, response, 405, model);
				} else {
					logRequestDetails(request, handler, e);
					response.setStatus(500);
					setErrorMessageForJSONRes(model, request, 500);
				}
			} catch (Exception e1) {
			}
		}
		return modelAndView;
	}

	private void updateError(final HttpServletRequest request, final Object handler, final Exception e, final HttpServletResponse response, final int errorCode, final Map<String, Object> model) {
		logRequestDetails(request, handler, e);
		response.setStatus(errorCode);
		request.setAttribute("errorCode", errorCode);
		setErrorMessageForJSONRes(model, request, errorCode);
	}

	private void logRequestDetails(HttpServletRequest request, Object handler, Exception e) {
		try {
			StringBuffer sb = request.getRequestURL();
			sb.append(NL).append("Request Parameters:").append(NL);
			for (Map.Entry<String, String[]> entry : (Set<Map.Entry<String, String[]>>) request.getParameterMap().entrySet()) {
				sb.append("\t").append(entry.getKey()).append(" => ").append(Arrays.toString(entry.getValue())).append(NL);
			}
			sb.append("method: ").append(request.getMethod()).append(NL);
			sb.append("headers: ").append(NL);
			Enumeration<String> headers = request.getHeaderNames();
			while (headers.hasMoreElements()) {
				String key = headers.nextElement();
				sb.append("\t").append(key).append(" => ").append(enumToString(request.getHeaders(key))).append(NL);
			}
			sb.append("Remote Addr: ").append(request.getRemoteAddr()).append(NL);
			LOG.error("Error for handler " + handler.getClass() + " :: " + e.getMessage() + " :: " + NL + sb, e);
		} catch (Exception e1) {
			LOG.error(e.getMessage(), e);
		}

	}

	private String enumToString(Enumeration<String> vals) {
		if (vals == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder("[");
		while (vals.hasMoreElements()) {
			sb.append(vals.nextElement()).append(", ");
		}
		sb.setLength(sb.length() - 2);
		sb.append("]");
		return sb.toString();
	}

	private void setErrorMessageForJSONRes(Map<String, Object> model, HttpServletRequest request, int errorCode) {
		if ("application/json".equals(request.getHeader("Accept"))) {
			try {
				JSONResponse res = new JSONResponse();
				res.setErrorCode(errorCode);
				res.setStatus(ResStatus.ERROR);
				res.setVersion(servletContext.getAttribute("version").toString());
				res.setMessage(messageSource.getMessage("errorcode." + errorCode, null, Locale.getDefault()));
				// TODO - keep response under single key, need to fix different JsonView for this
				model.put("JSONResponse", res);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

}
