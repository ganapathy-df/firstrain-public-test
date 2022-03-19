package com.firstrain.web.service.core;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.firstrain.utils.JSONUtility;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;

@Service
public class HttpClientService {

	private static final Logger LOG = Logger.getLogger(HttpClientService.class);

	@Qualifier("httpClient")
	@Autowired
	private HttpClient httpClient;

	public <T> T postDataInReqBody(String urlParam, String method, Map<String, String> params, TypeReference<T> responseType) throws Exception {
		String url = urlParam;
		PostMethod postMethod = null;
		String jsonInut = null;
		// long st = PerfMonitor.currentTime();
		String res = null;
		try {
			url = url + "/" + method;
			postMethod = new PostMethod(url);
			postMethod.addRequestHeader(new Header("Accept", "application/json"));

			NameValuePair param1 = new NameValuePair("objectids", params.get("objectids"));
			NameValuePair param2 = new NameValuePair("scopes", params.get("scopes"));
			NameValuePair[] nvp = new NameValuePair[] {param1, param2};
			postMethod.setRequestBody(nvp);

			httpClient.executeMethod(postMethod);
			InputStream in = postMethod.getResponseBodyAsStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024 * 10];
			int n = 0;
			while ((n = in.read(buffer)) > -1) {
				out.write(buffer, 0, n);
			}
			res = new String(out.toByteArray(), "UTF-8");
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

	public byte[] getData(String url) throws Exception {
		GetMethod getMethod = null;
		String jsonInut = null;
		long st = PerfMonitor.currentTime();
		String res = null;
		try {
			getMethod = new GetMethod(url);
			httpClient.executeMethod(getMethod);
			InputStream in = getMethod.getResponseBodyAsStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024 * 10];
			int n = 0;
			while ((n = in.read(buffer)) > -1) {
				out.write(buffer, 0, n);
			}
			return out.toByteArray();
		} catch (Exception e) {
			LOG.error("Error while getting data for: " + url + " :: reqBody: " + jsonInut + " :: response: " + res, e);
			throw e;
		} finally {
			if (getMethod != null) {
				getMethod.releaseConnection();
			}
			PerfMonitor.recordStats(st, PerfActivityType.Rest, "image.do");
		}
	}
}
