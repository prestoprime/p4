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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for StillRegionSpatialDecompositionType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="StillRegionSpatialDecompositionType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:mpeg:mpeg7:schema:2004}SpatialSegmentDecompositionType">
 *       &lt;sequence>
 *         &lt;element name="StillRegion" type="{urn:mpeg:mpeg7:schema:2004}StillRegionType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StillRegionSpatialDecompositionType", propOrder = { "stillRegion" })
public class StillRegionSpatialDecompositionType extends SpatialSegmentDecompositionType implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(name = "StillRegion", required = true)
	protected List<StillRegionType> stillRegion;

	/**
	 * Gets the value of the stillRegion property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the stillRegion property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getStillRegion().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link StillRegionType }
	 * 
	 * 
	 */
	public List<StillRegionType> getStillRegion() {
		if (stillRegion == null) {
			stillRegion = new ArrayList<StillRegionType>();
		}
		return this.stillRegion;
	}

}
