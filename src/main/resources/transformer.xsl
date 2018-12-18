<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        version="1.0">
    <!--<xsl:output method="html"/>-->

    <xsl:template match="/organization">
        <organization inn="{@inn}" kpp="{@kpp}">
            <xsl:attribute name="ogrn">
                <xsl:value-of select="ogrn"/>
            </xsl:attribute>
            <xsl:apply-templates/>
        </organization>
    </xsl:template>

    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="/okveds/main_okved">
        <okved inn="{@inn}" kpp="{@kpp}">
            <xsl:attribute name="main">
                <xsl:value-of select="true"/>
            </xsl:attribute>
        </okved>
    </xsl:template>


</xsl:stylesheet>