//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.24 at 11:42:28 PM CEST 
//

package eu.prestoprime.model.ext.qa;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for TextualType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="TextualType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:mpeg:mpeg7:schema:2004>TextualBaseType">
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TextualType")
@XmlSeeAlso({ eu.prestoprime.model.ext.qa.OrganizationType.Name.class, eu.prestoprime.model.ext.qa.TermDefinitionBaseType.Name.class, eu.prestoprime.model.ext.qa.PersonGroupType.Name.class, eu.prestoprime.model.ext.qa.InlineTermDefinitionType.Name.class, eu.prestoprime.model.ext.qa.KeywordAnnotationType.Keyword.class })
public class TextualType extends TextualBaseType implements Serializable {

	private final static long serialVersionUID = 1L;

}
