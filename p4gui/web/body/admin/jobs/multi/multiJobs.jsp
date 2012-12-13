<%@page import="java.util.Set"%>
<%@page import="java.util.TreeSet"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="eu.prestoprime.p4gui.model.JobList.Job"%>
<%@page import="java.util.Iterator"%>
<%@page import="eu.prestoprime.p4gui.util.TableColouringManager"%>
<%@page import="java.util.List"%>

<jsp:useBean id="jobList" class="eu.prestoprime.p4gui.model.JobList" scope="request" />

<%
request.setAttribute("title", "Jobs Monitoring");

String filter = request.getParameter("filter");
if (filter == null)
	filter = "all";
%>

<div id="top_menu">
	<ul>
		<li<%if(filter.equals("all")) out.write(" class='top_menu_current'"); %>><a href="?filter=all">All</a></li>
		<li<%if(filter.equals("waiting")) out.write(" class='top_menu_current'"); %>><a href="?filter=waiting">Waiting</a></li>
		<li<%if(filter.equals("running")) out.write(" class='top_menu_current'"); %>><a href="?filter=running">Running</a></li>
		<li<%if(filter.equals("failed")) out.write(" class='top_menu_current'"); %>><a href="?filter=failed">Failed</a></li>
		<li<%if(filter.equals("completed")) out.write(" class='top_menu_current'"); %>><a href="?filter=completed">Completed</a></li>
	</ul>
</div>

<%
List<Job> jobs = jobList.getJobs();
Set<String> workflows = new TreeSet<String>();
for (Job job : jobs)
	workflows.add(job.getWfID());

for (String wfID : workflows) { %>
	<div style="display: inline-block;"><input type="checkbox" value="<%=wfID %>" onchange="updateJobsList('jobs_table_container')" checked="checked" /><%=wfID %>&nbsp;</div>
<%} %>

<script type="text/javascript">
	function updateJobsList(div) {
		var wf = [];
		$("input:checkbox:checked").each(function(index) {
			wf.push($(this).val());
		});
		console.log(wf);
		$("#" + div + " tr").each(function(index) {
			var curwf = $(this).find("td:nth-child(4)").text();
			if (index != 0) {
				if ($.inArray(curwf, wf) > -1) {	
					$(this).fadeIn();
					console.log(curwf + ": yes");
				} else {
					$(this).fadeOut();
					console.log(curwf + ": no");
				}
			}
		});
	}
</script>

<div id="jobs_table_container">
	<jsp:include page="jobsTable.jsp" />
</div>

<script type="text/javascript">
	function deleteWfStatus(jobID) {
		var path = "<%=request.getContextPath() %>/admin/job/delete";
		$.post(path, {jobID: jobID}, function(data) {
			$("#row_" + jobID).fadeOut(1600);
		});
	}
	$(document).ready(function() {
		setInterval(function() {
			var path = "<%=request.getContextPath() %>/admin/jobs";
			$.get(path, {table : "on"}, function(data) {
				$("#jobs_table_container").html(data);
				updateJobsList("jobs_table_container");
			}, "html");
		}, 10000);
	});
</script>

<style type="text/css">
.completed {
	background-color: #4AA02C;
	font-weight: bolder;
}
.failed {
	background-color: #F62817;
	font-weight: bolder;
}
.running {
	background-color: #FFE87C;
	font-weight: bolder;
}
.waiting {
	background-color: #E0FFFF;
	font-weight: bolder;
}
</style>