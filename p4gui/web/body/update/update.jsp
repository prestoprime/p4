<%@page import="eu.prestoprime.p4gui.util.TableColouringManager"%>
<%@page import="eu.prestoprime.model.workflow.WfType"%>
<%@page import="eu.prestoprime.model.workflow.WfDescriptor.Workflows.Workflow"%>
<%@page import="eu.prestoprime.p4gui.connection.WorkflowConnection"%>
<%@page import="eu.prestoprime.model.workflow.WfDescriptor"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<%
request.setAttribute("title", "AIP Update");

String aipID = request.getParameter("id");
WfDescriptor descriptor = (WfDescriptor) request.getAttribute("wfDescriptor");

TableColouringManager tcm = new TableColouringManager();
%>

<form id="update_form" method="POST" action="<%=request.getContextPath() %>/update" enctype="multipart/form-data">
	<div style="text-align: center;">
		<div class="panel">
			<table class="coloured">
				<tr><td></td><th>WORKFLOW</th><th>DESCRIPTION</th></tr>
				<%for (Workflow workflow : descriptor.getWorkflows().getWorkflow()) {
					if (workflow.getType() != null && workflow.getType().equals(WfType.UPDATE)) { %>
						<tr class="<%=tcm.getColouringClass() %>">
							<td><input type="radio" name="wfID" value="<%=workflow.getId() %>" checked="checked"/></td>
							<th><%=workflow.getId() %></th>
							<td><% if (workflow.getDescription() == null) out.write("N/A"); else out.write(workflow.getDescription().getValue()); %></td>
						</tr>
					<%tcm.newRow();
					}
				} %>
			</table>
		</div>
		<br/>
		<div class="panel">
			<table class="coloured">
				<tr class="odd">	
					<td align="right"><b>AIP ID *</b></td>
					<td align="left"><input type="text" name="aipID" value="<%=aipID == null? "" : aipID %>" /></td>
				</tr>
				<tr class="pair">
					<td align="right"><b>File *</b></td>
					<td><input type="file" name="updateFile" /></td>
				</tr>
				<tr class="odd">
					<td colspan="2" align="center"><input type="submit" value="Update" /></td>
				</tr>
			</table>
		</div>
	</div>
</form>
