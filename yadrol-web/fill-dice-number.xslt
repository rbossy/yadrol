<xsl:stylesheet version = "1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                >
  
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <xsl:param name="n"/>
  
  <xsl:template match="@*">
    <xsl:copy-of select="."/>
  </xsl:template>
  
  <xsl:template match="*">
    <xsl:copy select=".">
      <xsl:choose>
	<xsl:when test="name() = 'tspan'">
	  <xsl:apply-templates select="@*"/>
	  <xsl:value-of select="$n"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:apply-templates select="@*|*|text()"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
