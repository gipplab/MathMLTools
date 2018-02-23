# MathML Converters

[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![MavenCentral](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine/MathMLConverters/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine/mathmlconverters/)
[![Build Status](https://travis-ci.org/ag-gipp/MathMLConverters.svg?branch=master)](https://travis-ci.org/ag-gipp/MathMLConverters)
[![Coverage Status](https://coveralls.io/repos/github/ag-gipp/MathMLConverters/badge.svg)](https://coveralls.io/github/ag-gipp/MathMLConverters)

This library is a collection of utilities and service calls to convert from various input formats to MathML.
The desired MathML output is always a well-formed MathML containing the presentation and content semantic. 

  * LaTeXML Service Call: LaTeXML to MathML
  * Mathoid Service Call: LaTeXML to Enriched PMML
  * Mathoid Service Call: PMML to Enriched PMML
  * EnrichedMathMLTransformer: Enriched PMML to MathML

It also includes the MathMLCanonicalizer with a custom configuration, which is adapted
to the required input format. This utility library canonicalizes an input MathML string.

### Dependencies ###

Note-worthy dependencies for this library.

**MathML Tools**: Library with various tools for processing MathML using Java. (https://github.com/physikerwelt/MathMLTools)

    <dependency>        <groupId>com.formulasearchengine</groupId>
        <artifactId>mathmltools</artifactId>
        <version>...</version>
    </dependency>

**MathML-Canonicalizer**: Canonicalizer for MathML formulas. (https://github.com/michal-ruzicka/MathMLCan)

    <dependency>
        <groupId>cz.muni.fi.mir</groupId>
        <artifactId>mathml-canonicalizer</artifactId>
        <version>...</version>
    </dependency>