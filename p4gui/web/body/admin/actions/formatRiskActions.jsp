<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="eu.prestoprime.p4gui.util.TableColouringManager"%>
<%@page import="eu.prestoprime.p4gui.util.parse.AdminActions.Action"%>
<%@page import="java.util.Map"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />
<jsp:useBean id="result" class="java.util.ArrayList" scope="request" />

<%
request.setAttribute("title", "AIP to be migrated");
%>

<div class="panels_container">
	<div class="panel">
		<table class="coloured" style="width: 500px;">
			<tr><td>
			</td><td>
				<%String workflow = "uncompressed_format_migration"; %>
				<input type="button" value="Run Jobs" onclick="executeMultiWorkflow('<%=workflow %>');" />
			</td></tr>
			<tr>
				<th>AIPid</th>
				<th>
					<input type="button" value="Select All" onclick="$('input:checkbox').prop('checked', true);" />
				</th>
			</tr>
			<%
			TableColouringManager tcm = new TableColouringManager();
			for (Object id : result) { %>
				<tr class="<%=tcm.getColouringClass() %>">
					<td><a href="<%=request.getContextPath() %>/access/viewer?id=<%=id %>" class="coloured"><%= id %></a></td>
					<td class="last_update" id="<%=id %>">
						<input type="checkbox" value="<%=id %>" />
					</td>
				</tr>
				<%tcm.newRow();
			} %>
		</table>
	</div>
</div>
