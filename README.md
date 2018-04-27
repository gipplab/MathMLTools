<a href="https://go.java/index.html"><img align="right" src="https://forthebadge.com/images/badges/made-with-java.svg" alt="Made With Java" height="32"></a>

| License | Maven | Travis | Coveralls | Codeclimate |
| :---: | :---: | :---: | :---: | :---: |
| [![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html) | [![MavenCentral](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine/mathmltools/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine/mathmltools/) | [![Build Status](https://travis-ci.org/ag-gipp/MathMLTools.svg?branch=refactor)](https://travis-ci.org/ag-gipp/MathMLTools) | [![Coverage Status](https://coveralls.io/repos/github/ag-gipp/MathMLTools/badge.svg?branch=refactor)](https://coveralls.io/github/ag-gipp/MathMLTools?branch=refactor) | [![Maintainability](https://api.codeclimate.com/v1/badges/41afd4eab2afc1b28b4b/maintainability)](https://codeclimate.com/github/ag-gipp/MathMLTools/maintainability)|

<img hspace="20" align="right" src="/mml3.svg" alt="MMLTools Logo" height="256"/>

<p align="justify"><i>MathML Tools</i> is an open source project for processing content MathML within Java.
It provides tools to load, store, check validity and automatically repair and enhance 
documents for the new MathML 3.0 standard.
Furthermore, we provide Java adapters to convert LaTeX to MathML, full compatibility for our developed <a href="https://mathmlben.wmflabs.org">gold standard</a>,
programming language independent libraries of useful XPath and XQuery strings, and
distance measure algorithms to compare two MathML documents.</p>

## User guide
<p align="justify">The project is structured into specialized packages you can easily and separately include into your projects.
For example, if you just want to process MathML documents, the core module perfectly fits your needs.
We use maven for our build process and the entire project is available on Maven central. Note, automatically
imports the core module. Therefore, if you wish to use our similarity module in your project, you only need to
add the following snippet to your dependencies pom</p>

``` xml
<dependency>
    <groupId>com.formulasearchengine.mathmltools</groupId>
    <artifactId>mathml-similarity</artifactId>
    <version>2.0.0</version>
</dependency>
```

## Project Structure

- __[MathML Converters](mathml-converters)__ (mathml-converters):
Collection of tools for converting LaTeX to MathML. Also includes the canonicalization tool.

- __[MathML Core](mathml-core)__ (mathml-core): 
To load, store, check validity, repair and manipulate MathML documents.

- __[MathML Gold Standard](mathml-gold)__ (mathml-gold): 
Process the [MathMLBen](https://mathmlben.wmflabs.org) gold standard within Java.

- __[MathML Libraries](mathml-libs)__ (mathml-libs): 
Collection of XPath and XQuery strings for content MathML (includes Java pojos).

- __[MathML Similarity](mathml-similarity)__ (mathml-similarity): 
Collection of distance measurements for MathML documents.

- __[MathML Utilities](mathml-utils)__ (mathml-utils): 
Useful utility functions and definitions (always included in the other modules above).

