package com.formulasearchengine.mathmltools.utils.reader;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * @author Andre Greiner-Petter
 */
public final class ResourceLoader {

    private ResourceLoader() { }

    public static String getResourceFile(Class clazz, String filename) throws IOException {
        return IOUtils.toString(clazz.getResourceAsStream(filename), "UTF-8");
    }
}
