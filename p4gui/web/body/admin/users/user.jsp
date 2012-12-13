<%@page import="eu.prestoprime.p4gui.model.P4Service"%>
<%@page import="java.util.List"%>
<%@page import="eu.prestoprime.p4gui.model.User"%>
<%@page import="eu.prestoprime.p4gui.util.DataBaseManager"%>
<%@page import="java.sql.ResultSet"%>

<%
String username = request.getParameter("username");
User user = DataBaseManager.getInstance().getUserByUsername(username);

List<P4Service> p4services = DataBaseManager.getInstance().getP4Services(username);

if (user != null) { %>
	<h2>User</h2>
	Username: <%=user.getUsername() %><br/>
	Email: <%=user.getEmail() %>
<%}

if (p4services != null) {
	for (P4Service service : p4services) { %>
		<br/>
		<h2>P4 Service</h2>
		URL: <%=service.getURL() %><br/>
		UserID: <%=service.getUserID() %>
	<%}
} %>