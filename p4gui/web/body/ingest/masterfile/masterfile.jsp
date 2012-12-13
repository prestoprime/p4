<form method="POST" action="<%=request.getContextPath() %>/ingest/masterfile" enctype="multipart/form-data">
	<input id="masterFile" type="file" name="masterFile" onchange="submit()"/>
</form>