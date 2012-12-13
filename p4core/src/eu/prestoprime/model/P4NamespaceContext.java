package eu.prestoprime.model;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class P4NamespaceContext implements NamespaceContext {

	public static final String METS = "http://www.loc.gov/METS/";
	public static final String DC = "http://purl.org/dc/elements/1.1/";
	public static final String XLINK = "http://www.w3.org/1999/xlink";
	public static final String DNX = "http://www.prestoprime.eu/model/2012/dnx";
	public static final String OWL = "http://www.w3.org/2002/07/owl";
	public static final String ACL = "http://www.prestoprime.eu/model/2012/acl";
	public static final String PREMIS = "http://www.loc.gov/standards/premis/v1";
	public static final String OAI_PMH = "http://www.openarchives.org/OAI/2.0/";

	public static final String USERS = "http://www.prestoprime.eu/model/2012/users";
	public static final String DATATYPES = "http://www.prestoprime.eu/model/2012/datatypes";
	public static final String SEARCH = "http://www.prestoprime.eu/model/2012/search";
	public static final String TERMS = "http://www.prestoprime.eu/model/2012/terms";
	public static final String TOOLS = "http://www.prestoprime.eu/model/2012/tools";
	public static final String WORKFLOW = "http://www.prestoprime.eu/model/2012/wf";

	@Override
	public String getNamespaceURI(String prefix) {
		switch (prefix) {
		// DATA_MODEL
		case "mets":
			return METS;
		case "dc":
			return DC;
		case "xlink":
			return XLINK;
		case "dnx":
			return DNX;
		case "owl":
			return OWL;
		case "acl":
			return ACL;
		case "premis":
			return PREMIS;
			// CONF
		case "users":
			return USERS;
		case "datatypes":
			return DATATYPES;
		case "search":
			return SEARCH;
		case "terms":
			return TERMS;
		case "tools":
			return TOOLS;
		case "wf":
			return WORKFLOW;

		default:
			return METS;
		}
	}

	@Override
	public String getPrefix(String namespaceURI) {
		return null;
	}

	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {
		return null;
	}
}