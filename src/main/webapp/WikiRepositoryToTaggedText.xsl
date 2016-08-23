<?xml version='1.0'?>

<xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:wr="http://errol.oclc.org/oai:xmlregistry.oclc.org:errol/WikiRepository">

<!-- Wiki Repository -->

<xsl:template match="wr:Deposit">
    <xsl:apply-templates/>
    <tr><td align="right" width="25%" valign="top"><b>object</b>:<xsl:text> </xsl:text></td><td>
    <a><xsl:attribute name="href"><xsl:value-of select="wr:browserPath"/>/<xsl:value-of select="wr:relativePath"/>/<xsl:value-of select="wr:fullRefID"/></xsl:attribute>
      WikiRepository/<xsl:value-of select="wr:relativePath"/>/<xsl:value-of select="wr:fullRefID"/></a>
    </td></tr>
</xsl:template>

<xsl:template match="wr:refID">
    <tr><td align="right" width="25%" valign="top"><b>refID</b>:<xsl:text> </xsl:text></td><td>
    <xsl:value-of select="."/>
    </td></tr>
</xsl:template>
<xsl:template match="wr:userName">
    <tr><td align="right" width="25%" valign="top"><b>userName</b>:<xsl:text> </xsl:text></td><td>
    <xsl:value-of select="."/>
    </td></tr>
</xsl:template>
<xsl:template match="wr:set">
    <tr><td align="right" width="25%" valign="top"><b>set</b>:<xsl:text> </xsl:text></td><td>
    <xsl:value-of select="."/>
    </td></tr>
</xsl:template>
<xsl:template match="wr:collection">
    <tr><td align="right" width="25%" valign="top"><b>collection</b>:<xsl:text> </xsl:text></td><td>
    <xsl:value-of select="."/>
    </td></tr>
</xsl:template>
<xsl:template match="wr:relativePath">
    <tr><td align="right" width="25%" valign="top"><b>relativePath</b>:<xsl:text> </xsl:text></td><td>
    <xsl:value-of select="."/>
    </td></tr>
</xsl:template>
<xsl:template match="wr:fullRefID">
    <tr><td align="right" width="25%" valign="top"><b>fullRefID</b>:<xsl:text> </xsl:text></td><td>
    <xsl:value-of select="."/>
    </td></tr>
</xsl:template>
<xsl:template match="wr:datestamp">
    <tr><td align="right" width="25%" valign="top"><b>datestamp</b>:<xsl:text> </xsl:text></td><td>
    <xsl:value-of select="."/>
    </td></tr>
</xsl:template>

</xsl:stylesheet>
