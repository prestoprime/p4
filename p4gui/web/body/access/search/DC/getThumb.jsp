<%@page import="eu.prestoprime.p4gui.connection.AccessConnection"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<img src="<%=AccessConnection.getThumbPath(user.getCurrentP4Service(), request.getParameter("id")) %>" width="120px" />