package eu.prestoprime.model;

import it.eurix.archtools.config.NamespaceManager;

public class P4NamespaceContext extends NamespaceManager {

	public static final String METS = "http://www.loc.gov/METS/";
	public static final String DC = "http://purl.org/dc/elements/1.1/";
	public static final String XLINK = "http://www.w3.org/1999/xlink";
	public static final String DNX = "http://www.prestoprime.eu/model/2012/dnx";
	public static final String OWL = "http://www.w3.org/2002/07/owl";
	public static final String ACL = "http://www.prestoprime.eu/model/2012/acl";
	public static final String PREMIS = "http://www.loc.gov/standards/premis/v1";
	public static final String OAI_PMH = "http://www.openarchives.org/OAI/2.0/";

	public static final String DATATYPES = "http://www.prestoprime.eu/model/2012/datatypes";
	public static final String SEARCH = "http://www.prestoprime.eu/model/2012/search";
	public static final String TERMS = "http://www.prestoprime.eu/model/2012/terms";

	public static enum P4Namespace {
		mets(METS),
		dc(DC),
		xlink(XLINK),
		dnx(DNX),
		owl(OWL),
		acl(ACL),
		premis(PREMIS),
		oai_pmh(OAI_PMH),
		
		datatypes(DATATYPES),
		search(SEARCH),
		terms(TERMS);
		
		private String namespaceURI;
		
		private P4Namespace(String namespaceURI) {
			this.namespaceURI = namespaceURI;
		}
		
		public String getNamespaceURI() {
			return namespaceURI;
		}
	}
	
	private static P4NamespaceContext instance;
	
	private P4NamespaceContext() {
		for (P4Namespace namespace : P4Namespace.values())
			super.addNamespace(namespace.toString(), namespace.getNamespaceURI());
	}
	
	public static P4NamespaceContext getInstance() {
		if (instance == null)
			instance = new P4NamespaceContext();
		return instance;
	}
	
	
	public static void main(String[] args) {
		System.out.println(P4NamespaceContext.getInstance().getNamespaceURI("mets"));
	}
//	@Override
//	public String getNamespaceURI(String prefix) {
//		switch (prefix) {
//		// DATA_MODEL
//		case "mets":
//			return METS;
//		case "dc":
//			return DC;
//		case "xlink":
//			return XLINK;
//		case "dnx":
//			return DNX;
//		case "owl":
//			return OWL;
//		case "acl":
//			return ACL;
//		case "premis":
//			return PREMIS;
//			// CONF
//		case "users":
//			return USERS;
//		case "datatypes":
//			return DATATYPES;
//		case "search":
//			return SEARCH;
//		case "terms":
//			return TERMS;
//		case "tools":
//			return TOOLS;
//		case "wf":
//			return WORKFLOW;
//
//		default:
//			return METS;
//		}
//	}
}