package com.firstrain.frapi.config;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.reflect.Whitebox.invokeMethod;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.Arrays;
import java.util.Collection;

import com.firstrain.solr.client.util.SolrServerReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({
	ServiceConfig.class,
	SolrServerReader.class,
	XPathFactory.class
})
public class ServiceConfigTest {

	private static final String XPATH_EXPRESSION = "//ServiceConfig/Solr/*";
	private static final String URL = "test_url";
	private static final String XPF = "xpf";
	private static final String PING_Q = "pingQ";
	private static final String IS_FOUND_SERVER = "isFoundServer";
	private static final String GET_TEXT_CONTENT = "getTextContent";
	private static final String POPULATE_SOLR_SERVERS_BASE = "populateSolrServersBase";

	@Mock
	private Document document;
	@Mock
	private NamedNodeMap namedNodeMap;
	@Mock
	private Node node;
	@Mock
	private NodeList nodeList;
	@Mock
	private CommonsHttpSolrServer solrServer;
	@Mock
	private XPath xPath;
	@Mock
	private XPathFactory xPathFactory;

	private final ServiceConfig serviceConfig = new ServiceConfig();

	private final String expected;
	private final String returned;

	public ServiceConfigTest(String expected, String returned) {
		this.expected = expected;
		this.returned = returned;
	}

	@Parameters
	public static Collection<String[]> data() {
		return Arrays.asList(new String[][] { { "SECSolrServer:test_url", "SECServerURL" },
				{ "UserSearchesSolrServer:test_url", "UserSearchesServerURL" },
				{ "FavIconServer:test_url", "FavIconServerURL" }, { "TwitterServer:test_url", "TwitterServerURL" },
				{ "CompanyModelServerURL:test_url", "CompanyModelServerURL" },
				{ "QuoteServerURL:test_url", "QuoteServerURL" } });
	}

	@Before
	public void setup() throws Exception {
		mockStatic(XPathFactory.class);
		mockStatic(ServiceConfig.class);
		mockStatic(SolrServerReader.class);

		doCallRealMethod().when(ServiceConfig.class, POPULATE_SOLR_SERVERS_BASE, serviceConfig, document);
		doCallRealMethod().when(ServiceConfig.class, IS_FOUND_SERVER, any(ServiceConfig.ServerUrl.class),
				eq(serviceConfig), anyBoolean(), any(Node.class));

		setInternalState(ServiceConfig.class, XPF, xPathFactory);

		PowerMockito.when(XPathFactory.newInstance()).thenReturn(xPathFactory);
		PowerMockito.when(SolrServerReader.createSolrServer(URL)).thenReturn(solrServer);
		PowerMockito.doReturn(URL).when(ServiceConfig.class, GET_TEXT_CONTENT, node);

		when(xPathFactory.newXPath()).thenReturn(xPath);
		when(xPath.evaluate(XPATH_EXPRESSION, document, XPathConstants.NODESET)).thenReturn(nodeList);
		when(namedNodeMap.getNamedItem(PING_Q)).thenReturn(node);
		when(node.getAttributes()).thenReturn(namedNodeMap);
		when(nodeList.getLength()).thenReturn(2);
		when(nodeList.item(0)).thenReturn(node);
		when(nodeList.item(1)).thenReturn(node);
	}

	@Test
	@Ignore("02/21/2019 Ignoring due to incompatibility with offline instrumentation. Fix in FIRSTRAIN-21726")
	public void populateSolrServersBaseForEntityServerURL() throws Exception {
		// Arrange
		when(node.getNodeName()).thenReturn(returned);

		// Act
		invokeMethod(ServiceConfig.class, POPULATE_SOLR_SERVERS_BASE, serviceConfig, document);

		// Assert
		assertTrue(serviceConfig.toString().contains(expected));
	}
}