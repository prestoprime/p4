//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.07.24 at 11:42:28 PM CEST 
//

package eu.prestoprime.model.ext.qa;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Spatial2DCoordinateSystemType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="Spatial2DCoordinateSystemType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}HeaderType">
 *       &lt;sequence>
 *         &lt;element name="Unit" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="pixel"/>
 *               &lt;enumeration value="meter"/>
 *               &lt;enumeration value="pictureHeight"/>
 *               &lt;enumeration value="pictureWidth"/>
 *               &lt;enumeration value="pictureWidthAndHeight"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="LocalCoordinateSystem" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;choice>
 *                     &lt;sequence maxOccurs="3">
 *                       &lt;element name="Pixel">
 *                         &lt;simpleType>
 *                           &lt;restriction base="{urn:mpeg:mpeg7:schema:2004}integerVector">
 *                             &lt;length value="2"/>
 *                           &lt;/restriction>
 *                         &lt;/simpleType>
 *                       &lt;/element>
 *                       &lt;element name="CoordPoint">
 *                         &lt;simpleType>
 *                           &lt;restriction base="{urn:mpeg:mpeg7:schema:2004}floatVector">
 *                             &lt;length value="2"/>
 *                           &lt;/restriction>
 *                         &lt;/simpleType>
 *                       &lt;/element>
 *                     &lt;/sequence>
 *                     &lt;sequence maxOccurs="3">
 *                       &lt;element name="CurrPixel">
 *                         &lt;simpleType>
 *                           &lt;restriction base="{urn:mpeg:mpeg7:schema:2004}integerVector">
 *                             &lt;length value="2"/>
 *                           &lt;/restriction>
 *                         &lt;/simpleType>
 *                       &lt;/element>
 *                       &lt;element name="SrcPixel">
 *                         &lt;simpleType>
 *                           &lt;restriction base="{urn:mpeg:mpeg7:schema:2004}integerVector">
 *                             &lt;length value="2"/>
 *                           &lt;/restriction>
 *                         &lt;/simpleType>
 *                       &lt;/element>
 *                     &lt;/sequence>
 *                   &lt;/choice>
 *                   &lt;element name="MappingFunct" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="2" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="dataSet" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="IntegratedCoordinateSystem" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *                   &lt;element name="TimeIncr" type="{urn:mpeg:mpeg7:schema:2004}MediaIncrDurationType"/>
 *                   &lt;element name="MotionParams" type="{http://www.w3.org/2001/XMLSchema}float" maxOccurs="12" minOccurs="2"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="modelType" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="translational"/>
 *                       &lt;enumeration value="rotationAndScaling"/>
 *                       &lt;enumeration value="affine"/>
 *                       &lt;enumeration value="perspective"/>
 *                       &lt;enumeration value="quadratic"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="xOrigin" use="required" type="{http://www.w3.org/2001/XMLSchema}float" />
 *                 &lt;attribute name="yOrigin" use="required" type="{http://www.w3.org/2001/XMLSchema}float" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="xRepr" use="required" type="{urn:mpeg:mpeg7:schema:2004}unsigned8" />
 *       &lt;attribute name="yRepr" use="required" type="{urn:mpeg:mpeg7:schema:2004}unsigned8" />
 *       &lt;attribute name="xSrcSize" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="ySrcSize" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Spatial2DCoordinateSystemType", propOrder = { "unit", "localCoordinateSystem", "integratedCoordinateSystem" })
public class Spatial2DCoordinateSystemType extends HeaderType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "Unit")
	protected String unit;
	@XmlElement(name = "LocalCoordinateSystem")
	protected Spatial2DCoordinateSystemType.LocalCoordinateSystem localCoordinateSystem;
	@XmlElement(name = "IntegratedCoordinateSystem")
	protected Spatial2DCoordinateSystemType.IntegratedCoordinateSystem integratedCoordinateSystem;
	@XmlAttribute(name = "xRepr", required = true)
	protected int xRepr;
	@XmlAttribute(name = "yRepr", required = true)
	protected int yRepr;
	@XmlAttribute(name = "xSrcSize")
	@XmlSchemaType(name = "positiveInteger")
	protected BigInteger xSrcSize;
	@XmlAttribute(name = "ySrcSize")
	@XmlSchemaType(name = "positiveInteger")
	protected BigInteger ySrcSize;

	/**
	 * Gets the value of the unit property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Sets the value of the unit property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUnit(String value) {
		this.unit = value;
	}

	/**
	 * Gets the value of the localCoordinateSystem property.
	 * 
	 * @return possible object is
	 *         {@link Spatial2DCoordinateSystemType.LocalCoordinateSystem }
	 * 
	 */
	public Spatial2DCoordinateSystemType.LocalCoordinateSystem getLocalCoordinateSystem() {
		return localCoordinateSystem;
	}

	/**
	 * Sets the value of the localCoordinateSystem property.
	 * 
	 * @param value
	 *            allowed object is
	 *            {@link Spatial2DCoordinateSystemType.LocalCoordinateSystem }
	 * 
	 */
	public void setLocalCoordinateSystem(Spatial2DCoordinateSystemType.LocalCoordinateSystem value) {
		this.localCoordinateSystem = value;
	}

	/**
	 * Gets the value of the integratedCoordinateSystem property.
	 * 
	 * @return possible object is
	 *         {@link Spatial2DCoordinateSystemType.IntegratedCoordinateSystem }
	 * 
	 */
	public Spatial2DCoordinateSystemType.IntegratedCoordinateSystem getIntegratedCoordinateSystem() {
		return integratedCoordinateSystem;
	}

	/**
	 * Sets the value of the integratedCoordinateSystem property.
	 * 
	 * @param value
	 *            allowed object is
	 *            {@link Spatial2DCoordinateSystemType.IntegratedCoordinateSystem }
	 * 
	 */
	public void setIntegratedCoordinateSystem(Spatial2DCoordinateSystemType.IntegratedCoordinateSystem value) {
		this.integratedCoordinateSystem = value;
	}

	/**
	 * Gets the value of the xRepr property.
	 * 
	 */
	public int getXRepr() {
		return xRepr;
	}

	/**
	 * Sets the value of the xRepr property.
	 * 
	 */
	public void setXRepr(int value) {
		this.xRepr = value;
	}

	/**
	 * Gets the value of the yRepr property.
	 * 
	 */
	public int getYRepr() {
		return yRepr;
	}

	/**
	 * Sets the value of the yRepr property.
	 * 
	 */
	public void setYRepr(int value) {
		this.yRepr = value;
	}

	/**
	 * Gets the value of the xSrcSize property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getXSrcSize() {
		return xSrcSize;
	}

	/**
	 * Sets the value of the xSrcSize property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setXSrcSize(BigInteger value) {
		this.xSrcSize = value;
	}

	/**
	 * Gets the value of the ySrcSize property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getYSrcSize() {
		return ySrcSize;
	}

	/**
	 * Sets the value of the ySrcSize property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setYSrcSize(BigInteger value) {
		this.ySrcSize = value;
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
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
	 *         &lt;element name="TimeIncr" type="{urn:mpeg:mpeg7:schema:2004}MediaIncrDurationType"/>
	 *         &lt;element name="MotionParams" type="{http://www.w3.org/2001/XMLSchema}float" maxOccurs="12" minOccurs="2"/>
	 *       &lt;/sequence>
	 *       &lt;attribute name="modelType" use="required">
	 *         &lt;simpleType>
	 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *             &lt;enumeration value="translational"/>
	 *             &lt;enumeration value="rotationAndScaling"/>
	 *             &lt;enumeration value="affine"/>
	 *             &lt;enumeration value="perspective"/>
	 *             &lt;enumeration value="quadratic"/>
	 *           &lt;/restriction>
	 *         &lt;/simpleType>
	 *       &lt;/attribute>
	 *       &lt;attribute name="xOrigin" use="required" type="{http://www.w3.org/2001/XMLSchema}float" />
	 *       &lt;attribute name="yOrigin" use="required" type="{http://www.w3.org/2001/XMLSchema}float" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "timeIncrAndMotionParams" })
	public static class IntegratedCoordinateSystem implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlElements({ @XmlElement(name = "TimeIncr", type = MediaIncrDurationType.class), @XmlElement(name = "MotionParams", type = Float.class) })
		protected List<Serializable> timeIncrAndMotionParams;
		@XmlAttribute(name = "modelType", required = true)
		protected String modelType;
		@XmlAttribute(name = "xOrigin", required = true)
		protected float xOrigin;
		@XmlAttribute(name = "yOrigin", required = true)
		protected float yOrigin;

		/**
		 * Gets the value of the timeIncrAndMotionParams property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the timeIncrAndMotionParams property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getTimeIncrAndMotionParams().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link MediaIncrDurationType } {@link Float }
		 * 
		 * 
		 */
		public List<Serializable> getTimeIncrAndMotionParams() {
			if (timeIncrAndMotionParams == null) {
				timeIncrAndMotionParams = new ArrayList<Serializable>();
			}
			return this.timeIncrAndMotionParams;
		}

		/**
		 * Gets the value of the modelType property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getModelType() {
			return modelType;
		}

		/**
		 * Sets the value of the modelType property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setModelType(String value) {
			this.modelType = value;
		}

		/**
		 * Gets the value of the xOrigin property.
		 * 
		 */
		public float getXOrigin() {
			return xOrigin;
		}

		/**
		 * Sets the value of the xOrigin property.
		 * 
		 */
		public void setXOrigin(float value) {
			this.xOrigin = value;
		}

		/**
		 * Gets the value of the yOrigin property.
		 * 
		 */
		public float getYOrigin() {
			return yOrigin;
		}

		/**
		 * Sets the value of the yOrigin property.
		 * 
		 */
		public void setYOrigin(float value) {
			this.yOrigin = value;
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
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;choice>
	 *           &lt;sequence maxOccurs="3">
	 *             &lt;element name="Pixel">
	 *               &lt;simpleType>
	 *                 &lt;restriction base="{urn:mpeg:mpeg7:schema:2004}integerVector">
	 *                   &lt;length value="2"/>
	 *                 &lt;/restriction>
	 *               &lt;/simpleType>
	 *             &lt;/element>
	 *             &lt;element name="CoordPoint">
	 *               &lt;simpleType>
	 *                 &lt;restriction base="{urn:mpeg:mpeg7:schema:2004}floatVector">
	 *                   &lt;length value="2"/>
	 *                 &lt;/restriction>
	 *               &lt;/simpleType>
	 *             &lt;/element>
	 *           &lt;/sequence>
	 *           &lt;sequence maxOccurs="3">
	 *             &lt;element name="CurrPixel">
	 *               &lt;simpleType>
	 *                 &lt;restriction base="{urn:mpeg:mpeg7:schema:2004}integerVector">
	 *                   &lt;length value="2"/>
	 *                 &lt;/restriction>
	 *               &lt;/simpleType>
	 *             &lt;/element>
	 *             &lt;element name="SrcPixel">
	 *               &lt;simpleType>
	 *                 &lt;restriction base="{urn:mpeg:mpeg7:schema:2004}integerVector">
	 *                   &lt;length value="2"/>
	 *                 &lt;/restriction>
	 *               &lt;/simpleType>
	 *             &lt;/element>
	 *           &lt;/sequence>
	 *         &lt;/choice>
	 *         &lt;element name="MappingFunct" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="2" minOccurs="0"/>
	 *       &lt;/sequence>
	 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *       &lt;attribute name="dataSet" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "pixelAndCoordPoint", "currPixelAndSrcPixel", "mappingFunct" })
	public static class LocalCoordinateSystem implements Serializable {

		private final static long serialVersionUID = 1L;
		@XmlElementRefs({ @XmlElementRef(name = "CoordPoint", namespace = "urn:mpeg:mpeg7:schema:2004", type = JAXBElement.class, required = false), @XmlElementRef(name = "Pixel", namespace = "urn:mpeg:mpeg7:schema:2004", type = JAXBElement.class, required = false) })
		protected List<JAXBElement<? extends List<? extends Comparable>>> pixelAndCoordPoint;
		@XmlElementRefs({ @XmlElementRef(name = "CurrPixel", namespace = "urn:mpeg:mpeg7:schema:2004", type = JAXBElement.class, required = false), @XmlElementRef(name = "SrcPixel", namespace = "urn:mpeg:mpeg7:schema:2004", type = JAXBElement.class, required = false) })
		protected List<JAXBElement<List<BigInteger>>> currPixelAndSrcPixel;
		@XmlElement(name = "MappingFunct")
		protected List<String> mappingFunct;
		@XmlAttribute(name = "name", required = true)
		protected String name;
		@XmlAttribute(name = "dataSet")
		@XmlSchemaType(name = "anyURI")
		protected String dataSet;

		/**
		 * Gets the value of the pixelAndCoordPoint property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the pixelAndCoordPoint property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getPixelAndCoordPoint().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link JAXBElement }{@code <}{@link List }{@code <}{@link Float }
		 * {@code >}{@code >} {@link JAXBElement }{@code <}{@link List }{@code <}
		 * {@link BigInteger }{@code >}{@code >}
		 * 
		 * 
		 */
		public List<JAXBElement<? extends List<? extends Comparable>>> getPixelAndCoordPoint() {
			if (pixelAndCoordPoint == null) {
				pixelAndCoordPoint = new ArrayList<JAXBElement<? extends List<? extends Comparable>>>();
			}
			return this.pixelAndCoordPoint;
		}

		/**
		 * Gets the value of the currPixelAndSrcPixel property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the currPixelAndSrcPixel property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getCurrPixelAndSrcPixel().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link JAXBElement }{@code <}{@link List }{@code <}{@link BigInteger }
		 * {@code >}{@code >} {@link JAXBElement }{@code <}{@link List }{@code <}
		 * {@link BigInteger }{@code >}{@code >}
		 * 
		 * 
		 */
		public List<JAXBElement<List<BigInteger>>> getCurrPixelAndSrcPixel() {
			if (currPixelAndSrcPixel == null) {
				currPixelAndSrcPixel = new ArrayList<JAXBElement<List<BigInteger>>>();
			}
			return this.currPixelAndSrcPixel;
		}

		/**
		 * Gets the value of the mappingFunct property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the mappingFunct property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getMappingFunct().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link String }
		 * 
		 * 
		 */
		public List<String> getMappingFunct() {
			if (mappingFunct == null) {
				mappingFunct = new ArrayList<String>();
			}
			return this.mappingFunct;
		}

		/**
		 * Gets the value of the name property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets the value of the name property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setName(String value) {
			this.name = value;
		}

		/**
		 * Gets the value of the dataSet property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDataSet() {
			return dataSet;
		}

		/**
		 * Sets the value of the dataSet property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDataSet(String value) {
			this.dataSet = value;
		}

	}

}
