package com.formulasearchengine.mathmltools.mml.elements;

import static org.xmlunit.util.Convert.toInputSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.formulasearchengine.mathmltools.xmlhelper.PartialLocalEntityResolver;
import com.formulasearchengine.mathmltools.xmlhelper.XMLHelper;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlunit.builder.Input;
import org.xmlunit.util.IterableNodeList;
import org.xmlunit.validation.JAXPValidator;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationProblem;
import org.xmlunit.validation.ValidationResult;
import org.xmlunit.validation.Validator;

public class MathDoc {
    private static final Logger log = LogManager.getLogger("Math");
    private static final String DOCTYPE = "<!DOCTYPE math PUBLIC \"-//W3C//DTD MATHML 3.0 Transitional//EN\" \n"
            + "     \"http://www.w3.org/Math/DTD/mathml3/mathml3.dtd\">\n";
    private static final String MATHML3_XSD = "https://www.w3.org/Math/XMLSchema/mathml3/mathml3.xsd";
    private static final String APPLICATION_X_TEX = "application/x-tex";
    private static Validator v;
    private Source source;
    private Document dom;
    private List<CSymbol> cSymbols = null;

    /**
     * Generates a Math tag from a valid xml String.
     *
     * @param inputXMLString a valid XML String
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public MathDoc(String inputXMLString) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder documentBuilder = getDocumentBuilder();
        try {
            buildDom(inputXMLString, documentBuilder);
        } catch (Exception e) {
            log.warn("Error parsing input \n{}\n. Adding MathML3 Document headers.", inputXMLString);
            inputXMLString = tryFixHeader(inputXMLString);
            try {
                buildDom(inputXMLString, documentBuilder);
            } catch (SAXException | IOException e1) {
                // Throw the exception caused by the original input, not by the corrected input.
                throw e;
            }
        }
    }

    static String tryFixHeader(String inputXMLString) {
        final StringBuffer input = new StringBuffer(inputXMLString);
        XMLHelper.removeXmlDeclaration(input);
        XMLHelper.removeDoctype(input);
        inputXMLString = DOCTYPE + input;
        return inputXMLString;
    }

    /**
     * NOTE! Currently no new annotations are added
     *
     * @param newTeX
     */
    void changeTeXAnnotation(String newTeX) {
        dom.getDocumentElement().setAttribute("alttext", newTeX);
        if (getAnnotationElements().getLength() > 0) {
            log.trace("Found annotation elements");
            for (Node node : new IterableNodeList(getAnnotationElements())) {
                if (node.getAttributes().getNamedItem("encoding").getNodeValue().equals(APPLICATION_X_TEX)) {
                    log.trace("Found annotation elements with encoding {}", APPLICATION_X_TEX);
                    node.setTextContent(newTeX);
                }
            }
        } else {
            throw new NotImplementedException("Implement no annotations case.");
        }
    }

    private NodeList getAnnotationElements() {
        return dom.getElementsByTagName("annotation");
    }

    private void buildDom(String inputXMLString, DocumentBuilder documentBuilder) throws SAXException, IOException {
        source = Input.fromString(inputXMLString).build();
        final InputSource is = toInputSource(source);
        dom = documentBuilder.parse(is);
    }

    Iterable<ValidationProblem> getValidationProblems() throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
        ValidationResult result = getValidationResult();
        return result.getProblems();
    }

    private ValidationResult getValidationResult() throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
        Validator v = getXsdValidator();
        return v.validateInstance(Input.fromDocument(dom).build());
    }

    private static Validator getXsdValidator() throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
        if (v == null) {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(Languages.W3C_XML_SCHEMA_NS_URI);
            final PartialLocalEntityResolver resolver = new PartialLocalEntityResolver();
            schemaFactory.setResourceResolver(resolver);
            v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI, schemaFactory);
            final InputSource inputSource = resolver.resolveEntity("math", MATHML3_XSD);
            assert inputSource != null;
            final StreamSource streamSource = new StreamSource(inputSource.getByteStream());
            streamSource.setPublicId(inputSource.getPublicId());
            streamSource.setSystemId(inputSource.getSystemId());
            v.setSchemaSource(streamSource);
        }
        return v;
    }

    static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = getDocumentBuilderFactory();
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setErrorHandler(new ThrowAllErrorHandler());
        db.setEntityResolver(new PartialLocalEntityResolver());
        return db;
    }

    private static DocumentBuilderFactory getDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);
        dbf.setFeature("http://xml.org/sax/features/validation", true);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbf.setNamespaceAware(true);
        return dbf;
    }

    String serializeDom() throws TransformerException {
        return XMLHelper.printDocument(dom);
    }

    @Override
    public String toString() {
        try {
            return serializeDom();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    void fixGoldCd() {
        getSymbolsFromCd("latexml").filter(n -> n.getCName().startsWith("Q")).forEach(cSymbol -> {
            log.trace("Processing symbol {}", cSymbol);
            cSymbol.setCd("wikidata");
        });

    }

    private Stream<CSymbol> getSymbolsFromCd(String cd) {
        return getCSymbols().stream().filter(n -> n.getCd().equals(cd));
    }

    List<CSymbol> getCSymbols() {
        if (cSymbols == null) {
            final IterableNodeList nodeList = new IterableNodeList(dom.getElementsByTagName("csymbol"));
            cSymbols = new ArrayList<>();
            nodeList.forEach(n -> cSymbols.add(new CSymbol((Element) n, false)));
        }
        return cSymbols;
    }
}
