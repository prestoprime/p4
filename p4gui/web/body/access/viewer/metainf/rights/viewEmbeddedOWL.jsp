<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />
<jsp:useBean id="owl" class="java.lang.String" scope="request" />

<div id="access_embedded_owl">

</div>

<script type="text/javascript">
	LoadXMLString("access_embedded_owl", '<%=owl %>');
</script>