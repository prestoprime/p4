<%@page import="eu.prestoprime.p4gui.util.TableColouringManager"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>

<img id="fixity_check_loader" src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_orange.gif" style="position: fixed; top: 120px; left: 160px; display: none;"/>

<%
request.setAttribute("title", "Fixity Checks");
ArrayList<String> records = (ArrayList<String>) request.getAttribute("records");
Iterator<String> it = records.iterator();
TableColouringManager tcm = new TableColouringManager();
if (it.hasNext()) { %>
<div class="panels_container">
	<div class="panel">
		<table class="coloured" style="width: 800px;">
			<tr><th>AIP ID</th><th>FIXITY CHECK</th><th width="120px">ACTION</th></tr>
		<%while (it.hasNext()) {
			String tmpAipId = it.next(); %>
				<tr id="<%=tmpAipId %>_row" class="<%=tcm.getColouringClass() %>">
					<td><%=tmpAipId %></td>
					<td></td>
					<td><input type="button" value="Check" onclick="javascript:check('<%=tmpAipId %>'); this.style.visibility='hidden';"/></td>
				</tr>
			<%
			tcm.newRow();
		} %>
		</table>
	</div>
</div>
<%} %>

<script type="text/javascript">
	function check(tmp_id) {
		$("#fixity_check_loader").css("display", "inline");
		var path = "<%=request.getContextPath() %>/admin/fixity/check.do";
		$.get(path, {"id": tmp_id}, function(data) {
			$("#"+tmp_id+"_row").html(data);
			try {
				$("#fixity_check_loader").css("display", "none");
				var script = data.split("<script type=\"text/javascript\">")[1].split("</script")[0];
				eval(script);
			} catch (err) {
				
			}
		});
	}
	
	function restoreFromLTO(id, from, to) {
		$("#fixity_check_loader").css("display", "inline");
		var path = "<%=request.getContextPath() %>/admin/fixity/restoreFromLTO.do";
		$.get(path, {id: id, from: from, to: to}, function(data) {
			$("#"+id+"_row").html(data);
			try {
				$("#fixity_check_loader").css("display", "none");
				var script = data.split("<script type=\"text/javascript\">")[1].split("</script")[0];
				eval(script);
			} catch (err) {
				
			}
		}, "html");
	}
</script>