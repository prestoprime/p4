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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for eventComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eventComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}eventIdentifier"/>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}eventType"/>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}eventDateTime"/>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}eventDetail" minOccurs="0"/>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}eventOutcomeInformation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}linkingAgentIdentifier" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{info:lc/xmlns/premis-v2}linkingObjectIdentifier" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="xmlID" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="version" type="{info:lc/xmlns/premis-v2}versionSimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventComplexType", propOrder = {
    "eventIdentifier",
    "eventType",
    "eventDateTime",
    "eventDetail",
    "eventOutcomeInformation",
    "linkingAgentIdentifier",
    "linkingObjectIdentifier"
})
public class EventComplexType {

    @XmlElement(required = true)
    protected EventIdentifierComplexType eventIdentifier;
    @XmlElement(required = true)
    protected String eventType;
    @XmlElement(required = true)
    protected String eventDateTime;
    protected String eventDetail;
    protected List<EventOutcomeInformationComplexType> eventOutcomeInformation;
    protected List<LinkingAgentIdentifierComplexType> linkingAgentIdentifier;
    protected List<LinkingObjectIdentifierComplexType> linkingObjectIdentifier;
    @XmlAttribute(name = "xmlID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String xmlID;
    @XmlAttribute(name = "version")
    protected String version;

    /**
     * Gets the value of the eventIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link EventIdentifierComplexType }
     *     
     */
    public EventIdentifierComplexType getEventIdentifier() {
        return eventIdentifier;
    }

    /**
     * Sets the value of the eventIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventIdentifierComplexType }
     *     
     */
    public void setEventIdentifier(EventIdentifierComplexType value) {
        this.eventIdentifier = value;
    }

    /**
     * Gets the value of the eventType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Sets the value of the eventType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventType(String value) {
        this.eventType = value;
    }

    /**
     * Gets the value of the eventDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventDateTime() {
        return eventDateTime;
    }

    /**
     * Sets the value of the eventDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventDateTime(String value) {
        this.eventDateTime = value;
    }

    /**
     * Gets the value of the eventDetail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventDetail() {
        return eventDetail;
    }

    /**
     * Sets the value of the eventDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventDetail(String value) {
        this.eventDetail = value;
    }

    /**
     * Gets the value of the eventOutcomeInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eventOutcomeInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEventOutcomeInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EventOutcomeInformationComplexType }
     * 
     * 
     */
    public List<EventOutcomeInformationComplexType> getEventOutcomeInformation() {
        if (eventOutcomeInformation == null) {
            eventOutcomeInformation = new ArrayList<EventOutcomeInformationComplexType>();
        }
        return this.eventOutcomeInformation;
    }

    /**
     * Gets the value of the linkingAgentIdentifier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the linkingAgentIdentifier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLinkingAgentIdentifier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LinkingAgentIdentifierComplexType }
     * 
     * 
     */
    public List<LinkingAgentIdentifierComplexType> getLinkingAgentIdentifier() {
        if (linkingAgentIdentifier == null) {
            linkingAgentIdentifier = new ArrayList<LinkingAgentIdentifierComplexType>();
        }
        return this.linkingAgentIdentifier;
    }

    /**
     * Gets the value of the linkingObjectIdentifier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the linkingObjectIdentifier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLinkingObjectIdentifier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LinkingObjectIdentifierComplexType }
     * 
     * 
     */
    public List<LinkingObjectIdentifierComplexType> getLinkingObjectIdentifier() {
        if (linkingObjectIdentifier == null) {
            linkingObjectIdentifier = new ArrayList<LinkingObjectIdentifierComplexType>();
        }
        return this.linkingObjectIdentifier;
    }

    /**
     * Gets the value of the xmlID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlID() {
        return xmlID;
    }

    /**
     * Sets the value of the xmlID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlID(String value) {
        this.xmlID = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

}