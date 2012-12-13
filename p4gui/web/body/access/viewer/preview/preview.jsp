<%@page import="eu.prestoprime.p4gui.model.P4Service"%>
<%@page import="eu.prestoprime.p4gui.connection.AccessConnection"%>
<%@page import="eu.prestoprime.p4gui.util.parse.Preview"%>
<%@page import="eu.prestoprime.p4gui.P4GUI"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/body/access/viewer/preview/preview.css" />

<%
P4Service service = user.getCurrentP4Service();
String id = request.getParameter("id");
%>

<video id="preview_video" controls width="425" height="340" poster="<%=AccessConnection.getThumbPath(service, id) %>">
	<%
	Iterator<Preview> it = AccessConnection.getPreviewsPath(service, id).iterator();
	while (it.hasNext()) { 
		Preview tmp = it.next();%>
		<source src="<%=tmp.getHref() %>" type="<%=tmp.getMimetype() %>"></source>
	<%} %>
</video>