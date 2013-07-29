//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.11.13 at 06:00:06 PM CET 
//


package eu.prestoprime.premis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for linkingRightsStatementIdentifierComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="linkingRightsStatementIdentifierComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}linkingRightsStatementIdentifierType"/>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}linkingRightsStatementIdentifierValue"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}simpleLink"/>
 *       &lt;attribute name="LinkPermissionStatementXmlID" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "linkingRightsStatementIdentifierComplexType", propOrder = {
    "linkingRightsStatementIdentifierType",
    "linkingRightsStatementIdentifierValue"
})
public class LinkingRightsStatementIdentifierComplexType {

    @XmlElement(required = true)
    protected String linkingRightsStatementIdentifierType;
    @XmlElement(required = true)
    protected String linkingRightsStatementIdentifierValue;
    @XmlAttribute(name = "LinkPermissionStatementXmlID")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object linkPermissionStatementXmlID;
    @XmlAttribute(name = "type", namespace = "http://www.w3.org/1999/xlink")
    protected String type;
    @XmlAttribute(name = "href", namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAttribute(name = "role", namespace = "http://www.w3.org/1999/xlink")
    protected String role;
    @XmlAttribute(name = "arcrole", namespace = "http://www.w3.org/1999/xlink")
    protected String arcrole;
    @XmlAttribute(name = "title", namespace = "http://www.w3.org/1999/xlink")
    protected String title;
    @XmlAttribute(name = "show", namespace = "http://www.w3.org/1999/xlink")
    protected String show;
    @XmlAttribute(name = "actuate", namespace = "http://www.w3.org/1999/xlink")
    protected String actuate;

    /**
     * Gets the value of the linkingRightsStatementIdentifierType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkingRightsStatementIdentifierType() {
        return linkingRightsStatementIdentifierType;
    }

    /**
     * Sets the value of the linkingRightsStatementIdentifierType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinkingRightsStatementIdentifierType(String value) {
        this.linkingRightsStatementIdentifierType = value;
    }

    /**
     * Gets the value of the linkingRightsStatementIdentifierValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkingRightsStatementIdentifierValue() {
        return linkingRightsStatementIdentifierValue;
    }

    /**
     * Sets the value of the linkingRightsStatementIdentifierValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinkingRightsStatementIdentifierValue(String value) {
        this.linkingRightsStatementIdentifierValue = value;
    }

    /**
     * Gets the value of the linkPermissionStatementXmlID property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getLinkPermissionStatementXmlID() {
        return linkPermissionStatementXmlID;
    }

    /**
     * Sets the value of the linkPermissionStatementXmlID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setLinkPermissionStatementXmlID(Object value) {
        this.linkPermissionStatementXmlID = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        if (type == null) {
            return "simple";
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the href property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Gets the value of the arcrole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Sets the value of the arcrole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArcrole(String value) {
        this.arcrole = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the show property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShow(String value) {
        this.show = value;
    }

    /**
     * Gets the value of the actuate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActuate() {
        return actuate;
    }

    /**
     * Sets the value of the actuate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActuate(String value) {
        this.actuate = value;
    }

}
