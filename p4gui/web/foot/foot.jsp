<%@page import="eu.prestoprime.p4gui.P4GUI.P4guiProperty"%>
<%@page import="eu.prestoprime.p4gui.P4GUI"%>
<%@page import="eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE"%>
<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/foot/foot.css" />

<div id="foot_container">
	<div id="presto_logo">
		<a href="http://www.prestoprime.eu"><img src="<%=request.getContextPath() %>/foot/pprime_logo.png" /></a>
	</div>
	<div id="foot_menu" align="center"><%int i = 1; %>
		<a href="<%=request.getContextPath() %>/" id="foot_item1" class="foot_item"><span><span>Home</span><b>0<%=i++ %></b></span></a>
<%if (user.getCurrentP4Service() != null) { 
	if (user.getCurrentP4Service().getRole().getLevel() >= USER_ROLE.consumer.getLevel()) { %>
		<a href="<%=request.getContextPath() %>/access/search" id="foot_item2" class="foot_item"><span><span>Access</span><b>0<%=i++ %></b></span></a>
	<%}
	if (user.getCurrentP4Service().getRole().getLevel() >= USER_ROLE.producer.getLevel()) { %>
		<a href="<%=request.getContextPath() %>/ingest" id="foot_item3" class="foot_item"><span><span>Ingest</span><b>0<%=i++ %></b></span></a>
		<a href="<%=request.getContextPath() %>/update" id="foot_item4" class="foot_item"><span><span>Update</span><b>0<%=i++ %></b></span></a>
	<%}
	if (user.getCurrentP4Service().getRole().getLevel() >= USER_ROLE.admin.getLevel()) { %>
		<a href="<%=request.getContextPath() %>/admin" id="foot_item5" class="foot_item"><span><span>Admin</span><b>0<%=i++ %></b></span></a>
	<%}
} %>
		<a href="<%=request.getContextPath() %>/?_b=body/help/help.jsp" id="foot_item6" class="foot_item"><span><span>Help</span><b>0<%=i++ %></b></span></a>
		</div>
	<div id="copyright">
		p4gui&nbsp;<%=P4GUI.getProperty(P4guiProperty.GUI_VERSION) %>&nbsp;-&nbsp;&copy;&nbsp;PrestoPRIME&nbsp;2009-2012
	</div>
	<div id="eurix_logo">
		<a href="http://www.eurixgroup.com"><img src="<%=request.getContextPath() %>/foot/eurix.png" style="width: 120px; height: 80px;" /></a>
	</div>
</div>