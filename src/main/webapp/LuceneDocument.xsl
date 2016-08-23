<?xml version='1.0'?>

<xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:lucene="http://www.oclc.org/LuceneDocument">

<!-- Lucene -->

<xsl:template match="lucene:LuceneDocument">
  <table>
    <xsl:apply-templates/>
    </table>
  </xsl:template>

<xsl:template match="lucene:field">
  <tr>
    <td align="right" width="15%" valign="top">
      <b><xsl:value-of select="@name"/></b>:<xsl:text> </xsl:text>
      </td>
    <td>
      <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>
