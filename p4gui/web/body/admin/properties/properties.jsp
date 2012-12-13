<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Properties"%>
<%@page import="eu.prestoprime.p4gui.P4GUI"%>

<%
Properties properties = new Properties();
Enumeration<Object> keys = properties.keys();

if (keys.hasMoreElements()) { %>
<div>	
	<form method="GET" action="<%=request.getContextPath() %>/admin/properties">
		<%while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = properties.getProperty(key);
			%>
			<%=key %><input type="text" name="<%=key %>" value="<%=value %>" /><br/>
		<%} %>
		<input type="submit" value="Apply" />
	</form>
</div>
<%} %>

<div>
	<form method="GET" action="<%=request.getContextPath() %>/admin/properties">
		&lt;entry key="<input type="text" name="key" />"&gt;<input type="text" name="value" />&lt;/entry&gt;<br/>
		<input type="submit" value="Apply" />
	</form>
</div>