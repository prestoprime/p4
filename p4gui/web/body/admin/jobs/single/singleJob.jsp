<jsp:useBean id="wfStatus" class="it.eurix.archtools.workflow.jaxb.WfStatus" scope="request" />
<%
request.setAttribute("title", wfStatus.getId());
%>

<div id="job_container">
	<jsp:include page="jobDiv.jsp" />
</div>

<script type="text/javascript">
	$(document).ready(function() {
		setInterval(function() {
			var path = "<%=request.getContextPath() %>/admin/job";
			$.get(path, {div : "on", id: "<%=wfStatus.getId() %>"}, function(data) {
				$("#job_container").html(data);
			}, "html");
		}, 10000);
	});
</script>