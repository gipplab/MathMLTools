MathML Query Generator
======================

Converts individual content MathML elements to equivalent
XQuery statements in DB2 dialect.

The content MathML elements may be given as

1. [MathWebSearch](http://search.mathweb.org/) expressions,
2. annotation-xml elements with the attribute ```encoding="MathML-Content"```,
3. semantic elements, or
4. MathML tags that contain content MathML only

##Details 

If a document contains multiple elements, only the first MathWebSearch expression is processed.

If no MathWebSearch element is available, the first annotation-xml element will be used.

If no annotation-xml Math element is available, the first semantic element will be used.

...

##qvar

Both qvar formats ```<qvar xmlns="http://search.mathweb.org/ns" />x</qvar>``` and
 ```<qvar xmlns="http://search.mathweb.org/ns" name="x" />``` are supported.

##Ntcir Topics
The NtcirTopicReader class supports parsing of queries specified in the NTCIR topic format
http://ntcir-math.nii.ac.jp/wp-content/blogs.dir/13/files/2014/05/NTCIR11-Math-topics.pdf .
The expected input is a DOMDocuement and the result is a List of NtcirPattern instances that
include the XQuery expressions.

[![Build Status](https://travis-ci.org/physikerwelt/MathMLQueryGenerator.svg?branch=travis)](https://travis-ci.org/physikerwelt/MathMLQueryGenerator)
[![MavenCentral](https://maven-badges.herokuapp.com/maven-central/com.formulasearchengine/mathmlquerygenerator/badge.svg)](maven-badges.herokuapp.com/maven-central/com.formulasearchengine/mathmlquerygenerator/)
[![Coverage Status](https://coveralls.io/repos/physikerwelt/MathMLQueryGenerator/badge.svg)](https://coveralls.io/r/physikerwelt/MathMLQueryGenerator)
