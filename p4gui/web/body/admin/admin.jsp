<%@page import="eu.prestoprime.p4gui.services.auth.RoleManager.USER_ROLE"%>
<%@page import="eu.prestoprime.p4gui.P4GUI"%>
<%request.setAttribute("title", "Admin panel"); %>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/body/admin/admin.css" />

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<div id="admin">
	<div id="admin_persistence" class="admin_icon">
		<img src="<%=request.getContextPath() %>/resources/admin/p4_service_admin.png" /><br/>
		<span>P4 Service Administration</span>
		<script type="text/javascript">
			$(document).ready(function(){
				var tooltip_content = '<div class="admin_icon">';
 				tooltip_content += '<a href="<%=request.getContextPath() %>/?_b=body/pageEmbedded.jsp&page=/p4db">';
				tooltip_content += '<img src="<%=request.getContextPath() %>/resources/admin/database.png" /></a>';
 				tooltip_content += '<br/><span>Persistence DB</span></div><br/>';
 				
 				tooltip_content += '<div class="admin_icon">';
 				tooltip_content += '<a href="<%=request.getContextPath() %>/?_b=body/pageEmbedded.jsp&page=/solr/admin">';
				tooltip_content += '<img src="<%=request.getContextPath() %>/resources/admin/search.png" /></a>';
 				tooltip_content += '<br/><span>Search Engine</span></div><br/>';
 				
 				$("#admin_persistence").et(tooltip_content, "click", {color: "sand", position: "bottom", align: "center"});
			});
		</script>
	</div>
	
	<div id="admin_preservation" class="admin_icon">
		<img src="<%=request.getContextPath() %>/resources/admin/preservation_actions.png" />
		<span>Preservation Actions</span>
		<script type="text/javascript">
			$(document).ready(function(){
				var tooltip_content = "<div style=\"text-align: center;\">";
				
				tooltip_content += '<div class="admin_icon">';
				tooltip_content += '<a href="<%=request.getContextPath() %>/admin/actions?type=fixityRisk">';
 				tooltip_content += '<img src="<%=request.getContextPath() %>/resources/admin/fixity.jpg" /></a>';
 				tooltip_content += '<br/><span>Integrity Checks</span></div>';

 				tooltip_content += '<div class="admin_icon">';
 				tooltip_content += '<a href="<%=request.getContextPath() %>/admin/actions?type=formatRisk">';
 				tooltip_content += '<img src="<%=request.getContextPath() %>/resources/admin/format.jpg" /></a>';
 				tooltip_content += '<br/><span>Format Migration (Uncompressed)</span></div>';
 				
 				tooltip_content += "</div><br/><div style=\"text-align: center;\">";
 				
 				tooltip_content += '<div class="admin_icon">';
 				tooltip_content += '<a href="/fab4/fab4.jnlp">';
 				tooltip_content += '<img src="<%=request.getContextPath() %>/resources/fab4_logo.png" /></a>';
 				tooltip_content += '<br/><span>Multivalent</span></div>';
 				
 				tooltip_content += '<div class="admin_icon">';
 				tooltip_content += '<a href="<%=request.getContextPath() %>/admin/actions?type=dataType&dataType=qa">';
 				tooltip_content += '<img src="<%=request.getContextPath() %>/resources/admin/qa.png" /></a>';
 				tooltip_content += '<br/><span>Quality Assessment</span></div>';

 				tooltip_content += '<div class="admin_icon">';
 				tooltip_content += '<a href="<%=request.getContextPath() %>/admin/actions?type=dataType&dataType=fprint">';
 				tooltip_content += '<img src="<%=request.getContextPath() %>/resources/admin/fingerprint.png" /></a>';
 				tooltip_content += '<br/><span>FingerPrint</span></div>';
 				
 				tooltip_content += "</div>";
 				
				$("#admin_preservation").et(tooltip_content, "click", {color: "sand", position: "bottom", align: "center"});
			});
		</script>
	</div>
	
	<div class="admin_icon">
	<a href="<%=request.getContextPath() %>/admin/jobs">
		<img src="<%=request.getContextPath() %>/resources/admin/jobs_monitoring.png" /><br/>
	</a>
		<span>Jobs Monitoring</span>
	</div>
	 				
 <br/>

 <!-- 	
 	<div class="admin_icon">
		<a href="<%=request.getContextPath() %>/admin/clearDracma.do">
			<img src="<%=request.getContextPath() %>/resources/admin/dracma.png" /><br/>
		</a>
		<span>DRACMA</span>
	</div>
-->
<%if (user.getCurrentP4Service().getRole().getLevel() >= USER_ROLE.superadmin.getLevel())	
	{ %>
	<br/><hr width="80%" /><br/>
	
	<div class="admin_icon">
	<a href="<%=request.getContextPath() %>/admin/properties">
		<img src="<%=request.getContextPath() %>/resources/admin/properties.png" /><br/>
	</a>
		<span>Properties</span>
	</div>
	
	<div class="admin_icon">
	<a href="<%=request.getContextPath() %>/admin/users">
		<img src="<%=request.getContextPath() %>/resources/admin/usermanagement.png" /><br/>
	</a>
		<span>Users</span>
	</div>
	<div id="rebuild_index_admin" class="admin_icon">
		<img src="<%=request.getContextPath() %>/resources/admin/rebuildIndex.png" /><br/>
		<span>Indexes</span>
		<script type="text/javascript">
			var tooltip_content = "<div style=\"text-align: center;\">";
			
			tooltip_content += '<div class="admin_icon">';
			tooltip_content += '<a href="<%=request.getContextPath() %>/admin/actions?type=rebuildSolrIndex">';
			tooltip_content += '<img src="<%=request.getContextPath() %>/resources/admin/solr.png" /></a>';
			tooltip_content += '<br/><span>Rebuild Solr Index</span></div>';

			tooltip_content += '<div class="admin_icon">';
			tooltip_content += '<a href="<%=request.getContextPath() %>/admin/actions?type=rebuildRightsIndex">';
			tooltip_content += '<img src="<%=request.getContextPath() %>/resources/admin/mpeg_21_mco.png" /></a>';
			tooltip_content += '<br/><span>Rebuild Rights Index</span></div>';
			
			tooltip_content += '</div>';
						
 			$("#rebuild_index_admin").et(tooltip_content, "click", {color: "sand", position: "bottom", align: "center"});
		</script>
	</div>
<%} %>
</div>