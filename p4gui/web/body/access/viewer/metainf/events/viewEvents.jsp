<%@page import="eu.prestoprime.p4gui.util.TableColouringManager"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="eu.prestoprime.p4gui.model.Event"%>
<%@page import="java.util.List"%>

<% List<Event> events = (List<Event>) request.getAttribute("events"); %>

<table class="coloured">
	<tr><th>DATE</th><th>TYPE</th><th>DETAILS</th></tr>
	<%if (events != null && events.size() > 0) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		TableColouringManager tcm = new TableColouringManager(); %>
		<%for (Event event : events) {
			if (event.getDateTime() != null && event.getType() != null) { %>
				<tr class="<%=tcm.getColouringClass() %>">
					<td><%=df.format(event.getDateTime().getTime()) %></td>
					<td><%=event.getType() %></td>
					<td><%=event.getDetail() == null? "N/A" : event.getDetail() %></td>	
				</tr>
			<%} tcm.newRow(); %>
		<%} %>
	<%} else { %>
		<tr class="odd"><td colspan="3">No registered events for current DIP</td></tr>
	<%} %>
</table>