<%@page import="eu.prestoprime.p4gui.connection.CommonConnection"%>
<%@page import="java.util.Iterator"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />
<jsp:useBean id="masterfileName" class="java.lang.String" scope="request" />

<%
Iterator<String> it = CommonConnection.getAvailableFileList(user.getCurrentP4Service()).iterator();
StringBuffer sb = new StringBuffer();
while (it.hasNext()) {
	String tmp = it.next();
	sb.append("<option value='" + tmp + "'>" + tmp + "</option>");
}
%>

<script type="text/javascript" src="<%=request.getContextPath() %>/resources/jquery-1.6.2.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$(".resource_href_select", window.parent.document).each(function() {
			$(this).empty().append("<%=sb.toString() %>");
		});
		$("#masterfile_loader", window.parent.document).css("display", "none");
		$("#masterfile_name", window.parent.document).html("<%=masterfileName %>");
	});
</script>

<jsp:include page="/body/ingest/masterfile/masterfile.jsp" />