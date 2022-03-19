package com.firstrain.integrationTest;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.firstrain.common.db.config.DataSourceConfigRuleSet;
import com.firstrain.feed.processor.DocumentsFeedProcessor;
import com.firstrain.log4j.Log4jUtils;
import com.firstrain.utils.FR_Loader;

public class ProgressMarkJDBCIT {
	
	@BeforeClass
	  public static void init() throws Exception {
		Log4jUtils.initializeConsoleLogger(Level.DEBUG);
	    Digester digester = new Digester();
	    digester.addRuleSet(new DataSourceConfigRuleSet());
	    digester.parse(FR_Loader.getResourceAsStream("resources/DataSource.xml"));
	}
	
	@Test
	public void executeProgressMarkingQuery() {
		DocumentsFeedProcessor documentsFeedProcessor = new DocumentsFeedProcessor();
		documentsFeedProcessor.setProgressPath("jdbc:progressDB:PROGRESS_MARK:SageWorksDocuments Feed Tool");
		documentsFeedProcessor.readProgressMarkers();
		Assert.assertTrue("No Sql exception occured in accessing progress marking db with 2016 driver", true);
		}

}
