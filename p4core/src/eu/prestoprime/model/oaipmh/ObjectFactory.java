//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.04 at 03:25:13 AM CEST 
//

package eu.prestoprime.model.oaipmh;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the eu.prestoprime.model.oaipmh package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _TextURLTypeURL_QNAME = new QName("http://www.openarchives.org/OAI/1.1/eprints", "URL");
	private final static QName _TextURLTypeText_QNAME = new QName("http://www.openarchives.org/OAI/1.1/eprints", "text");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: eu.prestoprime.model.oaipmh
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link Identify }
	 * 
	 */
	public Identify createIdentify() {
		return new Identify();
	}

	/**
	 * Create an instance of {@link OAIPMH }
	 * 
	 */
	public OAIPMH createOAIPMH() {
		return new OAIPMH();
	}

	/**
	 * Create an instance of {@link RequestType }
	 * 
	 */
	public RequestType createRequestType() {
		return new RequestType();
	}

	/**
	 * Create an instance of {@link OAIPMHerrorType }
	 * 
	 */
	public OAIPMHerrorType createOAIPMHerrorType() {
		return new OAIPMHerrorType();
	}

	/**
	 * Create an instance of {@link IdentifyType }
	 * 
	 */
	public IdentifyType createIdentifyType() {
		return new IdentifyType();
	}

	/**
	 * Create an instance of {@link ListMetadataFormatsType }
	 * 
	 */
	public ListMetadataFormatsType createListMetadataFormatsType() {
		return new ListMetadataFormatsType();
	}

	/**
	 * Create an instance of {@link ListSetsType }
	 * 
	 */
	public ListSetsType createListSetsType() {
		return new ListSetsType();
	}

	/**
	 * Create an instance of {@link GetRecordType }
	 * 
	 */
	public GetRecordType createGetRecordType() {
		return new GetRecordType();
	}

	/**
	 * Create an instance of {@link ListIdentifiersType }
	 * 
	 */
	public ListIdentifiersType createListIdentifiersType() {
		return new ListIdentifiersType();
	}

	/**
	 * Create an instance of {@link ListRecordsType }
	 * 
	 */
	public ListRecordsType createListRecordsType() {
		return new ListRecordsType();
	}

	/**
	 * Create an instance of {@link MetadataType }
	 * 
	 */
	public MetadataType createMetadataType() {
		return new MetadataType();
	}

	/**
	 * Create an instance of {@link HeaderType }
	 * 
	 */
	public HeaderType createHeaderType() {
		return new HeaderType();
	}

	/**
	 * Create an instance of {@link RecordType }
	 * 
	 */
	public RecordType createRecordType() {
		return new RecordType();
	}

	/**
	 * Create an instance of {@link SetType }
	 * 
	 */
	public SetType createSetType() {
		return new SetType();
	}

	/**
	 * Create an instance of {@link DescriptionType }
	 * 
	 */
	public DescriptionType createDescriptionType() {
		return new DescriptionType();
	}

	/**
	 * Create an instance of {@link ResumptionTokenType }
	 * 
	 */
	public ResumptionTokenType createResumptionTokenType() {
		return new ResumptionTokenType();
	}

	/**
	 * Create an instance of {@link MetadataFormatType }
	 * 
	 */
	public MetadataFormatType createMetadataFormatType() {
		return new MetadataFormatType();
	}

	/**
	 * Create an instance of {@link AboutType }
	 * 
	 */
	public AboutType createAboutType() {
		return new AboutType();
	}

	/**
	 * Create an instance of {@link Identify.Description }
	 * 
	 */
	public Identify.Description createIdentifyDescription() {
		return new Identify.Description();
	}

	/**
	 * Create an instance of {@link Eprints }
	 * 
	 */
	public Eprints createEprints() {
		return new Eprints();
	}

	/**
	 * Create an instance of {@link TextURLType }
	 * 
	 */
	public TextURLType createTextURLType() {
		return new TextURLType();
	}

	/**
	 * Create an instance of {@link Friends }
	 * 
	 */
	public Friends createFriends() {
		return new Friends();
	}

	/**
	 * Create an instance of {@link OaiIdentifier }
	 * 
	 */
	public OaiIdentifier createOaiIdentifier() {
		return new OaiIdentifier();
	}

	/**
	 * Create an instance of {@link Sets }
	 * 
	 */
	public Sets createSets() {
		return new Sets();
	}

	/**
	 * Create an instance of {@link Set }
	 * 
	 */
	public Set createSet() {
		return new Set();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://www.openarchives.org/OAI/1.1/eprints", name = "URL", scope = TextURLType.class)
	public JAXBElement<String> createTextURLTypeURL(String value) {
		return new JAXBElement<String>(_TextURLTypeURL_QNAME, String.class, TextURLType.class, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://www.openarchives.org/OAI/1.1/eprints", name = "text", scope = TextURLType.class)
	public JAXBElement<String> createTextURLTypeText(String value) {
		return new JAXBElement<String>(_TextURLTypeText_QNAME, String.class, TextURLType.class, value);
	}

}
