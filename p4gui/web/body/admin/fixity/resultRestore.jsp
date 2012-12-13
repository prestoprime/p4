<%
String result = (String) request.getAttribute("result");
String id = request.getParameter("id");
if (result.equals("error")) { %>
	<script type="text/javascript">
		alert("error");
	</script>
<%} else { %>
	<td><%=id %></td>
	<td>RESTORED_FROM_LTO</td>
	<td><input type="button" value="Check" onclick="javascript:check('<%=request.getParameter("id") %>'); this.style.visibility='hidden';"/></td>
	<script type="text/javascript">
		$("#<%=id %>_row").removeClass("error");
	</script>
<%} %>