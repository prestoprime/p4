//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.11.13 at 06:00:06 PM CET 
//


package eu.prestoprime.premis;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for softwareComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="softwareComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}swName"/>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}swVersion" minOccurs="0"/>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}swType"/>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}swOtherInformation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}swDependency" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "softwareComplexType", propOrder = {
    "swName",
    "swVersion",
    "swType",
    "swOtherInformation",
    "swDependency"
})
public class SoftwareComplexType {

    @XmlElement(required = true)
    protected String swName;
    protected String swVersion;
    @XmlElement(required = true)
    protected String swType;
    protected List<String> swOtherInformation;
    protected List<String> swDependency;

    /**
     * Gets the value of the swName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSwName() {
        return swName;
    }

    /**
     * Sets the value of the swName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSwName(String value) {
        this.swName = value;
    }

    /**
     * Gets the value of the swVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSwVersion() {
        return swVersion;
    }

    /**
     * Sets the value of the swVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSwVersion(String value) {
        this.swVersion = value;
    }

    /**
     * Gets the value of the swType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSwType() {
        return swType;
    }

    /**
     * Sets the value of the swType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSwType(String value) {
        this.swType = value;
    }

    /**
     * Gets the value of the swOtherInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the swOtherInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSwOtherInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSwOtherInformation() {
        if (swOtherInformation == null) {
            swOtherInformation = new ArrayList<String>();
        }
        return this.swOtherInformation;
    }

    /**
     * Gets the value of the swDependency property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the swDependency property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSwDependency().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSwDependency() {
        if (swDependency == null) {
            swDependency = new ArrayList<String>();
        }
        return this.swDependency;
    }

}
