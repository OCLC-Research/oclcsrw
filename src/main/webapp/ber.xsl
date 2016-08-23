<?xml version='1.0'?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="ber">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="*">
  <xsl:variable name="name1" select="name()"/>
  <xsl:for-each select="*">
    <xsl:if test="count(*)=0">
      <tr><td align="right" width="25%" valign="top"><b><xsl:value-of select="$name1"/>.<xsl:value-of select="name()"/></b>:<xsl:text> </xsl:text> </td><td><xsl:text> </xsl:text> <xsl:value-of select="."/></td></tr>
    </xsl:if>
    <xsl:variable name="name2" select="name()"/>
    <xsl:for-each select="*">
	<xsl:if test="count(*)=0">
        <tr><td align="right" width="25%" valign="top"><b><xsl:value-of select="$name1"/>.<xsl:value-of select="$name2"/>.<xsl:value-of select="name()"/></b>:<xsl:text> </xsl:text> </td><td><xsl:text> </xsl:text> <xsl:value-of select="."/></td></tr>
      </xsl:if>
      <xsl:variable name="name3" select="name()"/>
      <xsl:for-each select="*">
        <xsl:if test="count(*)=0">
          <tr><td align="right" width="25%" valign="top"><b><xsl:value-of select="$name1"/>.<xsl:value-of select="$name2"/>.<xsl:value-of select="$name3"/>.<xsl:value-of select="name()"/></b>:<xsl:text> </xsl:text> </td><td><xsl:text> </xsl:text> <xsl:value-of select="."/></td></tr>
        </xsl:if>
        <xsl:variable name="name4" select="name()"/>
        <xsl:for-each select="*">
          <xsl:if test="count(*)=0">
            <tr><td align="right" width="25%" valign="top"><b><xsl:value-of select="$name1"/>.<xsl:value-of select="$name2"/>.<xsl:value-of select="$name3"/>.<xsl:value-of select="$name4"/>.<xsl:value-of select="name()"/></b>:<xsl:text> </xsl:text> </td><td><xsl:text> </xsl:text> <xsl:value-of select="."/></td></tr>
          </xsl:if>
          <xsl:variable name="name5" select="name()"/>
          <xsl:for-each select="*">
            <xsl:if test="count(*)=0">
              <tr><td align="right" width="25%" valign="top"><b><xsl:value-of select="$name1"/>.<xsl:value-of select="$name2"/>.<xsl:value-of select="$name3"/>.<xsl:value-of select="$name4"/>.<xsl:value-of select="$name5"/>.<xsl:value-of select="name()"/></b>:<xsl:text> </xsl:text> </td><td><xsl:text> </xsl:text> <xsl:value-of select="."/></td></tr>
            </xsl:if>
            <xsl:variable name="name6" select="name()"/>
            <xsl:for-each select="*">
              <xsl:if test="count(*)=0">
                <tr><td align="right" width="25%" valign="top"><b><xsl:value-of select="$name1"/>.<xsl:value-of select="$name2"/>.<xsl:value-of select="$name3"/>.<xsl:value-of select="$name4"/>.<xsl:value-of select="$name5"/>.<xsl:value-of select="$name6"/>.<xsl:value-of select="name()"/></b>:<xsl:text> </xsl:text> </td><td><xsl:text> </xsl:text> <xsl:value-of select="."/></td></tr>
              </xsl:if>
              <xsl:variable name="name7" select="name()"/>
              <xsl:if test="$name1='tag0'">
              <xsl:if test="$name2='tag201'">
              <xsl:if test="$name3='tag202'">
              <xsl:if test="$name4='tag300'">
              <xsl:if test="$name5='tag301'">
              <xsl:if test="$name6='tag2'">
              <xsl:if test="$name7='tag54'">
                <tr><td align="right" width="25%" valign="top"><b><xsl:value-of select="$name1"/>.<xsl:value-of select="$name2"/>.<xsl:value-of select="$name3"/>.<xsl:value-of select="$name4"/>.<xsl:value-of select="$name5"/>.<xsl:value-of select="$name6"/>.<xsl:value-of select="$name7"/></b>:<xsl:text> </xsl:text> </td><td><xsl:text> </xsl:text><a><xsl:attribute name="href">http://levan-r:8080/Curiouser?oclcNum=<xsl:value-of select="."/></xsl:attribute><xsl:value-of select="."/></a></td></tr>
              </xsl:if>
              </xsl:if>
              </xsl:if>
              </xsl:if>
              </xsl:if>
              </xsl:if>
              </xsl:if>
              <xsl:for-each select="*">
                <tr><td align="right" width="25%" valign="top"><b><xsl:value-of select="$name1"/>.<xsl:value-of select="$name2"/>.<xsl:value-of select="$name3"/>.<xsl:value-of select="$name4"/>.<xsl:value-of select="$name5"/>.<xsl:value-of select="$name6"/>.<xsl:value-of select="$name7"/>.<xsl:value-of select="name()"/></b>:<xsl:text> </xsl:text> </td><td><xsl:text> </xsl:text> <xsl:value-of select="."/></td></tr>
              </xsl:for-each>
            </xsl:for-each>
          </xsl:for-each>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
