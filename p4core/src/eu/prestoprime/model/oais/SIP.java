package eu.prestoprime.model.oais;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import eu.prestoprime.model.dnx.Dnx;

public interface SIP extends RWInformationPackage {

	public Map<String, List<String>> getDCFields() throws IPException;

	public String setRights(Node rights) throws IPException;

	public void addDNX(Dnx dnx, String id, boolean isRef) throws IPException;

	public void setCreateDate(GregorianCalendar date) throws IPException;

	public void purgeFiles() throws IPException;
}
