//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.04 at 03:25:13 AM CEST 
//

package eu.prestoprime.model.oaipmh;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for GetRecordType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="GetRecordType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="record" type="{http://www.openarchives.org/OAI/2.0/}recordType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetRecordType", propOrder = { "record" })
public class GetRecordType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(required = true)
	protected RecordType record;

	/**
	 * Gets the value of the record property.
	 * 
	 * @return possible object is {@link RecordType }
	 * 
	 */
	public RecordType getRecord() {
		return record;
	}

	/**
	 * Sets the value of the record property.
	 * 
	 * @param value
	 *            allowed object is {@link RecordType }
	 * 
	 */
	public void setRecord(RecordType value) {
		this.record = value;
	}

}
