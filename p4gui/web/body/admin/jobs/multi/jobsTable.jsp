<%@page import="java.text.SimpleDateFormat"%>
<%@page import="eu.prestoprime.p4gui.model.JobList.Job"%>
<%@page import="eu.prestoprime.p4gui.util.TableColouringManager"%>

<jsp:useBean id="jobList" class="eu.prestoprime.p4gui.model.JobList" scope="request" />

<table class="coloured" style="text-align: center;">
	<tr><th></th><th>JobID</th><th>Status</th><th>Workflow</th><th>Startup</th><th>Duration</th><th>Progress</th><th>Last Completed Service</th></tr>
	
	<%
	TableColouringManager tcm = new TableColouringManager(); 
	for (Job job : jobList.getJobs()) { %>
		<tr id="row_<%=job.getJobID() %>" class="<%=tcm.getColouringClass() %>">
			<td><img src="<%=request.getContextPath() %>/resources/dracma/delete.png" onclick="deleteWfStatus('<%=job.getJobID() %>')" style="width: 40px;"/></td>
			<td><a class="coloured" href="<%=request.getContextPath() %>/admin/job?id=<%=job.getJobID() %>"><%=job.getJobID() %></a></td>
			<td class="<%=job.getStatus().toString().toLowerCase() %>"><%=job.getStatus() %></td>
			<td><%=job.getWfID() %></td>
			<td><%=job.getStartup() == null ? "N/A" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(job.getStartup().toGregorianCalendar().getTime()) %></td>
			<td><%=job.getDuration() == 0L ? "N/A" : job.getDuration() + " ms" %></td>
			<td><%=(int) (job.getLastCompletedStep() * 1f / job.getTotalSteps() * 100f) %>%</td>
			<td><%=job.getLastCompletedService() == null ? "N/A" : job.getLastCompletedService() %></td>
		</tr>
		<%tcm.newRow();
	}
	%>
</table>