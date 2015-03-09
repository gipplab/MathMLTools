package com.formulasearchengine.mathmlquerygenerator;

import com.formulasearchengine.xmlhelper.DomDocumentHelper;
import com.formulasearchengine.xmlhelper.NonWhitespaceNodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

	public NtcirTopicReader( Document topics ) {
		this.topics = topics;
		queryGenerator = new XQueryGenerator( topics );
	}

	public NtcirTopicReader( File topicFile ) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilder documentBuilder = DomDocumentHelper.getDocumentBuilderFactory().newDocumentBuilder();
		topics = documentBuilder.parse( topicFile );

		//TODO: Find out how this code duplication can be avoided in Java.
		queryGenerator = new XQueryGenerator( topics );
	}

	public final NtcirTopicReader setFooter( String footer ) {
		queryGenerator.setFooter( footer );
		return this;
	}

	public final NtcirTopicReader setHeader( String header ) {
		queryGenerator.setHeader( header );
		return this;
	}

	public final NtcirTopicReader setRestrictLength( boolean restrictLength ) {
		queryGenerator.setRestrictLength( restrictLength );
		return this;
	}

	public final List<NtcirPattern> extractPatterns() throws XPathExpressionException {
		final XPath xpath = DomDocumentHelper.namespaceAwareXpath( "t", NS_NII );
		final XPathExpression xNum = xpath.compile( "./t:num" );
		final XPathExpression xFormula = xpath.compile( "./t:query/t:formula" );
		final NonWhitespaceNodeList topicList = new NonWhitespaceNodeList(
			topics.getElementsByTagNameNS( NS_NII, "topic" ) );
		for ( Node node : topicList ) {
			final String num = xNum.evaluate( node );
			final NonWhitespaceNodeList formulae = new NonWhitespaceNodeList( (NodeList)
				xFormula.evaluate( node, XPathConstants.NODESET ) );
			for ( final Node formula : formulae ) {
				final String id = formula.getAttributes().getNamedItem( "id" ).getTextContent();
				final Node mathMLNode = getFirstChild( formula );
				queryGenerator.setMainElement( getFirstChild( mathMLNode ) );
				patterns.add( new NtcirPattern( num, id, queryGenerator.toString(), mathMLNode ) );
			}
		}
		return patterns;
	}
}
