<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
 Attempt at a XSL transformation from enriched MathML to CMML.
 @author Vincent Stange
-->
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1998/Math/MathML"
                xpath-default-namespace="http://www.w3.org/1998/Math/MathML">
    <xsl:output method="xml" indent="yes"/>

    <!-- === mrow =============================================================== -->

    <!-- outer row without Id is only for formatting, just go one element deeper -->
    <xsl:template match="mrow[not(@data-semantic-id)]">
        <xsl:apply-templates/>
    </xsl:template>

    <!-- emtpy row -->
    <xsl:template match="mrow[@data-semantic-type='empty']">
    </xsl:template>

    <!-- fencing around the children, apply templates on the children only -->
    <xsl:template match="mrow[@data-semantic-type='fenced']">
        <!-- iterate over the children -->
        <xsl:call-template name="iterateList">
            <xsl:with-param name="list" select="@data-semantic-children"/>
            <xsl:with-param name="separator" select="','"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Operation: infixop, relseq, multirel (plus or minus) -->
    <xsl:template match="mrow[@data-semantic-type='infixop'
      or @data-semantic-type='relseq'
      or @data-semantic-type='multirel'
      or @data-semantic-type='prefixop'
      or @data-semantic-type='postfixop']">
        <apply>
            <!-- set id attributes -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>

            <!-- take only the first content node, in case the expression is a+b+c -->
            <xsl:variable name="temp" select="tokenize(@data-semantic-content,',')[1]"/>
            <xsl:apply-templates select="//*[@data-semantic-id=$temp]"/>

            <!-- iterate over all children -->
            <xsl:call-template name="iterateList">
                <xsl:with-param name="list" select="@data-semantic-children"/>
                <xsl:with-param name="separator" select="','"/>
            </xsl:call-template>
        </apply>
    </xsl:template>

    <!-- Operation: appl (simple function) -->
    <xsl:template match="mrow[@data-semantic-type='appl']">
        <apply>
            <!-- set id attributes -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>

            <!-- iterate over all children -->
            <xsl:call-template name="iterateList">
                <xsl:with-param name="list" select="@data-semantic-children"/>
                <xsl:with-param name="separator" select="','"/>
            </xsl:call-template>
        </apply>
    </xsl:template>

    <!-- Integral: https://www.w3.org/TR/MathML3/chapter4.html#contm.int -->
    <xsl:template match="mrow[@data-semantic-type='integral']">
        <xsl:element name="apply">
            <!-- set id attributes -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>

            <xsl:element name="int">
                <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
                <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>
            </xsl:element>

            <!-- third child is the integration var -->
            <xsl:variable name="temp3" select="tokenize(@data-semantic-children,',')[3]"/>
            <xsl:if test="//*[@data-semantic-id=$temp3 and not(@data-semantic-type='empty')]">
                <xsl:element name="bvar">
                    <xsl:attribute name="id">u<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
                    <xsl:attribute name="xref">p<xsl:value-of select="$temp3"/></xsl:attribute>

                    <xsl:apply-templates select="//*[@data-semantic-id=$temp3]"/>
                </xsl:element>
            </xsl:if>

            <!-- first child is the integral -->
            <xsl:variable name="temp1" select="tokenize(@data-semantic-children,',')[1]"/>
            <xsl:apply-templates select="//*[@data-semantic-id=$temp1]"/>

            <!-- second child is integrand, -->
            <xsl:variable name="temp2" select="tokenize(@data-semantic-children,',')[2]"/>
            <xsl:apply-templates select="//*[@data-semantic-id=$temp2]"/>
        </xsl:element>
    </xsl:template>

    <!-- BigOp Sum / Product: https://www.w3.org/TR/MathML3/chapter4.html#contm.sum -->
    <xsl:template match="mrow[@data-semantic-type='bigop']">
        <xsl:element name="apply">
            <!-- set id attributes -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/>
            </xsl:attribute>
            <xsl:attribute name="xref">
                <xsl:value-of select="@id"/>
            </xsl:attribute>

            <!-- operator for the apply node -->
            <xsl:variable name="tagname">
                <xsl:choose>
                    <xsl:when test="@data-semantic-role = 'sum'">sum</xsl:when>
                    <xsl:when test="@data-semantic-role = 'product'">product</xsl:when>
                    <xsl:otherwise>unknown</xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:element name="{$tagname}">
                <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
                <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>
            </xsl:element>

            <!-- first child is the operator -->
            <xsl:variable name="temp1" select="tokenize(@data-semantic-children,',')[1]"/>
            <xsl:apply-templates select="//*[@data-semantic-id=$temp1]"/>

            <!-- second child is the operant -->
            <xsl:variable name="temp2" select="tokenize(@data-semantic-children,',')[2]"/>
            <xsl:apply-templates select="//*[@data-semantic-id=$temp2]"/>

        </xsl:element>
    </xsl:template>

    <!-- === other roots ================================================================ -->

    <!-- Operation: fraction, square root, power -->
    <xsl:template match="mfrac|msqrt|msup|msub">
        <!-- recognize correct tag name, default is 'unknown' -->
        <xsl:variable name="tagname">
            <xsl:choose>
                <xsl:when test="name() = 'mfrac'">divide</xsl:when>
                <xsl:when test="name() = 'msqrt'">root</xsl:when>
                <xsl:when test="name() = 'msup'">power</xsl:when>
                <xsl:when test="name() = 'msub'">subscript</xsl:when>
                <xsl:otherwise>unknown</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:element name="apply">
            <!-- set id attributes -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>

            <!-- inner element (operator)-->
            <xsl:element name="{$tagname}">
                <!-- set (unique) id attributes -->
                <xsl:attribute name="id">u<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
                <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>
            </xsl:element>

            <!-- iterate over the children -->
            <xsl:call-template name="iterateList">
                <xsl:with-param name="list" select="@data-semantic-children"/>
                <xsl:with-param name="separator" select="','"/>
            </xsl:call-template>
        </xsl:element>
    </xsl:template>

    <!-- Operation: square root -->
    <xsl:template match="mroot">
        <xsl:element name="apply">
            <!-- set id attributes -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>

            <!-- inner element (operator): root -->
            <xsl:element name="root">
                <!-- set (unique) id attributes -->
                <xsl:attribute name="id">u<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
                <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>
            </xsl:element>

            <!-- second child node is the degree -->
            <xsl:variable name="temp1" select="tokenize(@data-semantic-children,',')[1]"/>
            <xsl:element name="degree">
                <xsl:apply-templates select="//*[@data-semantic-id=$temp1]"/>
            </xsl:element>

            <xsl:variable name="temp0" select="tokenize(@data-semantic-children,',')[2]"/>
            <xsl:apply-templates select="//*[@data-semantic-id=$temp0]"/>

        </xsl:element>
    </xsl:template>


    <!-- msubsup or munderover: lower and upper limit -->
    <xsl:template match="msubsup[@data-semantic-type='limboth']|munderover">
        <xsl:element name="lowlimit">
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>

            <!-- second child node is the lower limit -->
            <xsl:variable name="temp2" select="tokenize(@data-semantic-children,',')[2]"/>
            <xsl:apply-templates select="//*[@data-semantic-id=$temp2]"/>
        </xsl:element>

        <xsl:element name="uplimit">
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>

            <!-- third child node is the upper limit -->
            <xsl:variable name="temp3" select="tokenize(@data-semantic-children,',')[3]"/>
            <xsl:apply-templates select="//*[@data-semantic-id=$temp3]"/>
        </xsl:element>
    </xsl:template>

    <!-- munder and mover -->
    <xsl:template match="munder[@data-semantic-role='limit function']|mover[@data-semantic-role='limit function']">
        <!-- recognize correct tag name, default is unknown -->
        <xsl:variable name="tagname">
            <xsl:choose>
                <xsl:when test="name() = 'munder'">lowlimit</xsl:when>
                <xsl:when test="name() = 'mover'">uplimit</xsl:when>
                <xsl:otherwise>unknown</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!-- print out the first child -->
        <xsl:variable name="temp1" select="tokenize(@data-semantic-children,',')[1]"/>
        <xsl:apply-templates select="//*[@data-semantic-id=$temp1]"/>

        <!--build the limit tag -->
        <xsl:element name="{$tagname}">
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>

            <!-- second child node is the lower limit -->
            <xsl:variable name="temp2" select="tokenize(@data-semantic-children,',')[2]"/>
            <xsl:apply-templates select="//*[@data-semantic-id=$temp2]"/>
        </xsl:element>
    </xsl:template>

    <!-- === elements ================================================================ -->

    <!-- idenfitiers and constants -->
    <xsl:template match="mi|mn">
        <!-- recognize correct tag name, default is ci -->
        <xsl:variable name="tagname">
            <xsl:choose>
                <xsl:when test="name() = 'mi'">ci</xsl:when>
                <xsl:when test="name() = 'mn'">cn</xsl:when>
                <xsl:otherwise>ci</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!--build the new identifier or constant -->
        <xsl:element name="{$tagname}">
            <!-- new unique id starting with the letter "c" -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:attribute name="cd">
                <xsl:value-of select="@data-semantic-type"/>
            </xsl:attribute>
            <xsl:value-of select="normalize-space(text())"/>
        </xsl:element>
    </xsl:template>

    <!-- operators -->
    <xsl:template match="mo[@data-semantic-type='operator']">
        <!-- recognize correct tag name, default is 'unknown' -->
        <xsl:variable name="tagname">
            <xsl:choose>
                <xsl:when test="@data-semantic-role = 'addition'">plus</xsl:when>
                <xsl:when test="@data-semantic-role = 'subtraction'">minus</xsl:when>
                <xsl:when test="@data-semantic-role = 'multiplication'">times</xsl:when>
                <xsl:when test="@data-semantic-role = 'division'">divide</xsl:when>
                <xsl:otherwise>unknown</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!-- create new operation element -->
        <xsl:element name="{$tagname}">
            <!-- new unique id starting with the letter "c" -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:attribute name="cd">
                <xsl:value-of select="@data-semantic-type"/>
            </xsl:attribute>
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>

    <!-- relation -->
    <xsl:template match="mo[@data-semantic-type='relation']">
        <!-- recognize correct tag name, default is 'eq' -->
        <xsl:variable name="tagname">
            <xsl:choose>
                <xsl:when test="@data-semantic-role = 'equality'">eq</xsl:when>
                <xsl:when test="@data-semantic-role = 'inequality'">neq</xsl:when>
                <xsl:when test="@data-semantic-role = 'arrow'">tendsto</xsl:when>
                <xsl:otherwise>eq</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:element name="{$tagname}">
            <!-- new unique id starting with the letter "c" -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:attribute name="cd">
                <xsl:value-of select="@data-semantic-type"/>
            </xsl:attribute>
            <xsl:value-of select="normalize-space(text())"/>
        </xsl:element>
    </xsl:template>

    <!-- limit symbol -->
    <xsl:template match="mo[@data-semantic-role='limit function']">
        <xsl:element name="limit">
            <!-- new unique id starting with the letter "c" -->
            <xsl:attribute name="id">c<xsl:value-of select="@data-semantic-id"/></xsl:attribute>
            <xsl:attribute name="xref"><xsl:value-of select="@id"/></xsl:attribute>
            <xsl:attribute name="cd"><xsl:value-of select="@data-semantic-type"/></xsl:attribute>
        </xsl:element>
    </xsl:template>

    <!-- === misc =============================================================== -->

    <!-- Recursive method over a list of id nodes. (XSLT 1.0 conform) -->
    <xsl:template name="iterateList">
        <xsl:param name="list"/> <!-- e.g. '1,5,9,15' -->
        <xsl:param name="separator"/> <!-- e.g. ',' -->
        <xsl:variable name="first" select="substring-before($list, $separator)"/>
        <xsl:variable name="remaining" select="substring-after($list, $separator)"/>

        <xsl:choose>
            <xsl:when test="not(contains($list, $separator))">
                <!-- the stopper, if no separator is present anymore -->
                <xsl:apply-templates select="//*[@data-semantic-id=$list]"/>
            </xsl:when>
            <xsl:otherwise>
                <!-- take first element and start recursion with the remaining string -->
                <xsl:apply-templates select="//*[@data-semantic-id=$first]"/>
                <xsl:if test="$remaining">
                    <xsl:call-template name="iterateList">
                        <xsl:with-param name="list" select="$remaining"/>
                        <xsl:with-param name="separator" select="$separator"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- default node processing (just a copy) -->
    <!--<xsl:template match="*" mode="#all">-->
        <!--<xsl:copy>-->
            <!--<xsl:copy-of select="@*"/>-->
            <!--<xsl:apply-templates mode="#current"/>-->
        <!--</xsl:copy>-->
    <!--</xsl:template>-->

</xsl:stylesheet>
