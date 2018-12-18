<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:template match="/organization">
        <organization inn="{@inn}" kpp="{@kpp}">
            <ogrn>
                <xsl:value-of select="@ogrn"/>
            </ogrn>
            <xsl:apply-templates select="/organization/*[not(name()='founders') and not(name()='leaders')]"/>
            <entities>
                <xsl:apply-templates select="//founders"/>
                <xsl:apply-templates select="//leaders"/>
            </entities>
        </organization>
    </xsl:template>

    <xsl:template match="@* | node()" name="identity">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>



    <xsl:template match="/organization/okveds/okved">
        <xsl:choose>
            <xsl:when test="@main = 'true'">
                <okved_main>
                    <xsl:copy-of select="node()"/>
                </okved_main>
            </xsl:when>
            <xsl:otherwise>
                <okved>
                    <xsl:copy-of select="node()"/>
                </okved>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="/organization/founder">
        <xsl:copy-of select="node()"/>
    </xsl:template>

    <xsl:template match="/organization/status">
        <status>
            <xsl:choose>
                <xsl:when test=".='active'">1</xsl:when>
                <xsl:otherwise>0</xsl:otherwise>
            </xsl:choose>
        </status>
    </xsl:template>


</xsl:stylesheet>