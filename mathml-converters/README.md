# MathML Converters

[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![MavenCentral](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine.mathmltools/mathml-converters/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine.mathmltools/mathml-converters/)
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

The converters module provides adapters for 3rd party tools for manipulating MathML and converting 
LaTeX to MathML and content MathML. The adapters are written in Java and call native methods if necessary.
This presumes that the wanted conversion tool is installed on your local machine. If not, an error will
be thrown. MathML Converters also provides a simple adapter for MathMLCan, a canonicalizer for MathML documents.
Our default configuration for this canonicalizer can be found in 
[the resources](src/resources/com/formulasearchengine/mathmltools/converters/canonicalize/).
We implemented adapters for the following conversion tools:
   - LaTeXML
   - [LaTeX2MathML](https://github.com/Code-ReaQtor/latex2mathml)
   - [Mathematical](https://github.com/gjtorikian/mathematical)
   - [MathToWeb](http://www.mathtoweb.com/cgi-bin/mathtoweb_home.pl)
   - SnuggleTeX
   - Part-Of-Math Tagger
   - Mathoid
   - TeXZilla


### Dependencies ###

Note-worthy dependencies for this library.

**MathML Tools**: Library with various tools for processing MathML using Java. (https://github.com/physikerwelt/MathMLTools)

    <dependency>
        <groupId>com.formulasearchengine</groupId>
        <artifactId>mathmltools</artifactId>
        <version>...</version>
    </dependency>

**MathML-Canonicalizer**: Canonicalizer for MathML formulas. (https://github.com/michal-ruzicka/MathMLCan)

    <dependency>
        <groupId>cz.muni.fi.mir</groupId>
        <artifactId>mathml-canonicalizer</artifactId>
        <version>...</version>
    </dependency>
