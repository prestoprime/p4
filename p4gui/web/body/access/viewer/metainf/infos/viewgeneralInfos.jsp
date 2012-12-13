<%@page import="eu.prestoprime.p4gui.util.parse.DC"%>
<%@page import="eu.prestoprime.p4gui.model.oais.DIP"%>
<%@page import="eu.prestoprime.p4gui.util.TableColouringManager"%>
<%@page import="eu.prestoprime.p4gui.util.parse.DCField"%>
<%@page import="java.util.Iterator"%>
<%@page import="eu.prestoprime.p4gui.util.Tools"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />
<jsp:useBean id="dcFields" class="java.util.ArrayList" scope="request" />

<%
Iterator<DCField> it = dcFields.iterator();
TableColouringManager tcm = new TableColouringManager();
%>

<table class="coloured">
	<%while (it.hasNext()) {
		DCField dcField = it.next(); %>
		<tr class="<%=tcm.getColouringClass() %>"><th><%=dcField.getTitle() %></th><td><%=dcField.getValue() %></td></tr>
		<%tcm.newRow();
	} %>
</table>