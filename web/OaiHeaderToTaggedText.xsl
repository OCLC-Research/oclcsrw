<?xml version='1.0'?>

<xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:oai="http://www.openarchives.org/OAI/2.0/">

<!-- OAI Header -->

<xsl:template match="oai:header">
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="oai:identifier">
    <tr><td align="right" width="25%" valign="top"><b>oai:identifier</b>:<xsl:text> </xsl:text></td><td>
    <xsl:value-of select="."/>
    </td></tr>
</xsl:template>
<xsl:template match="oai:datestamp">
    <tr><td align="right" width="25%" valign="top"><b>oai:datestamp</b>:<xsl:text> </xsl:text></td><td>
    <xsl:value-of select="."/>
    </td></tr>
</xsl:template>

</xsl:stylesheet>
