<%request.setAttribute("title", "SIP Ingest"); %>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/body/ingest/ingest.css" />

<style>
.ingest_choose_button {
	cursor: pointer;
	width: 200px;
}
#ingest_choose_container {
	text-align: center;
	width: 100%;
	font-family: helvetica;
	font-size: 30px;
}
</style>
<br/>
<br/>
<br/>
<table id="ingest_choose_container">
	<tr>
		<td>
			<img class="ingest_choose_button" src="<%=request.getContextPath() %>/resources/ingest/new_up.png" onclick="$('#create_new').submit();" />
		</td>
		<td>	
			<img class="ingest_choose_button" src="<%=request.getContextPath() %>/resources/ingest/sip_up.png" onclick="$('#edit_existing').click();" />
		</td>
		<td>
			<img class="ingest_choose_button" src="<%=request.getContextPath() %>/resources/ingest/sip_up.png" onclick="$('#ingest_existing').click();" />
		</td>
	</tr>

	<tr style="height: 80px">
		<td>
			Create new SIP
		</td>
		<td>
			Edit existing SIP
		</td>
		<td>
			Ingest existing SIP
		</td>
	</tr>
</table>

<form id="create_new" method="POST" action="<%=request.getContextPath() %>/ingest/initialize" enctype="multipart/form-data" style="display: none;">
	<input type="hidden" name="action" value="new" />
</form>

<form id="edit_existing_form" method="POST" action="<%=request.getContextPath() %>/ingest/initialize" enctype="multipart/form-data" style="display: none;">
	<input type="hidden" name="action" value="edit" />
	<input id="edit_existing" name="sipFile" type="file" onchange="submit()" />
</form>

<form id="ingest_existing_form" method="POST" action="<%=request.getContextPath() %>/ingest/initialize" enctype="multipart/form-data" style="display: none;">
	<input type="hidden" name="action" value="ingest" />
	<input id="ingest_existing" name="sipFile" type="file" onchange="submit()" />
</form>