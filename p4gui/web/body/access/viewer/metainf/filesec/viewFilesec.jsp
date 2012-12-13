<%@page import="eu.prestoprime.p4gui.model.oais.DIP"%>
<%@page import="eu.prestoprime.p4gui.util.TableColouringManager"%>
<%@page import="eu.prestoprime.p4gui.util.parse.Location"%>
<%@page import="eu.prestoprime.p4gui.util.parse.Resource"%>
<%@page import="java.util.Iterator"%>
<%@page import="eu.prestoprime.p4gui.util.Tools"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<%
DIP dip = (DIP) request.getAttribute("dip");

Iterator<Resource> itResources = dip.getResources().iterator();
if (itResources.hasNext()) {
%>
	<table class="coloured">
		<tr><th>ID</th><th>MIMETYPE</th><th>LOCTYPE</th><th>DETAILS</th></tr>
		<%
		TableColouringManager tcm = new TableColouringManager();
		int i = 0;
		while (itResources.hasNext()) {
			Resource tmpResource = itResources.next();
			Iterator<Location> itLocations = tmpResource.getLocations().iterator();
			int rowspan = tmpResource.getLocations().size();
			%>
			<tr class="<%=tcm.getColouringClass() %>">
				<td rowspan="<%=rowspan %>">
					<%=tmpResource.getId() %>
					<br/><%=tmpResource.getChecksumtype() %>: <%=tmpResource.getChecksum() %>
				</td>
				<td rowspan="<%=rowspan %>"><%=tmpResource.getMimetype() %></td>
				<%
				boolean newrow = false;
				while (itLocations.hasNext()) {
					i++;
					Location tmpLocation = itLocations.next();
					if (newrow) {%>
			</tr>
			<tr class="<%=tcm.getColouringClass() %>">
					<%}%>
				<td><%=tmpLocation.getLoctype() %></td>
				<td>
					<a id="location_<%=i %>" class="coloured">
						<%=tmpLocation.getTitle() %>
					</a>
					
					<script type="text/javascript">
						$(document).ready(function() {
							<%if (!tmpLocation.getLoctype().equals("URL")) { %>
								$("#location_<%=i %>").et("<p><span class=\"coloured\"><%=tmpLocation.getHref() %></span></p>", "click", {color: "sand", position: "bottom", align: "end"});
							<%} else { %>
								$("#location_<%=i %>").et("<p><a class=\"coloured\" href=\"<%=tmpLocation.getHref() %>\"><%=tmpLocation.getHref() %></span></p>", "click", {color: "sand", position: "bottom", align: "end"});
							<%} %>
							updateKeyframesSelection();
						});
					</script>
				</td>
					<%if (!newrow) { %>
				<td rowspan="<%=rowspan %>">
					<form method="POST" action="<%=request.getContextPath() %>/access/consumercopy">
						<input type="hidden" name="id" value="<%=dip.getID() %>" />
						<input type="hidden" name="mimetype" value="<%=tmpResource.getMimetype() %>" />
						<input type="submit" value="Export File" />
					</form>
					<%if (tmpResource.getMimetype().equals("application/mxf")) { %>
						<form id="segment_form" method="POST" action="<%=request.getContextPath() %>/access/consumersegment" style="display: none;">
							<input type="hidden" name="id" value="<%=dip.getID() %>" />
							<input type="hidden" name="mimetype" value="<%=tmpResource.getMimetype() %>" />
							<input type="hidden" name="start.frame" id="start_frame_field" />
							<input type="hidden" name="stop.frame" id="stop_frame_field" />
							<input type="submit" id="segment_form_submit" value="Extract Segment" />
						</form>
					<%} %>
				</td>
					<%} %>
					<%newrow = true;
				} %>
			</tr>	
				<%tcm.newRow();
		} %>
	</table>
<%} %>