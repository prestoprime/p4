<jsp:useBean id="terms" class="java.lang.String" scope="request" />

<%request.setAttribute("title", "Terms of Use for AV Materials"); %>

<%=terms %>

<hr width="80%"/>

<div class="panels_container">
	<div class="panel">
		<form method="POST" action="<%=request.getContextPath() %>/p4service/request">
			<input type="hidden" name="p4service" value="<%=request.getParameter("p4service") %>" />
			<input type="hidden" name="role" value="<%=request.getParameter("role") %>" />
			I accept the terms of use for this P4 Service:&nbsp;&nbsp;&nbsp;
			<input type="radio" name="agree" value="yes" onclick="displayRequestServiceButton(true)"/>yes&nbsp;&nbsp;
			<input type="radio" name="agree" value="no" onclick="displayRequestServiceButton(false)" checked="checked" />no<br/>
			<input id="request_service_button" type="submit" value="Request P4 Service" style="display: none;"/>
		</form>
	</div>
</div>
<br/>
<br/>

<script type="text/javascript">
	function displayRequestServiceButton(visible) {
		if (visible) {
			$("#request_service_button").css("display", "inline");
		} else {
			$("#request_service_button").css("display", "none");
		}
	}
</script>