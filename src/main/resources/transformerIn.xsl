<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:template match="/organization">
        <organization inn="{@inn}" kpp="{@kpp}">
            <xsl:attribute name="ogrn">
                <xsl:value-of select="ogrn"/>
            </xsl:attribute>
            <xsl:apply-templates/>
        </organization>
    </xsl:template>

    <xsl:template match="@* | node()" name="identity">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="/organization/ogrn"/>

    <xsl:template match="/organization/okveds/main_okved">
        <okved version="2001" main="true">
            <xsl:copy-of select="node()"/>
        </okved>
    </xsl:template>

    <xsl:template match="/organization/okveds/okved">
        <okved version="2001" main="false">
            <xsl:copy-of select="node()"/>
        </okved>
    </xsl:template>

    <xsl:template match="/organization/entities">
        <xsl:copy-of select="node()"/>
    </xsl:template>

    <xsl:template match="/organization/status">
        <status>
            <xsl:choose>
                <xsl:when test=".=1">active</xsl:when>
                <xsl:otherwise>eliminated</xsl:otherwise>
            </xsl:choose>
        </status>
    </xsl:template>
</xsl:stylesheet>