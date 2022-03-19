<%@page import="org.apache.solr.client.solrj.impl.CommonsHttpSolrServer"%>
<%@page import="org.apache.solr.client.solrj.response.SolrPingResponse"%>
<%@page import="org.apache.solr.client.solrj.request.SolrPing"%>
<%@page import="java.io.File"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="java.util.concurrent.TimeUnit"%>
<%@page import="com.firstrain.content.pipeline.PipelineComponent"%>
<%@page import="com.firstrain.content.pipeline.PipelineDriver"%>
<%@page import="com.firstrain.content.pipeline.PipelineComponentInfo"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.firstrain.utils.FR_Integer"%>
<%@page import="com.firstrain.common.db.handlers.FR_BeanHandler"%>
<%@page import="com.firstrain.common.db.FR_QueryRunner"%>
<%@page import="com.firstrain.mip.db.object.impl.FR_DBTransaction"%>
<%@page import="com.firstrain.feed.processor.DocumentsFeedProcessor"%>
<%@page import="org.apache.solr.client.solrj.SolrServer"%>
<%@page import="org.apache.commons.httpclient.HttpStatus"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Health Monitor SageWorks Documents Feed Tool Pipeline</title>
</head>
<%!
	Logger LOG = LoggerFactory.getLogger("_healthmonitor_");
	final String pipelineId = "SageWorksDocuments Feed Tool";
	SolrServer entitySolrServer = null;
	SolrServer docImageServer = null;
	SolrServer docSolrServer = null;
	String ftpLocation = null;
%>	
<% 
	long sTime = System.nanoTime();
	StringBuffer loggingInfo = new StringBuffer();
	try {
		//get info from pipeline config
		PipelineDriver driver = (PipelineDriver) application.getAttribute("PipelineDriver");
		List<PipelineComponentInfo> infos = driver.getPipelineComponentInfos(pipelineId);
		if (infos == null || infos.isEmpty()) {
			throw new Exception("Pipeline " + pipelineId + " isn't available yet.");
		}
		for (PipelineComponentInfo info : infos) {
			PipelineComponent component = info.getInstance();
			if (component instanceof DocumentsFeedProcessor) {
				DocumentsFeedProcessor processor = (DocumentsFeedProcessor) component;
				entitySolrServer = processor.getEntitySolrServer();
				docSolrServer = processor.getDocSolrServer();
				docImageServer = processor.getDocImageServer();
				ftpLocation = processor.getDestLocation();
				break;
			}
		}
	
		if (entitySolrServer == null || docSolrServer == null || docImageServer == null) {
			throw new Exception("One of the Solr server is null.");
		}
		
		Map<SolrServer, String> solrServers = new HashMap<SolrServer, String>();
		solrServers.put(entitySolrServer, "attrCompanyId:3864");
		solrServers.put(docSolrServer, "headline: microsoft");
		
		if(request.getParameter("all") != null) {
			solrServers.put(docImageServer, "insertTime:[NOW/DAY-1DAYS TO *]");
		}

		loggingInfo.append("<table style=\"border: 1px solid black;\">");

		//check solr servetrs
		for (Map.Entry<SolrServer, String> entry : solrServers.entrySet()) {
			SolrServer ss = entry.getKey();
			String query = entry.getValue();
			try {
				SolrPing sp = new SolrPing();
				sp.getParams().set("qt", "standard");
				sp.getParams().set("q", query);
				SolrPingResponse spr = sp.process(ss);
				if(spr != null && spr.getStatus() != 0) {
					throw new Throwable("Solr ping failed on query " + query);
				}
				loggingInfo.append("<tr><td> Server: " + ((CommonsHttpSolrServer)ss).getBaseURL() + " response is ok. </td><td>");
	 		} catch(Throwable t) {
	 			if(ss instanceof CommonsHttpSolrServer) {
		 			throw new Throwable("Server " + ((CommonsHttpSolrServer)ss).getBaseURL() + " failed to respond due to " + t.getMessage(), t);
	 			}
	 			throw t;
	 		}
		}
		//check sql server
		String dataSource = "progressDB";
		String pingQuery = "SELECT 1 as number;";
 		FR_DBTransaction txn = null;
 		try {
 			FR_QueryRunner queryRunner = new FR_QueryRunner();
 			FR_BeanHandler<FR_Integer> handler = FR_BeanHandler.create(FR_Integer.class);
 			txn = new FR_DBTransaction(dataSource);
 			queryRunner.query(txn.getConnection(), pingQuery, handler);
 			loggingInfo.append("<tr><td> DB Server: " + dataSource + " connection is ok.</td><td>");
 		} catch(Throwable t) {
 			throw new Throwable("Error pinging server with ds " + dataSource + " due to " + t.getMessage(), t);
 		} finally {
 			if(txn != null) {
 				txn.close();
 			}
 		}
 		//check if ftp is accessible
 		File f = new File(ftpLocation + "/BAK/touch.txt");
		if (f.exists()){
			loggingInfo.append("<tr><td> ftp location: "+ ftpLocation +"is accessible.</td><td>");
		} else {
			throw new Throwable("ftp location: "+ ftpLocation +" is not accessible.");
		}
		response.setStatus(HttpStatus.SC_OK); // Everything seems OK now.
		out.println("Response health OK.");
	} catch (Throwable th) {
		loggingInfo.append(th.getMessage());
		response.setStatus(HttpStatus.SC_SERVICE_UNAVAILABLE);
		LOG.error(th.getMessage(), th);
	} finally {
		out.println(loggingInfo.toString() + "</table><br/>");
		out.println("Health Check time is " + ((System.nanoTime() - sTime) / 1000000) +"ms");
	}
%>
<body>

</body>
</html>