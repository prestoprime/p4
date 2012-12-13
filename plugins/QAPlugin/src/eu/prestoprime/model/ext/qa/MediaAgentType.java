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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for MediaAgentType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="MediaAgentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Role" type="{urn:mpeg:mpeg7:schema:2004}ControlledTermUseType"/>
 *         &lt;choice>
 *           &lt;element name="Agent" type="{urn:mpeg:mpeg7:schema:2004}AgentType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MediaAgentType", propOrder = { "role", "agent" })
@XmlSeeAlso({ CreatorType.class })
public class MediaAgentType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "Role", required = true)
	protected ControlledTermUseType role;
	@XmlElement(name = "Agent")
	protected AgentType agent;

	/**
	 * Gets the value of the role property.
	 * 
	 * @return possible object is {@link ControlledTermUseType }
	 * 
	 */
	public ControlledTermUseType getRole() {
		return role;
	}

	/**
	 * Sets the value of the role property.
	 * 
	 * @param value
	 *            allowed object is {@link ControlledTermUseType }
	 * 
	 */
	public void setRole(ControlledTermUseType value) {
		this.role = value;
	}

	/**
	 * Gets the value of the agent property.
	 * 
	 * @return possible object is {@link AgentType }
	 * 
	 */
	public AgentType getAgent() {
		return agent;
	}

	/**
	 * Sets the value of the agent property.
	 * 
	 * @param value
	 *            allowed object is {@link AgentType }
	 * 
	 */
	public void setAgent(AgentType value) {
		this.agent = value;
	}

}