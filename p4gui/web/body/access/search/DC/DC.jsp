<%@page import="eu.prestoprime.p4gui.util.parse.DCField"%>
<%@page import="eu.prestoprime.p4gui.P4GUI"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>

<div id="<%=request.getParameter("rid") %>"></div>

<%
Object o = request.getAttribute("search_result");
ArrayList<String> records;
if (o instanceof ArrayList<?>)
	records = (ArrayList<String>) o;
else {
	records = new ArrayList<String>();
}
String js_array = "";
if (!records.isEmpty()) {
	Iterator<String> itRecords = records.iterator();
	while (itRecords.hasNext()) {
		String tmpRecord = itRecords.next();
		js_array = js_array + "'" + tmpRecord + "'";
		if (itRecords.hasNext())
			js_array = js_array + ", ";%>
		<div class="search_result">
			<table>
				<tr>
					<td id="<%=tmpRecord %>_thumb" valign="top">
						<img id="dc_search_loader" src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_orange.gif" align="right"/><br/>
					</td>
					<td>
						AIP: <a class="coloured" href="<%=request.getContextPath() %>/access/viewer?id=<%=tmpRecord %>"><%=tmpRecord %></a><br/>
						<div id="<%=tmpRecord %>_dc">
							
						</div>
					</td>
				</tr>
			</table>
		</div>
	<%}
}%>
<script type="text/javascript">
	var id_array = new Array(<%=js_array %>);
	startAjaxDownloads(id_array);
</script>