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
 *         &lt;element name="scheme" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="repositoryIdentifier" type="{http://www.openarchives.org/OAI/2.0/oai-identifier}repositoryIdentifierType"/>
 *         &lt;element name="delimiter" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sampleIdentifier" type="{http://www.openarchives.org/OAI/2.0/oai-identifier}sampleIdentifierType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "scheme", "repositoryIdentifier", "delimiter", "sampleIdentifier" })
@XmlRootElement(name = "oai-identifier", namespace = "http://www.openarchives.org/OAI/2.0/oai-identifier")
public class OaiIdentifier implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(namespace = "http://www.openarchives.org/OAI/2.0/oai-identifier", required = true)
	protected String scheme;
	@XmlElement(namespace = "http://www.openarchives.org/OAI/2.0/oai-identifier", required = true)
	protected String repositoryIdentifier;
	@XmlElement(namespace = "http://www.openarchives.org/OAI/2.0/oai-identifier", required = true)
	protected String delimiter;
	@XmlElement(namespace = "http://www.openarchives.org/OAI/2.0/oai-identifier", required = true)
	protected String sampleIdentifier;

	/**
	 * Gets the value of the scheme property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getScheme() {
		return scheme;
	}

	/**
	 * Sets the value of the scheme property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setScheme(String value) {
		this.scheme = value;
	}

	/**
	 * Gets the value of the repositoryIdentifier property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRepositoryIdentifier() {
		return repositoryIdentifier;
	}

	/**
	 * Sets the value of the repositoryIdentifier property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRepositoryIdentifier(String value) {
		this.repositoryIdentifier = value;
	}

	/**
	 * Gets the value of the delimiter property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * Sets the value of the delimiter property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDelimiter(String value) {
		this.delimiter = value;
	}

	/**
	 * Gets the value of the sampleIdentifier property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSampleIdentifier() {
		return sampleIdentifier;
	}

	/**
	 * Sets the value of the sampleIdentifier property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSampleIdentifier(String value) {
		this.sampleIdentifier = value;
	}

}