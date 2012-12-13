<%@page import="java.util.List"%>
<%@page import="eu.prestoprime.p4gui.util.TableColouringManager"%>
<%@page import="eu.prestoprime.p4gui.model.P4Service"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<div style="text-align: right;">
	<form method="POST" action="<%=request.getContextPath() %>/logout">
		<input type="submit" value="Logout"/>
	</form>
</div>

<div class="minititle separator">
	Registered P4 Services
</div>
<table class="coloured centered">
	<%TableColouringManager tcm = new TableColouringManager();
	P4Service currentService = user.getCurrentP4Service();
	List<P4Service> services = user.getP4Services();
	if (services.size() == 0) { %>
		<tr class="odd"><td>There are no p4services configured...</td></tr>
	<% } else {
		for (P4Service service : services) { %>
			<tr class="<%=tcm.getColouringClass() %>">
				<td>
					<%=service.getURL() %><br/>
					<a href="<%=request.getContextPath() %>/p4service/plugins?p4service=<%=service.getURL() %>">Plugins</a>
				</td>
				<td align="center">
				<%if (currentService != null && currentService.equals(service)) { %>
					<img class="dracma_container_button" src="<%=request.getContextPath() %>/resources/dracma/tick.png" />
				<%} else { %>
					<form method="POST" action="<%=request.getContextPath() %>/p4service">
						<input type="hidden" name="p4service" value="<%=service.getURL() %>" />
						<input type="submit" value="Select" />
					</form>
				<%} %>
				</td>				
			</tr>
			<%tcm.newRow();
		}
	} %>
</table>

<div class="minititle separator">
	Request New P4 Service
</div>
<form method="GET" action="<%=request.getContextPath() %>/p4service/terms">
	<table class="centered">
		<tr>
			<td><input type="text" name="p4service" /></td>
			<td>P4 Service URL</td>
		</tr>
		<tr>
			<td>
				<select name="role">
					<option value="consumer" selected="selected">Access</option>
					<option value="producer">Access, Ingest</option>
					<option value="admin">Access, Ingest, Manage</option>
				</select>
			</td>
			<td>P4 Permission</td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit" value="Request" /></td>
		</tr>
	</table>
</form>

<div class="minititle separator">
	Add New P4 Service
</div>
<form method="POST" action="<%=request.getContextPath() %>/p4service/add">
	<table class="centered">
		<tr>
			<td><input type="text" name="p4service" /></td>
			<td>P4 Service URL</td>
		</tr>
		<tr>
			<td><input type="password" name="userID" /></td>
			<td>User ID</td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit" value="Add" /></td>
		</tr>
	</table>
</form>
