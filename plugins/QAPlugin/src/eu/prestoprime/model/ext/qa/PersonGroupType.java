//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.24 at 11:42:28 PM CEST 
//

package eu.prestoprime.model.ext.qa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for PersonGroupType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="PersonGroupType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}AgentType">
 *       &lt;sequence>
 *         &lt;element name="Name" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;urn:mpeg:mpeg7:schema:2004>TextualType">
 *                 &lt;attribute name="type">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *                       &lt;enumeration value="former"/>
 *                       &lt;enumeration value="variant"/>
 *                       &lt;enumeration value="main"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="NameTerm" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{urn:mpeg:mpeg7:schema:2004}ControlledTermUseType">
 *                 &lt;attribute name="type">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *                       &lt;enumeration value="former"/>
 *                       &lt;enumeration value="variant"/>
 *                       &lt;enumeration value="main"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Kind" type="{urn:mpeg:mpeg7:schema:2004}TermUseType" minOccurs="0"/>
 *         &lt;element name="Member" type="{urn:mpeg:mpeg7:schema:2004}PersonType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Address" type="{urn:mpeg:mpeg7:schema:2004}PlaceType" minOccurs="0"/>
 *         &lt;element name="ElectronicAddress" type="{urn:mpeg:mpeg7:schema:2004}ElectronicAddressType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonGroupType", propOrder = { "name", "nameTerm", "kind", "member", "address", "electronicAddress" })
public class PersonGroupType extends AgentType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "Name")
	protected List<PersonGroupType.Name> name;
	@XmlElement(name = "NameTerm")
	protected List<PersonGroupType.NameTerm> nameTerm;
	@XmlElement(name = "Kind")
	protected TermUseType kind;
	@XmlElement(name = "Member")
	protected List<PersonType> member;
	@XmlElement(name = "Address")
	protected PlaceType address;
	@XmlElement(name = "ElectronicAddress")
	protected ElectronicAddressType electronicAddress;

	/**
	 * Gets the value of the name property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the name property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getName().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link PersonGroupType.Name }
	 * 
	 * 
	 */
	public List<PersonGroupType.Name> getName() {
		if (name == null) {
			name = new ArrayList<PersonGroupType.Name>();
		}
		return this.name;
	}

	/**
	 * Gets the value of the nameTerm property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the nameTerm property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getNameTerm().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link PersonGroupType.NameTerm }
	 * 
	 * 
	 */
	public List<PersonGroupType.NameTerm> getNameTerm() {
		if (nameTerm == null) {
			nameTerm = new ArrayList<PersonGroupType.NameTerm>();
		}
		return this.nameTerm;
	}

	/**
	 * Gets the value of the kind property.
	 * 
	 * @return possible object is {@link TermUseType }
	 * 
	 */
	public TermUseType getKind() {
		return kind;
	}

	/**
	 * Sets the value of the kind property.
	 * 
	 * @param value
	 *            allowed object is {@link TermUseType }
	 * 
	 */
	public void setKind(TermUseType value) {
		this.kind = value;
	}

	/**
	 * Gets the value of the member property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the member property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getMember().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link PersonType }
	 * 
	 * 
	 */
	public List<PersonType> getMember() {
		if (member == null) {
			member = new ArrayList<PersonType>();
		}
		return this.member;
	}

	/**
	 * Gets the value of the address property.
	 * 
	 * @return possible object is {@link PlaceType }
	 * 
	 */
	public PlaceType getAddress() {
		return address;
	}

	/**
	 * Sets the value of the address property.
	 * 
	 * @param value
	 *            allowed object is {@link PlaceType }
	 * 
	 */
	public void setAddress(PlaceType value) {
		this.address = value;
	}

	/**
	 * Gets the value of the electronicAddress property.
	 * 
	 * @return possible object is {@link ElectronicAddressType }
	 * 
	 */
	public ElectronicAddressType getElectronicAddress() {
		return electronicAddress;
	}

	/**
	 * Sets the value of the electronicAddress property.
	 * 
	 * @param value
	 *            allowed object is {@link ElectronicAddressType }
	 * 
	 */
	public void setElectronicAddress(ElectronicAddressType value) {
		this.electronicAddress = value;
	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;simpleContent>
	 *     &lt;extension base="&lt;urn:mpeg:mpeg7:schema:2004>TextualType">
	 *       &lt;attribute name="type">
	 *         &lt;simpleType>
	 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
	 *             &lt;enumeration value="former"/>
	 *             &lt;enumeration value="variant"/>
	 *             &lt;enumeration value="main"/>
	 *           &lt;/restriction>
	 *         &lt;/simpleType>
	 *       &lt;/attribute>
	 *     &lt;/extension>
	 *   &lt;/simpleContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class Name extends TextualType implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlAttribute(name = "type")
		@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
		protected String type;

		/**
		 * Gets the value of the type property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getType() {
			return type;
		}

		/**
		 * Sets the value of the type property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setType(String value) {
			this.type = value;
		}

	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}ControlledTermUseType">
	 *       &lt;attribute name="type">
	 *         &lt;simpleType>
	 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
	 *             &lt;enumeration value="former"/>
	 *             &lt;enumeration value="variant"/>
	 *             &lt;enumeration value="main"/>
	 *           &lt;/restriction>
	 *         &lt;/simpleType>
	 *       &lt;/attribute>
	 *     &lt;/extension>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class NameTerm extends ControlledTermUseType implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlAttribute(name = "type")
		@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
		protected String type;

		/**
		 * Gets the value of the type property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getType() {
			return type;
		}

		/**
		 * Sets the value of the type property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setType(String value) {
			this.type = value;
		}

	}

}