<%@page import="eu.prestoprime.p4gui.model.P4Service"%>
<%@page import="eu.prestoprime.p4gui.util.TableColouringManager"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<!-- p4services menu -->
<div class="menu" style="margin-right: 20px;">
	<a class="title">
		<img src="<%=request.getContextPath() %>/resources/my_p4.png" />
	</a>
	<div id="p4ws_container" class="menu_container">
		<jsp:include page="p4services/p4services_menu.jsp" />
	</div>
</div>

<!-- workflows menu -->
<div class="menu">
	<a class="title">
		<img src="<%=request.getContextPath() %>/resources/workflow.png" />
	</a>
	<div id="p4ws_container" class="menu_container">
		<jsp:include page="jobs/jobs_menu.jsp" />
	</div>
</div>