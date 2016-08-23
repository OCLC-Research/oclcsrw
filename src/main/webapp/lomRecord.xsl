<?xml version='1.0'?>

<xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:lom="http://www.imsglobal.org/xsd/imsmd_rootv1p2">

<!-- IEEE Lom -->

<xsl:template match="lom:lom">
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="lom:*">
  <xsl:if test="not(name()=lom)">
    <xsl:variable name="name1" select="name()"/>
    <xsl:for-each select="*">
    <xsl:variable name="name2" select="name()"/>
    <xsl:for-each select="*">
    <tr><td align="right" width="25%" valign="top"><b><xsl:value-of select="$name1"/>.<xsl:value-of select="$name2"/>.<xsl:value-of select="name()"/></b>:<xsl:text> </xsl:text> </td><td><xsl:text> </xsl:text> <xsl:value-of select="."/></td></tr>
    </xsl:for-each>
    </xsl:for-each>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>
