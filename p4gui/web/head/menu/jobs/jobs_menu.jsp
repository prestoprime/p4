<%@page import="eu.prestoprime.p4gui.connection.WorkflowConnection"%>
<%@page import="eu.prestoprime.p4gui.connection.AdminConnection"%>
<%@page import="eu.prestoprime.p4gui.model.JobList.Job"%>
<%@page import="eu.prestoprime.p4gui.model.JobList"%>
<%@page import="java.util.List"%>
<%@page import="eu.prestoprime.p4gui.util.TableColouringManager"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />
 
<%
JobList jobList;
if (user.getCurrentP4Service() != null) {
	jobList = WorkflowConnection.getMyJobs(user.getCurrentP4Service());
} else {
	jobList = new JobList();
}
List<Job> jobs = jobList.getJobs();
TableColouringManager tcm = new TableColouringManager();
%>
	
<div class="minititle">
	Your Jobs
</div>
<table class="centered coloured">
	<%if (jobs.size() == 0) { %>
		<tr class="odd">
			<td>There are no running jobs...</td>
		</tr>
	<%} else { %>
	<tr style="text-align: center;"><th>JobID</th><th>Status</th><th>%</th></tr>
	<%int counter = 0;
	for (Job job : jobs) {
		counter++;%>
		<tr class="<%=tcm.getColouringClass() %>">
			<td><a class="coloured" href="<%=request.getContextPath() %>/admin/job?id=<%=job.getJobID() %>"><%=job.getJobID() %></a></td>
			<td class="<%=job.getStatus().toString().toLowerCase() %>"><%=job.getStatus() %></td>
			<td><%=(int) (job.getLastCompletedStep() * 1f / job.getTotalSteps() * 100f) %>%</td>
		</tr>
		<%if (counter > 15)
			break;
		tcm.newRow();
	}
	} %>
</table>


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