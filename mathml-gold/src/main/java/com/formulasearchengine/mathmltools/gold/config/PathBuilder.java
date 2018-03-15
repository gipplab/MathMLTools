package com.formulasearchengine.mathmltools.gold.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("all")
public class PathBuilder {

    private static final Logger LOG = LogManager.getLogger( PathBuilder.class.getName() );

    private Path path;

    public PathBuilder() {}

    public PathBuilder(Path base ){
        this.path = base;
    }

    public PathBuilder initResourcesPath(){
        try {
            path = Paths.get( ClassLoader.getSystemResource("").toURI() );
        } catch ( URISyntaxException urie ){
            LOG.error("Initialization error when building system resource path.", urie);
        } finally {
            return this;
        }
    }

    public PathBuilder addSubPath( String name ){
        path = path.resolve(name);
        return this;
    }

    public Path build(){
        return path;
    }

}
