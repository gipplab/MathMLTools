package com.formulasearchengine.mathmltools.xmlhelper;

        import org.junit.Assert;
        import org.junit.Test;

/**
 * Created by Moritz on 18.03.2017.
 */
public class PartialLocalEntityResolverTest {
    @Test
    public void resolveEntity() throws Exception {

        final PartialLocalEntityResolver resolver = new PartialLocalEntityResolver();
        Assert.assertNull(resolver.resolveEntity("a", "b"));
        Assert.assertNotNull(resolver.resolveEntity("a", "http://www.w3.org/Math/DTD/mathml2/xhtml-math11-f.dtd"));
    }
    @Test
    public void resolvePublicID() throws Exception {

        final PartialLocalEntityResolver resolver = new PartialLocalEntityResolver();
        Assert.assertNull(resolver.resolveEntity("a", "b"));
        Assert.assertNotNull(resolver.resolveEntity("-//W3C//ENTITIES HTML MathML Set//EN//XML", "local path"));
    }

}