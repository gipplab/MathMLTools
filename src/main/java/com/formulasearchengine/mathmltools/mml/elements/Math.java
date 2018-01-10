package com.formulasearchengine.mathmltools.mml.elements;

import static org.xmlunit.util.Convert.toInputSource;

import java.io.IOException;
import java.net.URISyntaxException;

import com.formulasearchengine.mathmltools.xmlhelper.PartialLocalEntityResolver;
import com.formulasearchengine.mathmltools.xmlhelper.XMLHelper;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlunit.builder.Input;
import org.xmlunit.validation.JAXPValidator;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationProblem;
import org.xmlunit.validation.ValidationResult;
import org.xmlunit.validation.Validator;

public class Math {
    private static final Logger log = LogManager.getLogger("Math");
    private static final String DOCTYPE = "<!DOCTYPE math PUBLIC \"-//W3C//DTD MATHML 3.0 Transitional//EN\" \n"
            + "     \"http://www.w3.org/Math/DTD/mathml3/mathml3.dtd\">\n";
    private static final String MATHML3_XSD = "https://www.w3.org/Math/XMLSchema/mathml3/mathml3.xsd";
    private static Validator v;
    private Source source;
    private Document dom;

    /**
     * Generates a Math tag from a valid xml String.
     *
     * @param inputXMLString a valid XML String
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Math(String inputXMLString) throws ParserConfigurationException, SAXException, IOException {
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

    public static String tryFixHeader(String inputXMLString) {
        final StringBuffer input = new StringBuffer(inputXMLString);
        XMLHelper.removeXmlDeclaration(input);
        XMLHelper.removeDoctype(input);
        inputXMLString = DOCTYPE + input;
        return inputXMLString;
    }

    private void buildDom(String inputXMLString, DocumentBuilder documentBuilder) throws SAXException, IOException {
        source = Input.fromString(inputXMLString).build();
        final InputSource is = toInputSource(source);
        dom = documentBuilder.parse(is);
    }

    public Iterable<ValidationProblem> getValidationProblems() throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
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

    public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
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

}
