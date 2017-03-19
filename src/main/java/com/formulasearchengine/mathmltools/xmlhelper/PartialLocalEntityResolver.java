package com.formulasearchengine.mathmltools.xmlhelper;


import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.IOException;

/**
 * Provides local copies of certain DTD rather than downloading them from w3.org all the time, which would most likely result in HTTP 429 (Forbidden due to abuse) after some time.
 * See also https://github.com/TU-Berlin/mathosphere/issues/126#issuecomment-264963633
 * <p>
 * The code is based on https://stackoverflow.com/questions/16137280/how-to-validate-an-xml-file-against-a-given-dtd-file?noredirect=1&lq=1
 * <p>
 * Created by felix on 06.12.16.
 */
public class PartialLocalEntityResolver implements EntityResolver {
    private static final String XHTML_MATH11_F_DTD = "xhtml-math11-f.dtd";

    @Override
    public org.xml.sax.InputSource resolveEntity(String publicId, String systemId) throws org.xml.sax.SAXException, IOException {
        if (systemId.equals("http://www.w3.org/Math/DTD/mathml2/xhtml-math11-f.dtd")) {
            return new InputSource(PartialLocalEntityResolver.class.getResourceAsStream(XHTML_MATH11_F_DTD));
        }
        return null;
    }
}
