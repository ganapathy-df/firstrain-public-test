<%@page import="javax.sql.DataSource"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.List"%>
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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Standard Health Monitor</title>
</head>
<%--
	1. Load ServiceConfig.xml here, I think its not readily available in context.
	2. If loaded successfully, save it in application-context.
	3. Now do the following checks:
		a.) Check out each SOLR url availability and do some more quick tests for checking its responsiveness ex: ping it, fire an ultra-fast query(warm-up query).
		b.) Check the configured DB availability & responsiveness through some quick queries.
		c.) Check the groovy url availability.
		d.) Check the FeedLogoUrl availability.
	4. Check out the responsiveness of other service urls in spring_config.properties.
--%>
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
%>
<%
	boolean isAll = (request.getParameter("all") != null ? true : false);
 	long sTime = System.nanoTime();
 	try {
 		ServiceConfig sc = null;
 		if((sc = (ServiceConfig)application.getAttribute(ServiceConfig.class.getName() + "_hm")) == null) { // _hm stands for health monitor.
			sc = ServiceConfig.getInstance();
 			application.setAttribute(ServiceConfig.class.getName() + "_hm", sc);
 		}
 		if(sc == null) {
 			throw new Throwable("Attempt to load ServiceConfig.xml failed.");
 		}
 		checkSolrServers(sc, isAll);
 		checkDBServers();
 		
 		ResourceBundle rb = ResourceBundle.getBundle("resource.spring_config");
 		//check static resources
 		checkRemoteService(rb.getString("img.css.base.url") + "/css/" + rb.getString("app.name") + "/" + rb.getString("api.version") + "/visualization.css");
 		checkRemoteService(rb.getString("img.css.base.url") + "/images/spacer.gif");
 		checkRemoteService(rb.getString("js.base.url") + "/js/" + rb.getString("app.name") + "/" + rb.getString("api.version") + "/graph.js");
 		checkRemoteService(rb.getString("storage.service.url") + "/getbulk?objectids=D:785671740&scopes=entityLinking");
 		
 		//for non critical service - check on demand
 		if(isAll) {
 			checkRemoteService(rb.getString("user.activity.service.url") + "/useractivity/getListByType.json?type=ACTIVITY");
 			checkRemoteServicesInCSV(rb.getString("image.service.urls.csv")+ "/readme.txt");
 		}
 		
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
 	void checkSolrServers(ServiceConfig sc, boolean isAll) throws Throwable {
		SolrServer[] docServers = sc.getDistributedSearchConfig().docServers;
		if(docServers == null) {
			throw new Throwable("Doc servers not found configured, portal configuration issue.");
		}
		for(Map.Entry<SolrServer, String> e : sc.getSolrVsPingQ().entrySet()) {
			 pingServer(e.getKey(), e.getValue(), isAll);
		}
	}

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
 	
 	void checkDBServers() throws Throwable {
 		final String pingQuery1 = "SELECT 1 as id;"; // MySQl ping query
 		pingMySQLServer(pingQuery1);
	}

 	// TODO: NEXT is to contruct a special request handler for server self-diagnostics purposes.
 	// This would be an extension of Solr ping handler to be taken care on bug.
 	void pingServer(SolrServer ss, String query, boolean isAll) throws Throwable {
 		try {
 			SolrPing sp = new SolrPing();
			sp.getParams().set("qt", "standard");
			sp.getParams().set("q", query);
			SolrPingResponse spr = null;
			if(((CommonsHttpSolrServer) ss).getBaseURL().endsWith("/docimage")) {
				if(isAll) {
					spr = sp.process(ss);
				}
			} else {
				spr = sp.process(ss);
			}
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
 	
 	void checkRemoteServicesInCSV(String csvURLs) throws Throwable {
		checkForNonNullability(csvURLs);
		String[] uRLs = csvURLs.split(",");
		for (String url : uRLs) {
			checkRemoteService(url);
		}
	}
 	
 	private void checkForNonNullability(Object obj) throws Throwable {
		if (obj == null) {
			throw new Throwable(obj.toString() + " is null, configuration issue.");
		}
	}

%>
<body>
</body>
</html>