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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p>
 * Java class for IntegerMatrixType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="IntegerMatrixType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:mpeg:mpeg7:schema:2004>integerVector">
 *       &lt;attribute ref="{urn:mpeg:mpeg7:schema:2004}dim use="required""/>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntegerMatrixType", propOrder = { "value" })
@XmlSeeAlso({ BoxListType.class })
public class IntegerMatrixType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlValue
	protected List<BigInteger> value;
	@XmlAttribute(name = "dim", namespace = "urn:mpeg:mpeg7:schema:2004", required = true)
	protected List<BigInteger> dim;

	/**
	 * Gets the value of the value property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the value property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getValue().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link BigInteger }
	 * 
	 * 
	 */
	public List<BigInteger> getValue() {
		if (value == null) {
			value = new ArrayList<BigInteger>();
		}
		return this.value;
	}

	/**
	 * Gets the value of the dim property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the dim property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getDim().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link BigInteger }
	 * 
	 * 
	 */
	public List<BigInteger> getDim() {
		if (dim == null) {
			dim = new ArrayList<BigInteger>();
		}
		return this.dim;
	}

}
