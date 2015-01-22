package com.formulasearchengine.mathmlquerygenerator;

import com.formulasearchengine.xmlhelper.NonWhitespaceNodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.formulasearchengine.xmlhelper.NonWhitespaceNodeList.getFirstChild;

/**
 * Created by Moritz on 08.11.2014.
 * <p/>
 * Reads the topic format specified in
 * http://ntcir-math.nii.ac.jp/wp-content/blogs.dir/13/files/2014/05/NTCIR11-Math-topics.pdf
 */
public class NtcirTopicReader {
	public static final String NS_NII = "http://ntcir-math.nii.ac.jp/";
	private final Document topics;
	private final List<NtcirPattern> patterns = new ArrayList<>();
	private final XQueryGenerator queryGenerator;

	public NtcirTopicReader (Document topics) {
		this.topics = topics;
		queryGenerator = new XQueryGenerator( topics );
	}

	public NtcirTopicReader (File topicFile) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilder documentBuilder = getDocumentBuilderFactory().newDocumentBuilder();
		topics = documentBuilder.parse( topicFile );
		
		//TODO: Find out how this code duplication can be avoided in Java.
		queryGenerator = new XQueryGenerator( topics );
	}

	private static DocumentBuilderFactory getDocumentBuilderFactory () {
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware( true );
		return documentBuilderFactory;
	}

	private static XPath namespaceAwareXpath (final String prefix, final String nsURI) {
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		NamespaceContext ctx = new NamespaceContext() {
			@Override
			public String getNamespaceURI (String aPrefix) {
				if ( aPrefix.equals( prefix ) )
					return nsURI;
				else
					return null;
			}

			@Override
			public Iterator getPrefixes (String val) {
				throw new UnsupportedOperationException();
			}

			@Override
			public String getPrefix (String uri) {
				throw new UnsupportedOperationException();
			}
		};
		xpath.setNamespaceContext( ctx );
		return xpath;
	}

	public NtcirTopicReader setFooter (String footer) {
		queryGenerator.setFooter( footer );
		return this;
	}

	public NtcirTopicReader setHeader (String header) {
		queryGenerator.setHeader( header );
		return this;
	}

	public NtcirTopicReader setRestricLength ( boolean restrictLength) {
		queryGenerator.setRestrictLength( restrictLength );
		return this;
	}

	public List<NtcirPattern> extractPatterns () throws XPathExpressionException {
		XPath xpath = namespaceAwareXpath( "t", NS_NII );
		XPathExpression xNum = xpath.compile( "./t:num" );
		XPathExpression xFormula = xpath.compile( "./t:query/t:formula" );
		NonWhitespaceNodeList topicList = new NonWhitespaceNodeList(
			topics.getElementsByTagNameNS( NS_NII, "topic" ) );
		for ( Node node : topicList ) {
			final String num = xNum.evaluate( node );
			final NonWhitespaceNodeList formulae = new NonWhitespaceNodeList( (NodeList)
				xFormula.evaluate( node, XPathConstants.NODESET ) );
			for ( Node formula : formulae ) {
				String id = formula.getAttributes().getNamedItem( "id" ).getTextContent();
				final Node mathMLNode = getFirstChild( formula );
				queryGenerator.setMainElement( getFirstChild( mathMLNode ) );
				patterns.add( new NtcirPattern( num, id, queryGenerator.toString(), mathMLNode ) );
			}
		}
		return patterns;
	}
}
