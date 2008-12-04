<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
  <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:xlink="http://www.w3.org/TR/xlink" version="1.0">
      <xsl:output encoding="UTF-8" indent="yes" method="html" standalone="yes" version="4.0"/>
      
      <xsl:template match="/">
        <html>
          <body>
            <xsl:apply-templates/>
          </body>
        </html>
      </xsl:template>
      
      <xsl:template match="mets:mets">
        <xsl:apply-templates select="mets:structMap"/>
        <xsl:apply-templates select="mets:metsHdr"/>
      </xsl:template>

      <xsl:template match="mets:metsHdr">
        <tr>
          <!--
          <th>
            <xsl:text>Header (</xsl:text>
            <xsl:value-of select="@RECORDSTATUS" />
            <xsl:text>)</xsl:text>
          </th>
          -->
          <td>
            <table cellpadding="0" cellspacing="0" class="datatable">
              <xsl:apply-templates/>
            </table>
          </td>
        </tr>
      </xsl:template>

      <xsl:template match="mets:agent">
        <tr>
          <th>
            <xsl:text>Agent: </xsl:text>
            <xsl:value-of select="@TYPE"/>
            <xsl:text> </xsl:text>
            <xsl:value-of select="@ROLE"/>
          </th>
        </tr>
        <tr>
          <td>
            <table cellpadding="0" cellspacing="0" class="datatable">
              <xsl:apply-templates/>
            </table>
          </td>
        </tr>
      </xsl:template>
      
      <xsl:template match="mets:structMap">
        <xsl:apply-templates mode="cstLevel" select="mets:div/mets:div[@LABEL='crosswalk']"/>
        <xsl:apply-templates mode="cstLevel" select="mets:div/mets:div[@LABEL='source']"/>
        <xsl:apply-templates mode="cstLevel" select="mets:div/mets:div[@LABEL='target']"/>
        <xsl:apply-templates mode="arLevel" select="mets:div/mets:div[@LABEL='application']"/>
        <xsl:apply-templates mode="arLevel" select="mets:div/mets:div[@LABEL='reference']"/>
        <!--
        <xsl:apply-templates />
        -->
      </xsl:template>

      <!-- LABEL="Crosswalk/Source/Target" -->
      <xsl:template match="mets:div" mode="cstLevel">
        <tr>
          <th>
            <xsl:value-of select="@LABEL"/>
          </th>
        </tr>
        <tr>
          <td>
            <table cellpadding="0" cellspacing="0" class="datatable" width="100%">
              <xsl:apply-templates mode="arLevel" select="mets:div[@LABEL='application']"/>
              <xsl:apply-templates mode="arLevel" select="mets:div[@LABEL='reference']"/>
            </table>
          </td>
        </tr>
      </xsl:template>

      <!-- LABEL="application/reference" -->
      <xsl:template match="mets:div" mode="arLevel">
        <xsl:variable name="arLabel">
          <xsl:value-of select="@LABEL"/>
        </xsl:variable>
        <tr>
          <th>
            <xsl:value-of select="$arLabel"/>
          </th>
        </tr>
        <xsl:variable name="dmdID">
          <xsl:value-of select="@DMDID"/>
        </xsl:variable>
        <tr>
          <td>
            <table cellpadding="0" cellspacing="0" class="datatable" width="100%">
              <tr>
                <th>Description</th>
              </tr>
              <tr>
                <td>
                  <xsl:apply-templates select="../../../../mets:dmdSec[@ID=$dmdID]"/>
                  <xsl:apply-templates select="../../../mets:dmdSec[@ID=$dmdID]"/>
                </td>
              </tr>
              <!--
              <tr>
                <th>URL</th>
              </tr>
              -->
              <xsl:apply-templates select="mets:fptr">
                <xsl:with-param name="label">
                  <xsl:value-of select="$arLabel"/>
                </xsl:with-param>
              </xsl:apply-templates>
              <xsl:apply-templates mode="leafLevel" select="mets:div">
                <xsl:with-param name="arLabel">
                  <xsl:value-of select="$arLabel"/>
                </xsl:with-param>
              </xsl:apply-templates>
            </table>
          </td>
        </tr>
      </xsl:template>

      <xsl:template match="mets:div" mode="leafLevel">
        <xsl:param name="arLabel"/>
        <tr>
          <th>
            <xsl:value-of select="@LABEL"/>
            <!--
            <xsl:text> (</xsl:text>
            <xsl:value-of select="$arLabel" />
            <xsl:text>)</xsl:text>
            -->
          </th>
        </tr>
        <xsl:apply-templates mode="level2" select="mets:fptr">
          <xsl:with-param name="arLabel">
            <xsl:value-of select="$arLabel"/>
          </xsl:with-param>
        </xsl:apply-templates>
      </xsl:template>

      <xsl:template match="mets:div">
        <tr>
<th>FOOBARBAZ</th>
</tr>
      </xsl:template>
      <!--
      <xsl:template match="mets:div">
        <xsl:if test="@LABEL">
          <tr>
            <th>
              <xsl:value-of select="@LABEL" />
            </th>
          </tr>
        </xsl:if>
        <xsl:if test="@DMDID">
          <xsl:variable name="dmdID">
            <xsl:value-of select="@DMDID" />
          </xsl:variable>
          <tr>
            <td>
              <xsl:apply-templates select="/mets:mets/mets:dmdSec[@ID=$dmdID]" />
            </td>
          </tr>
        </xsl:if>
        <xsl:apply-templates>
          <xsl:with-param name="label">
            <xsl:value-of select="@LABEL" />
          </xsl:with-param>
        </xsl:apply-templates>
      </xsl:template>
      -->

      <xsl:template match="mets:fptr">
        <xsl:param name="label"/>
        <xsl:variable name="fileID">
          <xsl:value-of select="@FILEID"/>
        </xsl:variable>
        <tr>
          <td>
            <a>
              <xsl:attribute name="href">
                <xsl:value-of select="../../../../../mets:fileSec/mets:fileGrp[@USE=$label]/mets:file[@ID=$fileID]/mets:FLocat/@xlink:href"/>
                <xsl:value-of select="../../../../mets:fileSec/mets:fileGrp[@USE=$label]/mets:file[@ID=$fileID]/mets:FLocat/@xlink:href"/>
              </xsl:attribute>
              <xsl:value-of select="../../../../../mets:fileSec/mets:fileGrp[@USE=$label]/mets:file[@ID=$fileID]/mets:FLocat/@xlink:href"/>
              <xsl:value-of select="../../../../mets:fileSec/mets:fileGrp[@USE=$label]/mets:file[@ID=$fileID]/mets:FLocat/@xlink:href"/>
            </a>
          </td>
        </tr>
      </xsl:template>

      <xsl:template match="mets:fptr" mode="level2">
        <xsl:param name="arLabel"/>
        <xsl:variable name="fileID">
          <xsl:value-of select="@FILEID"/>
        </xsl:variable>
        <tr>
          <td>
            <a>
              <xsl:attribute name="href">
                <xsl:value-of select="../../../../../../mets:fileSec/mets:fileGrp[@USE=$arLabel]/mets:file[@ID=$fileID]/mets:FLocat/@xlink:href"/>
                <xsl:value-of select="../../../../../mets:fileSec/mets:fileGrp[@USE=$arLabel]/mets:file[@ID=$fileID]/mets:FLocat/@xlink:href"/>
              </xsl:attribute>
              <xsl:value-of select="../../../../../../mets:fileSec/mets:fileGrp[@USE=$arLabel]/mets:file[@ID=$fileID]/mets:FLocat/@xlink:href"/>
              <xsl:value-of select="../../../../../mets:fileSec/mets:fileGrp[@USE=$arLabel]/mets:file[@ID=$fileID]/mets:FLocat/@xlink:href"/>
              <!--
              <xsl:value-of select="../../../../../../mets:fileSec/mets:fileGrp" />:<xsl:value-of select="$arLabel" />:<xsl:value-of select="$fileID" />
              -->
            </a>
          </td>
        </tr>
      </xsl:template>

      <xsl:template match="mets:dmdSec">
        <xsl:apply-templates/>
      </xsl:template>

      <xsl:template match="mets:mdWrap">
        <tr>
          <!--
          <th>
            <xsl:value-of select="@LABEL" />
            <xsl:text> (</xsl:text>
            <xsl:value-of select="@MDTYPE" />
            <xsl:text>)</xsl:text>
          </th>
          -->
          <td>
            <table cellpadding="0" cellspacing="0" class="datatable">
              <xsl:apply-templates/>
            </table>
          </td>
        </tr>
        
      </xsl:template>

      <xsl:template match="mets:xmlData">
        <xsl:apply-templates/>
      </xsl:template>

      <xsl:template match="mets:*">
        <tr>
          <th>
<xsl:value-of select="local-name()"/>
</th>
          <td>
            <xsl:choose>
              <xsl:when test="*">
                <table cellpadding="0" cellspacing="0" class="datatable">
                  <xsl:apply-templates/>
                </table>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="."/>
              </xsl:otherwise>
            </xsl:choose>
          </td>
        </tr>
      </xsl:template>
      
      <xsl:template match="dc:*">
        <xsl:if test=". and string-length(.)&gt;0">
          <tr>
            <th>
<xsl:value-of select="name()"/>
</th>
            <td>
<xsl:value-of select="."/>
</td>
          </tr>
        </xsl:if>
      </xsl:template>
      
    </xsl:stylesheet>
