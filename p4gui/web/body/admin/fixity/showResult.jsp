<%@page import="eu.prestoprime.p4gui.admin.fixity.FixityCheckResponse"%>

<%FixityCheckResponse result = (FixityCheckResponse) request.getAttribute("result");%>

<td><%=result.getId() %></td>
<td>
	<%if (result.getCode() != 200) out.print("FAILED"); else out.print("PASSED");%>
</td>
<td>
	<%if (result.getCode() != 200) { %>
		<input type="button" value="Restore from LTO" onclick="javascript:restoreFromLTO('<%=result.getId() %>', '<%=result.getURN() %>', '<%=result.getPath() %>'); this.style.visibility='hidden';"/>
		<script type="text/javascript">
			$("#<%=result.getId() %>_row").addClass("error");
			var et = "<div><%=result.getURN() %></div>";
			$("#<%=result.getId() %>_row").et(et, "hover", {color: "sand", position: "bottom", align: "center"});
		</script>
	<%} else { %>
		<input type="button"  style="visibility: hidden;"/>
		<script type="text/javascript">
			$("#<%=result.getId() %>_row").addClass("success");
		</script>
	<%} %>
</td>