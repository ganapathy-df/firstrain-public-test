<%@page import="org.apache.commons.httpclient.methods.RequestEntity"%>
<%@page import="org.apache.commons.httpclient.methods.StringRequestEntity"%>
<%@page import="com.firstrain.utils.JSONUtility"%>
<%@page import="org.apache.commons.httpclient.Header"%>
<%@page import="org.apache.commons.httpclient.methods.PostMethod"%>
<%@page import="javax.sql.DataSource"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.*"%>
<%@page import="com.firstrain.common.db.DBConnectionFactory"%>
<%@page import="javax.persistence.EntityManagerFactory"%>
<%@page import="java.util.Map"%>
<%@page import="org.apache.solr.client.solrj.request.SolrPing"%>
<%@page import="com.firstrain.db.obj.LongDataType"%>
<%@page import="com.firstrain.common.db.jpa.PersistenceProvider"%>
<%@page import="com.firstrain.common.db.jpa.Transaction"%>
<%@page import="org.apache.solr.client.solrj.impl.CommonsHttpSolrServer"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="org.apache.commons.httpclient.methods.GetMethod"%>
<%@page import="org.apache.commons.httpclient.URI"%>
<%@page import="org.apache.commons.httpclient.HttpMethodBase"%>
<%@page import="org.apache.commons.httpclient.HttpState"%>
<%@page import="org.apache.commons.httpclient.params.HttpClientParams"%>
<%@page import="org.apache.commons.httpclient.MultiThreadedHttpConnectionManager"%>
<%@page import="org.apache.commons.httpclient.HttpConnectionManager"%>
<%@page import="org.apache.commons.httpclient.params.HttpConnectionManagerParams"%>
<%@page import="org.apache.commons.httpclient.HttpClient"%>
<%@page import="com.firstrain.utils.FR_Integer"%>
<%@page import="com.firstrain.common.db.handlers.FR_BeanHandler"%>
<%@page import="com.firstrain.common.db.FR_QueryRunner"%>
<%@page import="com.firstrain.mip.db.object.impl.FR_DBTransaction"%>
<%@page import="org.apache.solr.common.SolrDocumentList"%>
<%@page import="org.apache.solr.client.solrj.SolrQuery"%>
<%@page import="org.apache.solr.client.solrj.response.SolrPingResponse"%>
<%@page import="org.apache.solr.client.solrj.SolrServer"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="org.apache.commons.httpclient.HttpStatus"%>
<%@page import="com.firstrain.frapi.config.ServiceConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>CustomAPI Health Monitor</title>
</head>
<%!
	private HttpClient httpClient;
	private static final Logger LOG = Logger.getLogger("_hmjsp");
	public void jspInit() {
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setMaxTotalConnections(5);
		HttpConnectionManager cm = new MultiThreadedHttpConnectionManager();
		cm.setParams(params);
		HttpClientParams hcParams = new HttpClientParams();
		hcParams.setConnectionManagerTimeout(60000);
		hcParams.setIntParameter(HttpClientParams.MAX_REDIRECTS, 3);
		hcParams.setSoTimeout(60000);
		this.httpClient = new HttpClient(hcParams, cm);
	}
	// Properties set to get from some resource bundle
	final String[][] resourceProperties = new String[][] {
		{"categorization.service.url", "/getCategorizedDoc","{\"docId\":1,\"appId\":\"Test\",\"industryClassificationId\":1,\"requiredTopicDim\":[1,2,3],"
		+"\"title\":\"Google faces up to $5 billion fine from Competition Commission of India\","
		+"\"body\":\"NEW DELHI: Google, which is facing antitrust investigation in India by fair trade watchdog CCI, can face a penalty of up to about five billion dollars\"}"}
	};
	private List<String> httpServiceURLs = null;
	private String[][] httpPostServiceURLs = new String[1][2];
%>

<%
	boolean isAll = (request.getParameter("all") != null ? true : false);
	ResourceBundle rb = ResourceBundle.getBundle("resource.spring_config");
	if(httpServiceURLs == null || httpPostServiceURLs == null) {
		httpServiceURLs = new ArrayList<String>();
 		int i = 0;
		for(String[] property : resourceProperties) {
 			try {
 				String reqBody = property[2];
 				if (reqBody != null) {
 					httpPostServiceURLs[i][0] = rb.getString(property[0])+property[1];
 					httpPostServiceURLs[i][1] = reqBody;
 					i++;
 				} else {
	 				String baseURL = rb.getString(property[0]);
	 				httpServiceURLs.add(baseURL+ property[1]);
 				}
 			} catch (Exception e) {
 	 				LOG.error(e.getMessage(), e);
 	 			}
 		}
	}
 	long sTime = System.nanoTime();
 	try {
 		if (httpPostServiceURLs != null && httpPostServiceURLs.length > 0) {
 			for (String[] serviceUrl : httpPostServiceURLs) {
 				checkRemoteService(serviceUrl[0],serviceUrl[1]);
 			}
 		}
 		for(String httpServiceURL : httpServiceURLs) {
 			checkRemoteService(httpServiceURL);
 		}
 		//for non critical service - check on demand
 		if(isAll) {
 			checkRemoteService(rb.getString("user.activity.service.url") + "/useractivity/getListByType.json?type=ACTIVITY");
 		}
 		//check solr server
 		ServiceConfig sc = null;
 		if((sc = (ServiceConfig)application.getAttribute(ServiceConfig.class.getName() + "_hm")) == null) { // _hm stands for health monitor.
			sc = ServiceConfig.getInstance();
 			application.setAttribute(ServiceConfig.class.getName() + "_hm", sc);
 		}
 		if(sc == null) {
 			throw new Throwable("Attempt to load ServiceConfig.xml failed.");
 		}
 		checkSolrServers(sc);
 		//check mysql db servers
 		checkDBServers();
 		response.setStatus(HttpStatus.SC_OK); // Everything seems OK now.
 		LOG.info(application.getServerInfo() + " " + application.getServletContextName() + " Health OK.");
 		out.println("Response health OK.");
 	} catch(Throwable t) {
 		response.setStatus(HttpStatus.SC_SERVICE_UNAVAILABLE);
 		LOG.error(application.getServerInfo() + " " + application.getServletContextName() + " Health bad.", t);
 		out.println("Service error reason: " + t.getMessage());
 	} finally {
 		out.println("Health Check time is " + ((System.nanoTime() - sTime) / 1000000) +"ms");
 	}
%>

<%! 
	void checkRemoteService(String serviceUrl) throws Throwable {
		HttpMethodBase req = new GetMethod();
 		try {
	 		req.setURI(new URI(serviceUrl, true));
	 		int responseCode = this.httpClient.executeMethod(null, req, new HttpState());
	 		if(responseCode != HttpStatus.SC_OK) {
	 			throw new Throwable("Bad response " + responseCode + " for " + serviceUrl);
	 		}
 		} catch(Throwable t) {
 			throw new Throwable("Problem contacting " + serviceUrl, t);
 		} finally {
 			req.releaseConnection();
 		}
	}

	void checkRemoteService(String url, String reqBody) throws Throwable {
		PostMethod postMethod = null;
		try {
			postMethod = new PostMethod(url);
			postMethod.addRequestHeader(new Header("Accept", "application/json"));
			RequestEntity re = new StringRequestEntity(reqBody, "application/json", "UTF-8");
			postMethod.setRequestEntity(re);
			int responseCode = this.httpClient.executeMethod(null, postMethod, new HttpState());
	 		if(responseCode != HttpStatus.SC_OK) {
	 			throw new Throwable("Bad response " + responseCode + " for url[" + url + "], reqBody : [" + reqBody + "], methodType : POST");
	 		}
		} catch(Throwable t) {
 			throw new Throwable("Problem contacting url : [" + url + "], reqBody : [" + reqBody + "], methodType : POST", t);
 		} finally {
 			postMethod.releaseConnection();
 		}
	}
	
	void checkSolrServers(ServiceConfig sc) throws Throwable {
		SolrServer[] docServers = sc.getDistributedSearchConfig().docServers;
		if(docServers == null) {
			throw new Throwable("Doc servers not found configured, portal configuration issue.");
		}
		for(Map.Entry<SolrServer, String> e : sc.getSolrVsPingQ().entrySet()) {
			 pingServer(e.getKey(), e.getValue());
		}
	}
	
	void checkDBServers() throws Throwable {
 		final String pingQuery1 = "SELECT 1 as id;"; // MySQl ping query
 		pingMySQLServer(pingQuery1);
	}
	
	private void pingMySQLServer(String pingQuery) throws Throwable {
		Map<String, EntityManagerFactory> factories = PersistenceProvider.factories;
		for(String punit : factories.keySet()) {
			try {
				PersistenceProvider.executeNativeQuery(punit, pingQuery, LongDataType.class);
	 		} catch(Throwable t) {
	 			throw new Throwable("Error pinging "+ punit +" server due to " + t.getMessage(), t);
	 		}
		}
 	}
	
	void pingServer(SolrServer ss, String query) throws Throwable {
 		try {
 			SolrPing sp = new SolrPing();
			sp.getParams().set("qt", "standard");
			sp.getParams().set("q", query);
			SolrPingResponse spr = sp.process(ss);
			if(spr != null && spr.getStatus() != 0) {
				throw new Throwable("Solr ping failed on query " + query);
			}
 		} catch(Throwable t) {
 			if(ss instanceof CommonsHttpSolrServer) {
	 			throw new Throwable("Server " + ((CommonsHttpSolrServer)ss).getBaseURL() + " failed to respond due to " + t.getMessage(), t);
 			}
 			throw t;
 		}
 	}
%>
<body>
</body>
</html>