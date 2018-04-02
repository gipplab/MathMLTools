# MathMLSim

[![Build Status](https://travis-ci.org/ag-gipp/MathMLSim.svg?branch=master)](https://travis-ci.org/ag-gipp/MathMLSim)
[![MavenCentral](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine/MathMLSim/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine/MathMLSim/)
[![Coverage Status](https://coveralls.io/repos/github/ag-gipp/MathMLSim/badge.svg?branch=master)](https://coveralls.io/github/ag-gipp/MathMLSim?branch=master)

This submodule provides calculation techniques to compare two MathML documents. We use a Java implementation of
the robust algorithm for tree edit distances (RTED) to compute tree edit distances between MathML documents.
Furthermore, we provide calculations for distances in the histogram and an earthmover distance. The
Visualization Tool for Mathematical Expression Trees (VMEXT) has a [web application](https://vmext.wmflabs.org/mergedASTs)
that visualizes differences between MathML trees with the MathML Similarity submodule.

## Usage ##

**MathPlag** This class provides methods to compare mathematical expressions via its
 MathML representation (It will read the content semantics). The input can either be
 as a string or CMMLInfo object. In the first case the string will be converted into
 the latter.
 
## Dependencies ##
 
Note-worthy dependencies for this library.
 
**MathML Tools**: Library with various tools for processing MathML using Java. (https://github.com/ag-gipp/MathMLTools)
 
    <dependency>
        <groupId>com.formulasearchengine</groupId>
        <artifactId>mathmltools</artifactId>
        <version>...</version>
    </dependency>
