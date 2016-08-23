<?xml version='1.0'?>

<xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:zthes="http://zthes.z3950.org/xml/0.5/">

<xsl:template match="/zthes:Zthes/zthes:term">
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="zthes:termId|zthes:termName|zthes:termModifiedDate|zthes:termType|zthes:termNote|zthes:relationType">
<tr><td align="right" width="25%" valign="top"><b><xsl:value-of select="local-name()"/></b>:<xsl:text> </xsl:text></td><td><xsl:text> </xsl:text>
<xsl:if test="name(..)='zthes:relation'"><xsl:text>&#160;&#160;&#160;&#160;</xsl:text></xsl:if>
<xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="zthes:relation">
<tr/><tr/><tr/><tr/><tr/><tr/>
<xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
