MathML Java Tools
======================

[![Build Status](https://travis-ci.org/ag-gipp/MathMLTools.svg?branch=master)](https://travis-ci.org/ag-gipp/MathMLTools)
[![MavenCentral](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine/mathmltools/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine/mathmltools/)
[![Coverage Status](https://coveralls.io/repos/github/ag-gipp/MathMLTools/badge.svg?branch=master)](https://coveralls.io/github/ag-gipp/MathMLTools?branch=master)

The core module provides essential tools to handle MathML and XML in Java. Internally we parse MathML 
to Java Document Object Model (DOM) objects with namespaces, doctype and validation techniques. Each
successfully parsed document will be a valid MathML 3.0 document. Furthermore, we provide tools to easily
access representation and content parts of content MathML. Most of these access methods use XPath or XQuery.
For used XPath strings check [MathML Libraries](../mathml-libs).

Parts of this library originate from the [MathMLQueryGenerator](https://github.com/physikerwelt/MathMLQueryGenerator).
