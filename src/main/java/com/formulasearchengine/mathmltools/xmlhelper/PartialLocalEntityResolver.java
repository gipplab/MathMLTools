package com.formulasearchengine.mathmltools.xmlhelper;


import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Provides local copies of certain DTD rather than downloading them from w3.org all the time, which would most likely result in HTTP 429 (Forbidden due to abuse) after some time.
 * See also https://github.com/TU-Berlin/mathosphere/issues/126#issuecomment-264963633
 * <p>
 * The code is based on https://stackoverflow.com/questions/16137280/how-to-validate-an-xml-file-against-a-given-dtd-file?noredirect=1&lq=1
 * <p>
 * Created by felix on 06.12.16.
 */
public class PartialLocalEntityResolver implements EntityResolver {


    private static final Map<String, String> LOCAL_SCHEMA;

    static {
        LOCAL_SCHEMA = ImmutableMap.<String, String>builder()
            .put("http://www.w3.org/Math/DTD/mathml2/xhtml-math11-f.dtd", "xhtml-math11-f.dtd")
            .put("https://www.w3.org/Math/XMLSchema/mathml3/mathml3.xsd", "mathml3.xsd")
            .put("https://www.w3.org/Math/XMLSchema/mathml3/mathml3-content.xsd", "mathml3-content.xsd")
            .put("https://www.w3.org/Math/XMLSchema/mathml3/mathml3-presentation.xsd", "mathml3-presentation.xsd")
            .put("https://www.w3.org/Math/XMLSchema/mathml3/mathml3-common.xsd", "mathml3-common.xsd")
            .put("https://www.w3.org/Math/XMLSchema/mathml3/mathml3-strict-content.xsd", "mathml3-strict-content.xsd")
            .build();
    }

    @Override
    public org.xml.sax.InputSource resolveEntity(String publicId, String systemId) throws org.xml.sax.SAXException, IOException {
        if (LOCAL_SCHEMA.containsKey(systemId)) {
            InputStream resourceAsStream = PartialLocalEntityResolver.class.getResourceAsStream(LOCAL_SCHEMA.get(systemId));
            return new InputSource(resourceAsStream);
        }
        return null;
    }

    public static Map<String, String> getLocalSchema() {
        return LOCAL_SCHEMA;
    }
}
