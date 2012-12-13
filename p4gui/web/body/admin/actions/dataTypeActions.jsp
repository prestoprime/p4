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
String dataType = request.getParameter("dataType");
boolean available = Boolean.parseBoolean(request.getParameter("available"));

request.setAttribute("title", "AIP with " + dataType + " Section");

DateFormat df = new SimpleDateFormat("yyyy");
%>

<div class="panels_container">
	<div class="panel">
		<table class="coloured" style="width: 500px;">
			<tr><td>
				<input type="button" value="Available" onclick="location.href='?type=dataType&dataType=<%=dataType %>&available=true'" <%if (available) out.write("disabled=\"disabled\""); %>/>
				<input type="button" value="Not Available" onclick="location.href='?type=dataType&dataType=<%=dataType %>'" <%if (!available) out.write("disabled=\"disabled\""); %>/>
			</td><td>
				<%if(!available) {
					String workflow = "";
					if (dataType.equals("qa")) {
						workflow = "qa_update_auto";
					} else if (dataType.equals("fprint")) {
						workflow = "fprint_upload";
					}%>
					<input type="button" value="Run Jobs" onclick="executeMultiWorkflow('<%=workflow %>');" />
				<%} else { %>
					<script type="text/javascript">
						$(document).ready(function() {
							var path = "<%=request.getContextPath() %>/admin/actions/lastupdate";
							$(".last_update").each(function(index, value) {
								var id = $(value).attr("id");
								$.get(path, {id : id, dataType : "<%=dataType %>"}, function(data) {
									$("#" + id).html(data);
								}, "html");
							});
						});
					</script>
				<%} %>
			</td></tr>
			<tr>
				<th>AIPid</th>
				<th>
					<%if(!available) { %>
						<input type="button" value="Select All" onclick="$('input:checkbox').prop('checked', true);" />
					<%} else {%>
						LAST UPDATE
					<%} %>
				</th>
			</tr>
			<%
			TableColouringManager tcm = new TableColouringManager();
			for (Object id : result) { %>
				<tr class="<%=tcm.getColouringClass() %>">
					<td><a href="<%=request.getContextPath() %>/access/viewer?id=<%=id %>" class="coloured"><%= id %></a></td>
					<td class="last_update" id="<%=id %>">
						<%if (!available) { %>
							<input type="checkbox" value="<%=id %>" />
						<%} else { %>
							<img id="dc_search_loader" src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_navy.gif" />
						<%} %>
					</td>
				</tr>
				<%tcm.newRow();
			} %>
		</table>
	</div>
</div>
