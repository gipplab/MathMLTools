package com.formulasearchengine.mathmltools.gold;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formulasearchengine.mathmltools.gold.pojo.JsonGouldiBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Andre Greiner-Petter
 */
public class GoldUtils {
    private static final Logger LOG = LogManager.getLogger(GoldUtils.class.getName());

    private GoldUtils() { }

    /**
     * Reads a gold file in json format from the given path. It will be stored as Java objects.
     *
     * @param pathToSingleGoldFile path to the json format gouldi entry
     * @return java object representing the gouldi entry
     * @throws IOException will be thrown if we cannot read from the file
     */
    public static JsonGouldiBean readGoldFile(Path pathToSingleGoldFile) throws IOException {
        File f = pathToSingleGoldFile.toFile();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(f, JsonGouldiBean.class);
    }

    /**
     * Writes a gouldi entry to the given path. Note that if the file already exists, it will be
     * overwritten and logging a warning message.
     *
     * @param outputPath where the gouldi entry will be stored
     * @param goldEntry  the gouldi entry as java object
     * @throws IOException
     */
    public static void writeGoldFile(Path outputPath, JsonGouldiBean goldEntry) {
        try {
            try {
                Files.createFile(outputPath);
            } catch (FileAlreadyExistsException e) {
                LOG.warn("File already exists!");
            }
            try (Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputPath.toFile()), "UTF-8"))) {
                out.write(goldEntry.toString());
            }
        } catch (IOException e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    }
}
