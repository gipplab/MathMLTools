MathML Query Generator
======================

Converts individual content MathML elements to equivalent
XQuery statements in DB2 dialect.

The content MathML elements may be given as

1. [MathWebSearch](http://search.mathweb.org/) expressions or
2. annotation-xml elements with the attribute ```encoding="MathML-Content"```.

##Details 

If a document contains multiple elements, only the first MathWebSearch expression is processed.
If no MathWebSearch element is available, the first annotation-xml element will be used.

