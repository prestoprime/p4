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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for ClassificationType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="ClassificationType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}DSType">
 *       &lt;sequence>
 *         &lt;element name="Form" type="{urn:mpeg:mpeg7:schema:2004}ControlledTermUseType" minOccurs="0"/>
 *         &lt;element name="Genre" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{urn:mpeg:mpeg7:schema:2004}ControlledTermUseType">
 *                 &lt;attribute name="type" default="main">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *                       &lt;enumeration value="main"/>
 *                       &lt;enumeration value="secondary"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Subject" type="{urn:mpeg:mpeg7:schema:2004}TextAnnotationType" minOccurs="0"/>
 *         &lt;element name="Purpose" type="{urn:mpeg:mpeg7:schema:2004}ControlledTermUseType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Language" type="{urn:mpeg:mpeg7:schema:2004}ExtendedLanguageType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="CaptionLanguage" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>language">
 *                 &lt;attribute name="closed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *                 &lt;attribute name="supplemental" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SignLanguage" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>language">
 *                 &lt;attribute name="primary" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                 &lt;attribute name="translation" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClassificationType", propOrder = { "form", "genre", "subject", "purpose", "language", "captionLanguage", "signLanguage" })
public class ClassificationType extends DSType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "Form")
	protected ControlledTermUseType form;
	@XmlElement(name = "Genre")
	protected List<ClassificationType.Genre> genre;
	@XmlElement(name = "Subject")
	protected TextAnnotationType subject;
	@XmlElement(name = "Purpose")
	protected List<ControlledTermUseType> purpose;
	@XmlElement(name = "Language")
	protected List<ExtendedLanguageType> language;
	@XmlElement(name = "CaptionLanguage")
	protected List<ClassificationType.CaptionLanguage> captionLanguage;
	@XmlElement(name = "SignLanguage")
	protected List<ClassificationType.SignLanguage> signLanguage;

	/**
	 * Gets the value of the form property.
	 * 
	 * @return possible object is {@link ControlledTermUseType }
	 * 
	 */
	public ControlledTermUseType getForm() {
		return form;
	}

	/**
	 * Sets the value of the form property.
	 * 
	 * @param value
	 *            allowed object is {@link ControlledTermUseType }
	 * 
	 */
	public void setForm(ControlledTermUseType value) {
		this.form = value;
	}

	/**
	 * Gets the value of the genre property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the genre property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getGenre().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ClassificationType.Genre }
	 * 
	 * 
	 */
	public List<ClassificationType.Genre> getGenre() {
		if (genre == null) {
			genre = new ArrayList<ClassificationType.Genre>();
		}
		return this.genre;
	}

	/**
	 * Gets the value of the subject property.
	 * 
	 * @return possible object is {@link TextAnnotationType }
	 * 
	 */
	public TextAnnotationType getSubject() {
		return subject;
	}

	/**
	 * Sets the value of the subject property.
	 * 
	 * @param value
	 *            allowed object is {@link TextAnnotationType }
	 * 
	 */
	public void setSubject(TextAnnotationType value) {
		this.subject = value;
	}

	/**
	 * Gets the value of the purpose property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the purpose property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getPurpose().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ControlledTermUseType }
	 * 
	 * 
	 */
	public List<ControlledTermUseType> getPurpose() {
		if (purpose == null) {
			purpose = new ArrayList<ControlledTermUseType>();
		}
		return this.purpose;
	}

	/**
	 * Gets the value of the language property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the language property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getLanguage().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ExtendedLanguageType }
	 * 
	 * 
	 */
	public List<ExtendedLanguageType> getLanguage() {
		if (language == null) {
			language = new ArrayList<ExtendedLanguageType>();
		}
		return this.language;
	}

	/**
	 * Gets the value of the captionLanguage property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the captionLanguage property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getCaptionLanguage().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ClassificationType.CaptionLanguage }
	 * 
	 * 
	 */
	public List<ClassificationType.CaptionLanguage> getCaptionLanguage() {
		if (captionLanguage == null) {
			captionLanguage = new ArrayList<ClassificationType.CaptionLanguage>();
		}
		return this.captionLanguage;
	}

	/**
	 * Gets the value of the signLanguage property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the signLanguage property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getSignLanguage().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ClassificationType.SignLanguage }
	 * 
	 * 
	 */
	public List<ClassificationType.SignLanguage> getSignLanguage() {
		if (signLanguage == null) {
			signLanguage = new ArrayList<ClassificationType.SignLanguage>();
		}
		return this.signLanguage;
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
	 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>language">
	 *       &lt;attribute name="closed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
	 *       &lt;attribute name="supplemental" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
	 *     &lt;/extension>
	 *   &lt;/simpleContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "value" })
	public static class CaptionLanguage implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlValue
		@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
		@XmlSchemaType(name = "language")
		protected String value;
		@XmlAttribute(name = "closed")
		protected Boolean closed;
		@XmlAttribute(name = "supplemental")
		protected Boolean supplemental;

		/**
		 * Gets the value of the value property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Sets the value of the value property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * Gets the value of the closed property.
		 * 
		 * @return possible object is {@link Boolean }
		 * 
		 */
		public boolean isClosed() {
			if (closed == null) {
				return true;
			} else {
				return closed;
			}
		}

		/**
		 * Sets the value of the closed property.
		 * 
		 * @param value
		 *            allowed object is {@link Boolean }
		 * 
		 */
		public void setClosed(Boolean value) {
			this.closed = value;
		}

		/**
		 * Gets the value of the supplemental property.
		 * 
		 * @return possible object is {@link Boolean }
		 * 
		 */
		public boolean isSupplemental() {
			if (supplemental == null) {
				return false;
			} else {
				return supplemental;
			}
		}

		/**
		 * Sets the value of the supplemental property.
		 * 
		 * @param value
		 *            allowed object is {@link Boolean }
		 * 
		 */
		public void setSupplemental(Boolean value) {
			this.supplemental = value;
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
	 *       &lt;attribute name="type" default="main">
	 *         &lt;simpleType>
	 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
	 *             &lt;enumeration value="main"/>
	 *             &lt;enumeration value="secondary"/>
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
	public static class Genre extends ControlledTermUseType implements Serializable {

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
			if (type == null) {
				return "main";
			} else {
				return type;
			}
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
	 *   &lt;simpleContent>
	 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>language">
	 *       &lt;attribute name="primary" type="{http://www.w3.org/2001/XMLSchema}boolean" />
	 *       &lt;attribute name="translation" type="{http://www.w3.org/2001/XMLSchema}boolean" />
	 *     &lt;/extension>
	 *   &lt;/simpleContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "value" })
	public static class SignLanguage implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlValue
		@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
		@XmlSchemaType(name = "language")
		protected String value;
		@XmlAttribute(name = "primary")
		protected Boolean primary;
		@XmlAttribute(name = "translation")
		protected Boolean translation;

		/**
		 * Gets the value of the value property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Sets the value of the value property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * Gets the value of the primary property.
		 * 
		 * @return possible object is {@link Boolean }
		 * 
		 */
		public Boolean isPrimary() {
			return primary;
		}

		/**
		 * Sets the value of the primary property.
		 * 
		 * @param value
		 *            allowed object is {@link Boolean }
		 * 
		 */
		public void setPrimary(Boolean value) {
			this.primary = value;
		}

		/**
		 * Gets the value of the translation property.
		 * 
		 * @return possible object is {@link Boolean }
		 * 
		 */
		public Boolean isTranslation() {
			return translation;
		}

		/**
		 * Sets the value of the translation property.
		 * 
		 * @param value
		 *            allowed object is {@link Boolean }
		 * 
		 */
		public void setTranslation(Boolean value) {
			this.translation = value;
		}

	}

}