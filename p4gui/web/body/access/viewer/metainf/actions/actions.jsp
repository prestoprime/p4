<%@page import="java.util.List"%>
<%@page import="eu.prestoprime.p4gui.connection.CommonConnection"%>
<%@page import="java.util.Iterator"%>
<%@page import="eu.prestoprime.p4gui.P4GUI"%>
<%@page import="eu.prestoprime.p4gui.connection.AccessConnection"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<jsp:useBean id="qa_status" class="java.lang.String" scope="request" />
<jsp:useBean id="qa_actions" class="java.lang.String" scope="request" />
<jsp:useBean id="qa_date" class="java.util.Date" scope="request" />

<jsp:useBean id="fprint_status" class="java.lang.String" scope="request" />
<jsp:useBean id="fprint_actions" class="java.lang.String" scope="request" />
<jsp:useBean id="fprint_date" class="java.util.Date" scope="request" />

<jsp:useBean id="usermd_status" class="java.lang.String" scope="request" />
<jsp:useBean id="usermd_actions" class="java.lang.String" scope="request" />
<jsp:useBean id="usermd_date" class="java.util.Date" scope="request" />

<%
String id = request.getParameter("id");
DateFormat df = new SimpleDateFormat(P4GUI.SHORT_DATE_PATTERN);
%>

<%if (AccessConnection.getPreviewsPath(user.getCurrentP4Service(), id).size() > 0) { %>
<table class="coloured">
	<tr><th></th><th>STATUS</th><th>LAST UPDATE</th></tr>
	<tr class="odd">
		<th>QA</th>
		<td><%=qa_status %></td>
		<td><%=qa_status.equals("NOT AVAILABLE") ? "NOT AVAILABLE" : df.format(qa_date) %></td>
		<td>
			<button onclick="executeWorkflow('<%=id %>', '<%=qa_actions %>'); this.disabled = true;">Run Automatic QA</button>
		</td>
	</tr>
	<tr class="pair">
		<th>FingerPrint</th>
		<td><%=fprint_status %></td>
		<td><%=fprint_status.equals("NOT AVAILABLE") ? "NOT AVAILABLE" : df.format(fprint_date) %></td>
		<td>
			<%//for (String fprint_action : fprint_actions.split(";")) { %>
				<button onclick="executeWorkflow('<%=id %>', '<%=fprint_actions %>'); this.disabled = true;">Upload AV File</button>
			<%//} %>
		</td>
	</tr>
	<tr class="odd">
		<th>UserMD</th>
		<td><%=usermd_status %></td>
		<td><%=usermd_status.equals("NOT AVAILABLE") ? "NOT AVAILABLE" : df.format(usermd_date) %></td>
		<td>
			<button onclick="executeWorkflow('<%=id %>', '<%=usermd_actions %>'); this.disabled = true;">Upload AV File</button>
		</td>
	</tr>
</table>
<form method="GET" action="<%=request.getContextPath() %>/update" style="margin-top: 20px;">
	<input type="hidden" name="id" value="<%=id %>" />
	<input type="submit" value="Go to the UPDATE page for this AIP" />
</form>
<%} else { %>
<form method="POST" action="<%=request.getContextPath() %>/update/avmaterial">
	<table class="coloured">
		<tr><th>MIMETIPE</th><th>LOCATION</th></tr>
		<tr class="pair">
			<td>
				<input type="text" disabled="disabled" id="mimetype_input2" style="color: black; width: 150px;" />
			</td>
			<td>
				<input type="hidden" name="aip.id" value="<%=id %>" />
				<input type="hidden" id="mimetype_input" name="file.mimetype" />
				<select name="file.path" style="width: 450px;" onchange="updateMIMEType(this.value);">
					<option value=""></option>
					<%List<String> fileList = CommonConnection.getAvailableFileList(user.getCurrentP4Service());
					Iterator<String> it = fileList.iterator();
					while (it.hasNext()) {
					String tmp = it.next(); %>
							<option value="<%=tmp %>"><%=tmp %></option>
					<%} %>
				</select>
			</td>
			<td><input type="submit" value="Add AV Material" /></td>
		</tr>
	</table>
</form>
<%} %>