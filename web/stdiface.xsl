<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:srw="http://www.loc.gov/zing/srw/"
  xmlns:diag="http://www.loc.gov/zing/srw/diagnostic/" exclude-result-prefixes="srw">

<xsl:output method="html" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>

<xsl:template name="stdiface">
<html>
<head>
<title><xsl:value-of select="$title"/></title>
<link href="http://www.oclc.org/common/css/basic_oclc.css" rel="stylesheet" type="text/css"/>
<link href="http://www.oclc.org/common/css/researchproject_oclc.css" rel="stylesheet" type="text/css"/>
<style type="text/css">
&lt;!--
table.layout { border: none; margin: 0; padding: 0; width: 100%; }
table.layout td { border: none; margin: 0; padding: 0; }
table.formtable th, table.formtable td { border-top: 1px solid #999; border-left: 1px solid #999; color: #333; padding: 4px; text-align: left; vertical-align: top}
table.formtable td { width: 100%}
input.button { margin: 0; }
#crumbs {
	font-size: xx-small;
	voice-family: "\"}\"";
	voice-family: inherit;
	font-size: x-small;
	margin-bottom: 15px;
}
--&gt;
</style>
</head>
<body>
<table cellspacing="0" id="bnrResearch">
<tr>
<td id="tdResearch"><a href="http://www.oclc.org/research/">A Project of OCLC Research</a></td>
<td id="tdOclc"><a href="http://www.oclc.org/">OCLC Online Computer Library Center</a></td>
</tr>
<tr>
<td id="tdProject">
<h2><xsl:value-of select="$title"/></h2>
</td>
<td id="tdLogo"><a href="http://www.oclc.org/"><img src="http://www.oclc.org/common/images/logos/oclclogo_gray.gif" alt="OCLC" width="60" height="31"/></a></td>
</tr>
</table>
<xsl:apply-templates/>
<p>
<a href="?">Home</a>
</p>
<p>
<a href="http://www.oclc.org/research/software/srw">
<img src="http://www.oclc.org/resources/research/images/badges/oclc_srwu.gif" alt="Powered by OCLC SRW/U" width="80" height="15"/>
</a>
</p>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', '%GAA%', '%GAD%');
  ga('send', 'pageview');

</script>
</body>
</html>
</xsl:template>

<xsl:template match="srw:version">
</xsl:template>

<xsl:template match="srw:diagnostics">
<tr><td><h2>Diagnostics</h2></td></tr>
<tr><td width="50%" style="padding-right: 10px;">
<xsl:apply-templates/>
</td><td></td></tr>
</xsl:template>

<xsl:template match="diag:diagnostic">
<table cellspacing="0" class="formtable">
<xsl:apply-templates/>
</table>
</xsl:template>

<xsl:template match="diag:uri">
<tr><th>Identifier:</th><td><xsl:value-of select="."/></td></tr>
<tr><th>Meaning:</th>
<xsl:variable name="diag" select="."/>
<td>
<xsl:choose>
  <xsl:when test="$diag='info:srw/diagnostic/1/1'">
    <xsl:text>General System Error</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/4'">
    <xsl:text>Unsupported Operation</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/6'">
    <xsl:text>Unsupported Parameter Value</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/7'">
    <xsl:text>Mandatory Parameter Not Supplied</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/10'">
    <xsl:text>Query Syntax Error</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/16'">
    <xsl:text>Unsupported Index</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/22'">
    <xsl:text>Unsupported Combination of Relation and Index</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/39'">
    <xsl:text>Proximity Not Supported</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/51'">
    <xsl:text>Result Set Does Not Exist</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/61'">
    <xsl:text>First Record Position Out Of Range</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/64'">
    <xsl:text>Record temporarily unavailable</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/66'">
    <xsl:text>Unknown Schema For Retrieval</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/71'">
    <xsl:text>Unsupported record packing</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/93'">
    <xsl:text>Sort Ended Due To Missing Value</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/94'">
    <xsl:text>When resultSetTTL=0, Sort Only Legal When startRec=1</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/110'">
    <xsl:text>Stylesheets Not Supported</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/120'">
    <xsl:text>Response Position Out Of Range</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='info:srw/diagnostic/1/130'">
    <xsl:text>Too Many Terms Matched By Masked Query Term</xsl:text>
    </xsl:when>
  </xsl:choose>
</td>
</tr>
</xsl:template>

<xsl:template match="diag:details">
<tr><th>Details:</th><td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="diag:message"><tr><td><b>Message:</b></td><td><xsl:value-of select="."/></td></tr></xsl:template>

</xsl:stylesheet>
