package com.formulasearchengine.mathmltools.xmlhelper;

import org.junit.Assert;
import org.junit.Test;

public class XMLHelperTest {

	@Test
	public void testString2Doc () throws Exception {
		Assert.assertNull( XMLHelper.string2Doc( "<open><open2></open2>", true ) );
		Assert.assertNotNull( XMLHelper.string2Doc( "<simple />", true ) );
		XMLHelper x = new XMLHelper(); //Does not really make sense
	}
}