# MathMLSim

[![Build Status](https://travis-ci.org/ag-gipp/MathMLSim.svg?branch=master)](https://travis-ci.org/ag-gipp/MathMLSim)
[![MavenCentral](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine/MathMLSim/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine/MathMLSim/)
[![Coverage Status](https://coveralls.io/repos/github/ag-gipp/MathMLSim/badge.svg?branch=master)](https://coveralls.io/github/ag-gipp/MathMLSim?branch=master)

Similarity calculation module for MathML formulae

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
