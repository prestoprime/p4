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
 * <p>Java class for inhibitorsComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="inhibitorsComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}inhibitorType"/>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}inhibitorTarget" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}inhibitorKey" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "inhibitorsComplexType", propOrder = {
    "inhibitorType",
    "inhibitorTarget",
    "inhibitorKey"
})
public class InhibitorsComplexType {

    @XmlElement(required = true)
    protected String inhibitorType;
    protected List<String> inhibitorTarget;
    protected String inhibitorKey;

    /**
     * Gets the value of the inhibitorType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInhibitorType() {
        return inhibitorType;
    }

    /**
     * Sets the value of the inhibitorType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInhibitorType(String value) {
        this.inhibitorType = value;
    }

    /**
     * Gets the value of the inhibitorTarget property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inhibitorTarget property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInhibitorTarget().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getInhibitorTarget() {
        if (inhibitorTarget == null) {
            inhibitorTarget = new ArrayList<String>();
        }
        return this.inhibitorTarget;
    }

    /**
     * Gets the value of the inhibitorKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInhibitorKey() {
        return inhibitorKey;
    }

    /**
     * Sets the value of the inhibitorKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInhibitorKey(String value) {
        this.inhibitorKey = value;
    }

}
