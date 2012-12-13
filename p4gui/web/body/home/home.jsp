<%@page import="java.sql.ResultSet"%>
<%@page import="eu.prestoprime.p4gui.util.DataBaseManager"%>
<%@page import="eu.prestoprime.p4gui.model.User"%>

<%request.setAttribute("title", "P4 Home"); %>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<img src="<%=request.getContextPath() %>/resources/p4_logo_inverted.png" align="left" height="450px"/>
<div class="panels_container">
	<%
	String hashs = request.getParameter("id");
	if (hashs != null && !hashs.equals("null")) {
		if (hashs.equals("new")) { %>
			<div class="panel">
				<div class="title">
					Create New P4 Account
				</div>
				<form method="POST" action="<%=request.getContextPath() %>/signup">
					<div id="username_message"></div>
					<table>
						<tr><td>Username</td><td><input type="text" name="username" onchange="checkUsername(this.value);"/></td></tr>
						<tr><td>Email</td><td><input type="text" name="email" /></td></tr>
						<tr><td>Confirm Email</td><td><input type="text" name="email2" /></td></tr>
					</table>
					<input type="submit" value="Sign Up" />
				</form>
				A confirmation email with instructions on how to activate<br/>
				your P4 account will be sent to your email address.
			</div>
			<script type="text/javascript">
				function checkUsername(username) {
					var path = "<%=request.getContextPath() %>/available";
					$.get(path, {username: username}, function(data) {
						$("#username_message").html(data);
					}, "html");
				}
			</script>
		<%} else if (hashs.equals("message")) {%>
			<jsp:useBean id="messageTitle" class="java.lang.String" scope="request" />
			<jsp:useBean id="messageBody" class="java.lang.String" scope="request" />
			<div class="panel">
				<div class="title">
					<%=messageTitle %>
				</div>
				<%=messageBody.replaceAll("\\n", "<br/>") %>
			</div>
		<%} else {
			int hashCode = Integer.parseInt(hashs);
			User customer = DataBaseManager.getInstance().getUserByHashCode(hashCode);
			if (customer != null) {
				String username = customer.getUsername();
				String email = customer.getEmail(); %>
				<div class="panel">
					<div class="title">
						Activate your account
					</div>
					<form method="POST" action="<%=request.getContextPath() %>/activate">
						<table style="width: 100%">
							<tr>
								<td align="right">Username</td><td><%=username %></td>
								<td style="width: 15%"></td>
								<td align="right">Full Name</td><td><input type="text" name="name" tabindex="3" /></td>
							</tr>
							<tr>
								<td align="right">email</td><td><%=email %></td>
										<td></td>
										<td align="right">Institution</td><td><input type="text" name="institution" tabindex="4" /></td>
									</tr>
									<tr>
										<td align="right">Password</td><td><input type="password" name="password" tabindex="1" /></td>
										<td></td>
										<td align="right">Address</td><td><input type="text" name="address" tabindex="5" /></td>
									</tr>
									<tr>
										<td align="right">Confirm Password</td><td><input type="password" name="password2" tabindex="2" /></td>
										<td></td>
										<td align="right">Phone</td><td><input type="text" name="phone" tabindex="6" /></td>
									</tr>
								</table>
								<textarea rows="5" cols="100" readonly="readonly">
Any personal information gathered is for internal use only, to allow us to enable your P4 account and access the service.
Any information provided to P4 by users through this site will be treated in confidence and under no circumstances we will sell, trade or otherwise disclose your details to third parties.
								</textarea><br/><br/>
								<input type="hidden" name="id" value="<%=hashCode %>" />
						<input type="submit" value="Activate Account" />
					</form>
				<%} else { %>
					<div class="title">
						Account already activated...
					</div>
				<%} %>
			</div>
		<%}
	} else { %>
		<div>
			<div class="title">Welcome<%
				boolean logged = user.isLogged(); if (!logged) out.print(""); else out.print(" " + user.getUsername());
			%>!</div>
			<div>
			This is the PrestoPRIME Preservation Platform (P4).<br/><br/>
			<a href="http://www.prestoprime.eu" class="coloured">PrestoPRIME</a> is a EU project funded under FP7.<br/>
			<a href="http://www.eurixgroup.com" class="coloured">EURIX</a> is partner of PrestoPRIME and is responsible for the design and development of P4.
			</div>
		</div>
		<br/><br/><br/>
		
		<div class="panel">
			<%if (!user.isLogged()) { %>
				<div class="subtitle">
					Sign in with your
				</div>
				<div class="title">
					P4 account
				</div>
				<form method="POST" action="<%=request.getContextPath() %>/login">
					Username: <input type="text" name="username" /><br/>
					Password: <input type="password" name="password" /><br/>
					<input type="submit" value="Sign In" />
				</form>
				New here? <a href="<%=request.getContextPath() %>/signup" class="coloured">Sign Up</a>
			<%} else {
				if (user.getCurrentP4Service() != null) { %>
					<div class="subtitle">
						Your current
					</div>
					<div class="title">
						P4 Service
					</div>
					<%=user.getCurrentP4Service().getURL() %>
				<%} else { %>
					<div class="title">
						No P4 Service registered.
					</div>
					<div class="subtitle">
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
				<%}
			} %>
		</div>
	<%} %>
</div>