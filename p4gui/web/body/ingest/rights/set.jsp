<jsp:useBean id="owl" class="java.lang.String" scope="request" />

<script type="text/javascript" src="<%=request.getContextPath() %>/resources/jquery-1.6.2.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#owl_input", window.parent.document).val('<%=owl %>');
		parent.reloadOWL();
	});
</script>

<jsp:include page="/body/ingest/rights/upload.jsp" />