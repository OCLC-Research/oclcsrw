<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:srw="http://www.loc.gov/zing/srw/"
  xmlns:diag="http://www.loc.gov/zing/srw/diagnostic/">

<xsl:output method="html" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>

<xsl:template name="stdiface">
<head>
<title><xsl:value-of select="$title"/></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<!--link type="text/css" rel="stylesheet" href="http://shipengrover-j.oa.oclc.org/terminologies/temp/scifi-ff-footer_files/researchproject_oclc.css"/-->
<link type="text/css" rel="stylesheet" href="http://www.oclc.org/research/common/css/researchproject_oclc.css"/>
<style type="text/css">
&lt;!--
table.layout { border: none; margin: 0; padding: 0; width: 100%; }
table.layout td { border: none; margin: 0; padding: 0; }
table.formtable th, table.formtable td { border-top: 1px solid #999; border-left: 1px solid #999; color: #333; padding: 4px; text-align: left; vertical-align: top}
table.formtable td { width: 100%}
input.button { margin: 0; }
--&gt;
</style>
</head>
<body>
<div id="masthead">

	<div id="or"><a href="http://www.oclc.org/research">A Project of OCLC Research</a></div>
	<div id="project"><xsl:value-of select="$dbname"/></div>
	<div id="logo"><a href="http://www.oclc.org/default.htm"><img src="http://www.oclc.org/research/common/images/logo_wh_h_gray.gif" alt="OCLC" /></a></div>

</div>
<!-- close masthead -->
  <xsl:apply-templates/>
<div id="footer">
	<div id="legal">
		<div id="copyright">&#169; 2008 OCLC</div>
		<div id="TandC">This project is covered by the <a href="http://www.oclc.org/research/researchworks/terms.htm">OCLC ResearchWorks Terms and Conditions</a></div>
		<!-- add or hide badges as required -->
		<div id="badges">
		<!-- sru/w -->
		<a href="http://www.oclc.org/research/software/srw"><img src="http://www.oclc.org/research/images/badges/oclc_srwu.gif"/></a>
		<!-- errol -->
		<!--img src="http://www.oclc.org/research/images/badges/oclc_errol.gif"-->
		<!-- Gwen -->
		<img src="http://www.oclc.org/research/images/badges/oclc_gwen.gif"/>
		<!-- oaicat -->
		<!--img src="http://www.oclc.org/research/images/badges/oclc_oaicat.gif"-->
		<!-- pears -->
		<img src="http://www.oclc.org/research/images/badges/oclc_pears.gif"/>
		<!-- xsltproc -->
		<!--img src="http://www.oclc.org/research/images/badges/oclc_xsltproc.gif"-->
		
		
		</div>
	</div>
</div>
</body>

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
