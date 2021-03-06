//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.04 at 03:25:13 AM CEST 
//

package eu.prestoprime.model.oaipmh;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="content" type="{http://www.openarchives.org/OAI/1.1/eprints}TextURLType" minOccurs="0"/>
 *         &lt;element name="metadataPolicy" type="{http://www.openarchives.org/OAI/1.1/eprints}TextURLType"/>
 *         &lt;element name="dataPolicy" type="{http://www.openarchives.org/OAI/1.1/eprints}TextURLType"/>
 *         &lt;element name="submissionPolicy" type="{http://www.openarchives.org/OAI/1.1/eprints}TextURLType" minOccurs="0"/>
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "content", "metadataPolicy", "dataPolicy", "submissionPolicy", "comment" })
@XmlRootElement(name = "eprints", namespace = "http://www.openarchives.org/OAI/1.1/eprints")
public class Eprints implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(namespace = "http://www.openarchives.org/OAI/1.1/eprints")
	protected TextURLType content;
	@XmlElement(namespace = "http://www.openarchives.org/OAI/1.1/eprints", required = true)
	protected TextURLType metadataPolicy;
	@XmlElement(namespace = "http://www.openarchives.org/OAI/1.1/eprints", required = true)
	protected TextURLType dataPolicy;
	@XmlElement(namespace = "http://www.openarchives.org/OAI/1.1/eprints")
	protected TextURLType submissionPolicy;
	@XmlElement(namespace = "http://www.openarchives.org/OAI/1.1/eprints")
	protected List<String> comment;

	/**
	 * Gets the value of the content property.
	 * 
	 * @return possible object is {@link TextURLType }
	 * 
	 */
	public TextURLType getContent() {
		return content;
	}

	/**
	 * Sets the value of the content property.
	 * 
	 * @param value
	 *            allowed object is {@link TextURLType }
	 * 
	 */
	public void setContent(TextURLType value) {
		this.content = value;
	}

	/**
	 * Gets the value of the metadataPolicy property.
	 * 
	 * @return possible object is {@link TextURLType }
	 * 
	 */
	public TextURLType getMetadataPolicy() {
		return metadataPolicy;
	}

	/**
	 * Sets the value of the metadataPolicy property.
	 * 
	 * @param value
	 *            allowed object is {@link TextURLType }
	 * 
	 */
	public void setMetadataPolicy(TextURLType value) {
		this.metadataPolicy = value;
	}

	/**
	 * Gets the value of the dataPolicy property.
	 * 
	 * @return possible object is {@link TextURLType }
	 * 
	 */
	public TextURLType getDataPolicy() {
		return dataPolicy;
	}

	/**
	 * Sets the value of the dataPolicy property.
	 * 
	 * @param value
	 *            allowed object is {@link TextURLType }
	 * 
	 */
	public void setDataPolicy(TextURLType value) {
		this.dataPolicy = value;
	}

	/**
	 * Gets the value of the submissionPolicy property.
	 * 
	 * @return possible object is {@link TextURLType }
	 * 
	 */
	public TextURLType getSubmissionPolicy() {
		return submissionPolicy;
	}

	/**
	 * Sets the value of the submissionPolicy property.
	 * 
	 * @param value
	 *            allowed object is {@link TextURLType }
	 * 
	 */
	public void setSubmissionPolicy(TextURLType value) {
		this.submissionPolicy = value;
	}

	/**
	 * Gets the value of the comment property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the comment property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getComment().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 * 
	 */
	public List<String> getComment() {
		if (comment == null) {
			comment = new ArrayList<String>();
		}
		return this.comment;
	}

}
