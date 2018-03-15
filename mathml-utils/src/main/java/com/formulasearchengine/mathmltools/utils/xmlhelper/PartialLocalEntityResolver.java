package com.formulasearchengine.mathmltools.utils.xmlhelper;


import java.io.InputStream;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
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
public class PartialLocalEntityResolver implements EntityResolver, LSResourceResolver {
    private static final Logger log = LogManager.getLogger("PartialLocalEntityResolver");


    private static final Map<String, String> SYSTEM_IDS;
    // Files that are internally referred in the MathML3 DTD
    private static final Map<String, String> PUBLIC_IDS;

    static {
        // Notice that moving the files to a subfolder will break the XSD resource resolution
        SYSTEM_IDS = ImmutableMap.<String, String>builder()
                .put("http://www.w3.org/Math/DTD/mathml2/xhtml-math11-f.dtd", "xhtml-math11-f.dtd")
//MathML3 DTD
                .put("http://www.w3.org/Math/DTD/mathml3/mathml3.dtd", "mathml3.dtd")
                .put("http://www.w3.org/Math/DTD/mathml3/htmlmathml-f.ent", "htmlmathml-f.ent")
                .put("http://www.w3.org/Math/DTD/mathml3/mathml3-qname.mod", "mathml3-qname.mod")
//MathML3 XSD
                .put("https://www.w3.org/Math/XMLSchema/mathml3/mathml3.xsd", "mathml3.xsd")
                .put("https://www.w3.org/Math/XMLSchema/mathml3/mathml3-content.xsd", "mathml3-content.xsd")
                .put("https://www.w3.org/Math/XMLSchema/mathml3/mathml3-presentation.xsd", "mathml3-presentation.xsd")
                .put("https://www.w3.org/Math/XMLSchema/mathml3/mathml3-common.xsd", "mathml3-common.xsd")
                .put("https://www.w3.org/Math/XMLSchema/mathml3/mathml3-strict-content.xsd", "mathml3-strict-content.xsd")
                .build();

        PUBLIC_IDS = ImmutableMap.<String, String>builder()
//MathML3 DTD
                .put("-//W3C//ENTITIES MathML 3.0 Qualified Names 1.0//EN", "mathml3-dtd/mathml3-qname.mod")
                .put("-//W3C//ENTITIES HTML MathML Set//EN//XML", "mathml3-dtd/htmlmathml-f.ent")
                .build();
    }

    @Override
    public org.xml.sax.InputSource resolveEntity(String publicId, String systemId) {
        InputStream stream;
        log.trace("Resolving entity (\"" + publicId + "\", \"" + systemId + "\")");
        if (SYSTEM_IDS.containsKey(systemId)) {
            stream = getStream(SYSTEM_IDS.get(systemId));
        } else if (PUBLIC_IDS.containsKey(publicId)) {
            // consider removing this branch since it might never be used
            stream = getStream(PUBLIC_IDS.get(publicId));
        } else {
            log.debug("Can not resolve entity {} {}", systemId, publicId);
            return null;
        }
        final InputSource source = new InputSource(stream);
        source.setSystemId(systemId);
        source.setPublicId(publicId);
        return source;
    }

    private InputStream getStream(String name) {
        return PartialLocalEntityResolver.class.getResourceAsStream(name);
    }


    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        log.trace("Resolving resource {}-{}-{}-{}-{}", type, namespaceURI, publicId, systemId, baseURI);
        if (SYSTEM_IDS.containsKey(baseURI)) {
            log.trace("Try to resolve from known url {}", baseURI);
            if (SYSTEM_IDS.containsValue(systemId)) {
                log.trace("Try to resolve local entity {}", systemId);
                final String globalSystemId = SYSTEM_IDS.entrySet().stream()
                        .filter(entry -> systemId.equals(entry.getValue()))
                        .findFirst().get().getKey();
                return getLsInput(publicId, globalSystemId, baseURI);
            }
            log.debug("Could not resolve resource {}-{}-{}-{}-{}", type, namespaceURI, publicId, systemId, baseURI);
        }
        return null;
    }


    private LSInput getLsInput(String publicId, String systemId, String baseURI) {
        final InputSource inputSource = resolveEntity(publicId, systemId);
        return new InputSourceLSInput(inputSource, baseURI);
    }

    private static class InputSourceLSInput extends InputSource implements LSInput {
        private boolean certifiedText;
        private String baseURI;
        private String stringData;

        private InputSourceLSInput(InputSource inputSource, String baseURI) {
            super(inputSource.getByteStream());
            setPublicId(inputSource.getPublicId());
            setSystemId(inputSource.getSystemId());
            setBaseURI(baseURI);
        }

        @Override
        public String getStringData() {
            return stringData;
        }

        @Override
        public void setStringData(String stringData) {
            this.stringData = stringData;
        }

        @Override
        public String getBaseURI() {
            return baseURI;
        }

        @Override
        public void setBaseURI(String baseURI) {
            this.baseURI = baseURI;
        }

        @Override
        public boolean getCertifiedText() {
            return certifiedText;
        }

        @Override
        public void setCertifiedText(boolean certifiedText) {
            this.certifiedText = certifiedText;
        }
    }
}

/*
Possible extensions (currently not implemented)

Idea 1

Support for Apache commons-configuration2

The DefaultEntityResolver class however can not
        <!-- https://mvnrepository.com/artifact/org.apache.commons/commons-configuration2 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-configuration2</artifactId>
            <version>2.2</version>
        </dependency>


    public static void registerDefaultEntries() {
        DefaultEntityResolver defaultEntityResolver = new DefaultEntityResolver();
        for (Map.Entry<String, String> entry : SYSTEM_IDS.entrySet()) {
            try {
                defaultEntityResolver.registerEntityId(entry.getValue(), new URL(entry.getKey()));
            } catch (MalformedURLException e) {
                log.error("Can not register entry!", e);
            }
        }
    }

and Tests

    @org.junit.jupiter.api.Test
    @Disabled
    void showRE() {
        assertEquals(0, getRegisteredEntities().size());
        PartialLocalEntityResolver.registerDefaultEntries();
        assertThat(getRegisteredEntities().size(), is(greaterThan(0)));
    }

    private Map<String, URL> getRegisteredEntities() {
        DefaultEntityResolver defaultEntityResolver = new DefaultEntityResolver();
        return defaultEntityResolver.getRegisteredEntities();
    }


 */
