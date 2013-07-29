//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.11.13 at 06:00:06 PM CET 
//


package eu.prestoprime.premis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fixityComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fixityComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}messageDigestAlgorithm"/>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}messageDigest"/>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}messageDigestOriginator" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fixityComplexType", propOrder = {
    "messageDigestAlgorithm",
    "messageDigest",
    "messageDigestOriginator"
})
public class FixityComplexType {

    @XmlElement(required = true)
    protected String messageDigestAlgorithm;
    @XmlElement(required = true)
    protected String messageDigest;
    protected String messageDigestOriginator;

    /**
     * Gets the value of the messageDigestAlgorithm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageDigestAlgorithm() {
        return messageDigestAlgorithm;
    }

    /**
     * Sets the value of the messageDigestAlgorithm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageDigestAlgorithm(String value) {
        this.messageDigestAlgorithm = value;
    }

    /**
     * Gets the value of the messageDigest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageDigest() {
        return messageDigest;
    }

    /**
     * Sets the value of the messageDigest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageDigest(String value) {
        this.messageDigest = value;
    }

    /**
     * Gets the value of the messageDigestOriginator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageDigestOriginator() {
        return messageDigestOriginator;
    }

    /**
     * Sets the value of the messageDigestOriginator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageDigestOriginator(String value) {
        this.messageDigestOriginator = value;
    }

}