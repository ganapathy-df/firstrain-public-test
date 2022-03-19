package com.firstrain.web.service.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.firstrain.utils.BusinessJSONUtility;
import com.firstrain.utils.JSONUtility;
import com.firstrain.utils.object.FRThreadLocalTrackObject;
import com.firstrain.utils.object.FRThreadLocalTrackObject.TrackObj;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;

@Service
public class HttpClientService {

	private static final Logger LOG = Logger.getLogger(HttpClientService.class);

	@Qualifier("httpClient")
	@Autowired
	private HttpClient httpClient;

	public <T> T getData(String url, String method, TypeReference<T> responseType) throws Exception {
		return getData(url, method, (Map<String, String>) null, responseType);
	}

	public <T> T getData(String url, String method, Map<String, String> params, TypeReference<T> responseType) throws Exception {
		CloseableHttpResponse response = null;
		URI uri = null;
		long st = PerfMonitor.currentTime();
		try {
			uri = new URI(url + "/" + method);
			URIBuilder uriBuilder = new URIBuilder(uri);
			if (params != null) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					if (entry.getValue() == null || entry.getValue().isEmpty()) {
						continue;
					}
					uriBuilder.setParameter(entry.getKey(), entry.getValue());
				}
			}
			uri = uriBuilder.build();
			GetMethod getMethod = new GetMethod(uri.toString());
			addHeaders(getMethod);
			httpClient.executeMethod(getMethod);
			LOG.info("Serving data from service GET: " + uri);
			return parseObject(response.getEntity(), responseType);

		} catch (Exception e) {
			LOG.error("Error for service GET: " + uri, e);
			throw e;
		} finally {
			if (response != null) {
				response.close();
			}
			PerfMonitor.recordStats(st, PerfActivityType.Rest, "rest.get." + method);
		}
	}

	public String getData(String url, String method1, Map<String, String> params) throws Exception {
		String result = null;
		GetMethod method = null;
		URI uri = null;
		long st = PerfMonitor.currentTime();
		try {
			uri = new URI(url + "/" + method1);
			URIBuilder uriBuilder = new URIBuilder(uri);
			if (params != null) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					if (entry.getValue() == null || entry.getValue().isEmpty()) {
						continue;
					}
					uriBuilder.setParameter(entry.getKey(), entry.getValue());
				}
			}
			uri = uriBuilder.build();
			method = new GetMethod(uri.toString());
			addHeaders(method);
			httpClient.executeMethod(method);
			InputStream in = method.getResponseBodyAsStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024 * 10];
			int n = 0;
			while ((n = in.read(buffer)) > -1) {
				out.write(buffer, 0, n);
			}
			result = new String(out.toByteArray(), "UTF-8");
			LOG.info("Serving data from service GET: " + uri);
		} catch (Exception e) {
			LOG.error("Error while getting data for: " + uri, e);
			throw e;
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
			PerfMonitor.recordStats(st, PerfActivityType.Rest, "chat.post");
		}
		return result;
	}

	private void addHeaders(GetMethod request) {
		request.addRequestHeader("Accept", "application/json");
		TrackObj trackObj = FRThreadLocalTrackObject.getTrackObj();
		if (trackObj != null) {
			request.addRequestHeader(FRThreadLocalTrackObject.CLIENT_APPID, trackObj.appId);
			request.addRequestHeader(FRThreadLocalTrackObject.TRACKING_HEADER, trackObj.userName);
		}
	}

	public <T> T postDataInReqBody(String urlParam, String method, Object reqBean, Class<T> responseType, Header... headers) throws Exception {
		String url = urlParam;
		PostMethod postMethod = null;
		String jsonInut = null;
		// long st = PerfMonitor.currentTime();
		String res = null;
		try {
			url = url + "/" + method;
			postMethod = new PostMethod(url);
			postMethod.addRequestHeader(new Header("Accept", "application/json"));
			if (headers != null) {
				for (Header hd : headers) {
					postMethod.addRequestHeader(hd);
				}
			}
			jsonInut = JSONUtility.serialize(reqBean);
			RequestEntity re = new StringRequestEntity(JSONUtility.serialize(reqBean), "application/json", "UTF-8");
			postMethod.setRequestEntity(re);
			httpClient.executeMethod(postMethod);
			InputStream in = postMethod.getResponseBodyAsStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024 * 10];
			int n = 0;
			while ((n = in.read(buffer)) > -1) {
				out.write(buffer, 0, n);
			}
			res = new String(out.toByteArray());
			T t = JSONUtility.deserialize(res, responseType);
			LOG.info("Serving data from service POST: " + url + " :: reqBody: " + jsonInut);
			return t;
		} catch (Exception e) {
			LOG.error("Error while getting data for: " + url + " :: reqBody: " + jsonInut + " :: response: " + res, e);
			throw e;
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
			// PerfMonitor.recordStats(st, PerfActivityType.Rest, method);
		}
	}

	private <T> T parseObject(HttpEntity resEntity, TypeReference<T> responseType) throws Exception {
		T t = null;
		if (resEntity != null) {
			InputStream in = resEntity.getContent();
			String jsonStr = streamToString(in);
			in.close();
			t = BusinessJSONUtility.deserialize(jsonStr, responseType);
			ensureResponseStatus(t);
		}
		return t;
	}

	private String streamToString(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 10];
		int n = 0;
		while ((n = in.read(buffer)) > -1) {
			out.write(buffer, 0, n);
		}
		return new String(out.toByteArray(), Consts.UTF_8);
	}

	private Method methodStatus = null;
	private Method methodMessage = null;
	private Method methodErrorStackTrace = null;

	private @SuppressWarnings("rawtypes") Class resClass;
	{
		try {
			resClass = Class.forName("com.firstrain.business.pojo.ServiceResponse");
		} catch (ClassNotFoundException e) {
			LOG.warn("Class com.firstrain.business.pojo.ServiceResponse not found, disabling RestAPI status check...");
		}
	}

	private <T> void ensureResponseStatus(T res) throws Exception {
		try {
			if (res == null || resClass == null || !resClass.isInstance(res)) {
				return;
			}
			if (this.methodStatus == null) {
				this.methodStatus = res.getClass().getMethod("getStatus");
			}
			String status = this.methodStatus.invoke(res).toString();
			if ("ERROR".equalsIgnoreCase(status)) {
				if (this.methodErrorStackTrace == null) {
					this.methodErrorStackTrace = res.getClass().getMethod("getErrorStackTrace");
				}
				if (this.methodMessage == null) {
					this.methodMessage = res.getClass().getMethod("getMessage");
				}
				String msg = (String) this.methodMessage.invoke(res);
				Object errorStackTrace = this.methodErrorStackTrace.invoke(res);
				if (errorStackTrace != null) {
					LOG.error("Service Error: " + msg + "; error trace: " + errorStackTrace);
				}
				throw new Exception("Service Error: " + msg);
			}
		} catch (NoSuchMethodException e) {
			LOG.warn(e.getMessage(), e);
		} catch (SecurityException e) {
			LOG.warn(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			LOG.warn(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOG.warn(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LOG.warn(e.getMessage(), e);
		}
	}
}
