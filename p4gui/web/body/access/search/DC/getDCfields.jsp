<%@page import="eu.prestoprime.p4gui.util.parse.DCField"%>
<%@page import="java.util.Iterator"%>
<%@page import="eu.prestoprime.p4gui.connection.AccessConnection"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<%
String id = request.getParameter("id");
Iterator<DCField> itFields = AccessConnection.getDCFields(user.getCurrentP4Service(), id).iterator();
while (itFields.hasNext()) {
	DCField tmpField = itFields.next(); %>
	<b><%=tmpField.getTitle() %></b>: <%=tmpField.getValue() %><br/>
<%} %>