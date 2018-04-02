<img align="right" src="/mml3.svg" alt="MMLTools Logo" width="64" height="128" />

# MathML Tools - Java API

[![Build Status](https://travis-ci.org/ag-gipp/MathMLTools.svg?branch=refactor)](https://travis-ci.org/ag-gipp/MathMLTools)
[![Coverage Status](https://coveralls.io/repos/github/ag-gipp/MathMLTools/badge.svg?branch=refactor)](https://coveralls.io/github/ag-gipp/MathMLTools?branch=refactor)
[![Maintainability](https://api.codeclimate.com/v1/badges/41afd4eab2afc1b28b4b/maintainability)](https://codeclimate.com/github/ag-gipp/MathMLTools/maintainability)
[![MavenCentral](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine/mathmltools/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine/mathmltools/)

This project provides various tools for processing content MathML documents using Java. 
It is organized in the following specialized submodules:

- __[MathML Converters](mathml-converters)__ (mathml-converters): 
The converters module provides adapters for 3rd party tools for manipulating MathML and converting 
LaTeX to MathML and content MathML. The adapters are written in Java and call native methods if necessary.
This presumes that the wanted conversion tool is installed on your local machine. If not, an error will
be thrown. MathML Converters also provides a simple adapter for MathMLCan, a canonicalizer for MathML documents.
Our default configuration for this canonicalizer can be found in 
[the resources](mathml-converters/src/resources/com/formulasearchengine/mathmltools/converters/canonicalize/).
We implemented adapters for the following conversion tools:
   - LaTeXML
   - [LaTeX2MathML](https://github.com/Code-ReaQtor/latex2mathml)
   - [Mathematical](https://github.com/gjtorikian/mathematical)
   - [MathToWeb](http://www.mathtoweb.com/cgi-bin/mathtoweb_home.pl)
   - SnuggleTeX
   - Part-Of-Math Tagger
   - Mathoid
   - TeXZilla

- __[MathML Core](mathml-core)__ (mathml-core): 
The core module provides essential tools to handle MathML and XML in Java. Internally we parse MathML 
to Java Document Object Model (DOM) objects with namespaces, doctype and validation techniques. Each
successfully parsed document will be a valid MathML 3.0 document. Furthermore, we provide tools to easily
access representation and content parts of content MathML. Most of these access methods use XPath or XQuery.
For used XPath strings check [MathML Libraries](mathml-libs).

- __[MathML Gold Standard](mathml-gold)__ (mathml-core): 
This submodule contains tools to load, parse and manipulate our recently developed 
gold standard [MathMLBen](https://mathmlben.wmflabs.org/about). The gold standard works independently from
the core package of MathMLTools and provide pojo java classes for each entry of the gold standard. With this
submodule it is easy to use the gold standard for your own Java project.

- __[MathML Libraries](mathml-libs)__ (mathml-libs): 
The MathML Libraries is mainly not a Java project. It contains several useful strings to work with MathML.
For example, valid headers, doctypes, a list of valid MathML 3.0 content tags and so on. Furthermore, we
stored all used XPath strings also. The libraries are represented as YAML files. You can use these libraries
to work with content MathML in other programming languages. If you want to use the libraries in a Java project,
we also provide simple pojo projects to access and use the XPath strings.

- __[MathML Similarity](mathml-similarity)__ (mathml-similarity): 
This submodule provides calculation techniques to compare two MathML documents. We use a Java implementation of
the robust algorithm for tree edit distances (RTED) to compute tree edit distances between MathML documents.
Furthermore, we provide calculations for distances in the histogram and an earthmover distance. The
Visualization Tool for Mathematical Expression Trees (VMEXT) has a [web application](https://vmext.wmflabs.org/mergedASTs)
that visualizes differences between MathML trees with the MathML Similarity submodule.

- __[MathML Utilities](mathml-utils)__ (mathml-utils): 
The utilities is a simple small project containing some utility classes for MathMLTools. For example resource loaders,
custom exceptions, and tools to call native programs from inside the JVM.
