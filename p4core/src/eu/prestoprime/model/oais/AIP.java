package eu.prestoprime.model.oais;

import org.w3c.dom.Node;

public interface AIP extends RWInformationPackage {

	public String updateSection(Node resultNode, String updateType) throws IPException;

	public void addPreservationEvent(String type, String info) throws IPException;
}
