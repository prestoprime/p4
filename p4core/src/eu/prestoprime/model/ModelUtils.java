package eu.prestoprime.model;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class ModelUtils {

	public static enum P4JAXBPackage {
		DATA_MODEL("eu.prestoprime.model.acl:eu.prestoprime.model.dc:eu.prestoprime.model.dnx:eu.prestoprime.model.mets:eu.prestoprime.model.premis"),
		CONF("it.eurix.archtools.workflow.jaxb:eu.prestoprime.model.datatypes:eu.prestoprime.model.search:eu.prestoprime.model.terms"),
		OAI_PMH("eu.prestoprime.model.oaipmh");

		private String ns = null;

		private P4JAXBPackage(String ns) {
			this.ns = ns;
		}

		public final String getValue() {
			return ns;
		}
	};

	public static Marshaller getMarshaller(P4JAXBPackage jaxbPackage) throws JAXBException {

		JAXBContext context = JAXBContext.newInstance(jaxbPackage.getValue());
		Marshaller marshaller = context.createMarshaller();

		return marshaller;
	}

	public static Unmarshaller getUnmarshaller(P4JAXBPackage jaxbPackage) throws JAXBException {

		JAXBContext context = JAXBContext.newInstance(jaxbPackage.getValue());
		Unmarshaller unmarshaller = context.createUnmarshaller();

		return unmarshaller;
	}

	public static XMLGregorianCalendar Date2XMLGC(Date d) throws DatatypeConfigurationException {

		DatatypeFactory df = DatatypeFactory.newInstance();
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(d.getTime());
		return df.newXMLGregorianCalendar(gc);

	}

	/*
	 * public static Dc getOaiDcType(Record record){
	 * 
	 * Dc oaiDcType = new Dc();
	 * 
	 * ObjectFactory OaiDcObj = new ObjectFactory();
	 * 
	 * 
	 * //title List<String> titles = record.getTitle(); for (String dctitle :
	 * titles) {
	 * 
	 * 
	 * 
	 * ElementType titleElement = new ElementType();
	 * titleElement.setValue(dctitle);
	 * oaiDcType.getTitleOrCreatorOrSubject().add(titleElement);
	 * 
	 * OaiDcObj.createDc().getTitleOrCreatorOrSubject().
	 * oaiDcType.getTitleOrCreatorOrSubject().add(title); }
	 * 
	 * //creator List<String> creators = record.getCreator(); for (String
	 * dccreator : creators) { ElementType creatorElement = new ElementType();
	 * creatorElement.setValue(dccreator); JAXBElement<ElementType> creator =
	 * OaiDcObj.createCreator(creatorElement);
	 * oaiDcType.getTitleOrCreatorOrSubject().add(creator); }
	 * 
	 * //subject List<String> subjects = record.getSubject(); for (String
	 * dcsubject : subjects) { ElementType subjectElement = new ElementType();
	 * subjectElement.setValue(dcsubject); JAXBElement<ElementType> subject =
	 * OaiDcObj.createSubject(subjectElement);
	 * oaiDcType.getTitleOrCreatorOrSubject().add(subject); }
	 * 
	 * //description List<String> descriptions = record.getDescription(); for
	 * (String dcdescription : descriptions) { ElementType descriptionElement =
	 * new ElementType(); descriptionElement.setValue(dcdescription);
	 * JAXBElement<ElementType> description =
	 * OaiDcObj.createDescription(descriptionElement);
	 * oaiDcType.getTitleOrCreatorOrSubject().add(description); }
	 * 
	 * //publisher List<String> publishers = record.getPublisher(); for (String
	 * dcpublisher : publishers) { ElementType publisherElement = new
	 * ElementType(); publisherElement.setValue(dcpublisher);
	 * JAXBElement<ElementType> publisher =
	 * OaiDcObj.createPublisher(publisherElement);
	 * oaiDcType.getTitleOrCreatorOrSubject().add(publisher); }
	 * 
	 * 
	 * //contributor List<String> contributors = record.getContributor(); for
	 * (String dccontributor : contributors) { ElementType contributorElement =
	 * new ElementType(); contributorElement.setValue(dccontributor);
	 * JAXBElement<ElementType> contributor =
	 * OaiDcObj.createContributor(contributorElement);
	 * oaiDcType.getTitleOrCreatorOrSubject().add(contributor); }
	 * 
	 * //date List<String> dates = record.getDate(); for (String dcdate : dates)
	 * { ElementType dateElement = new ElementType();
	 * dateElement.setValue(dcdate); JAXBElement<ElementType> date =
	 * OaiDcObj.createDate(dateElement);
	 * oaiDcType.getTitleOrCreatorOrSubject().add(date); }
	 * 
	 * //type List<String> types = record.getType(); for (String dctype : types)
	 * { ElementType typeElement = new ElementType();
	 * typeElement.setValue(dctype); JAXBElement<ElementType> type =
	 * OaiDcObj.createType(typeElement);
	 * oaiDcType.getTitleOrCreatorOrSubject().add(type); }
	 * 
	 * //format List<String> formats = record.getFormat(); for (String dcformat
	 * : formats) { ElementType formatElement = new ElementType();
	 * formatElement.setValue(dcformat); JAXBElement<ElementType> format =
	 * OaiDcObj.createFormat(formatElement);
	 * oaiDcType.getTitleOrCreatorOrSubject().add(format); }
	 * 
	 * //identifier List<String> identifiers = record.getIdentifier(); for
	 * (String dcidentifier : identifiers) { ElementType identifierElement = new
	 * ElementType(); identifierElement.setValue(dcidentifier);
	 * JAXBElement<ElementType> identifier =
	 * OaiDcObj.createIdentifier(identifierElement);
	 * oaiDcType.getTitleOrCreatorOrSubject().add(identifier); }
	 * 
	 * //source List<String> sources = record.getSource(); for (String dcsource
	 * : sources) { ElementType sourceElement = new ElementType();
	 * sourceElement.setValue(dcsource); JAXBElement<ElementType> source =
	 * OaiDcObj.createSource(sourceElement);
	 * oaiDcType.getTitleOrCreatorOrSubject().add(source); }
	 * 
	 * //language List<String> languages = record.getLanguage(); for (String
	 * dclanguage : languages) { ElementType languageElement = new
	 * ElementType(); languageElement.setValue(dclanguage);
	 * JAXBElement<ElementType> language =
	 * OaiDcObj.createLanguage(languageElement);
	 * oaiDcType.getTitleOrCreatorOrSubject().add(language); }
	 * 
	 * //relation List<String> relations = record.getRelation(); for (String
	 * dcrelation : relations) { ElementType relationElement = new
	 * ElementType(); relationElement.setValue(dcrelation);
	 * JAXBElement<ElementType> relation =
	 * OaiDcObj.createRelation(relationElement);
	 * oaiDcType.getTitleOrCreatorOrSubject().add(relation); }
	 * 
	 * //coverage List<String> coverages = record.getCoverage(); for (String
	 * dccoverage : coverages) { ElementType coverageElement = new
	 * ElementType(); coverageElement.setValue(dccoverage);
	 * JAXBElement<ElementType> coverage =
	 * OaiDcObj.createCoverage(coverageElement);
	 * oaiDcType.getTitleOrCreatorOrSubject().add(coverage); }
	 * 
	 * //rights List<String> rightsList = record.getRights(); for (String
	 * dcrights : rightsList) { ElementType rightsElement = new ElementType();
	 * rightsElement.setValue(dcrights); JAXBElement<ElementType> rights =
	 * OaiDcObj.createRights(rightsElement);
	 * oaiDcType.getTitleOrCreatorOrSubject().add(rights); }
	 * 
	 * return oaiDcType;
	 * 
	 * }
	 */

	/*
	 * public static String writeAIP(AIPModel aip) throws JAXBException{
	 * 
	 * JAXBContext context =
	 * JAXBContext.newInstance(Constants.pprimeDefaultPersistenceUnitName);
	 * 
	 * StringWriter sw = new StringWriter(); Marshaller marshaller =
	 * context.createMarshaller();
	 * marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new
	 * Boolean(true));
	 * 
	 * marshaller.marshal(aip.getMetsType(), sw);
	 * 
	 * return sw.toString(); }
	 */

}
