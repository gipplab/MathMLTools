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