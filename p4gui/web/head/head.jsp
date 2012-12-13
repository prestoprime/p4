<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/head/head.css" />

<!-- page title -->
<div id="pagetitle" class="title">

</div>
<div id="menu">
	
</div>
<%if (user.isLogged()) { %>

	<script type="text/javascript">
		function executeWorkflow(id, workflow) {
			var path = "<%=request.getContextPath() %>/wf/execute";
			var data = new FormData();
			data.append("wfid", workflow);
			data.append("id", id);
			data.append("dipID", id);
			data.append("aipID", id);
			$.ajax({
				url: path,
				data: data,
				contentType: false,
				processData: false,
				type: 'POST',
			});
		}
		
		function executeMultiWorkflow(workflow) {
			$("input:checkbox:checked").each(function(index, value) {
				executeWorkflow($(value).val(), workflow);
				$(value).attr('checked', false).hide();
			});
		}
	</script>

	<script type="text/javascript">
		//update entire menu
		function updateMenu() {
			var path = "<%=request.getContextPath() %>/head/menu/menu.jsp";
			$.get(path, function(data) {
				var menu = $("#menu");
				//var scroll = div.scrollTop();
				//div.scrollTop(scroll);
				menu.html(data);
			}, "html");
		}
		$(document).ready(function() {
			updateMenu();
			//setInterval("updateMenu();", 30000);
		});
	</script>
<%} %>