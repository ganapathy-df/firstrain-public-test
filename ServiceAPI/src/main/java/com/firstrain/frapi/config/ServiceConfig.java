package com.firstrain.frapi.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.LogMF;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import com.firstrain.content.similarity.DocumentSimilarityUtil;
import com.firstrain.solr.client.DistributedSearchConfigBuilder;
import com.firstrain.solr.client.DistributedSolrSearcher.DistributedSearchConfig;
import com.firstrain.solr.client.util.SolrServerReader;
import com.firstrain.utils.FR_Loader;

/**
 * This object is required by service providers as it contains various parameters needed to initialize the service properly.
 *
 */
public class ServiceConfig {

	public static final String CONFIG_PATH_JNDI_NAME = "java:comp/env/FR/PortalServices/ServiceConfig";
	public static final String DEFAULT_FILENAME = "ServiceConfig.xml";
	private static final int DEFAULT_RETRY_DELAY_SECONDS = 60;
    private static final String RETRY_DELAY_NODE_NAME = "retryDelaySeconds";

	private static final Logger LOG = Logger.getLogger(ServiceConfig.class);

	private static ServiceConfig serviceConfig = null;

	public static ServiceConfig getInstance(String configFilePath) throws ServiceException {
		if (serviceConfig == null) {
			try {
				if (configFilePath != null && !configFilePath.isEmpty()) {
					serviceConfig = loadFromXML(configFilePath);
				} else {
					serviceConfig = ServiceConfig.load();
				}
			} catch (ServiceException e) {
				throw new RuntimeException("Service Config not initialized properly." + e.getMessage());
			}
		}
		return serviceConfig;
	}

	public static ServiceConfig getInstance() throws ServiceException {
		if (serviceConfig == null) {
			try {
				serviceConfig = ServiceConfig.load();
			} catch (ServiceException e) {
				throw new RuntimeException("Service Config not initialized properly." + e.getMessage());
			}
		}
		return serviceConfig;
	}

	private SolrServer entitySolrServer = null;
	private String entitySolrServerUrl = null;
	private SolrServer smartSummarySolrServer = null;
	private String smartSummarySolrServerUrl = null;
	private SolrServer peopleSolrServer = null;
	private String peopleSolrServerUrl = null;
	private SolrServer secSolrServer = null;
	private String secSolrServerUrl = null;
	private SolrServer eventSolrServer = null;
	private String eventSolrServerUrl = null;
	private String userSearchesSolrServerUrl = null;
	private SolrServer userSearchesSolrServer = null;
	private SolrServer docImageSolrServer = null;
	private String docImageSolrServerUrl = null;
	private SolrServer faviconSolrServer = null;
	private String faviconSolrServerUrl = null;
	private SolrServer twitterSolrServer = null;
	private String twitterSolrServerUrl = null;
	private SolrServer tweetGroupSolrServer = null;
	private String tweetGroupSolrServerURL = null;
	private SolrServer companyModelServer = null;
	private String companyModelServerURL = null;
	private SolrServer personDocServer = null;
	private String personDocServerURL = null;
	private SolrServer quoteSolrServer = null;
	private String quoteSolrServerURL = null;
	private int retryDelaySeconds = DEFAULT_RETRY_DELAY_SECONDS;

	private SolrServer docSolrServer = null;
	private String distributedSearchFile = "DistributedDocServer.xml";
	private DistributedSearchConfig distributedSearchConfig = null;
	private DocumentSimilarityUtil documentSimilarityUtil = null;

	private Map<String, String> props;

	private Map<SolrServer, String> solrVsPingQ = new HashMap<SolrServer, String>();
	private String distributedDSPingQ;

	public SolrServer getEntitySolrServer() {
		return this.entitySolrServer;
	}

	public void setEntitySolrServer(SolrServer solrServer) {
		this.entitySolrServer = solrServer;
	}

	public void setPeopleSolrServer(SolrServer peopleSolrServer) {
		this.peopleSolrServer = peopleSolrServer;
	}

	public SolrServer getPeopleSolrServer() {
		return this.peopleSolrServer;
	}

	/**
	 * @return the secSolrServer
	 */
	public SolrServer getSecSolrServer() {
		return secSolrServer;
	}

	/**
	 * @param secSolrServer the secSolrServer to set
	 */
	public void setSecSolrServer(SolrServer secSolrServer) {
		this.secSolrServer = secSolrServer;
	}

	/**
	 * @return the eventSolrServer
	 */
	public SolrServer getEventSolrServer() {
		return eventSolrServer;
	}

	/**
	 * @param eventSolrServer the eventSolrServer to set
	 */
	public void setEventSolrServer(SolrServer eventSolrServer) {
		this.eventSolrServer = eventSolrServer;
	}

	public SolrServer getUserSearchesServer() {
		return this.userSearchesSolrServer;
	}

	public void setUserSearchesSolrServer(SolrServer userSearchesSolrServer) {
		this.userSearchesSolrServer = userSearchesSolrServer;
	}

	public SolrServer getDocImageSolrServer() {
		return this.docImageSolrServer;
	}

	public void setDocImageSolrServer(SolrServer docImageSolrServer) {
		this.docImageSolrServer = docImageSolrServer;
	}

	public SolrServer getFaviconSolrServer() {
		return this.faviconSolrServer;
	}

	public void setFaviconSolrServer(SolrServer faviconSolrServer) {
		this.faviconSolrServer = faviconSolrServer;
	}

	public void setSmartSummarySolrServer(SolrServer smartSummarySolrServer) {
		this.smartSummarySolrServer = smartSummarySolrServer;
	}

	public SolrServer getSmartSummarySolrServer() {
		return this.smartSummarySolrServer;
	}

	public void setTwitterSolrServer(SolrServer twitterSolrServer) {
		this.twitterSolrServer = twitterSolrServer;
	}

	public SolrServer getTwitterSolrServer() {
		return twitterSolrServer;
	}

	public void setTweetGroupSolrServer(SolrServer tweetGroupSolrServer) {
		this.tweetGroupSolrServer = tweetGroupSolrServer;
	}

	public SolrServer getTweetGroupSolrServer() {
		return tweetGroupSolrServer;
	}

	public void setCompanyModelServer(SolrServer companyModelServer) {
		this.companyModelServer = companyModelServer;
	}

	public SolrServer getCompanyModelServer() {
		return companyModelServer;
	}

	public void setPersonDocServer(SolrServer personDocServer) {
		this.personDocServer = personDocServer;
	}

	public SolrServer getPersonDocServer() {
		return personDocServer;
	}

	public SolrServer getDocSolrServer() {
		return this.docSolrServer;
	}

	public void setDocSolrServer(SolrServer docSolrServer) {
		this.docSolrServer = docSolrServer;
	}

	public DistributedSearchConfig getDistributedSearchConfig() {
		return distributedSearchConfig;
	}

	public int getRetryDelaySeconds() {
		return retryDelaySeconds;
	}

	public void setRetryDelaySeconds(int retryDelaySeconds) {
		this.retryDelaySeconds = retryDelaySeconds;
	}

	public void setDistributedSearchConfig(DistributedSearchConfig distributedSearchConfig) {
		this.distributedSearchConfig = distributedSearchConfig;
		if (this.distributedDSPingQ != null && !this.distributedDSPingQ.isEmpty() && distributedSearchConfig != null) {
			for (int i = 0; i < distributedSearchConfig.docServers.length; i++) {
				solrVsPingQ.put(distributedSearchConfig.docServers[i], this.distributedDSPingQ);
			}
		}
	}

	public DocumentSimilarityUtil getDocumentSimilarityUtil() {
		return documentSimilarityUtil;
	}

	public void setDocumentSimilarityUtil(String pathStopList, String pathDict, int distanceThreshold) throws Exception {
		try {
			DocumentSimilarityUtil dsutil = new DocumentSimilarityUtil(pathStopList, pathDict, null, null);
			dsutil.setDistanceThreshold(distanceThreshold);
			dsutil.setIgnoreCategoryFiltering(true);
			dsutil.setIgnoreMaxCandidatesPerMail(true);
			dsutil.setIndexDistancesForResearch(false);
			dsutil.setComputeWordnetDistance(false);
			dsutil.setComputeWordPrefixDistance(true);

			this.documentSimilarityUtil = dsutil;
		} catch (Exception e) {
			LOG.error("Exception initializing DocumentSimilarityUtil ", e);
			throw e;
		}
	}

	public Map<SolrServer, String> getSolrVsPingQ() {
		return solrVsPingQ;
	}

	private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	private static XPathFactory xpf = XPathFactory.newInstance();


	static ServiceConfig loadFromXML(InputSource is) throws ServiceException {
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(is);
			LOG.debug("The class of w3c Document object implementation is: " + doc.getClass());
			if (doc.getClass().getName().indexOf("crimson") != -1) {
				LOG.warn("Obsolete crimson xml parser is being used. Please use the standard JDK in-built parser instead.");
			}
			ServiceConfig rv = new ServiceConfig();
			populateProperties(rv, doc);
			populateSolrServersBase(rv, doc);
			populateDocSimilarityConf(rv, doc);
			if (rv.distributedSearchFile != null && rv.entitySolrServerUrl != null && rv.getDistributedSearchConfig() == null
					&& rv.getDocSolrServer() == null) {
                int retryCount = 0;
                DistributedSearchConfig config;
                do {
                    if (retryCount > 0) {
                        try {
                            LogMF.info(LOG, "Waiting for {0} seconds for solr to be available "
                                            + "retry number: {1}", rv.getRetryDelaySeconds(), retryCount);
                            TimeUnit.SECONDS.sleep(rv.getRetryDelaySeconds());
                        } catch (InterruptedException e) {
                            LOG.error("Delay was interrupted", e);
                            Thread.currentThread().interrupt();
                        } catch (RuntimeException e) {
                            LOG.error("Exception during sleep", e);
                        }
                    }
                    config = DistributedSearchConfigBuilder.createFrom(rv.getEntitySolrServer(),
                            rv.distributedSearchFile);
                    if (config == null) {
                        retryCount++;
                    } else {
                        retryCount = 0;
                    }
                } while (retryCount > 0 && retryCount < Integer.MAX_VALUE);
                if (config == null) {
                    LOG.fatal("It was impossible to connect with Solr url: {0} execution will be stopped");
                    throw new RuntimeException("It was impossible to connect with Solr url: " + rv.getEntitySolrServer() +
                            " execution will be stopped");
                }
                rv.setDistributedSearchConfig(config);
			}
			rv.logPingSolrInfo();
			return rv;
		} catch (Exception e) {
			LOG.error("Error loading service config", e);
			throw new ServiceException(e);
		}
	}

	private static void populateProperties(ServiceConfig rv, Document doc) throws XPathExpressionException {
		XPath xp = xpf.newXPath();
		Object o = xp.evaluate("//ServiceConfig/MiscProperties/property", doc, XPathConstants.NODESET);
		HashMap<String, String> props = new HashMap<String, String>();
		if (o != null && (o instanceof NodeList)) {
			NodeList nl = (NodeList) o;
			for (int i = 0; i < nl.getLength(); ++i) {
				Node n = nl.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				Element e = (Element) n;
				String name = e.getAttribute("name");
				if (name == null) {
					continue;
				}
				String value = e.getAttribute("value");
				if (value == null) {
					continue;
				}
				props.put(name, value.trim());
			}
		}
		rv.props = props;
	}

	private static void populateDocSimilarityConf(ServiceConfig rv, Document doc) throws Exception {
		XPath xp = xpf.newXPath();
		Object o = xp.evaluate("//ServiceConfig/DocSimilarity/*", doc, XPathConstants.NODESET);

		if (o != null && o instanceof NodeList) {
			String pathStopList = null;
			String pathDict = null;
			int distanceThreshold = 50;

			NodeList dsNodes = (NodeList) o;
			for (int i = 0; i < dsNodes.getLength(); ++i) {
				Node n = dsNodes.item(i);
				String ds = n.getNodeName();
				String dsName = n.getTextContent().trim();
				/*
				 * <pathStopList>E:\\firstRain\\Tomcat6\\webapps\\solr\\config\\FinalStopList.txt</pathStopList>
				 * <pathDict>E:\\firstRain\\Tomcat6\\webapps\\solr\\config\\dict</pathDict> <threshhold>50</threshhold>
				 */
				if (ds.equals("pathStopList")) {
					pathStopList = dsName;
				} else if (ds.equals("pathDict")) {
					pathDict = dsName;
				} else if (ds.equals("threshhold")) {
					distanceThreshold = (Integer.valueOf(dsName));
				}
			}
			rv.setDocumentSimilarityUtil(pathStopList, pathDict, distanceThreshold);
		}
	}

	private static void populateSolrServersBase(ServiceConfig rv, Document doc) throws Exception {
		XPath xp = xpf.newXPath();
		Object o = xp.evaluate("//ServiceConfig/Solr/*", doc, XPathConstants.NODESET);
		boolean foundDocServer = false;
		boolean foundEntityServer = false;
		boolean foundPeopleServer = false;
		boolean foundSECServer = false;
		boolean foundEventServer = false;
		boolean foundUserSearchesServerURL = false;
		boolean foundDocImageServerURL = false;
		boolean foundFavIconServerURL = false;
		boolean foundSmartSummaryServerURL = false;
		boolean foundTwitterServerURL = false;
		boolean foundTweetGroupServerURL = false;
		boolean foundCompanyModelServerURL = false;
		boolean foundPersonDocServerURL = false;
		boolean foundQuoteServerURL = false;
		if (o != null && o instanceof NodeList) {
			NodeList solrNodes = (NodeList) o;
			for (int i = 0; i < solrNodes.getLength(); ++i) {
				Node n = solrNodes.item(i);
				String name = n.getNodeName();
				if (name.equals("EntityServerURL")) {
					final String url = getTextContent(n).trim();
					if (foundEntityServer) {
						LOG.warn("Found multiple entity server defs. Ignoring: " + url);
						continue;
					}
					SolrServer es = SolrServerReader.createSolrServer(url);
					rv.setEntitySolrServer(es);
					rv.entitySolrServerUrl = url;

					foundEntityServer = retrievePingQAndAdd(n, rv, es); 
					
				} else if (name.equals("PeopleServerURL")) {
					final String url = getTextContent(n).trim();
					if (foundPeopleServer) {
						LOG.warn("Found multiple people server defs. Ignoring: " + url);
						continue;
					}
					SolrServer es = SolrServerReader.createSolrServer(url);
					rv.setPeopleSolrServer(es);
					rv.peopleSolrServerUrl = url;

					foundPeopleServer = retrievePingQAndAdd(n, rv, es); 
					
				} else if (name.equals("SECServerURL")) {
					isFoundServer(ServerUrl.SEC_SERVER_URL, rv, foundSECServer, n);
					foundSECServer = true;
				} else if (name.equals("EventServerURL")) {
					final String url = getTextContent(n).trim();
					if (foundEventServer) {
						LOG.warn("Found multiple Event server defs. Ignoring: " + url);
						continue;
					}
					SolrServer es = SolrServerReader.createSolrServer(url);
					rv.setEventSolrServer(es);
					rv.eventSolrServerUrl = url;

					foundEventServer = retrievePingQAndAdd(n, rv, es); 
					
				} else if (name.equals("UserSearchesServerURL")) {
					isFoundServer(ServerUrl.USER_SEARCHES_SERVER_URL, rv, foundUserSearchesServerURL, n);
					foundUserSearchesServerURL = true;
				} else if (name.equals("DocImageServerURL")) {
					final String url = getTextContent(n).trim();
					if (foundDocImageServerURL) {
						LOG.warn("Found multiple DocImageServerURL server defs. Ignoring: " + url);
						continue;
					}
					SolrServer es = SolrServerReader.createSolrServer(url);
					rv.setDocImageSolrServer(es);
					rv.docImageSolrServerUrl = url;

					foundDocImageServerURL = retrievePingQAndAdd(n, rv, es); 
					
				} else if (name.equals("FavIconServerURL")) {
					isFoundServer(ServerUrl.FAV_ICON_SERVER_URL, rv, foundFavIconServerURL, n);
					foundFavIconServerURL = true;
				} else if (name.equals("SmartSummaryServerURL")) {
					final String url = getTextContent(n).trim();
					if (foundSmartSummaryServerURL) {
						LOG.warn("Found multiple SmartSummaryServerURL server defs. Ignoring: " + url);
						continue;
					}
					SolrServer ss = SolrServerReader.createSolrServer(url);
					rv.setSmartSummarySolrServer(ss);
					rv.smartSummarySolrServerUrl = url;

					foundSmartSummaryServerURL = retrievePingQAndAdd(n, rv, ss); 
					
				} else if (name.equals("TwitterServerURL")) {
					isFoundServer(ServerUrl.TWITTER_SERVER_URL, rv, foundTwitterServerURL, n);
					foundTwitterServerURL = true;

				} else if (name.equals("TweetGroupServerURL")) {
					final String url = getTextContent(n).trim();
					if (foundTweetGroupServerURL) {
						LOG.warn("Found multiple TweetGroupServerURL server defs. Ignoring: " + url);
						continue;
					}
					SolrServer ss = SolrServerReader.createSolrServer(url);
					rv.setTweetGroupSolrServer(ss);
					rv.tweetGroupSolrServerURL = url;

					foundTweetGroupServerURL = retrievePingQAndAdd(n, rv, ss); 
					

				} else if (name.equals("CompanyModelServerURL")) {
					isFoundServer(ServerUrl.COMPANY_MODEL_SERVER_URL, rv, foundCompanyModelServerURL, n);
					foundCompanyModelServerURL = true;

				} else if (name.equals("PersonDocServerURL")) {
					final String url = getTextContent(n).trim();
					if (foundPersonDocServerURL) {
						LOG.warn("Found multiple PersonDocServerURL server defs. Ignoring: " + url);
						continue;
					}
					SolrServer ss = SolrServerReader.createSolrServer(url);
					rv.setPersonDocServer(ss);
					rv.personDocServerURL = url;

					foundPersonDocServerURL = retrievePingQAndAdd(n, rv, ss); 
					

				} else if (name.equals("QuoteServerURL")) {
					isFoundServer(ServerUrl.QUOTE_SERVER_URL, rv, foundQuoteServerURL, n);
					foundQuoteServerURL = true;

				} else if (name.equals("DocServerURL")) {
					final String url = getTextContent(n).trim();
					if (foundDocServer) {
						LOG.warn("Found multiple doc server defs. Ignoring: " + url);
						continue;
					}
					SolrServer ds = SolrServerReader.createSolrServer(url);
					rv.setDocSolrServer(ds);

					foundDocServer = retrievePingQAndAdd(n, rv, ds); 
					
				} else if (name.equals("DistributedDocServer")) {
					if (foundDocServer) {
						LOG.warn("Found multiple doc server defs. Ignoring: " + getTextContent(n).trim());
						continue;
					}
					processDistributeDocServerFile(rv, n);
					foundDocServer = true;
				} else if (name.equals(RETRY_DELAY_NODE_NAME)) {
					rv.setRetryDelaySeconds(Integer.parseInt(getTextContent(n).trim()));
				}
			}
		}
	}
 
	private static boolean retrievePingQAndAdd(final Node n, final ServiceConfig rv, final SolrServer es) { 
		addNodeToSolrVsPingQ(n, rv, es);
		return true; 
	} 
	

	private static void isFoundServer(ServerUrl serverUrl, ServiceConfig server, boolean serverFound, Node node)
			throws MalformedURLException {
		final String url = getTextContent(node).trim();
		if (serverFound) {
			LOG.warn(String.format("Found multiple %s server defs. Ignoring: %s", serverUrl.getDescription(), url));
		} else {
			SolrServer solrServer = SolrServerReader.createSolrServer(url);
			switch (serverUrl) {
				case SEC_SERVER_URL:
					server.setSecSolrServer(solrServer);
					server.secSolrServerUrl = url;
					break;
				case USER_SEARCHES_SERVER_URL:
					server.setUserSearchesSolrServer(solrServer);
					server.userSearchesSolrServerUrl = url;
					break;
				case FAV_ICON_SERVER_URL:
					server.setFaviconSolrServer(solrServer);
					server.faviconSolrServerUrl = url;
					break;
				case TWITTER_SERVER_URL:
					server.setTwitterSolrServer(solrServer);
					server.twitterSolrServerUrl = url;
					break;
				case COMPANY_MODEL_SERVER_URL:
					server.setCompanyModelServer(solrServer);
					server.companyModelServerURL = url;
					break;
				case QUOTE_SERVER_URL:
					server.setQuoteSolrServer(solrServer);
					server.quoteSolrServerURL = url;
					break;
				default:
					// Do nothing
			}

			addNodeToSolrVsPingQ(node, server, solrServer);
		}
	}

	private static void addNodeToSolrVsPingQ(final Node node, final ServiceConfig server, final SolrServer solrServer) {
		Node pingQ = node.getAttributes().getNamedItem("pingQ");
		if (pingQ != null) {
			server.solrVsPingQ.put(solrServer, pingQ.getNodeValue());
		}
	}

	private static void processDistributeDocServerFile(ServiceConfig rv, Node n) throws Exception {
		if (n instanceof Element) {
			Element e = (Element) n;
			String fileName = e.getAttribute("file").trim();
			rv.distributedDSPingQ = e.getAttribute("pingQ").trim();

			if (fileName != null && fileName.length() > 0) {
				rv.distributedSearchFile = fileName;
			} else {
				rv.distributedSearchFile = null;
				rv.setDistributedSearchConfig(DistributedSearchConfigBuilder.createFrom(n));
			}
		}
	}

	private static String getTextContent(Node n) {
		Node c = n.getFirstChild();
		if (c instanceof Text) {
			Text e = (Text) c;
			return e.getData();
		}
		return null;
	}

	private static ServiceConfig loadFromXML(InputStream is) throws ServiceException {
		InputSource source = new InputSource(is);
		return loadFromXML(source);
	}

	private static ServiceConfig loadFromXML(File f) throws ServiceException {
		try {
			InputStream stream = new BufferedInputStream(new FileInputStream(f));
			ServiceConfig config = loadFromXML(stream);
			stream.close();
			return config;
		} catch (IOException e) {
			LOG.error("Error reading service config", e);
			throw new ServiceException(e);
		}
	}

	private static ServiceConfig loadFromXML(String configFilePath) throws ServiceException {
		File f = new File(configFilePath);
		return loadFromXML(f);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nEntitySolrServer:");
		sb.append(this.getEntitySolrServer() != null ? this.entitySolrServerUrl : "NULL");
		sb.append("\nPeopleSolrServer:");
		sb.append(this.getPeopleSolrServer() != null ? this.peopleSolrServerUrl : "NULL");
		sb.append("\nSECSolrServer:");
		sb.append(this.getSecSolrServer() != null ? this.secSolrServerUrl : "NULL");
		sb.append("\nEventSolrServer:");
		sb.append(this.getEventSolrServer() != null ? this.eventSolrServerUrl : "NULL");
		sb.append("\nUserSearchesSolrServer:");
		sb.append(this.getUserSearchesServer() != null ? this.userSearchesSolrServerUrl : "NULL");
		sb.append("\nDocImageServer:");
		sb.append(this.getDocImageSolrServer() != null ? this.docImageSolrServerUrl : "NULL");
		sb.append("\nFavIconServer:");
		sb.append(this.getFaviconSolrServer() != null ? this.faviconSolrServerUrl : "NULL");
		sb.append("\nSmartSummaryServer:");
		sb.append(this.getSmartSummarySolrServer() != null ? this.smartSummarySolrServerUrl : "NULL");
		sb.append("\nTwitterServer:");
		sb.append(this.getTwitterSolrServer() != null ? this.twitterSolrServerUrl : "NULL");
		sb.append("\nTweetGroupServerURL:");
		sb.append(this.getTweetGroupSolrServer() != null ? this.tweetGroupSolrServerURL : "NULL");
		sb.append("\nCompanyModelServerURL:");
		sb.append(this.getCompanyModelServer() != null ? this.companyModelServerURL : "NULL");
		sb.append("\nPersonDocServerURL:");
		sb.append(this.getPersonDocServer() != null ? this.personDocServerURL : "NULL");
		sb.append("\nQuoteServerURL:");
		sb.append(this.getQuoteSolrServer() != null ? this.quoteSolrServerURL : "NULL");
		sb.append("\nDocSolrServer:");
		sb.append(this.getDocSolrServer() != null ? "NOT NULL" : "NULL");
		sb.append("\nDistributedSearchConfig:");
		sb.append(this.getDistributedSearchConfig() != null ? "NOT NULL" : "NULL");
		if (this.getDistributedSearchConfig() != null) {
			sb.append("\n\tServers:");
			sb.append(this.getDistributedSearchConfig().docServers != null ? this.getDistributedSearchConfig().docServers.length : "0");
		}
		if (this.props != null && !this.props.isEmpty()) {
			for (Entry<String, String> entry : this.props.entrySet()) {
				sb.append("\n");
				sb.append(entry.getKey());
				sb.append(":");
				sb.append(entry.getValue());
			}
		}

		return sb.toString();
	}

	private static ServiceConfig load() throws ServiceException {
		// 1) First try JNDI
		String filename = null;
		try {
			Context c = new InitialContext();
			filename = (String) c.lookup(CONFIG_PATH_JNDI_NAME);
			LOG.info("JNDI lookup for ServiceConfig path : " + filename);
		} catch (NoInitialContextException e) {
			LOG.info("JNDI not configured (NoInitialContextEx)");
		} catch (NamingException e) {
			LOG.info("No ServiceConfig in JNDI: " + e.getMessage());
		} catch (RuntimeException e) {
			LOG.warn("Odd RuntimeException while testing for JNDI: " + e.getMessage());
		}

		if (filename != null) {
			if (filename.trim().startsWith("http://") || filename.trim().startsWith("https://")) {
				LOG.info("Loading service config from url: " + filename);
				InputStream in = FR_Loader.getUrlAsStream(filename);
				if (in != null) {
					return loadFromXML(in);
				}
				LOG.warn("Url: " + filename + " returned NULL stream.");
			} else {
				File f = new File(filename);
				LOG.info("Loading ServiceConfig from file:" + f.getAbsolutePath());
				if (f.exists()) {
					return loadFromXML(f);
				}
				LOG.warn("Cannot find " + f.getAbsolutePath());
			}
		}

		// 2) Try looking in the classpath
		LOG.info("Looking for ServiceConfig from classpath");
		InputStream in = FR_Loader.getResourceAsStream(DEFAULT_FILENAME);
		if (in != null) {
			LOG.info("Loading service config from classpath");
			return loadFromXML(in);
		}

		// 3) Last ditch attempt to look in current working dir.
		File f = new File(DEFAULT_FILENAME);
		LOG.info("Trying to load ServiceConfig in working dir :" + f.getAbsolutePath());
		if (f.exists()) {
			return loadFromXML(f);
		}

		LOG.error("Unable to discover ServiceConfig.xml");

		return null;
	}

	public String getProperty(String propertyName) {
		return props.get(propertyName);
	}

	public SolrServer getQuoteSolrServer() {
		return quoteSolrServer;
	}

	public void setQuoteSolrServer(SolrServer quoteSolrServer) {
		this.quoteSolrServer = quoteSolrServer;
	}

	private void logPingSolrInfo() {
		for (Map.Entry<SolrServer, String> e : this.solrVsPingQ.entrySet()) {
			if (e.getKey() instanceof CommonsHttpSolrServer) {
				LOG.info("Ping query for: " + ((CommonsHttpSolrServer) e.getKey()).getBaseURL() + " -> " + e.getValue());
			}
		}
		if (this.solrVsPingQ.isEmpty()) {
			LOG.warn("Ping query not defined for any SOLR");
		}
	}

	enum ServerUrl {
		SEC_SERVER_URL("SEC"),
		USER_SEARCHES_SERVER_URL("UserSearchesServerURL"),
		FAV_ICON_SERVER_URL("FavIconServerURL"),
		TWITTER_SERVER_URL("TwitterServerURL"),
		COMPANY_MODEL_SERVER_URL("CompanyModelServerURL"),
		QUOTE_SERVER_URL("QuoteServerURL");

		private final String description;

		private ServerUrl(String description) {
			this.description = description;
		}

		private String getDescription() {
			return description;
		}
	}
}
