<%@page import="eu.prestoprime.p4gui.services.auth.RoleManager"%>
<%@page import="eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>PrestoPRIME Preservation Platform - Access GUI</title>
		
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/index.css" />
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/body/body.css" />
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/resources/XMLdisplay/XMLdisplay.css" />
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/resources/tooltip/jquery.tooltip.css" />
		
		<script type="text/javascript" src="<%=request.getContextPath() %>/resources/jquery-1.6.2.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath() %>/resources/jquery.tools.min.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath() %>/resources/XMLdisplay/XMLdisplay.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath() %>/resources/tooltip/jquery.tooltip.min.js"></script>
	</head>
	<body>
		<div id="head">
			<jsp:include page="head/head.jsp" />
		</div>
		<div id="body">
			<%
			String body = request.getParameter("_b");
			if (body==null || body.equals("") || body.equals("null"))
				body = "/body/home/home.jsp";
			request.setAttribute("body", body);
			%>
			<jsp:include page="${body }" />
		</div>
		<div id="foot">
			<jsp:include page="foot/foot.jsp" />
		</div>

		<%
		String title = (String) request.getAttribute("title");
		if (title == null || title.equals("null"))
			title = request.getRequestURI();
		if (title.length() > 30) {
			title = title.substring(0, 29) + "...";
		}
		%>
		<script type="text/javascript">
			//set page title
			document.getElementById("pagetitle").innerHTML = "<%=title.toString() %>";
			
			//set body height
			$(document).ready(function() {
				$("#body").height($(window).height()-155);
			});
			$(window).resize(function() {
				$("#body").height($(window).height()-155);
			});
		</script>
	</body>
</html>