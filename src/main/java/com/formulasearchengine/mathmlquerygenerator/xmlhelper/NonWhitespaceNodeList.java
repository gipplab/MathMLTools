package com.formulasearchengine.mathmlquerygenerator.xmlhelper;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * from http://stackoverflow.com/questions/229310/how-to-ignore-whitespace-while-reading-a-file-to-produce-an-xml-dom
 */
public class NonWhitespaceNodeList implements NodeList, Iterable<Node> {

	private final List<Node> nodes;

	public NonWhitespaceNodeList( NodeList list ) {
		nodes = new ArrayList<>();
		for ( int i = 0; i < list.getLength(); i++ ) {
			if ( !isWhitespaceNode( list.item( i ) ) ) {
				nodes.add( list.item( i ) );
			}
		}
	}

	public static Node getFirstChild( Node node ) {
		NonWhitespaceNodeList children = new NonWhitespaceNodeList( node.getChildNodes() );
		return children.item( 0 );
	}

	@Override
	public Node item( int index ) {
		return nodes.get( index );
	}

	@Override
	public int getLength() {
		return nodes.size();
	}

	private static boolean isWhitespaceNode( Node n ) {
		if ( n.getNodeType() == Node.TEXT_NODE ) {
			String val = n.getNodeValue();
			return val.trim().length() == 0;
		} else {
			return false;
		}
	}

	@Override
	public Iterator<Node> iterator() {
		return nodes.iterator();
	}

	/**
	 * @return Node or Null if list is empty
	 */
	@SuppressWarnings({ "ReturnOfNull", "SuppressionAnnotation" })
	public final Node getFirstElement() {
		if ( nodes.isEmpty() ) {
			return null;
		} else {
			return item( 0 );
		}
	}
}