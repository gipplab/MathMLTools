package com.formulasearchengine.mathmltools.mml;

import com.formulasearchengine.mathmltools.helper.XMLHelper;
import com.formulasearchengine.mathmltools.io.XmlDocumentReader;
import com.formulasearchengine.mathmltools.utils.mml.CSymbol;
import com.formulasearchengine.mathmltools.xml.PartialLocalEntityResolver;
import com.sun.org.apache.xerces.internal.dom.DOMInputImpl;
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
import org.xmlunit.validation.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.xmlunit.util.Convert.toInputSource;

public class MathDoc {
    private static final Logger log = LogManager.getLogger("Math");
    private static final String DOCTYPE =
            "<!DOCTYPE math PUBLIC \"-//W3C//DTD MATHML 3.0 Transitional//EN\" \n"
                    + "     \"http://www.w3.org/Math/DTD/mathml3/mathml3.dtd\">\n";

    private static final String DOCTYPE_PREFIX =
            "<!DOCTYPE !!PREFIX!!:math\n"
                    + "     PUBLIC \"-//W3C//DTD MATHML 3.0 Transitional//EN\" \n"
                    + "            \"http://www.w3.org/Math/DTD/mathml3/mathml3.dtd\" [\n"
                    + "     <!ENTITY % MATHML.prefixed \"INCLUDE\">\n"
                    + "     <!ENTITY % MATHML.prefix \"!!PREFIX!!\">\n"
                    + "]>";

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
        DocumentBuilder documentBuilder = XmlDocumentReader.getDocumentBuilder();
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

    public MathDoc(Document dom) {
        this.dom = dom;
    }

    public static String tryFixHeader(String inputXMLString) {
        final StringBuffer input = new StringBuffer(inputXMLString);
        XMLHelper.removeXmlDeclaration(input);
        XMLHelper.removeDoctype(input);
        inputXMLString = DOCTYPE + input;
        return inputXMLString;
    }

    public static String tryFixHeader(String inputXMLString, String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return tryFixHeader(inputXMLString);
        }

        final StringBuffer input = new StringBuffer(inputXMLString);
        String docType = new String(DOCTYPE_PREFIX);
        docType = docType.replace("!!PREFIX!!", prefix);

        XMLHelper.removeXmlDeclaration(input);
        XMLHelper.removeDoctype(input);
        inputXMLString = docType + input;
        return inputXMLString;
    }

    public static DOMInputImpl getMathMLSchema() {
        SchemaInput schemaInput = new SchemaInput().invoke();
        InputSource inputSource = schemaInput.getInputSource();
        final DOMInputImpl input = new DOMInputImpl();
        input.setByteStream(inputSource.getByteStream());
        input.setPublicId(inputSource.getPublicId());
        input.setSystemId(inputSource.getSystemId());
        return input;
    }

    private static Validator getXsdValidator() {
        if (v == null) {
            SchemaInput schemaInput = new SchemaInput().invoke();
            SchemaFactory schemaFactory = schemaInput.getSchemaFactory();
            InputSource inputSource = schemaInput.getInputSource();
            v = new JAXPValidator(Languages.W3C_XML_SCHEMA_NS_URI, schemaFactory);
            final StreamSource streamSource = new StreamSource(inputSource.getByteStream());
            streamSource.setPublicId(inputSource.getPublicId());
            streamSource.setSystemId(inputSource.getSystemId());
            v.setSchemaSource(streamSource);
        }
        return v;
    }

    /**
     * NOTE! Currently no new annotations are added
     *
     * @param newTeX
     */
    public void changeTeXAnnotation(String newTeX) {
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

    public void fixGoldCd() {
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

    public Document getDom() {
        return dom;
    }

    private static class SchemaInput {
        private SchemaFactory schemaFactory;
        private InputSource inputSource;

        SchemaFactory getSchemaFactory() {
            return schemaFactory;
        }

        InputSource getInputSource() {
            return inputSource;
        }

        SchemaInput invoke() {
            schemaFactory = SchemaFactory.newInstance(Languages.W3C_XML_SCHEMA_NS_URI);
            final PartialLocalEntityResolver resolver = new PartialLocalEntityResolver();
            schemaFactory.setResourceResolver(resolver);
            inputSource = resolver.resolveEntity("math", MATHML3_XSD);
            assert inputSource != null;
            return this;
        }
    }
}
