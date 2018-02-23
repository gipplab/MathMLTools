# MathML Tools - Java API

[![Build Status](https://travis-ci.org/ag-gipp/MathMLTools.svg?branch=refactor)](https://travis-ci.org/ag-gipp/MathMLTools)
[![Coverage Status](https://coveralls.io/repos/github/ag-gipp/MathMLTools/badge.svg?branch=refactor)](https://coveralls.io/github/ag-gipp/MathMLTools?branch=refactor)
[![Maintainability](https://api.codeclimate.com/v1/badges/41afd4eab2afc1b28b4b/maintainability)](https://codeclimate.com/github/ag-gipp/MathMLTools/maintainability)
[![MavenCentral](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine/mathmltools/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine/mathmltools/)

This project provides various tools for processing MathML documents and strings using Java. 
It is organized in specialized submodules.

- __[MathML Libraries](mathml-libs)__ (mathml-libs): contain several useful libraries, e.g. a library of XPath strings, 
to work with MathML documents in other languages.
- __[MathML Utilities](mathml-utils)__ (mathml-utils): provide general functions to load, store and check MathML documents
- __[MathML Core](mathml-core)__ (mathml-core): is the core API of MathML Tools and provide a bunch of features to 
manipulate, correct and work with MathML documents.
- __[MathML Converters](mathml-converters)__ (mathml-converters): is an API to convert other formats to MathML documents 
and vice versa.
- __[MathML Similarity](mathml-similarity)__ (mathml-similarity): provides different tools to calculate distances and 
similarities between MathML documents 
