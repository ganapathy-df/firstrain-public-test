package com.firstrain.frapi.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public final class CompanyEndingLoader {

    public static final String ROOT_VARIANT = "root/variants/variant";
    private static final Logger logger = Logger.getLogger(CompanyEndingLoader.class);

    private CompanyEndingLoader() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static void loadCompanyEnding(Set<String> words, Set<String> wordsRegex,
            DocumentBuilderFactory documentBuilderFactory, InputStream inputStream) {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            XPath xPath = xPathFactory.newXPath();
            loadCompanyEnding(words, wordsRegex, xPath, document);
        } catch (XPathExpressionException e) {
            logError(e);
        } catch (SAXException e) {
            logError(e);
        } catch (IOException e) {
            logError(e);
        } catch (ParserConfigurationException e) {
            logError(e);
        }
    }

    private static void logError(Exception exception) {
        logger.error("Error while loading CompanyEndingWords form resource/endings.xml", exception);
    }

    public static void loadCompanyEnding(Set<String> words, Set<String> wordsRegex, XPath xPath, Document document)
            throws XPathExpressionException {
        Object object = xPath.evaluate(ROOT_VARIANT, document, XPathConstants.NODESET);
        NodeList solrNodes = (NodeList) object;
        for (int index = 0; index < solrNodes.getLength(); ++index) {
            loadCompanyEnding(words, wordsRegex, solrNodes, index);
        }
        logger.info("Loaded <" + words.size() + "> company ending words from resource/endings.xml");
    }

    public static void loadCompanyEnding(Set<String> words, Set<String> wordsRegex, NodeList solrNodes, int index) {
        Node node = solrNodes.item(index);
        Node childNode = node.getFirstChild();
        loadCompanyEnding(words, wordsRegex, childNode);
    }

    public static void loadCompanyEnding(Set<String> words, Set<String> wordsRegex, Node childNode) {
        if (childNode instanceof Text) {
            Text text = (Text) childNode;
            String word = text.getData().toLowerCase();
            words.add(word);
            wordsRegex.add("(?i)[,'.& ]\\s*" + Pattern.quote(word) + "$");
        }
    }

}
