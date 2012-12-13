<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<jsp:useBean id="source" class="java.lang.String" scope="request" />

<div id="hiddenEmbSourceDiv" style="display:none;">
	<%=source %>
</div>
<script type="text/javascript">
	LoadXMLString("metainf_panel_3", $('div#hiddenEmbSourceDiv').html());
</script>
