<%@page import="eu.prestoprime.p4gui.util.TableColouringManager"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Map.Entry"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />
<jsp:useBean id="resources" class="java.util.HashMap" scope="request" />

<%
Map<String, List<String>> res = (Map<String, List<String>>) resources;
TableColouringManager tcm = new TableColouringManager();
%>

<table class="coloured">
	<%for (Entry<String, List<String>> entry : res.entrySet()) {
		List<String> resourcesHref = entry.getValue();
		if (resourcesHref.size() > 0) { %>
			<tr class="<%=tcm.getColouringClass() %>">
				<td rowspan="<%=resourcesHref.size() %>"><%=entry.getKey() %></td>
			<%Iterator<String> resourcesHrefIt = resourcesHref.iterator();
			while (resourcesHrefIt.hasNext()) {
				String resourceHref = resourcesHrefIt.next().replace("P4_PH", user.getCurrentP4Service().getURL().toString());%>
				<td><a href="<%=request.getContextPath() %>/access/viewer/resource?url=<%=resourceHref %>" target="_blank" class="coloured"><%=resourceHref %></a></td>
			</tr>
				<%if (resourcesHrefIt.hasNext()) { %>
			<tr class="<%=tcm.getColouringClass() %>">
				<%} %>
			<%} %>
		<%}
		tcm.newRow(); %>
	<%} %>
</table>