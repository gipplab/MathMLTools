<a href="https://go.java/index.html"><img align="right" src="https://forthebadge.com/images/badges/made-with-java.svg" alt="Made With Java" height="32"></a>

# MathML Tools

| Maven | License | DOI | Travis | Coveralls | Codeclimate |
| :---: | :---: | :---: | :---: | :---: | :---: |
| [![MavenCentral](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine.mathmltools/mathmltools/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine.mathmltools/mathmltools/) | [![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html) | [![DOI](https://zenodo.org/badge/85396484.svg)](https://zenodo.org/badge/latestdoi/85396484) | [![Build Status](https://travis-ci.org/ag-gipp/MathMLTools.svg?branch=refactor)](https://travis-ci.org/ag-gipp/MathMLTools) | [![Coverage Status](https://coveralls.io/repos/github/ag-gipp/MathMLTools/badge.svg?branch=refactor)](https://coveralls.io/github/ag-gipp/MathMLTools?branch=refactor) | [![Maintainability](https://api.codeclimate.com/v1/badges/41afd4eab2afc1b28b4b/maintainability)](https://codeclimate.com/github/ag-gipp/MathMLTools/maintainability)|

<img hspace="20" align="right" src="/mml3.svg" alt="MMLTools Logo" height="256"/>

<p align="justify"><i>MathML Tools</i> is an open source project for processing content MathML within Java.
It provides tools to load, store, check validity and automatically repair and enhance 
documents for the new MathML 3.0 standard.
Furthermore, we provide Java adapters to convert LaTeX to MathML, full compatibility for our developed <a href="https://mathmlben.wmflabs.org">gold standard</a>,
programming language independent libraries of useful XPath and XQuery strings, and
distance measure algorithms to compare two MathML documents.</p>

## Install Instructions
<p align="justify">Since this is an open API, there is nothing to install.
If you want to use the API for you own project you can use maven central as explained in section <a href="#maven-central">Maven Central</a> below.
If you want to download the sources, run tests and change something in the code, follow the guide in section <a href="#local-install">Local Installation</a> below.</p>

### Maven Central
<p align="justify">The project is structured into specialized packages (maven-modules) that you can easily and separately include into your projects.
For example, if you just want to process MathML documents, the core module perfectly fits your needs.
We use maven for our build process and the entire project is available on Maven central (see the maven badge above). Note that 
specialized modules automatically imports the core module. For example, if you wish to use our similarity module in your project, 
you only need to add the following snippet to your dependencies pom:</p>

``` xml
<dependency>
    <groupId>com.formulasearchengine.mathmltools</groupId>
    <artifactId>mathml-similarity</artifactId>
    <version>2.0.1</version>
</dependency>
```

### Local Install
To download the project and run the tests you need `git` and `mvn` installed.
First download the sources into a directory.
``` bash
mkdir mathtools
cd mathtools
git clone https://github.com/ag-gipp/MathMLTools.git .
```
Now you can install the project locally via maven, which automatically runs the tests.
```bash
mvn clean install
```
Don't panic if you see error messages. We have written tests that expects exceptions.
If this process finished without errors you should see something like this in the end of the log.
```
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] MathML Tools ....................................... SUCCESS [  3.234 s]
[INFO] MathML Libraries ................................... SUCCESS [  1.493 s]
[INFO] MathML Utilities ................................... SUCCESS [  6.425 s]
[INFO] MathML Core ........................................ SUCCESS [02:31 min]
[INFO] MathML Converters .................................. SUCCESS [ 41.476 s]
[INFO] MathML Similarity .................................. SUCCESS [  7.930 s]
[INFO] MathML Gold Standard ............................... SUCCESS [  0.710 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
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

