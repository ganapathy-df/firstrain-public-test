<%@ page language="java" contentType="application/json" pageEncoding="UTF-8"%>
<%@page import="com.firstrain.utils.FR_ArrayUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.firstrain.common.db.jpa.PersistenceProvider"%>
<%@page import="com.firstrain.common.db.jpa.Transaction"%>
<%@page import="com.firstrain.db.api.TakeDownDbAPI"%>
<%@page import="com.firstrain.db.obj.APIArticleTakeDown"%>
<%@page import="com.firstrain.db.obj.APIArticleTakeDown.Status"%>
<%@ page import="net.sf.json.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.util.*" %>

<%
	long articleId = -1;

	if (null != request.getParameter("articleId")){
		try{
	       		articleId = Long.parseLong(request.getParameter("articleId"));
		}catch (NumberFormatException e) {
			// Ignore Error - articleId remain -1
		}	
	}


	JSONObject jsonOutput = new JSONObject();

	boolean fromFirstRainSubnet=(request.getRemoteAddr()==null || request.getRemoteAddr().startsWith("10."));
	
	if (!fromFirstRainSubnet){
		jsonOutput.put("status","FAIL");
		jsonOutput.put("message","Not from FirstRain Subnet.");
		out.println(jsonOutput.toString());
		return;
	}

	
        if ( articleId == -1){
                jsonOutput.put("status","FAIL");
                jsonOutput.put("message","articleId not provided.");
                out.println(jsonOutput.toString());
                return;
        }


	APIArticleTakeDown article = TakeDownDbAPI.getTakeDownArticle(articleId);	
	
	if(null == article){
		article = new APIArticleTakeDown();
		article.setArticleId(Long.toString(articleId));
		article.setStatus(Status.NEW);

		Transaction txn = null;

		try {
			txn = PersistenceProvider.newTxn(PersistenceProvider.SPY_DB);
			TakeDownDbAPI.persistTakeDownArticle(txn, article);
			txn.commit();
		} catch (Exception e) {
			//LOG.error("Error while persisting take down articleId: " + articleId, e);
			out.println(e.toString());
			throw e;
		} finally {
			if (txn != null) {
				txn.close();
			}
		}
	}


	if(null != article){
		
		JSONObject data = new JSONObject();
                data.put("articleId", article.getArticleId());
                data.put("status", article.getStatus());
	
		jsonOutput.put("data",data);
		jsonOutput.put("status","SUSCCESS");
		jsonOutput.put("message","");
		jsonOutput.put("statusCode","");

	}else{
		jsonOutput.put("status","FAIL");
	}
	
	out.println(jsonOutput.toString());
%>

