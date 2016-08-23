<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0" 
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:date="http://exslt.org/dates-and-times"
   xmlns:srw="http://www.loc.gov/zing/srw/"
   extension-element-prefixes="date">

  <xsl:variable name="WCatPointers" select="true()"/>
  <xsl:variable name="Environment" select="string('dev')"/>
  <xsl:variable name="abandoned" select="//abandonedIdentity"/>
  <xsl:variable name="longName">
    <xsl:call-template name="longName">
      <xsl:with-param name="here" select="//Identity/nameInfo/rawName"/>
      </xsl:call-template>
    </xsl:variable>
  <xsl:variable name="shortName" select="//Identity/nameInfo/rawName/suba"/>
  <xsl:variable name="namestr.space" select="string(' ')"/>
  <xsl:variable name="first.name" select="substring-after($shortName,',')"/>
  <xsl:variable name="str.lnt" select="string-length($first.name)"/>
  <xsl:variable name="last.name" select="substring-before($shortName,',')"/>
  <xsl:variable name="end.char" select="substring($first.name,number($str.lnt),number($str.lnt + 1))"/>

  <xsl:variable name="bestcover.title">
     <xsl:for-each select="//Identity/by/citation">
       <xsl:choose>
           <xsl:when test="string(//Identity/bestCover)=string(cover)">
             <xsl:value-of select="title"/>
           </xsl:when>
       </xsl:choose>
    </xsl:for-each>
    </xsl:variable>    

  <xsl:variable name="normalname">
    <xsl:choose>
      <xsl:when test="not(//Identity/nameInfo/@type = string('corporate'))">
        <xsl:choose>
          <xsl:when test="$end.char = '.'">
            <xsl:value-of select="concat(substring($first.name,0,number($str.lnt)),$namestr.space,$last.name)"/>
            </xsl:when>

          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="string-length($last.name) &gt; 1">
                <xsl:value-of select="concat($first.name,$namestr.space,$last.name)"/>
                </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="$shortName"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$shortName"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>    

  <xsl:variable name="targetHost" select="/srw:searchRetrieveResponse/srw:extraResponseData/targetURL/host"/>
  <xsl:variable name="worldCatHost">
    <xsl:choose>
        <!-- when in production or QA, generate relative links -->
        <xsl:when test="contains($targetHost, 'worldcat.org')"> <!-- production -->
        <xsl:text></xsl:text>
        </xsl:when>
      <xsl:when test="contains($targetHost, 'dev.oclc.org')"> <!-- QA -->
        <xsl:text></xsl:text>
        </xsl:when>
      <xsl:when test="contains($targetHost, 'levan-r')"> <!-- development -->
        <xsl:text>http://openwcdev1.dev.oclc.org</xsl:text>
        </xsl:when>
      <xsl:otherwise> <!-- anywhere else -->
        <xsl:text>http://worldcat.org</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

  <xsl:template name="longName">
    <xsl:param name="here"/>
    <xsl:for-each select="$here/*">
      <xsl:value-of select="."/>
      <xsl:text> </xsl:text>
      </xsl:for-each>
    </xsl:template>

 <xsl:template match="Identity">
<!--
  <link href="/identities/wcatid2.css" rel="stylesheet" type="text/css" />
  -->

  <div id="mainbody">
<table id="tblmainbody" border="0" cellspacing="0" cellpadding="0">
<tr>
	<td id="MainCol1" style="vertical-align:top;">
	<!-- Column1-->
<div id="Nav">
<span class="NavTitle">Jump To:&#160;</span> <a href="#linkoverview">Overview</a>
	 <span class="divider">|</span> 
	 <a href="#linktimeline">Publication Timeline</a>
	  <span class="divider">|</span> 
	  <a href="#linkworksabout">Works About</a>
	  <span class="divider">|</span>
	  <a href="#linkworksby">Works By</a>
	  <span class="divider">|</span>
          <xsl:if test="contains($Environment, 'dev')">
            <a href="#linkfix">Fix Record</a>
            <span class="divider">|</span> 
            </xsl:if>
	  <a href="#linkaudlevel">Audience Level</a>
	  <span class="divider">|</span> 
	  <a href="#linkassoc">Related Names</a>
<!--
	  <span class="divider">|</span> 
	  <a href="#linkcomments">Comments</a>
-->
	  <span class="divider">|</span> 
	  <a href="#linklinks">Useful Links</a>
	  <span class="divider">|</span> 
	  <a href="#linkfastheadings">Fast Headings</a>

</div> <!-- Nav -->

    <xsl:apply-templates select="nameInfo/rawName" />
    <xsl:call-template name="overview" />
    <xsl:apply-templates select="nameInfo/dates" />
    <xsl:apply-templates select="about" />
    <xsl:apply-templates select="by" />
    <xsl:if test="$Environment = 'dev'">
      <xsl:call-template name="fixRecord" />
      </xsl:if>
    <xsl:apply-templates select="audLevel" />
    <xsl:apply-templates select="associatedNames" />
    <xsl:apply-templates select="associatedNames/rawName/normName" />
    <xsl:apply-templates select="authorityInfo" />
    <xsl:apply-templates select="nameInfo/fastHeadings" />

<!--
    <xsl:call-template name="comments" />
-->
    </td> 
    <xsl:call-template name="bestCover" />
    </tr>
    </table>
    </div> <!-- mainbody -->
  


  </xsl:template>
  
<xsl:template name="overview">
  <a name="linkoverview"/>
  <div id="overview" class="SectionHead">
    <div class="label">Overview</div>
    <table id="tbl_overview" border="0" cellspacing="0" cellpadding="0">
      <xsl:if test="nameInfo/workCount&gt;0">
        <tr>
          <th>Works:</th>
          <td colspan="2">
            <xsl:value-of select="format-number(nameInfo/workCount,'##,##0')"/>
            works in
            <xsl:value-of select="format-number(nameInfo/recordCount,'##,##0')"/>
            publications in
            <xsl:value-of select="count(nameInfo/languages/lang)"/>
            <xsl:choose>
              <xsl:when test="count(nameInfo/languages/lang) = 1">
                 language and
                </xsl:when>
              <xsl:otherwise>
                languages and
                </xsl:otherwise>
              </xsl:choose>
            <xsl:value-of select="format-number(nameInfo/totalHoldings,'##,##0')"/>
            library holdings
            </td>
          </tr>
        </xsl:if>

      <xsl:if test="nameInfo/genres">
        <tr>
          <th>Genres:</th>
          <td>
            <xsl:for-each select="nameInfo/genres/genre">
              <xsl:variable name="href">
                <xsl:choose>
                  <xsl:when test="$WCatPointers">
                    <xsl:value-of select="$worldCatHost"/>
                    <xsl:text>/search?q=su%3a</xsl:text>
                    <xsl:value-of select="@norm"/>
                    </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>/identities/genre/</xsl:text>
                    <xsl:value-of select="@norm"/>
                    <xsl:text>.html</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>
              <a href="{$href}" target="_top">
                <xsl:call-template name="mouseoverName" >
                  <xsl:with-param name="name" select="format-number(@count,'##,##0')"/>
                  </xsl:call-template>
                <xsl:value-of select="."/>
                </a>&#160;
              </xsl:for-each>
            </td>
          </tr>
        </xsl:if>

      <xsl:if test="biogSHs">
        <tr>
          <th>Subject Headings:</th>
          <td>
            <xsl:for-each select="biogSHs/biogSH">
              <xsl:variable name="href">
                <xsl:choose>
                  <xsl:when test="$WCatPointers">
                    <xsl:value-of select="$worldCatHost"/>
                    <xsl:text>/search?q=su%3a</xsl:text>
                    <xsl:value-of select="@norm"/>
                    </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>/identities/subject/</xsl:text>
                    <xsl:value-of select="@norm"/>
                    <xsl:text>.html</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>
              <a href="{$href}" target="_top">
                <xsl:call-template name="mouseoverName" >
                  <xsl:with-param name="name" select="format-number(@count,'##,##0')"/>
                  </xsl:call-template>
                <xsl:value-of select="."/>
                </a>&#160;
              </xsl:for-each>
            </td>
          </tr>
        </xsl:if>

      <xsl:if test="nameInfo/relators/relator">
        <tr>
          <th>Roles:</th>
	  <xsl:variable name="rel.cnt" select="count(nameInfo/relators/relator)"/>
          <td>
            <xsl:for-each select="nameInfo/relators/relator">
              <xsl:variable name="code" select="@code"/>
              <xsl:variable name="code.str" select="document('relators.xml')/relatorCodes/relator[@code=$code]/@full"/>
              <xsl:variable name="srch.string">
                <xsl:call-template name="constructsrch">
                  <xsl:with-param name="oclcnums" select="oclcnum"/>
                  </xsl:call-template>
                </xsl:variable>
              <a href="{$worldCatHost}/search?q={$srch.string}" target="_top">
                <xsl:call-template name="mouseoverName" >
                  <xsl:with-param name="name" select="format-number(@count,'##,##0')"/>
                  </xsl:call-template>
                <xsl:choose>
                  <xsl:when test="string-length($code.str) &gt; 0">
                    <xsl:value-of select= "$code.str"/>
                    </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select= "@code"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </a>
              <xsl:if test="position()&lt;$rel.cnt">
                <xsl:text>, </xsl:text>
                </xsl:if>
              </xsl:for-each>
            </td>
          </tr> 
        </xsl:if>

      <xsl:if test="nameInfo/lcc">
        <tr>
          <th>Classifications:</th>
          <td>
            <xsl:value-of select="nameInfo/lcc"/>,
            <xsl:choose>
              <xsl:when test="$WCatPointers">
                <xsl:value-of select="nameInfo/ddc"/>
                </xsl:when>
              <xsl:otherwise>
                <a href="http://deweyresearch.oclc.org/ddcbrowser/wcat?link={nameInfo/ddc}">
                  <xsl:value-of select="nameInfo/ddc"/>
                  </a>
                </xsl:otherwise>
              </xsl:choose>
            </td>
          </tr>
        </xsl:if>
      </table>
    </div>
  </xsl:template>

<xsl:template name="constructsrch">
  <xsl:param name="oclcnums"/>
  <xsl:variable name="oclc.cnt" select="count($oclcnums)"/>
    <xsl:for-each select="$oclcnums">
          <xsl:variable name="sub.s" select="substring(.,4,10)"/>
     <xsl:choose>
       <xsl:when test="position() &lt; 1">
          <xsl:value-of select="concat('no:',$sub.s)"/>
       </xsl:when>
       <xsl:when test="position() = $oclc.cnt">
          <xsl:value-of select="concat('no:',$sub.s)"/>
       </xsl:when>
      <xsl:otherwise>
          <xsl:value-of select="concat('no:',$sub.s,'+OR+')"/>
      </xsl:otherwise>
     </xsl:choose>

   </xsl:for-each>
</xsl:template>

<xsl:template name="mouseoverrelator">
  <xsl:param name="count"/>
   <xsl:attribute name="title"><xsl:value-of select="$count"/>
</xsl:attribute>
</xsl:template>


  <xsl:template name="languageSet">
    <xsl:param name="lng"/>
    <xsl:value-of select="document('languages.xml')/languageCodes/language[@code=$lng]/@full"/>
    </xsl:template>


  <xsl:template match="nameInfo/rawName">
    <xsl:if test="$abandoned">
      <div id="expired">
        <h2>
          <img src="images/alert.gif" />
          PLEASE NOTE: This Identity has been been retired and is no longer being kept current.
          </h2>
        <div id="expired-search">
            <form method="get" action="/identities/find">
            <span>Please Try a New Search:</span>
            <input id="expired-search-text" name="fullName" type="text" size="40" value="{$longName}"/>
            <input value="Search" id="expired-SearchButton" type="submit"/>
            </form>
          </div>
        </div>
      </xsl:if>

    <div id="name">
      <h1><xsl:value-of select="$longName"/></h1>
      </div>
    </xsl:template>

  <xsl:template match="authorityInfo/x400">
    <xsl:for-each select=".">
      <xsl:variable name="str.lnt" select="string-length(rawName/suba)"/>
      <xsl:choose>
        <xsl:when test="substring(rawName/suba,($str.lnt),$str.lnt) =','">
          <xsl:value-of select="substring(rawName/suba,0,($str.lnt))"/>
          </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="rawName/suba"/>
          </xsl:otherwise>
        </xsl:choose>
      <xsl:choose>
        <xsl:when test="rawName/subb">
          <xsl:text> </xsl:text>
          <xsl:value-of select="rawName/subb"/>
          </xsl:when>
        <xsl:when test="rawName/subc">
          <xsl:text> </xsl:text>
          <xsl:value-of select="rawName/subc"/>
          </xsl:when>
        <xsl:when test="rawName/subd">
          <xsl:text> </xsl:text>
          <xsl:value-of select="rawName/subd"/>
          </xsl:when>
        <xsl:when test="name(.)='rawName/subq'">
          <xsl:text> </xsl:text>
          <xsl:value-of select="rawName/subq"/>
          </xsl:when>
        </xsl:choose>
      <br/>
      </xsl:for-each>
    </xsl:template>

  <xsl:template match="authorityInfo/xref">
    <xsl:for-each select=".">
      <xsl:variable name="href">
        <xsl:choose>
          <!--xsl:when test="$WCatPointers">
            <xsl:value-of select="$worldCatHost"/>
            <xsl:text>/search?q=kw%3a</xsl:text>
            <xsl:value-of select="./rawName"/>
            </xsl:when-->
          <xsl:when test="false"/> <!-- makes the compiler happy -->
          <xsl:otherwise>
            <xsl:text>/identities/</xsl:text>
            <xsl:value-of select="link"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
      <p>
        <img src="/identities/images/user-orange-mini.jpg"/>
        <a href="{$href}" target="_top">
          <xsl:value-of select="./rawName"/>
          </a>
        </p>
      </xsl:for-each>
    </xsl:template>

  <xsl:template match="nameInfo/altScript">
    <xsl:for-each select=".">
      <xsl:variable name="str.lnt" select="string-length(.)"/>
      <xsl:choose>
        <xsl:when test="substring(.,($str.lnt),$str.lnt) =','">
          <xsl:value-of select="substring(.,0,($str.lnt))"/>
          </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="."/>
          </xsl:otherwise>
        </xsl:choose>
      <br/>
      </xsl:for-each>
    </xsl:template>

<xsl:template name="bestCover">
<td id="MainCol2" style="vertical-align:top;"><!--Column 2 -->
<xsl:choose>
<xsl:when test="string-length(bestCover) > 0">
<div class="coverart">
          <xsl:call-template name="coverImage">
            <xsl:with-param name="oclc" select="bestCover/@oclc"/>
            <xsl:with-param name="type" select="bestCover/@type"/>
            <xsl:with-param name="number" select="bestCover"/>
            <xsl:with-param name="title" select="$bestcover.title"/>
       </xsl:call-template>

 </div>
</xsl:when>
</xsl:choose>

 <xsl:if test="nameInfo/altScript | authorityInfo/x400 | authorityInfo/xref" >
 
<div class="col2TitleB">Alternative Names</div> 
<div id="akalist" style="margin:5px 10px 10px 10px;">

 <xsl:apply-templates select="authorityInfo/xref" />
 <xsl:apply-templates select="authorityInfo/x400" />
 <xsl:apply-templates select="nameInfo/altScript" />
 </div>
 </xsl:if>
 
 <div class="col2TitleB">Languages</div> 
<div id="langs" style="margin:5px 10px 10px 10px;">
 <xsl:apply-templates select="nameInfo/languages" />
 </div>

  <xsl:if test="count(by/citation/cover) &gt; 0 or count(about/citation/cover) &gt; 0">
     <div class="col2TitleB">Covers</div> 
  </xsl:if>

<div id="rtCovers" style="margin:5px 10px 10px 10px;">

<xsl:if test="count(by/citation/cover) &gt; 0 or count(about/citation/cover) &gt; 0">
<div class="coverart">

<xsl:variable name="bycover.cnt" select="count(by/citation/cover)"/>
<xsl:choose>
<xsl:when test="$bycover.cnt &gt; 10">
   <xsl:for-each select="by/citation/cover">
  <xsl:if test="not(position()=1)">
    <xsl:if test="position() &lt; 10">

      <xsl:call-template name="coverImage">
        <xsl:with-param name="oclc" select="@oclc"/>
        <xsl:with-param name="type" select="@type"/>
        <xsl:with-param name="number" select="."/>
        <xsl:with-param name="title" select="../title"/>
      </xsl:call-template>

    </xsl:if>
    </xsl:if>
  </xsl:for-each>
</xsl:when>
<xsl:otherwise>
   <xsl:for-each select="by/citation/cover">
  <xsl:if test="not(position()=1)">
    <xsl:if test="position() &lt; 1+$bycover.cnt">

      <xsl:call-template name="coverImage">
        <xsl:with-param name="oclc" select="@oclc"/>      
        <xsl:with-param name="type" select="@type"/>
        <xsl:with-param name="number" select="."/>
        <xsl:with-param name="title" select="../title"/>
      </xsl:call-template>

    </xsl:if>
    </xsl:if>
   </xsl:for-each>

   <xsl:for-each select="about/citation/cover">
     <xsl:if test="not(position()=1)">
    <xsl:if test="position() &lt; 10-$bycover.cnt">
      <xsl:call-template name="coverImage">
        <xsl:with-param name="oclc" select="@oclc"/>      
        <xsl:with-param name="type" select="@type"/>
        <xsl:with-param name="number" select="."/>
        <xsl:with-param name="title" select="../title"/>
      </xsl:call-template>

    </xsl:if>
    </xsl:if>
  </xsl:for-each>
</xsl:otherwise>

</xsl:choose>

</div>
</xsl:if>
</div>
</td>
</xsl:template>

<xsl:template match="nameInfo/languages">
<xsl:if test="lang">

   <xsl:choose>

   <xsl:when test="count(lang) &lt; 26">
   <xsl:apply-templates select="lang[position() &lt; 26]"/>
   </xsl:when>
   
   <xsl:otherwise>
   <xsl:apply-templates select="lang[position() &lt; 26]"/>
   <span id="langAll" class="langNext" >
   <xsl:apply-templates select="lang[position() &gt; 25]"/>
   </span>


<div id="langOpenAll" class="langNext"  
	onmouseover="style.cursor='pointer'" 
	onclick="document.getElementById('langAll').style.display='inline',
	document.getElementById('langClose').style.display='block',
	document.getElementById('langOpenAll').style.display='none'">more<img src="/identities/images/icon_arrowdown_whbg.gif" /></div>

<div id="langClose" class="langNext" 
	onmouseover="style.cursor='pointer'" 
	onclick="document.getElementById('langAll').style.display='none',
	document.getElementById('langOpenAll').style.display='block',
	document.getElementById('langClose').style.display='none'">fewer<img src="/identities/images/icon_arrowup_whbg.gif" /></div>

</xsl:otherwise>
</xsl:choose>
</xsl:if>
</xsl:template>


<xsl:template match="lang">
<xsl:variable name="lng.cnt" select="count(.)"/>
<xsl:variable name="code" select="@code"/>

<a href="{$worldCatHost}/search?q=kw%3A{../../rawName/suba} {../../rawName/subb} {../../rawName/subc}&amp;qt=faceted;&amp;fq=+ln%3A{$code}" target="_top">
  <xsl:value-of select="document('languages.xml')/languageCodes/language[@code=$code]/@full"/>
  </a>
             <span class="label_xxsmall">
            (<xsl:value-of select="format-number(@count,'##,##0')"/>)</span>
               <br />
</xsl:template>


  <xsl:template match="name">
  </xsl:template>
<!--
  <xsl:template match="authorityInfo/standardForm/rawName/suba">
  </xsl:template>
-->
  <xsl:template match="authorityInfo/standardForm/rawName/subc">
  </xsl:template>
  <xsl:template match="authorityInfo/standardForm/rawName/subd">
  </xsl:template>
  <xsl:template match="authorityInfo/standardForm/rawName/subq">
  </xsl:template>

  <xsl:template match="authorityInfo/x400/rawName/suba">
  </xsl:template>
  <xsl:template match="authorityInfo/x400/rawName/subd">
  </xsl:template>
  <xsl:template match="authorityInfo/x400/rawName/subc">
  </xsl:template>
  <xsl:template match="authorityInfo/x400/rawName/subq">
  </xsl:template>

  <xsl:template match="authorityInfo/standardForm/rawName/subd">
  </xsl:template>

  <xsl:template match="Identity/pnkey">
  </xsl:template>

  <xsl:template match="Identity/nameInfo">
    <xsl:if test="string-length(lcc) > 0">
   <xsl:value-of select="lcc"/>
   </xsl:if>
    <xsl:if test="string-length(ddc) > 0">

   <xsl:value-of select="ddc"/>
   </xsl:if>
 
  </xsl:template>

  <xsl:template match="Identity/nameInfo/totalHoldings">
  </xsl:template>
  <xsl:template match="Identity/nameInfo/workCount">
  </xsl:template>

  <xsl:template match="Identity/nameInfo/fictionCount">
  </xsl:template>

  <xsl:template match="Identity/nameInfo/recordType">
  </xsl:template>

  <xsl:template match="Identity/nameInfo/rawName/suba">
  </xsl:template>

  <xsl:template match="Identity/nameInfo/rawName/subd">
  </xsl:template>

<xsl:template name="fixRecord">
  <a name="linkFix"/>
  <div id="fixRecord" class="SectionHead">
    <div class="label"><a href="/identities/{//Identity/pnkey}/fix">Assign some of these works to another Identity</a></div>
    </div>
    </xsl:template>

    
    <xsl:template match="audLevel">
  <xsl:variable name="min.l" select="min/level"/>
<xsl:variable name="avg.l" select="avg/level"/>
<xsl:variable name="max.l" select="max/level"/>
<a name="linkaudlevel"></a>

<div id="audiencelevel" class="SectionHead">
	<div class="label">Audience Level</div>
	<table id="tblAudLvl" border="0" cellspacing="0" cellpadding="0">
	<tr>
	<td>0</td>
	<td colspan="3">
		<div id="AudShell"><div id="AudLevel">
 <img id="imgAudLevel" src="/identities/images/spacer.gif" alt="Audience Level" style="width:{198*($avg.l)}px;" /></div></div>
	</td>
	<td>1</td>
	</tr>
	<tr>
		<td>&#160;</td>
		<td class="AudMarker" style="text-align:left;">Kids</td>
		<td class="AudMarker" style="text-align:center;">General</td>
		<td class="AudMarker" style="text-align:right;">Special</td>
		<td>&#160;</td>
	</tr>
	</table>

   <span id="audlevelcomment">Audience level: <strong><xsl:value-of select="avg/level"/></strong> (from <a><xsl:value-of select="min/level"/> for <em>
<xsl:call-template name="mouseoverName" >
      <xsl:with-param name="name" select="min/title"/>
    </xsl:call-template>
<xsl:value-of select="substring(min/title,1,10)"/>
</em></a>


... to <a><xsl:value-of select="max/level"/> for <em>
   <xsl:call-template name="mouseoverName" >
      <xsl:with-param name="name" select="max/title"/>
    </xsl:call-template>

<xsl:value-of select="substring(max/title,1,10)"/>...)
</em></a></span><br /></div> 
  </xsl:template>

  <xsl:template match="authorityInfo">
  <a name="linklinks"></a>

  <div id="links" class="SectionHead">
<div class="label">Useful Links</div><ul>
<li><a>
<xsl:attribute name="href">
<xsl:text>http://errol.oclc.org/laf/</xsl:text>
<xsl:variable name="LCCN.nospace" select="translate(lccn, ' ', '')"/>
<xsl:variable name="LCCN.length" select="string-length($LCCN.nospace)"/>
<xsl:choose>
<xsl:when test="substring($LCCN.nospace, $LCCN.length - 5, 1) != '0'">
<xsl:value-of select="concat(substring($LCCN.nospace, 1, $LCCN.length - 6), '-',
substring($LCCN.nospace, $LCCN.length - 5))"/>
</xsl:when>
<xsl:when test="substring($LCCN.nospace, $LCCN.length - 4, 1) != '0'">
<xsl:value-of select="concat(substring($LCCN.nospace, 1, $LCCN.length - 6), '-',
substring($LCCN.nospace, $LCCN.length - 4))"/>
</xsl:when>
<xsl:when test="substring($LCCN.nospace, $LCCN.length - 3, 1) != '0'">
<xsl:value-of select="concat(substring($LCCN.nospace, 1, $LCCN.length - 6), '-',
substring($LCCN.nospace, $LCCN.length - 3))"/>
</xsl:when>
<xsl:when test="substring($LCCN.nospace, $LCCN.length - 2, 1) != '0'">
<xsl:value-of select="concat(substring($LCCN.nospace, 1, $LCCN.length - 6), '-',
substring($LCCN.nospace, $LCCN.length - 2))"/>
</xsl:when>
</xsl:choose>
<xsl:text>.html</xsl:text>
</xsl:attribute>
<xsl:text>
Library of Congress Authority File (English)
</xsl:text>
        </a></li>
  <xsl:choose>
  <xsl:when test="string-length(pnd) > 0">
    <li>
<a><xsl:attribute name="href">http://dispatch.opac.ddb.de/DB=4.1/PPN?PPN=<xsl:value-of select="pnd" /></xsl:attribute>Deutsche Nationalbibliothek Authority File (German)</a>
</li>
</xsl:when>
</xsl:choose>
  <xsl:for-each select="wikiLink">

    <li>
<a><xsl:attribute name="href">http://en.wikipedia.org/wiki/Special:Search?search=<xsl:value-of select="." /></xsl:attribute>Wikipedia <xsl:value-of select="translate(.,'_',' ')"/></a>
</li> 
</xsl:for-each>

</ul></div>
  </xsl:template>


<xsl:template match="nameInfo/fastHeadings">
  <a name="linkfastheadings"></a>
  <div id="FAST" class="SectionHead">
    <div class="label">Associated Subjects</div>
    <div id="FASTCloud">
      <xsl:for-each select="fast">
        <xsl:sort select="." order="ascending" data-type="text" />
        <xsl:variable name="href">
          <xsl:choose>
            <xsl:when test="$WCatPointers">
              <xsl:value-of select="$worldCatHost"/>
              <xsl:text>/search?q=su%3a</xsl:text>
              <xsl:value-of select="."/>
              </xsl:when>
            <xsl:otherwise>
              <xsl:text>/identities/subject/</xsl:text>
              <xsl:value-of select="@norm"/>
              <xsl:text>.html</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
        <a class="size{@size}" href="{$href}" target="_top">
<!--
          <xsl:call-template name="mouseoverfast" >
            <xsl:with-param name="namex" select="number(@tag)"/>
            <xsl:with-param name="countx" select="@count"/>
            </xsl:call-template>
-->
          <xsl:value-of select="."/>
          </a><xsl:text> </xsl:text>
        </xsl:for-each>
      </div>
    </div>
  </xsl:template>


<xsl:template match="Identity/by">
<!--
  <xsl:apply-templates select="citation">
    <xsl:sort select="uniqueHoldings" order="descending" data-type="number"/>
    </xsl:apply-templates>
-->
  <a name="linkworksby"></a>
  <div id="worksby" class="SectionHead">
    <div class="label">Most widely held works by 
<!--
      <xsl:call-template name="shortName"/>
-->
      <xsl:value-of select="$normalname"/>
      </div>

    <xsl:choose>
      <xsl:when test="count(citation) &lt; 11">
        <xsl:apply-templates select="citation[position() &lt; 11]" mode="by">
<!--
          <xsl:sort select="uniqueHoldings" order="descending" data-type="number"/>
-->
          </xsl:apply-templates>
        <div id="worksbyLine">&#160;</div>
        </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="citation[position() &lt; 11]" mode="by">
<!--
          <xsl:sort select="uniqueHoldings" order="descending" data-type="number"/>
-->
          </xsl:apply-templates>
        <span id="worksbyExtended">
          <xsl:apply-templates select="citation[position() &gt; 10]" mode="by">
<!--
            <xsl:sort select="uniqueHoldings" order="descending" data-type="number"/>
-->
            </xsl:apply-templates>
          </span>

        <div id="worksbyExtender">
          <div id="worksbyLine">&#160;</div>
          <div id="worksbyBtnMore" onMouseOver="style.cursor='pointer'" onClick="document.getElementById('worksbyExtended').style.display='block', document.getElementById('worksbyBtnLess').style.display='block', document.getElementById('worksbyBtnMore').style.display='none'">
            <xsl:text>more</xsl:text>
            <img src="/identities/images/icon_arrowdown_whbg.gif" alt="Show More Titles"></img>
            </div>
          <div id="worksbyBtnLess" onMouseOver="style.cursor='pointer'" onClick="document.getElementById('worksbyExtended').style.display='none',document.getElementById('worksbyBtnLess').style.display='none', document.getElementById('worksbyBtnMore').style.display='block'">
            <xsl:text>fewer</xsl:text>
            <img src="/identities/images/icon_arrowup_whbg.gif" alt="Show Fewer Titles"></img>
            </div>
          </div>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

<xsl:template match="citation" mode="by">
<!--
      <xsl:apply-templates select=".">
      <xsl:sort select="uniqueHoldings" order="descending" data-type="number"/>
     </xsl:apply-templates>
-->
  <div class="WorksByEntry">
    <a href="{$worldCatHost}/search?q=no:{substring-after(oclcnum,'ocn')}" target="_top">
      <xsl:value-of select="title"/>
      </a>
    <xsl:choose>
      <xsl:when test="creator"> 
        <xsl:text> by </xsl:text>
        </xsl:when>
      <xsl:otherwise>
        </xsl:otherwise>
      </xsl:choose>  
    <xsl:choose>
      <xsl:when test="contains(creator,',')"> 
        <xsl:variable name="namestr.space" select="string(' ')"/>
        <xsl:value-of select="substring-after(creator,',')"/>
        <xsl:value-of select="$namestr.space"/>
        <xsl:value-of select="substring-before(creator,',')"/>
        </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="creator"/>
        </xsl:otherwise>
      </xsl:choose>
    <span class="format">(
      <xsl:variable name="rec.type" select="recordType"/>
      <xsl:value-of select="document('recordtype.xml')/recordTypeCodes/recordtype[@code=$rec.type]/@full"/>
    )</span>
    <br/>
 
    <xsl:variable name="num.edt" select="numEditions"/>
    <xsl:choose>
      <xsl:when test="dates">
        <xsl:value-of select="format-number(numEditions,'##,##0')"/>
        editions published between
        <xsl:value-of select="dates/@first"/>
        and
        <xsl:value-of select="dates/@last"/>
        </xsl:when>
      <xsl:when test="$num.edt > 1">
        <xsl:value-of select="$num.edt"/>
        editions published in
        <xsl:value-of select="date"/>
        </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="numEditions"/>
        edition published 
        <xsl:if test="date">
          <xsl:text> in </xsl:text>
          </xsl:if>
        <xsl:value-of select="date" />
        </xsl:otherwise>
      </xsl:choose>

    <xsl:variable name="lng.cnt" select="count(languages/lang)"/>
    <xsl:variable name="lang.code">
      <xsl:call-template name="languageSet">
        <xsl:with-param name="lng" select="languages/lang/@code"/>
        </xsl:call-template>
      </xsl:variable>
    <xsl:choose>
      <xsl:when test="$lang.code = 'No Linguistic content'" >
        </xsl:when> 
      <xsl:when test="$lng.cnt = 1" >
        in 
        <xsl:call-template name="languageSet">
          <xsl:with-param name="lng" select="languages/lang/@code"/>
          </xsl:call-template>
        </xsl:when>
      <xsl:when test="$lng.cnt = 2">
        in 
        <xsl:call-template name="languageSet">
          <xsl:with-param name="lng" select="languages/lang[1]/@code"/>
          </xsl:call-template>
        <xsl:text> and </xsl:text>
        <xsl:call-template name="languageSet">
          <xsl:with-param name="lng" select="languages/lang[2]/@code"/>
          </xsl:call-template>
        </xsl:when>
      <xsl:otherwise>
        in
        <xsl:value-of select="$lng.cnt"/>
        languages
        </xsl:otherwise>
      </xsl:choose>
    <xsl:choose>
      <xsl:when test="uniqueHoldings">
        and held by
        <xsl:value-of select="format-number(uniqueHoldings,'##,##0')"/>
        <xsl:choose>
          <xsl:when test="uniqueHoldings = 1">
            library
            </xsl:when>
          <xsl:otherwise>
            libraries
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
      <xsl:otherwise>
        and held by
        <xsl:value-of select="format-number(holdings,'##,##0')"/>
        <xsl:choose>
          <xsl:when test="holdings = 1">
            library
            </xsl:when>
          <xsl:otherwise>
            libraries
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    worldwide
    <br/>
 
    <xsl:if test="genre and not(summary)">
      <xsl:text> </xsl:text>
      <b><xsl:value-of select="genre"/></b>
      </xsl:if>
   
    <xsl:value-of select="summary"/>
    </div>
  </xsl:template>


<xsl:template match="Identity/by/citation">
  </xsl:template>

<xsl:template match="Identity/about">
  <xsl:apply-templates select="citation">
    <xsl:sort select="uniqueHoldings" order="descending" data-type="number" />
    </xsl:apply-templates>
   
  <a name="linkworksabout"></a>

  <div id="worksabout" class="SectionHead">
    <div class="label">Most widely held works about 
<!--
      <xsl:call-template name="shortName"/>
-->
      <xsl:value-of select="$normalname"/>
      </div>

    <xsl:variable name="Aboutcnt">
      <xsl:choose>
        <xsl:when test="not(//Identity/by/citation)">
          <xsl:value-of select="11"/>

          </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="6"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

    <xsl:choose>
      <xsl:when test="count(citation) &lt; $Aboutcnt">
        <ul>
          <xsl:apply-templates select="citation[position() &lt; $Aboutcnt]" mode="about"/>
          </ul>
        <div id="worksaboutLine">&#160;</div>
        </xsl:when>
   
      <xsl:otherwise>
        <ul>
          <xsl:apply-templates select="citation[position() &lt; $Aboutcnt]" mode="about"/>
          <span id="worksaboutExtended">
            <xsl:apply-templates select="citation[position() &gt; ($Aboutcnt -1)]" mode="about"/>
            </span>
          </ul>
        <div id="worksaboutExtender">
          <div id="worksaboutLine">&#160;</div>
          <div id="worksaboutBtnMore" onMouseOver="style.cursor='pointer'" onClick="document.getElementById('worksaboutExtended').style.display='block', document.getElementById('worksaboutBtnLess').style.display='block', document.getElementById('worksaboutBtnMore').style.display='none'">
            <xsl:text>more</xsl:text>
            <img src="/identities/images/icon_arrowdown_whbg.gif" alt="Show More Titles"></img>
            </div>
          <div id="worksaboutBtnLess" onMouseOver="style.cursor='pointer'" onClick="document.getElementById('worksaboutExtended').style.display='none',document.getElementById('worksaboutBtnLess').style.display='none', document.getElementById('worksaboutBtnMore').style.display='block'">
            <xsl:text>fewer</xsl:text>
            <img src="/identities/images/icon_arrowup_whbg.gif" alt="Show Fewer Titles"></img>
            </div>
          </div>
        </xsl:otherwise>
      </xsl:choose>
    </div>  
  </xsl:template>

<xsl:template match="citation" mode="about">


<xsl:choose>
<xsl:when test="not(//Identity/by/citation)">



<div class="WorksByEntry">
<!--
<a href="{$worldCatHost}/oclc/{substring-after(oclcnum,'ocn')}&amp;ht=edition" target="_top">
 -->
 <a href="{$worldCatHost}/search?q=no:{substring-after(oclcnum,'ocn')}" target="_top">
  <xsl:value-of select="title"/>
  </a>
<xsl:choose>
<xsl:when test="creator"> 
   <xsl:text> by </xsl:text>
</xsl:when>
<xsl:otherwise>
</xsl:otherwise>
</xsl:choose>  
<xsl:choose>
    <xsl:when test="contains(creator,',')"> 
    <xsl:variable name="namestr.space" select="string(' ')"/>
    <xsl:value-of select="substring-after(creator,',')"/>
    <xsl:value-of select="$namestr.space"/>
    <xsl:value-of select="substring-before(creator,',')"/>
</xsl:when>
<xsl:otherwise>
 <xsl:value-of select="creator"/>
</xsl:otherwise>
</xsl:choose>
<span class="format">(
<xsl:variable name="rec.type" select="recordType"/>
 <xsl:value-of select="document('recordtype.xml')/recordTypeCodes/recordtype[@code=$rec.type]/@full"/>
)</span>

<br />
 
<xsl:variable name="num.edt" select="numEditions"/>

<xsl:choose>
  <xsl:when test="dates">
<xsl:value-of select="format-number(numEditions,'##,##0')"/> editions published between <xsl:value-of select="dates/@first"/> and <xsl:value-of select="dates/@last"/>
</xsl:when>

   <xsl:when test="$num.edt > 1">
      <xsl:value-of select="$num.edt"/> editions published in <xsl:value-of select="date"/>
   </xsl:when>
   <xsl:otherwise>
   <xsl:value-of select="numEditions"/> edition published 
      <xsl:if test="date">
         <xsl:text> in </xsl:text>
      </xsl:if>
   <xsl:value-of select="date" />
  </xsl:otherwise>
  </xsl:choose>
   <xsl:variable name="lng.cnt" select="count(languages/lang)"/>

  <xsl:variable name="lang.code">
   <xsl:call-template name="languageSet">
      <xsl:with-param name="lng" select="languages/lang/@code"/>
   </xsl:call-template>
   </xsl:variable>

   <xsl:choose>
    <xsl:when test="$lang.code = 'No Linguistic content'" >
    </xsl:when> 
    <xsl:when test="$lng.cnt = 1" > in 
    <xsl:call-template name="languageSet">
      <xsl:with-param name="lng" select="languages/lang/@code"/>
    </xsl:call-template>
    </xsl:when>
    <xsl:when test="$lng.cnt = 2"> in 
    <xsl:call-template name="languageSet">
      <xsl:with-param name="lng" select="languages/lang[1]/@code"/>
    </xsl:call-template>
    <xsl:text> and </xsl:text>
    <xsl:call-template name="languageSet">
      <xsl:with-param name="lng" select="languages/lang[2]/@code"/>
    </xsl:call-template>
   </xsl:when>

   <xsl:otherwise> in <xsl:value-of select="$lng.cnt"/> languages</xsl:otherwise>
   </xsl:choose>
   <xsl:choose>
   <xsl:when test="uniqueHoldings">
        and held by <xsl:value-of select="format-number(uniqueHoldings,'##,##0')"/>
      <xsl:choose>
      <xsl:when test="uniqueHoldings = 1">
       library
       </xsl:when>
       <xsl:otherwise>
        libraries
       </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
        and held by <xsl:value-of select="format-number(holdings,'##,##0')"/>
      <xsl:choose>
      <xsl:when test="holdings = 1">
       library
       </xsl:when>
       <xsl:otherwise>
        libraries
       </xsl:otherwise>
      </xsl:choose>
       </xsl:otherwise>
      </xsl:choose>
worldwide
<br />
 
  <xsl:if test="genre and not(summary)">
   <xsl:text> </xsl:text>
   <b><xsl:value-of select="genre"/></b>
   </xsl:if>
   
   <xsl:value-of select="summary"/>

 </div>

</xsl:when>
<xsl:otherwise>
<li>
 <a href="{$worldCatHost}/search?q=no:{substring-after(oclcnum,'ocn')}" target="_top">

<xsl:choose>
<xsl:when test="count(languages/lang) = 1">
<xsl:variable name="lang.one">
<xsl:call-template name="languageSet">
      <xsl:with-param name="lng" select="languages/lang[1]/@code"/>
    </xsl:call-template>
</xsl:variable>

    <xsl:call-template name="mouseoverName3" >
      <xsl:with-param name="nameone" select="concat((format-number(numEditions,'##,##0')),string(' editions published between '),string(dates/@first),string(' and '),string(dates/@last),string(', held by '),(format-number(uniqueHoldings,'##,##0')),string(' libraries worldwide, in '))"/>
<xsl:with-param name="nametwo" select="$lang.one"/>
<xsl:with-param name="namethree" select="concat(string(', '),substring(summary,1,40))"/>
    </xsl:call-template>

</xsl:when>

<xsl:when test="count(languages/lang) = 2">
<xsl:variable name="lang.one">
<xsl:call-template name="languageSet">
      <xsl:with-param name="lng" select="languages/lang[1]/@code"/>
    </xsl:call-template>
</xsl:variable>
<xsl:variable name="lang.two">
<xsl:call-template name="languageSet">
      <xsl:with-param name="lng" select="languages/lang[2]/@code"/>
    </xsl:call-template>
</xsl:variable>

    <xsl:call-template name="mouseoverName3" >
      <xsl:with-param name="nameone" select="concat((format-number(numEditions,'##,##0')),string(' editions published between '),string(dates/@first),string(' and '),string(dates/@last),string(', held by '),(format-number(uniqueHoldings,'##,##0')),string(' libraries worldwide, in '))"/>
<xsl:with-param name="nametwo" select="concat($lang.one,string(' and '),$lang.two)"/>
<xsl:with-param name="namethree" select="concat(string(', '),substring(summary,1,40))"/>
    </xsl:call-template>

</xsl:when>


<xsl:otherwise>
    <xsl:call-template name="mouseoverName" >
      <xsl:with-param name="name" select="concat((format-number(numEditions,'##,##0')),string(' editions published between '),string(dates/@first),string(' and '),string(dates/@last),string(', held by '),(format-number(uniqueHoldings,'##,##0')),string(' libraries worldwide, in '),count(languages/lang),string(' languages,'),string(', '),substring(summary,1,40))"/>
    </xsl:call-template>
</xsl:otherwise>
</xsl:choose>

<xsl:value-of select="title"/>
</a>

<xsl:choose>
  <xsl:when test="creator"> 
  <xsl:text> by </xsl:text>
</xsl:when>
<xsl:otherwise>
</xsl:otherwise>
</xsl:choose>

<xsl:choose>
  <xsl:when test="contains(creator,',')"> 
 <xsl:variable name="namestr.space" select="string(' ')"/>
 <xsl:value-of select="substring-after(creator,',')"/>
 <xsl:value-of select="$namestr.space"/>
 <xsl:value-of select="substring-before(creator,',')"/>
</xsl:when>
<xsl:otherwise>
 <xsl:value-of select="creator"/>
</xsl:otherwise>
</xsl:choose>
<span class="format">(
<xsl:variable name="rec.type" select="recordType"/>
 <xsl:value-of select="document('recordtype.xml')/recordTypeCodes/recordtype[@code=$rec.type]/@full"/>
)</span>
</li>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="Identity/about/citation">
</xsl:template>

<xsl:template match="Identity/associatedNames">
  <a name="linkassoc"></a>
  <div id="associates" class="SectionHead">
    <div class="label"><img src="/identities/images/user-orange.gif" alt="WorldCat Identities"/>Related Identities</div>
    <ul>
      <xsl:for-each select="name">
        <li>
          <xsl:variable name="relatedName">
            <xsl:call-template name="longName">
              <xsl:with-param name="here" select="rawName"/>
              </xsl:call-template>
            </xsl:variable>
          <xsl:variable name="srch1.name"
            select="concat(concat($longName,' '),$relatedName)"/>
          <xsl:variable name="search.name"
            select="translate($srch1.name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ,','ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ')"/>
          <xsl:variable name="href">
            <xsl:choose>
              <!--xsl:when test="$WCatPointers">
                <xsl:value-of select="$worldCatHost"/>
                <xsl:text>/search?q=kw%3A</xsl:text>
                <xsl:value-of select="$search.name"/>
                </xsl:when-->
              <xsl:when test="false"/><!-- stuck in to make the compiler happy -->
              <xsl:otherwise>
                <xsl:text>/identities/</xsl:text>
                <xsl:value-of select="./normName"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
          <a href="{$href}" target="_top">
            <xsl:call-template name="mouseoverName" >
              <xsl:with-param name="name" select="format-number(@count,'##,##0')"/>
              </xsl:call-template>
            <xsl:value-of select="$relatedName"/>
            </a>
          <xsl:for-each select="relator">
            <xsl:variable name="code" select="."/><xsl:text> </xsl:text>
            <span class="rolenote">
              <xsl:call-template name="mouseoverName" >
                <xsl:with-param name="name" select="format-number(@count,'##,##0')"/>
                </xsl:call-template>
              <xsl:value-of select="document('relators.xml')/relatorCodes/relator[@code=$code]/@full"/>
              </span>
            </xsl:for-each>
          <xsl:text> </xsl:text>

          <!--xsl:if test="not($WCatPointers)"-->
            <a href="{$worldCatHost}/search?q=kw%3A{$search.name}" title="Search for {$shortName}, AND {./rawName/suba}" target="_top">
              <img class="plus" src="/identities/images/icon_plus_blue.png"/>
              </a>
            <!--/xsl:if-->
          </li>
        </xsl:for-each>
      </ul>
    </div>
  </xsl:template> 

<xsl:template match="nameInfo/dates">
<a name="linktimeline"></a>
<xsl:variable name="start"><xsl:value-of select="date[1]/."/></xsl:variable>
<xsl:variable name="end"><xsl:value-of select="date[last()]/."/></xsl:variable>
<xsl:variable name="lineStart">
  <xsl:value-of select="$start - ($start mod 10)"/>
  </xsl:variable>
<xsl:variable name="lineEnd">
  <xsl:value-of select="$end + 10 - ($end mod 10)"/>
  </xsl:variable>
<xsl:variable name="multiplier" select="ceiling(($lineEnd - $lineStart) div 80)"/>

<div id="TimeLine" class="SectionHead">
<div class="label">Publication Timeline</div>
<table id="TimeLineTable" cellpadding="0" cellspacing="0">
<tr><td>

<div id="TimeLineShell" style="margin-left:20px;">
<table border="0" cellspacing="0" cellpadding="0">

<tr>
<td class="TLMarkers" style="padding-right:2px;">
  <span><xsl:value-of select="$lineStart"/></span>
  <span style="font-size:1.5em; color:#ccc;">|</span>
</td>

    <xsl:call-template name="dateLoop">
      <xsl:with-param name="year" select="$lineStart"/>
      <xsl:with-param name="finalYear" select="$lineEnd"/>
      <xsl:with-param name="index" select="1"/>
      <xsl:with-param name="multiplier" select="$multiplier"/>
      </xsl:call-template>

<td class="TLMarkers" style="padding-left:2px;">
  <span style="font-size:1.5em; color:#ccc;">|</span>
  <xsl:value-of select="$lineEnd"/></td>
  </tr>

<!-- close TimeLineShell-->
</table>
</div><!-- close TimeLineShell -->
</td>
</tr>

<tr><td id="TimeLineDisplayBox">

<xsl:call-template name="mouseoverLoop">
  <xsl:with-param name="year" select="$lineStart"/>
  <xsl:with-param name="finalYear" select="$lineEnd"/>
  <xsl:with-param name="index" select="1"/>
  <xsl:with-param name="multiplier" select="$multiplier"/>
  </xsl:call-template>


</td>
</tr>

</table>
</div>
</xsl:template>

<xsl:template name="dateLoop">
  <xsl:param name="year"/>
  <xsl:param name="finalYear"/>
  <xsl:param name="index"/>
  <xsl:param name="multiplier"/>
  
  <xsl:variable name="abtcount">
    <xsl:call-template name="aboutcountLoop">
      <xsl:with-param name="year" select="$year"/>
      <xsl:with-param name="endYear" select="$year+$multiplier"/>
      <xsl:with-param name="runningCount" select="0"/>
      </xsl:call-template>
    </xsl:variable>
    
  <xsl:variable name="bycount">
    <xsl:call-template name="bycountLoop">
      <xsl:with-param name="year" select="$year"/>
      <xsl:with-param name="endYear" select="$year+$multiplier"/>
      <xsl:with-param name="runningCount" select="0"/>
      </xsl:call-template>
    </xsl:variable>


  <xsl:variable name="abtscaled">
    <xsl:call-template name="aboutscaledLoop">
      <xsl:with-param name="year" select="$year"/>
      <xsl:with-param name="endYear" select="$year+$multiplier"/>
      <xsl:with-param name="runningscaledCount" select="0"/>
      </xsl:call-template>
    </xsl:variable>

  <xsl:variable name="byscaled">
    <xsl:call-template name="byscaledLoop">
      <xsl:with-param name="year" select="$year"/>
      <xsl:with-param name="endYear" select="$year+$multiplier"/>
      <xsl:with-param name="runningscaledCount" select="0"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$abtcount">
        <xsl:variable name="abtheight">
        <xsl:choose>
          <xsl:when test="$abtcount = -1">
            <xsl:value-of select=".1"/>
         </xsl:when>
          <xsl:when test="$abtcount = 0">
            <xsl:value-of select="5"/>
         </xsl:when>
          <xsl:when test="$abtscaled = 0">
            <xsl:value-of select="5"/>
         </xsl:when>
       <xsl:otherwise>
         <xsl:value-of select="5*ceiling((($abtscaled + ($multiplier)) div $multiplier))"/>
       </xsl:otherwise>
    </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$bycount">
        <xsl:variable name="byheight">
        <xsl:choose>
          <xsl:when test="$bycount = -1">
            <xsl:value-of select=".1"/>
         </xsl:when>
          <xsl:when test="$bycount = 0">
            <xsl:value-of select="5"/>
         </xsl:when>
          <xsl:when test="$byscaled = 0">
            <xsl:value-of select="5"/>
         </xsl:when>
       <xsl:otherwise>
         <xsl:value-of select="5*ceiling((($byscaled + ($multiplier)) div $multiplier))"/>
       </xsl:otherwise>
    </xsl:choose>          
    </xsl:variable>
    
    <xsl:choose>
          <xsl:when test="$bycount&gt;0 or $abtcount&gt;0">
       <xsl:choose>
          <xsl:when test="($year + $multiplier &gt;//Identity/nameInfo/birthDate and $year &lt;=//Identity/nameInfo/deathDate) or not(//Identity/nameInfo/deathDate)">
          
 <td style="vertical-align:bottom;padding-bottom:1px;" >
 <table cellspacing="0" cellpadding="0" class="wrapper"><tbody>
<xsl:if test="$abtheight >= 5">
<tr>
<td>
         <a href="{$worldCatHost}/search?q=su%3A{../rawName/suba}{../rawName/subb}{../rawName/subc}&amp;fq=+yr%3A{$year}..{$year + $multiplier -1}"
            title="View works about {$normalname} {$year} - {$year + $multiplier -1}" target="_top">


<img class="markerAbt" style="height:{$abtheight}px;" 
onmouseover="document.getElementById('Year{$year}').style.display='block' ,getElementById('key').style.display='none'" 
onmouseout="getElementById('Year{$year}').style.display='none',getElementById('key').style.display='block'" src="/identities/images/spacer.gif"/>
</a>
</td>
</tr>
</xsl:if>
<xsl:if test="$byheight >= 5">
<tr>
<td> 

 <a href="{$worldCatHost}/search?q=au%3A{../rawName/suba}{../rawName/subb}{../rawName/subc}&amp;fq=+yr%3A{$year}..{$year + $multiplier -1}"
    title="View works by {$normalname} {$year} - {$year + $multiplier -1}" target="_top">
<img class="markerOn" style="height:{$byheight}px;" 
onmouseover="document.getElementById('Year{$year}').style.display='block' ,getElementById('key').style.display='none'" 
onmouseout="getElementById('Year{$year}').style.display='none',getElementById('key').style.display='block'" src="/identities/images/spacer.gif"/>
</a>
</td>
</tr>
</xsl:if>
</tbody>
</table>
</td>
         </xsl:when>
         <xsl:otherwise>
 <td style="vertical-align:bottom;padding-bottom:1px;" >
 <table cellspacing="0" cellpadding="0" class="wrapper"><tbody>
<xsl:if test="$abtheight >= 5">
<tr>
<td>
 <a href="{$worldCatHost}/search?q=su%3A{../rawName/suba}{../rawName/subb}{../rawName/subc}&amp;fq=+yr%3A{$year}..{$year + $multiplier -1}"
    title="View works about {$normalname} {$year} - {$year + $multiplier -1}" target="_top">
<img class="markerAbt" style="height:{$abtheight}px;" 
onmouseover="document.getElementById('Year{$year}').style.display='block' ,getElementById('key').style.display='none'" 
onmouseout="getElementById('Year{$year}').style.display='none',getElementById('key').style.display='block'" src="/identities/images/spacer.gif"/>
</a>
</td>
</tr>
</xsl:if>

<xsl:if test="$byheight >= 5">
<tr>
<td> 
<a href="{$worldCatHost}/search?q=au%3A{../rawName/suba}{../rawName/subb}{../rawName/subc}>&amp;fq=+yr%3A{$year}..{$year + $multiplier -1}"
   title="View works by {$normalname} {$year} - {$year + $multiplier -1}" target="_top">
<img class="markerDead" style="height:{$byheight}px;" 
onmouseover="document.getElementById('Year{$year}').style.display='block' ,getElementById('key').style.display='none'" 
onmouseout="getElementById('Year{$year}').style.display='none',getElementById('key').style.display='block'" src="/identities/images/spacer.gif"/>
</a>

</td>
</tr>
</xsl:if>
<xsl:if test="$bycount &lt;= 0">

</xsl:if>


 </tbody>

</table>
</td>                  
        </xsl:otherwise>
       </xsl:choose>
            </xsl:when>
          <xsl:otherwise>
  <td style="vertical-align:bottom;padding-bottom:1px;" >
 <table cellspacing="0" cellpadding="0" class="wrapper"><tbody>
 <tr>
<td> 
<a href="{$worldCatHost}/search?q=au%3A{../rawName/suba}{../rawName/subb}{../rawName/subc}&amp;fq=+yr%3A{$year}..{$year + $multiplier -1}"
   title="View works by {$normalname} {$year} - {$year + $multiplier -1}" target="_top">
<img class="markerOff" style="height:5px;" 
onmouseover="document.getElementById('Year{$year}').style.display='block' ,getElementById('key').style.display='none'" 
onmouseout="getElementById('Year{$year}').style.display='none',getElementById('key').style.display='block'"
src="/identities/images/spacer.gif"/>
</a>
</td>
</tr>
</tbody>
</table>
</td>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
      </xsl:choose>
  <xsl:if test="$year&lt;$finalYear">
    <xsl:call-template name="dateLoop">
      <xsl:with-param name="year" select="$year+$multiplier"/>
      <xsl:with-param name="finalYear" select="$finalYear"/>
      <xsl:with-param name="index" select="$index+1"/>
      <xsl:with-param name="multiplier" select="$multiplier"/>
      </xsl:call-template>
    </xsl:if>
    </xsl:when>
    </xsl:choose>
</xsl:template>

<xsl:template name="mouseoverLoop">
  <xsl:param name="year"/>
  <xsl:param name="finalYear"/>
  <xsl:param name="index"/>
  <xsl:param name="multiplier"/>

  <xsl:variable name="abtcount">
    <xsl:call-template name="aboutcountLoop">
      <xsl:with-param name="year" select="$year"/>
      <xsl:with-param name="endYear" select="$year+$multiplier"/>
      <xsl:with-param name="runningCount" select="0"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:variable name="abtpub">
    <xsl:choose>
      <xsl:when test="$abtcount = 1">
        <xsl:value-of select="string(' Publication')"/>
      </xsl:when>
        <xsl:otherwise>
        <xsl:value-of select="string(' Publications')"/>
      </xsl:otherwise>
      </xsl:choose>
  </xsl:variable>    

      <xsl:variable name="bycount">
    <xsl:call-template name="bycountLoop">
      <xsl:with-param name="year" select="$year"/>
      <xsl:with-param name="endYear" select="$year+$multiplier"/>
      <xsl:with-param name="runningCount" select="0"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="bypub">
    <xsl:choose>
      <xsl:when test="$bycount = 1">
        <xsl:value-of select="string(' Publication')"/>
      </xsl:when>
        <xsl:otherwise>
        <xsl:value-of select="string(' Publications')"/>
      </xsl:otherwise>
      </xsl:choose>
  </xsl:variable> 


<xsl:if test="$index = 1">
<div class="TimeLineDetails" id="key" style="display:block;">
<span class="dateRange">Key</span><br/>

<xsl:choose>
<xsl:when test="(//Identity/by/citation)">

<span class="dateDetail">
<img src="/identities/images/spacer.gif" class="keyMarkerAbt"/>
 Publications about <xsl:value-of select="$normalname"/></span><br/>
<span class="dateDetail">
<img src="/identities/images/spacer.gif" class="keyMarkerOn"/>
 Publications by <xsl:value-of select="$normalname"/></span><br/>
<xsl:if test="//Identity/nameInfo/deathDate">
<span class="dateDetail">
<img src="/identities/images/spacer.gif" class="keyMarkerDead"/>
 Publications by <xsl:value-of select="$normalname"/>, published posthumously.</span>
</xsl:if>
</xsl:when>
<xsl:otherwise>
<span class="dateDetail">
<img src="/identities/images/spacer.gif" class="keyMarkerAbt"/>
 Publications about <xsl:value-of select="$normalname"/></span><br/>
</xsl:otherwise>
</xsl:choose>

</div>




</xsl:if>

  <div id="Year{$year}" class="TimeLineDetails" style="display:none;">
    <xsl:choose>
      <xsl:when test="$multiplier=1">

              <span class="dateRange">
        <xsl:value-of select="$year"/>-<xsl:value-of select="$year + $multiplier - 1"/>
        </span><br />
        <xsl:if test="$bycount=0">
                <xsl:value-of select="$bycount"/> <xsl:value-of select="$bypub"/>  by <xsl:value-of select="$normalname"/>
</xsl:if>
       <xsl:choose>
          <xsl:when test="($year + $multiplier &gt;//Identity/nameInfo/birthDate and $year &lt;=//Identity/nameInfo/deathDate) or not(//Identity/nameInfo/deathDate)">

            <xsl:if test="$abtcount>=1">
               <span class="dateDetail"><img src="/identities/images/spacer.gif" class="keyMarkerAbt"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="$abtcount"/> <xsl:value-of select="$abtpub"/> about <xsl:value-of select="$normalname"/>
               </span>
               <BR/>
           </xsl:if>
           <xsl:if test="$bycount>=1">
               <span class="dateDetail"><img src="/identities/images/spacer.gif" class="keyMarkerBy"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="$bycount"/> <xsl:value-of select="$bypub"/> by <xsl:value-of select="$normalname"/>
               </span>
                <BR />
             </xsl:if>
            <xsl:if test="($bycount=-1)">
               <span class="dateDetail">
<img src="/identities/images/spacer.gif" class="keyMarkerOff"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="0"/> <xsl:value-of select="$bypub"/> by <xsl:value-of select="$normalname"/>
               </span>
            <br />
            </xsl:if>

           </xsl:when>
           <xsl:otherwise>
            <xsl:if test="$abtcount>=1">
               <span class="dateDetail"><img src="/identities/images/spacer.gif" class="keyMarkerAbt"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="$abtcount"/> <xsl:value-of select="$abtpub"/> about <xsl:value-of select="$normalname"/>
               </span>
            <br />
            </xsl:if>
            <xsl:if test="$bycount>=1">
               <span class="dateDetail"><img src="/identities/images/spacer.gif" class="keyMarkerDead"/>
                <xsl:text> </xsl:text>
             <xsl:value-of select="$bycount"/> <xsl:value-of select="$bypub"/> by <xsl:value-of select="$normalname"/> 
            </span>
            <br />
            </xsl:if>
            <xsl:if test="($bycount=-1 and $abtcount &gt; 0)">
               <span class="dateDetail">
<img src="/identities/images/spacer.gif" class="keyMarkerOff"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="0"/> <xsl:value-of select="$bypub"/> by <xsl:value-of select="$normalname"/>
               </span>
            <br />
            </xsl:if>

          </xsl:otherwise>
        </xsl:choose>
        </xsl:when>
      <xsl:otherwise>
              <span class="dateRange">
        <xsl:value-of select="$year"/>-<xsl:value-of select="$year + $multiplier - 1"/>
        </span><br />
        <xsl:if test="$bycount=0 and $abtcount=0">
                <xsl:value-of select="$abtcount"/> <xsl:value-of select="$abtpub"/> about <xsl:value-of select="$normalname"/>
</xsl:if>
       <xsl:choose>
          <xsl:when test="($year + $multiplier &gt;//Identity/nameInfo/birthDate and $year &lt;=//Identity/nameInfo/deathDate) or not(//Identity/nameInfo/deathDate)">

            <xsl:if test="$abtcount>=1">
               <span class="dateDetail"><img src="/identities/images/spacer.gif" class="keyMarkerAbt"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="$abtcount"/> <xsl:value-of select="$abtpub"/> about <xsl:value-of select="$normalname"/>
               </span>
               <BR/>
           </xsl:if>
            <xsl:if test="$bycount>=1">
               <span class="dateDetail">
<img src="/identities/images/spacer.gif" class="keyMarkerAbt"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="$bycount"/> <xsl:value-of select="$bypub"/> by <xsl:value-of select="$normalname"/>
               </span>
            <br />
            </xsl:if>
            <xsl:if test="($bycount=-1)">
               <span class="dateDetail">
<img src="/identities/images/spacer.gif" class="keyMarkerOff"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="0"/> <xsl:value-of select="$bypub"/> by <xsl:value-of select="$normalname"/>
               </span>
            <br />
            </xsl:if>

           </xsl:when>
           <xsl:otherwise>
            <xsl:if test="$abtcount>=1">
               <span class="dateDetail"><img src="/identities/images/spacer.gif" class="keyMarkerAbt"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="$abtcount"/> <xsl:value-of select="$abtpub"/> about <xsl:value-of select="$normalname"/>
               </span>
            <br />
            </xsl:if>
            <xsl:if test="$bycount>=1">
               <span class="dateDetail"><img src="/identities/images/spacer.gif" class="keyMarkerDead"/>
                <xsl:text> </xsl:text>
             <xsl:value-of select="$bycount"/> <xsl:value-of select="$bypub"/> by <xsl:value-of select="$normalname"/> posthumously
            </span>
            <br/>
            </xsl:if>
            <xsl:if test="($bycount=-1)">

               <span class="dateDetail">
                 <img src="/identities/images/spacer.gif" class="keyMarkerOff"/>
                 <xsl:text> </xsl:text>
                 <xsl:value-of select="0"/> <xsl:value-of select="$bypub"/> by <xsl:value-of select="$normalname"/>
                 </span>
            <br />
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  <xsl:if test="$year&lt;$finalYear">
    <xsl:call-template name="mouseoverLoop">
      <xsl:with-param name="year" select="$year+$multiplier"/>
      <xsl:with-param name="finalYear" select="$finalYear"/>
      <xsl:with-param name="index" select="$index+1"/>
      <xsl:with-param name="multiplier" select="$multiplier"/>
      </xsl:call-template>
    </xsl:if>
</xsl:template>

<xsl:template name="countLoop">
  <xsl:param name="year"/>
  <xsl:param name="endYear"/>
  <xsl:param name="runningCount"/>
  <xsl:choose>
    <xsl:when test="$year&gt;=$endYear">
      <xsl:value-of select="$runningCount"/>
      </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="date[.=$year]">
          <xsl:call-template name="bycountLoop">
            <xsl:with-param name="year" select="$year+1"/>
            <xsl:with-param name="endYear" select="$endYear"/>
            <xsl:with-param name="runningCount" select="$runningCount+date[.=$year]/@Count"/>
            </xsl:call-template>
          </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="bycountLoop">
            <xsl:with-param name="year" select="$year+1"/>
            <xsl:with-param name="endYear" select="$endYear"/>
            <xsl:with-param name="runningCount" select="$runningCount"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


<xsl:template name="bycountLoop">
  <xsl:param name="year"/>
  <xsl:param name="endYear"/>
  <xsl:param name="runningCount"/>
  <xsl:choose>
    <xsl:when test="$year&gt;=$endYear">
      <xsl:choose>
      <xsl:when test="$runningCount">
         <xsl:value-of select="$runningCount"/>
      </xsl:when>
      <xsl:otherwise>
         <xsl:value-of select="-1"/>
      </xsl:otherwise>
     </xsl:choose>

      </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="date[.=$year]">
          <xsl:call-template name="bycountLoop">
            <xsl:with-param name="year" select="$year+1"/>
            <xsl:with-param name="endYear" select="$endYear"/>
            <xsl:with-param name="runningCount" select="$runningCount+date[.=$year]/@byCount"/>
            </xsl:call-template>
          </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="bycountLoop">
            <xsl:with-param name="year" select="$year+1"/>
            <xsl:with-param name="endYear" select="$endYear"/>
            <xsl:with-param name="runningCount" select="$runningCount"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

<xsl:template name="byscaledLoop">
  <xsl:param name="year"/>
  <xsl:param name="endYear"/>
  <xsl:param name="runningscaledCount"/>
  <xsl:choose>
    <xsl:when test="$year&gt;=$endYear">
      <xsl:value-of select="$runningscaledCount"/>
      </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="date[.=$year]">
          <xsl:call-template name="byscaledLoop">
            <xsl:with-param name="year" select="$year+1"/>
            <xsl:with-param name="endYear" select="$endYear"/>
            <xsl:with-param name="runningscaledCount" select="$runningscaledCount+date[.=$year]/@byScaled"/>
            </xsl:call-template>
          </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="byscaledLoop">
            <xsl:with-param name="year" select="$year+1"/>
            <xsl:with-param name="endYear" select="$endYear"/>
            <xsl:with-param name="runningscaledCount" select="$runningscaledCount"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

<xsl:template name="aboutcountLoop">
  <xsl:param name="year"/>
  <xsl:param name="endYear"/>
  <xsl:param name="runningCount"/>
  <xsl:choose>
    <xsl:when test="$year&gt;=$endYear">
      <xsl:choose>
      <xsl:when test="not($runningCount)">
          <xsl:value-of select="-1" />
       </xsl:when>
       <xsl:otherwise>
          <xsl:value-of select="$runningCount"/>
      </xsl:otherwise>
      </xsl:choose>

      </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="date[.=$year]">
          <xsl:call-template name="aboutcountLoop">
            <xsl:with-param name="year" select="$year+1"/>
            <xsl:with-param name="endYear" select="$endYear"/>
            <xsl:with-param name="runningCount" select="$runningCount+date[.=$year]/@aboutCount"/>
            </xsl:call-template>
          </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="aboutcountLoop">
            <xsl:with-param name="year" select="$year+1"/>
            <xsl:with-param name="endYear" select="$endYear"/>
            <xsl:with-param name="runningCount" select="$runningCount"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

<xsl:template name="aboutscaledLoop">
  <xsl:param name="year"/>
  <xsl:param name="endYear"/>
  <xsl:param name="runningscaledCount"/>
  <xsl:choose>
    <xsl:when test="$year&gt;=$endYear">
          <xsl:choose>
      <xsl:when test="not($runningscaledCount)">
          <xsl:value-of select="0" />
       </xsl:when>
       <xsl:otherwise>
          <xsl:value-of select="$runningscaledCount"/>
      </xsl:otherwise>
      </xsl:choose>
      </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="date[.=$year]">
          <xsl:call-template name="aboutscaledLoop">
            <xsl:with-param name="year" select="$year+1"/>
            <xsl:with-param name="endYear" select="$endYear"/>
            <xsl:with-param name="runningscaledCount" select="$runningscaledCount+date[.=$year]/@aboutScaled"/>
            </xsl:call-template>
          </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="aboutscaledLoop">
            <xsl:with-param name="year" select="$year+1"/>
            <xsl:with-param name="endYear" select="$endYear"/>
            <xsl:with-param name="runningscaledCount" select="$runningscaledCount"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

<xsl:template name="coverImage">
  <xsl:param name="oclc"/>
  <xsl:param name="type"/>
  <xsl:param name="number"/>
  <xsl:param name="title"/>

  <xsl:choose>
    <xsl:when test="not($abandoned)">
      <a href="{$worldCatHost}/search?q=no:{substring-after($oclc,'ocn')}" target="_top">
        <xsl:call-template name="mouseoverName" >
          <xsl:with-param name="name" select="$title"/>
          </xsl:call-template>

        <xsl:choose>
          <xsl:when test="$type='isbn'">
            <img id="coverart" alt="{$title}"  border="0"
                 onerror="document.coverart.src='/identities/images/emptycover.gif';"
                 src="http://worldcat.org/wcpa/servlet/DCARead?standardNo={$number}&amp;standardNoType=1"/>
            </xsl:when>
          <xsl:when test="$type='upc'">
            <img id="coverart" alt="{$title}"  border="0"
                 onerror="document.coverart.src='/identities//images/emptycover.gif';"
                 src="http://worldcat.org/wcpa/servlet/DCARead?standardNo={$number}&amp;standardNoType=6"/>
            </xsl:when>
          <xsl:otherwise>
            <xsl:text>Unknown image type.</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </a>
      </xsl:when>
    <xsl:otherwise>
      <a href="{$worldCatHost}/search?q=no:{substring-after(//abandonedIdentity/justification/oclcnum,'ocn')}"
         target="_top">
        <xsl:call-template name="mouseoverName" >
          <xsl:with-param name="name" select="title"/>
          </xsl:call-template>
        <img id="coverart" onerror="document.coverart.src='/identities/images/emptycover.gif';"
             src="http://worldcat.org/wcpa/servlet/DCARead?standardNo={bestCover}&amp;standardNoType=1"
             alt="{//abandonedIdentity/oclcnum/Identity/title}"  border="0" />
        </a>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

<xsl:template name="mouseoverName">
  <xsl:param name="name"/>
   <xsl:attribute name="title"><xsl:value-of select="$name"/>
</xsl:attribute>
</xsl:template>

<xsl:template name="mouseoverName3">
  <xsl:param name="nameone"/>
  <xsl:param name="nametwo"/>
  <xsl:param name="namethree"/>
  <xsl:attribute name="title"><xsl:value-of select="concat($nameone,$nametwo,$namethree)"/>
</xsl:attribute>
</xsl:template>

<xsl:template name="mouseoverfast">
  <xsl:param name="namex"/>
  <xsl:param name="countx"/>

   <xsl:attribute name="title">
   <xsl:choose>
     <xsl:when test="$namex = 600">Personal Name, count=</xsl:when>
     <xsl:when test="$namex = 610">Corporate Name, count=</xsl:when>
     <xsl:when test="$namex = 611">Meeting Name, count=</xsl:when>
     <xsl:when test="$namex = 630">Uniform Title, count=</xsl:when>
     <xsl:when test="$namex = 650">Topical Term, count=</xsl:when>
     <xsl:when test="$namex = 651">Geographic Name, count=</xsl:when>
     <xsl:when test="$namex = 653">Uncontrolled, count=</xsl:when>
     <xsl:when test="$namex = 655">Genre/Form, count=</xsl:when>
   <xsl:otherwise>
     Subject, count=
   </xsl:otherwise>
   </xsl:choose>
    <xsl:value-of select="format-number($countx,'##,##0')"/>
</xsl:attribute>
</xsl:template>

<xsl:template name="comments">
 <a name="linkcomments"></a>
<div id="comments" class="SectionHead">
<div class="label">
<table border="0" cellspacing="0" cellpadding="0">
<tr>
<td>Comments, Notes &amp; Links from all over</td>
<td><span id="plus">
+
</span>

<!--
<xsl:variable name="docu.name" select="(concat('http://alcme.oclc.org/wikid/info:sid/localhost:CollectionWikiIdentities:', translate(pnkey,' ','_'), '?action=raw&amp;recordPrefix=xhtml'))"/>
-->

 <!--a><xsl:attribute name="href">
http://alcme.oclc.org/wikid/CollectionWikiIdentities:<xsl:value-of select="translate(pnkey,' ','_')"/></xsl:attribute-->
Add Your Own
<!--/a-->
</td>
</tr>
</table>
</div>

<!--
<xsl:if test="document(concat('http://alcme.oclc.org/wikid/info:sid/localhost:CollectionWikiIdentities:', translate(pnkey,' ','_'), '?action=display&amp;recordPrefix=xhtml'))/resource/content/srw:searchRetrieveResponse/srw:numberOfRecords=1">
    &#160;&#160;<xsl:copy-of select="document($docu.name)"/>
</xsl:if>
-->

</div><!-- close comments -->
<div id="lmd">
<div class="label">This Entry was last modified on:</div>
</div><!-- close lmd -->
</xsl:template>


</xsl:stylesheet>
