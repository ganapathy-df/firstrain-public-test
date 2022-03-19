package com.firstrain.frapi.util;

import static com.firstrain.frapi.util.CompanyEndingLoader.ROOT_VARIANT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.imageio.metadata.IIOMetadataNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class CompanyEndingLoaderTest {

    private Set<String> words;
    private Set<String> wordsRegex;
    private Document document;
    private XPath xPath;
    private Text mockText;
    private DocumentBuilderFactory documentBuilderFactory;
    private InputStream inputStream;

    @Before
    public void setUp() {
        words = new LinkedHashSet<String>();
        wordsRegex = new LinkedHashSet<String>();
        document = mock(Document.class);
        xPath = mock(XPath.class);
        mockText = mock(Text.class);
        documentBuilderFactory = mock(DocumentBuilderFactory.class);
        inputStream = generateInputStream();
    }

    @Test
    public void givenMockTextWhenLoadCompanyEndingRunsThenWordsHasContent() throws Exception {
        // Arrange
        when(mockText.getData()).thenReturn("Sample Value");

        // Act
        CompanyEndingLoader.loadCompanyEnding(words, wordsRegex, mockText);

        // Assert
        assertEquals(1, words.size());

    }

    @Test
    public void givenConditionWhenLoadCompanyEndingRunsThenParserConfigurationExceptionIsThrown() throws Exception {
        // Arrange
        when(documentBuilderFactory.newDocumentBuilder()).thenThrow(new ParserConfigurationException());

        actAndAssertForLoadCompanyEndingWithDocumentBuilderFactory();
    }

    @Test
    public void givenDocumentBuilderFactoryWhenLoadCompanyEndingRunsThenSAXExceptionIsThrown() throws Exception {
        // Arrange
        documentBuilderFactory = DocumentBuilderFactory.newInstance();;

        actAndAssertForLoadCompanyEndingWithDocumentBuilderFactory();
    }

    @Test
    public void givenConditionWhenLoadCompanyEndingRunsThenIOExceptionIsThrown() throws Exception {
        // Arrange
        DocumentBuilder documentBuilder = mock(DocumentBuilder.class);
        when(documentBuilder.parse(inputStream)).thenThrow(new IOException());
        when(documentBuilderFactory.newDocumentBuilder()).thenReturn(documentBuilder);

        actAndAssertForLoadCompanyEndingWithDocumentBuilderFactory();
    }

    private void actAndAssertForLoadCompanyEndingWithDocumentBuilderFactory() {
        // Act
        CompanyEndingLoader.loadCompanyEnding(words, wordsRegex, documentBuilderFactory, inputStream);

        // Assert
        assertEquals(0, words.size());
    }

    @Test
    public void givenGeneratedNodeListWhenLoadCompanyEndingRunsThenWordsSizeIsZero() throws Exception {
        // Arrange
        when(xPath.evaluate(ROOT_VARIANT, document, XPathConstants.NODESET)).thenReturn(generateNodeList());

        actAndAssertForLoadCompanyEnding();
    }

    @Test
    public void givenGeneratedEmptyNodeListWhenLoadCompanyEndingRunsThenWordsSizeIsZero() throws Exception {
        // Arrange
        when(xPath.evaluate(ROOT_VARIANT, document, XPathConstants.NODESET)).thenReturn(generateEmptyNodeList());

        actAndAssertForLoadCompanyEnding();
    }

    private void actAndAssertForLoadCompanyEnding() throws Exception {
        // Act
        CompanyEndingLoader.loadCompanyEnding(words, wordsRegex, xPath, document);

        // Assert
        assertEquals(0, words.size());
    }

    public InputStream generateInputStream() {
        return new InputStream() {

            @Override
            public int read() throws IOException {
                return 0;
            }
        };
    }

    private NodeList generateEmptyNodeList() {
        return new NodeList() {

            @Override
            public Node item(int index) {
                return null;
            }

            @Override
            public int getLength() {
                return 0;
            }
        };
    }

    private NodeList generateNodeList() {
        return new NodeList() {

            @Override
            public Node item(int index) {
                return new IIOMetadataNode();
            }

            @Override
            public int getLength() {
                return 1;
            }
        };
    }

}
