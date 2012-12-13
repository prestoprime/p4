<%@page import="eu.prestoprime.p4gui.connection.WorkflowConnection"%>
<%@page import="eu.prestoprime.p4gui.connection.WorkflowConnection.P4Workflow"%>

<table>
	<tr><td>
		<input type="text" id="param" />
	</td><td>
		<button onclick="addParam('text');">Add String Param</button><br/>
		<button onclick="addParam('file');">Add File Param</button>
	</td></tr>
</table>

<form method="POST" action="<%=request.getContextPath() %>/wf/execute" enctype="multipart/form-data">
	<select name="wfid">
		<%String workflow = "nothing..."; %>
			<option value="<%=workflow %>"><%=workflow %></option>
	</select>
	<table id="params" class="coloured">
		
	</table>
	<input type="submit" />
</form>

<script type="text/javascript">
	function addParam(type) {
		var name = $('#param').val();
		var input = $('<tr><th>' + name + '</th><td><input type="' + type + '" name="' + name + '" /></td></tr>');
		$('#params').append(input);
		$('#param').val('');
	}
</script>