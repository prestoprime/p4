<form method="POST" action="<%=request.getContextPath() %>/ingest/rights" enctype="multipart/form-data">
	<input id="owlFile" type="file" name="owlFile" onchange="submit()"/>
</form>