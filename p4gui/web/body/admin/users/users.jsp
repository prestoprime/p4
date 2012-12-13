<%@page import="java.util.List"%>
<%@page import="eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE"%>
<%@page import="eu.prestoprime.p4gui.util.DataBaseManager"%>
<%@page import="java.sql.ResultSet"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<%
request.setAttribute("title", "Users Manager");
List<String> usernames = DataBaseManager.getInstance().getRegisteredUsernames();
%>

<div class="panels_container">
	<div class="panel">
		<div class="subtitle">Registered</div>
		<div class="title">Users</div>
		<form method="POST" action="<%=request.getContextPath() %>/admin/users/delete">
			<select name="username">
				<%for (String username : usernames) { %>
					<option value="<%=username %>"><%=username %></option>
				<%} %>
			</select>
			<input type="submit" value="Delete" />
		</form>
	</div>
	<div class="panel">
		<div class="subtitle">Create new</div>
		<div class="title">userID</div>
		<div class="subtitle"><%=user.getCurrentP4Service().getURL() %></div>
		<form method="POST" action="<%=request.getContextPath() %>/admin/users/service/create">
			<table>
				<tr>
					<td>Username:</td>
					<td><select name="username">
						<%for (String username : usernames) { %>
							<option value="<%=username %>"><%=username %></option>
						<%} %>
					</select></td>
				</tr><tr>
					<td>Role:</td>
					<td><select name="role">
						<%for (USER_ROLE role : USER_ROLE.values()) { %>
							<option value="<%=role %>"><%=role %></option>
						<%} %>
					</select></td>
				</tr>
			</table>
			<input type="submit" value="Create" />
		</form>
	</div>
</div>