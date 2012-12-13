<%@page import="eu.prestoprime.workflow.plugin.WfPlugin.WfService"%>
<%@page import="eu.prestoprime.p4gui.util.TableColouringManager"%>
<%@page import="java.util.Set"%>
<%@page import="eu.prestoprime.workflow.plugin.WfPlugin"%>
<%@page import="java.util.Map"%>

<%
Map<WfPlugin, Set<WfService>> plugins = (Map<WfPlugin, Set<WfService>>) request.getAttribute("plugins");
TableColouringManager tcm = new TableColouringManager();
%>

<div style="text-align: center;">
	<div class="panel">
		<table class="coloured">
			<tr><th>PLUGIN</th><th>SERVICE</th><th>VERSION</th></tr>
			<%
				if (plugins.size() > 0) {
					for (WfPlugin plugin : plugins.keySet()) {
				Set<WfService> tasks = plugins.get(plugin);
				boolean newRow = false;
			%>
					<tr class="<%=tcm.getColouringClass()%>"><td rowspan="<%=tasks.size()%>"><%=plugin.name()%></td>
					<%
						for (WfService task : tasks) {
							if (newRow) {
					%>
					<tr class="<%=tcm.getColouringClass() %>">
						<%} newRow = true; %>
						<td><%=task.name() %></td><td><%=task.version() %></td>
					</tr>
					<%}
					tcm.newRow();
				}
			} %>
		</table>
	</div>
</div>