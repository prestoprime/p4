<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />
<jsp:useBean id="graph_path" class="java.lang.String" scope="request" />

<%
String id = request.getParameter("id");
String dataType = "rights";
%>

<div style="width: 100%; text-align: right;">
	<form method="GET" action="<%=request.getContextPath() %>/access/viewer/datatype" target="_blank">
		<input type="hidden" name="id" value="<%=id %>" />
		<input type="hidden" name="dataType" value="<%=dataType %>" />
		<input type="submit" value="Download" />
	</form>
</div>
<div style="text-align: center;">
	<img src="<%=graph_path %>" style="max-width: 100%; max-height: 100%;" />
</div>