
<!--
/* xxxxxxxx.h version xxx.x	Author: L. Boch
 *
 * Copyright (C) 2010 RAI - Radiotelevisione Italiana  <cr_segreteria@rai.it>
 * 
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; If not, see <http://www.gnu.org/licenses/>.
 *
 */
-->

<xsl:stylesheet version="1.0" 
	xmlns:owl="http://www.w3.org/2002/07/owl#"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
<xsl:output method="text" indent="yes" omit-xml-declaration="yes"/>
<xsl:param name="colors"/>

<xsl:template match="/">
	<xsl:apply-templates select="owl:Ontology" />
</xsl:template>

<xsl:template match="owl:Ontology">
 	digraph G {
<xsl:apply-templates select="owl:ClassAssertion" />
<xsl:apply-templates select="owl:ObjectPropertyAssertion" />
<xsl:apply-templates select="owl:DataPropertyAssertion" />
<xsl:apply-templates select="owl:NegativeObjectPropertyAssertion" />
	}
</xsl:template>

<xsl:template match="owl:ClassAssertion">
<xsl:variable name="class"><xsl:value-of select="substring-after(owl:Class/@IRI,'#')"/></xsl:variable>
<xsl:variable name="whiteclasscolor">white</xsl:variable>
<xsl:variable name="classcolor">
	<xsl:choose>
		<xsl:when test="$colors = 'on'">
			<xsl:choose>
				<xsl:when test="$class = 'Action' or $class = 'CommunicationToThePublic' or $class = 'PublicPerformance' or $class = 'Transform' or $class = 'Duplicate' or $class = 'Fixate' or $class = 'AudiovisualDistribute' or $class = 'ExploitIPRights' or $class = 'MakeCutAndEdit' or $class = 'MakeExcerpt'">"#ffaaff"</xsl:when>
				<xsl:when test="$class = 'Permission'">"#aaffaa"</xsl:when>
				<xsl:when test="$class = 'IPEntity'">cyan</xsl:when>
				<xsl:when test="$class = 'User'">orange</xsl:when>
				<xsl:when test="$class = 'FactUnion' or $class = 'FactIntersection'">yellow</xsl:when>
				<xsl:otherwise>"#ffffaa"</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:otherwise>white</xsl:otherwise>
	</xsl:choose>
</xsl:variable>
<xsl:text>		</xsl:text><xsl:value-of select="substring-after(owl:NamedIndividual/@IRI,'#')"/> [style=filled,fillcolor=<xsl:value-of select="$classcolor"/>,label="<xsl:value-of select="substring-after(owl:Class/@IRI,'#')"/>\n<xsl:value-of select="substring-after(owl:NamedIndividual/@IRI,'#')"/>"];
</xsl:template>

<xsl:template match="owl:ObjectPropertyAssertion">
	<xsl:variable name="arc"><xsl:value-of select="substring-after(owl:ObjectProperty/@IRI,'#')"/></xsl:variable>
	<xsl:variable name="first"><xsl:value-of select="substring-after(owl:NamedIndividual[position()=1]/@IRI,'#')"/></xsl:variable>
	<xsl:variable name="second"><xsl:value-of select="substring-after(owl:NamedIndividual[position()=2]/@IRI,'#')"/></xsl:variable>
<xsl:text>		</xsl:text><xsl:value-of select="$first"/>-><xsl:value-of select="$second"/> [label="<xsl:value-of select="$arc"/>"];
</xsl:template>

<xsl:template match="owl:NegativeObjectPropertyAssertion">
	<xsl:variable name="arc"><xsl:value-of select="substring-after(owl:ObjectProperty/@IRI,'#')"/></xsl:variable>
	<xsl:variable name="first"><xsl:value-of select="substring-after(owl:NamedIndividual[position()=1]/@IRI,'#')"/></xsl:variable>
	<xsl:variable name="second"><xsl:value-of select="substring-after(owl:NamedIndividual[position()=2]/@IRI,'#')"/></xsl:variable>
<xsl:text>		</xsl:text><xsl:value-of select="$first"/>-><xsl:value-of select="$second"/> [label="<xsl:value-of select="$arc"/>", color=red, fontcolor=red, arrowhead=tee, arrowtail=invodot];
</xsl:template>

<xsl:template match="owl:DataPropertyAssertion">
	<xsl:variable name="key"><xsl:value-of select="substring-after(owl:DataProperty/@IRI,'#')"/></xsl:variable>
	<xsl:variable name="value"><xsl:value-of select="owl:Literal/text()"/></xsl:variable>
	<xsl:variable name="owner"><xsl:value-of select="substring-after(owl:NamedIndividual/@IRI,'#')"/></xsl:variable>
	<xsl:variable name="label">label="<xsl:value-of select="$key"/>=<xsl:value-of select="$value"/>"</xsl:variable>
	<xsl:variable name="boxid"><xsl:value-of select="$owner"/>_<xsl:value-of select="$key"/></xsl:variable>
<xsl:if test="$key != '' and $value != ''">
<xsl:text>		</xsl:text><xsl:value-of select="$boxid"/> [shape=box,style=filled,fillcolor=grey,<xsl:value-of select="$label"/>];
<xsl:text>		</xsl:text><xsl:value-of select="$owner"/>-><xsl:value-of select="$boxid"/> [dir=none];
</xsl:if>
</xsl:template>

<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
