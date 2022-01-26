<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:param name="version"></xsl:param>

	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="*[local-name()='context-param' and child::node()[local-name()='param-name' and child::text()='version']]">
		<context-param>
			<param-name>version</param-name>
			<param-value><xsl:value-of select="$version" /></param-value>
		</context-param>
	</xsl:template>
</xsl:stylesheet>