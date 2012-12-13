package eu.prestoprime.model.oais;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import eu.prestoprime.datamanagement.P4IPException;

public interface DIP extends InformationPackage {

	public static enum DCField {
		identifier, title, description, creator, source, format, language, publisher, contributor, coverage, date, relation, subject, type
	};

	public Map<String, List<String>> getDCFields() throws IPException;

	public List<String> getDCField(DCField field) throws IPException;

	public URL getThumbnail() throws IPException;

	public List<URL> getFrames() throws IPException;

	public URL getGraph() throws IPException;

	public String hasDatatype(String type) throws IPException;

	public List<String> getMDResource(String type) throws IPException;

	public List<Node> getMDResourceAsDOM(String type) throws IPException;

	public List<Node> getTechMdAsDOM() throws P4IPException;
}
