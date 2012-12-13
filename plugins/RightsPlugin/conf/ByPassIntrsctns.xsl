<!--
#  rightsdraw
#  Version: 2.0.1
#  Authors: L. Boch
#
#  Copyright (C) 2010-2012 RAI – Radiotelevisione Italiana <cr_segreteria@rai.it>
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

-->

<xsl:stylesheet version="1.0" 
	xmlns:owl="http://www.w3.org/2002/07/owl#"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xml="http://www.w3.org/XML/1998/namespace"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" >

<xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>

<xsl:variable name="bypasslist"> <xsl:for-each select="//owl:ClassAssertion[substring-after(owl:Class/@IRI,'#')='Permission']"> <xsl:variable name="permid">#<xsl:value-of select="substring-after(owl:NamedIndividual/@IRI,'#')"/></xsl:variable> <xsl:for-each select="../owl:ClassAssertion[substring-after(owl:Class/@IRI,'#')='FactIntersection']"> <xsl:variable name="fiid">#<xsl:value-of select="substring-after(owl:NamedIndividual/@IRI,'#')"/></xsl:variable> (<xsl:value-of select="$permid"/>; <xsl:for-each select="../owl:ObjectPropertyAssertion[owl:NamedIndividual[position()=1]/@IRI = $permid and owl:NamedIndividual[position()=2]/@IRI = $fiid]"> <xsl:for-each select="../owl:ObjectPropertyAssertion[owl:NamedIndividual[position()=1]/@IRI = $fiid]/owl:NamedIndividual[position()=2]/@IRI"> <xsl:value-of select="."/><xsl:text>;</xsl:text> </xsl:for-each> </xsl:for-each>) </xsl:for-each> </xsl:for-each> </xsl:variable>
<xsl:variable name="bypassneglist"> <xsl:for-each select="//owl:ClassAssertion[substring-after(owl:Class/@IRI,'#')='Permission']"> <xsl:variable name="permid">#<xsl:value-of select="substring-after(owl:NamedIndividual/@IRI,'#')"/></xsl:variable> <xsl:for-each select="../owl:ClassAssertion[substring-after(owl:Class/@IRI,'#')='FactIntersection']"> <xsl:variable name="fiid">#<xsl:value-of select="substring-after(owl:NamedIndividual/@IRI,'#')"/></xsl:variable> (<xsl:value-of select="$permid"/>; <xsl:for-each select="../owl:ObjectPropertyAssertion[owl:NamedIndividual[position()=1]/@IRI = $permid and owl:NamedIndividual[position()=2]/@IRI = $fiid]"> <xsl:for-each select="../owl:NegativeObjectPropertyAssertion[owl:NamedIndividual[position()=1]/@IRI = $fiid]/owl:NamedIndividual[position()=2]/@IRI"> <xsl:value-of select="."/><xsl:text>;</xsl:text> </xsl:for-each> </xsl:for-each>) </xsl:for-each> </xsl:for-each> </xsl:variable>

<xsl:template match="/">
	<!--xsl:value-of select="$bypasslist"/>
	<xsl:value-of select="$bypassneglist"/-->
	<xsl:apply-templates select="owl:Ontology" />
</xsl:template>

<xsl:template name="bypassobj">
	<xsl:param name="thisbypasslist"/>
	<xsl:param name="negative"/>
	<xsl:variable name="tail"><xsl:value-of select="substring-after($thisbypasslist,')')"/></xsl:variable>
	<xsl:variable name="one"><xsl:value-of select="substring-before(substring-after($thisbypasslist,'('),'; ')"/></xsl:variable>
	<xsl:variable name="targets"><xsl:value-of select="substring-after(substring-before($thisbypasslist,')'),'; ')"/></xsl:variable>
	<xsl:if test="$targets != ''">
		<xsl:call-template name="pasteobj">
			<xsl:with-param name="one"><xsl:value-of select="$one"/></xsl:with-param>
			<xsl:with-param name="targets"><xsl:value-of select="$targets"/></xsl:with-param>
			<xsl:with-param name="negative"><xsl:value-of select="$negative"/></xsl:with-param>
		</xsl:call-template>
	</xsl:if>
	<xsl:if test="$tail != ''">
		<xsl:call-template name="bypassobj">
			<xsl:with-param name="thisbypasslist"><xsl:value-of select="$tail"/></xsl:with-param>
			<xsl:with-param name="negative"><xsl:value-of select="$negative"/></xsl:with-param>
		</xsl:call-template>
	</xsl:if>
</xsl:template>
<xsl:template name="pasteobj">
	<xsl:param name="one"/>
	<xsl:param name="targets"/>
	<xsl:param name="negative"/>
	<xsl:variable name="tail"><xsl:value-of select="substring-after($targets,';')"/></xsl:variable>
	<xsl:variable name="twoname"><xsl:value-of select="substring-before($targets,';')"/></xsl:variable>
	<xsl:choose>
		<xsl:when test="$negative = 'no'">
			<xsl:element name="owl:ObjectPropertyAssertion">
				<xsl:element name="owl:ObjectProperty"> <xsl:attribute name="IRI">http://purl.oclc.org/NET/mvco.owl#hasRequired</xsl:attribute> </xsl:element>
				<xsl:element name="owl:NamedIndividual"> <xsl:attribute name="IRI"><xsl:value-of select="$one"/></xsl:attribute> </xsl:element>
				<xsl:element name="owl:NamedIndividual"> <xsl:attribute name="IRI"><xsl:value-of select="$twoname"/></xsl:attribute> </xsl:element>
			</xsl:element>
		</xsl:when>
		<xsl:otherwise>
			<xsl:element name="owl:NegativeObjectPropertyAssertion">
				<xsl:element name="owl:ObjectProperty"> <xsl:attribute name="IRI">http://purl.oclc.org/NET/mvco.owl#hasRequired</xsl:attribute> </xsl:element>
				<xsl:element name="owl:NamedIndividual"> <xsl:attribute name="IRI"><xsl:value-of select="$one"/></xsl:attribute> </xsl:element>
				<xsl:element name="owl:NamedIndividual"> <xsl:attribute name="IRI"><xsl:value-of select="$twoname"/></xsl:attribute> </xsl:element>
			</xsl:element>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:if test="$tail != ''">
		<xsl:call-template name="pasteobj">
			<xsl:with-param name="one"><xsl:value-of select="$one"/></xsl:with-param>
			<xsl:with-param name="targets"><xsl:value-of select="$tail"/></xsl:with-param>
			<xsl:with-param name="negative"><xsl:value-of select="$negative"/></xsl:with-param>
		</xsl:call-template>
	</xsl:if>
</xsl:template>

<xsl:template match="owl:Ontology">
	<xsl:copy>
		<xsl:copy-of select="@*"/>
		<xsl:apply-templates select="owl:Prefix" />
		<xsl:apply-templates select="owl:Import" />
		<xsl:apply-templates select="owl:Declaration" />
		<xsl:apply-templates select="owl:ClassAssertion" />
		<xsl:apply-templates select="owl:ObjectPropertyAssertion" />
		<xsl:call-template name="bypassobj">
			<xsl:with-param name="thisbypasslist"><xsl:value-of select="$bypasslist"/></xsl:with-param>
			<xsl:with-param name="negative">no</xsl:with-param>
		</xsl:call-template>
		<xsl:apply-templates select="owl:NegativeObjectPropertyAssertion" />
		<xsl:call-template name="bypassobj">
			<xsl:with-param name="thisbypasslist"><xsl:value-of select="$bypassneglist"/></xsl:with-param>
			<xsl:with-param name="negative">yes</xsl:with-param>
		</xsl:call-template>
		<xsl:apply-templates select="owl:DataPropertyAssertion" />
		<xsl:apply-templates select="owl:NegativeDataPropertyAssertion" />
	</xsl:copy>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
