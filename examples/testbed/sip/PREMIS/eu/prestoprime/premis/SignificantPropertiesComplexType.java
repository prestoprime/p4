//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.11.13 at 06:00:06 PM CET 
//


package eu.prestoprime.premis;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for significantPropertiesComplexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="significantPropertiesComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element ref="{info:lc/xmlns/premis-v2}significantPropertiesType"/>
 *           &lt;element ref="{info:lc/xmlns/premis-v2}significantPropertiesValue" minOccurs="0"/>
 *           &lt;element ref="{info:lc/xmlns/premis-v2}significantPropertiesExtension" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{info:lc/xmlns/premis-v2}mdSec" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{info:lc/xmlns/premis-v2}significantPropertiesValue"/>
 *           &lt;element ref="{info:lc/xmlns/premis-v2}significantPropertiesExtension" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{info:lc/xmlns/premis-v2}mdSec" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element ref="{info:lc/xmlns/premis-v2}significantPropertiesExtension"/>
 *           &lt;element ref="{info:lc/xmlns/premis-v2}mdSec"/>
 *         &lt;/choice>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "significantPropertiesComplexType", propOrder = {
    "content"
})
public class SignificantPropertiesComplexType {

    @XmlElementRefs({
        @XmlElementRef(name = "mdSec", namespace = "info:lc/xmlns/premis-v2", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "significantPropertiesValue", namespace = "info:lc/xmlns/premis-v2", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "significantPropertiesExtension", namespace = "info:lc/xmlns/premis-v2", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "significantPropertiesType", namespace = "info:lc/xmlns/premis-v2", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> content;

    /**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "SignificantPropertiesValue" is used by two different parts of a schema. See: 
     * line 834 of file:/home/francesco/Dropbox/P4_TEST_FILES/Test/PREMIS/premis.xsd
     * line 829 of file:/home/francesco/Dropbox/P4_TEST_FILES/Test/PREMIS/premis.xsd
     * <p>
     * To get rid of this property, apply a property customization to one 
     * of both of the following declarations to change their names: 
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link MdSecDefinition }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link ExtensionComplexType }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getContent() {
        if (content == null) {
            content = new ArrayList<JAXBElement<?>>();
        }
        return this.content;
    }

}