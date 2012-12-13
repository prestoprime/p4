<%@page import="eu.prestoprime.p4gui.util.TableColouringManager"%>
<%@page import="eu.prestoprime.model.workflow.WfStatus.Params.DParamFile"%>
<%@page import="eu.prestoprime.model.workflow.WfStatus.Params.DParamString"%>
<%@page import="eu.prestoprime.model.workflow.WfStatus.Params.SParam"%>
<%@page import="eu.prestoprime.model.workflow.StatusType"%>
<%@page import="eu.prestoprime.p4gui.P4GUI"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.awt.Color"%>
<%@page import="java.util.Random"%>
<%@page import="eu.prestoprime.model.workflow.WfStatus.TimeTable.TaskReport"%>
<%@page import="java.util.List"%>

<jsp:useBean id="wfStatus" class="eu.prestoprime.model.workflow.WfStatus" scope="request" />
<%TableColouringManager tcm = new TableColouringManager(); %>

<div id="top_menu">
	<ul>
		<li id="top_menu_1" onclick="javascript:setPanel(1);"><a>TimeTable</a></li>
		<li id="top_menu_2" onclick="javascript:setPanel(2);"><a>Params</a></li>
		<li id="top_menu_3" onclick="javascript:setPanel(3);"><a>Result</a></li>
	</ul>
	<script type="text/javascript">
		function setPanel(i) {
			var n = 3;
			for (var t = 1; t <= n; t++) {
				$("#top_menu_"+t).removeClass("top_menu_current");
				$("#panel_"+t).css("display", "none");
			}
			$("#top_menu_"+i).addClass("top_menu_current");
			$("#panel_"+i).css("display","inline");
		}
		$(document).ready(function() {
			setPanel(1);
		});
	</script>
</div>

<div id="panel_1">
	<div class="subtitle">
		Workflow: <%=wfStatus.getWorkflow() %><br/>
		Progress: <%=wfStatus.getLastCompletedStep() * 1f / wfStatus.getTotalSteps() * 100f %>%<br/>
		Status: <%=wfStatus.getStatus() %><br/>
		<%if (wfStatus.getStartup() != null) { %>
			Startup: <%=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(wfStatus.getStartup().toGregorianCalendar().getTime()) %><br/>
		<%} %>
		<%if (wfStatus.getDuration() != null) { %>
			Duration: <%=wfStatus.getDuration() %> ms
		<%} %>
	</div>
	<%if (wfStatus.getStartup() != null && wfStatus.getTimeTable() != null && wfStatus.getTimeTable().getTaskReport().size() != 0) { %>
		<div class="minititle">TimeTable</div>
		<%List<TaskReport> taskReports = wfStatus.getTimeTable().getTaskReport();
		long mainStartup = wfStatus.getStartup().toGregorianCalendar().getTimeInMillis();
		long mainDuration = wfStatus.getDuration();
		int step = 1;
		String rgb = "FFD46E";
		for (TaskReport taskReport : taskReports) {
			if (taskReport.getStep() != step) {
				step = taskReport.getStep();
		
				Random rand = new Random();
				float r = rand.nextFloat() * 0.5f + 0.4f;
				float g = rand.nextFloat() * 0.5f + 0.4f;
				float b = rand.nextFloat() * 0.5f + 0.4f;
		
				Color color = new Color(r, g, b);
				rgb = Integer.toHexString(color.getRGB());
				rgb = rgb.substring(2, rgb.length());%>
				<div style="width: 100%; height: 1px; background-color: red;"></div>
			<%}
			long start = taskReport.getStartup().toGregorianCalendar().getTimeInMillis() - mainStartup;
			long duration = taskReport.getDuration(); %>
			<div class="task_report_graph" style="margin-left: <%=start %>px; width: <%=duration %>px; height: 20px; background-color: #<%=rgb %>; border: solid 2px navy;">
				<div id="task_report_tip_<%=taskReports.indexOf(taskReport) %>" class="tooltip">
					<table>
						<tr><td>Service: </td><td><%=taskReport.getService() %></td></tr>
						<tr><td>Step: </td><td><%=taskReport.getStep() %></td></tr>
						<tr><td>Duration: </td><td><%=taskReport.getDuration() %> ms</td></tr>
					</table>
				</div>
			</div>
		<%} %>
		
		<script type="text/javascript">
		$(document).ready(function() {
			var scale = <%=mainDuration %> / $("#body").width()+1;
			$(".task_report_graph").each(function(key) {
				var div = $(this);
				
				var ml = div.css("margin-left").replace("px", "");
				div.css("margin-left", ml/scale + "px");
				
				var w = div.css("width").replace("px", "");
				div.css("width", w/scale + "px");
		
				div.tooltip({tip: '#task_report_tip_' + key, offset: [10, 2], effect: 'slide'}).dynamic({ bottom: { direction: 'down', bounce: true } });
			});
		});
		</script>
	
		<style type="text/css">
		.tooltip {
			display: none;
			background-color: #FF6600;
			border: 2px solid #20A386;
			padding: 5px;
			font-weight: bolder;
			border-radius: 15px;
		}
		</style>
	<%} %>
</div>

<div id="panel_2">
	<%if (wfStatus.getParams() != null) { %>
		<%if (wfStatus.getParams().getSParam().size() != 0) { %>
			<div style="display: inline-block;">
				<div class="minititle">Static Params</div>
				<table class="coloured" style="width: auto;">
					<tr><th>Key</th><th>Value</th></tr>
					<%for (SParam sParam : wfStatus.getParams().getSParam()) { %>
						<tr class="<%=tcm.getColouringClass() %>"><td><%=sParam.getKey() %></td><td><%=sParam.getValue() %></td></tr>
						<%tcm.newRow(); %>
					<%} %>
				</table>
			</div>
		<%} %>
		<%if (wfStatus.getParams().getDParamString().size() != 0) { %>
			<div style="display: inline-block;">
				<div class="minititle">Dynamic String Params</div>
				<table class="coloured" style="width: auto;">
					<tr><th>Key</th><th>Value</th></tr>
					<%for (DParamString dParamString : wfStatus.getParams().getDParamString()) { %>
						<tr class="<%=tcm.getColouringClass() %>"><td><%=dParamString.getKey() %></td><td><%=dParamString.getValue() %></td></tr>
						<%tcm.newRow(); %>
					<%} %>
				</table>
			</div>
		<%} %>
		<%if (wfStatus.getParams().getDParamFile().size() != 0) { %>
			<div style="display: inline-block;">
				<div class="minititle">Dynamic File Params</div>
				<table class="coloured" style="width: auto;">
					<tr><th>Key</th><th>Value</th></tr>
					<%for (DParamFile dParamFile : wfStatus.getParams().getDParamFile()) { %>
						<tr class="<%=tcm.getColouringClass() %>"><td><%=dParamFile.getKey() %></td><td><%=dParamFile.getValue() %></td></tr>
						<%tcm.newRow(); %>
					<%} %>
				</table>
			</div>
		<%} %>
	<%} %>
</div>

<div id="panel_3">
	<%if (wfStatus.getResult() != null && !wfStatus.getResult().getValue().equals("")) { %>
		<div class="minititle">Result</div>
		<div id="result_preview" style="overflow-x: hide;"></div>
		<script type="text/javascript">
			$(document).ready(function() {
				LoadXMLString("result_preview", '<result><%=wfStatus.getResult().getValue() %></result>');
			});
		</script>
	<%} else { %>
		<script type="text/javascript">
			$(document).ready(function() {
				$("#top_menu_3").css("display", "none");
			});
		</script>
	<%} %>
</div>