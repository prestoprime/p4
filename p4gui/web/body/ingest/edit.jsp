<%@page import="java.util.List"%>
<%@page import="java.io.StringWriter"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="javax.xml.transform.stream.StreamResult"%>
<%@page import="org.w3c.dom.Node"%>
<%@page import="javax.xml.transform.dom.DOMSource"%>
<%@page import="javax.xml.transform.TransformerFactory"%>
<%@page import="eu.prestoprime.model.mets.MdSecType"%>
<%@page import="eu.prestoprime.model.mets.AmdSecType"%>
<%@page import="eu.prestoprime.p4gui.connection.CommonConnection"%>
<%@page import="eu.prestoprime.model.workflow.WfType"%>
<%@page import="eu.prestoprime.model.workflow.WfDescriptor.Workflows.Workflow"%>
<%@page import="eu.prestoprime.model.workflow.WfDescriptor"%>
<%@page import="eu.prestoprime.p4gui.connection.WorkflowConnection"%>
<%@page import="eu.prestoprime.p4gui.P4GUI"%>
<%@page import="java.util.ArrayList"%>
<%@page import="eu.prestoprime.p4gui.util.Tools"%>
<%@page import="java.io.File"%>
<%@page import="java.io.IOException"%>
<%@page import="java.nio.file.Files"%>
<%@page import="java.nio.file.Paths"%>
<%@page import="java.nio.file.Path"%>
<%@page import="eu.prestoprime.p4gui.util.parse.Location"%>
<%@page import="java.util.Iterator"%>
<%@page import="eu.prestoprime.p4gui.util.parse.Resource"%>
<%@page import="eu.prestoprime.p4gui.util.parse.DC"%>
<%@page import="eu.prestoprime.p4gui.model.oais.SIP"%>
<%@page import="eu.prestoprime.model.dc.Record"%>
<%@page import="eu.prestoprime.model.mets.MetsType"%>
<%@page import="eu.prestoprime.p4gui.util.TableColouringManager"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />
<jsp:useBean id="currentSip" class="eu.prestoprime.p4gui.model.oais.SIP" scope="session" />

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/body/ingest/ingest.css" />

<%
String action = request.getParameter("action");
if (action == null)
	action = "new";

if (action.equals("ingest")) {
	request.setAttribute("title", "SIP Ingest");
} else {
	request.setAttribute("title", "SIP Edit&Ingest");
}
TableColouringManager tcm = new TableColouringManager();
%>

<div id="top_menu">
	<ul>
		<li id="top_menu_1" onclick="javascript:setIngestEditPanel(1);"><a>General Info</a></li>
		<li id="top_menu_2" onclick="javascript:setIngestEditPanel(2);"><a>Rights</a></li>
		<li id="top_menu_3" onclick="javascript:setIngestEditPanel(3);"><a>Resources</a></li>
		<li id="top_menu_4" onclick="javascript:setIngestEditPanel(4);"><a>Workflow</a></li>
		<li id="top_menu_5" onclick="javascript:<%=action.equals("ingest") ? "" : "getPreview();" %>setIngestEditPanel(5);"><a>Preview</a></li>
	</ul>
	<script type="text/javascript">
		function setIngestEditPanel(i) {
			var n = 5;
			for (var t = 1; t <= n; t++) {
				$("#top_menu_"+t).removeClass("top_menu_current");
				$("#ingest_edit_panel_"+t).css("display", "none");
			}
			$("#top_menu_"+i).addClass("top_menu_current");
			$("#ingest_edit_panel_"+i).css("display","inline");
		}
		$(document).ready(function() {
			<%if (action.equals("ingest")) { %>
				$("#top_menu_1").css("display", "none");
				$("#top_menu_2").css("display", "none");
				$("#top_menu_3").css("display", "none");
				setIngestEditPanel(4);
				setPreview($('div#hiddenSIP').html());
			<%} else { %>
				setIngestEditPanel(1);
			<%} %>
		});
	</script>
</div>
<div id="hiddenSIP" style="display:none;" >
<%= currentSip.getContentAsString(true) %>
</div> 
<%if (action.equals("ingest")) { %>
	<div id="ingest_edit_panel_1" class="panels_container">Not in edit mode...</div>
	<div id="ingest_edit_panel_2" class="panels_container">Not in edit mode...</div>
	<div id="ingest_edit_panel_3" class="panels_container">Not in edit mode...</div>
<%} else { %>
	<form id="edit_form" target="_blank" action="<%=request.getContextPath() %>/ingest/preview" method="POST">
		<div id="ingest_edit_panel_1" class="panels_container">
			<br/>
			<table class="coloured" style="width: 400px;">
				<%
				DC dc = currentSip.getDublinCore();
				Iterator<String> itDc = dc.getKeySet().iterator();
				while (itDc.hasNext()) {
					String key = itDc.next();
					String value = dc.getDcField(key); %>
					<tr class="<%=tcm.getColouringClass() %>">
						<th><%=key %>: </th>
						<td><input type="text" name="dc_<%=key %>" value="<%=value %>"<%if (key.equals("identifier")) out.print(" onkeyup=\"javascript:checkIdentifier();\" id=\"dc_identifier\" "); %>/>
							<%if (key.equals("identifier") || key.equals("title")) out.print("*"); else out.print("&nbsp;"); %>
						</td>
					</tr>
					<%
					tcm.newRow();
				} %>
			</table>
			<script type="text/javascript">
				function checkIdentifier() {
					var path = "<%=request.getContextPath() %>/ingest/checkIdentifier.do";
					$.get(path, {"identifier": $("#dc_identifier").val()}, function(data) {
						$("#ingest_identifier_alert").html(data);
						replaceIdentifierMessage();
					}, "html");	
				}
				function replaceIdentifierMessage() {
					var top = $("#dc_identifier").offset().top;
					var left = $("#dc_identifier").offset().left;
					$("#ingest_identifier_alert").children().css("position", "absolute");
					$("#ingest_identifier_alert").children().css("top", top-8);
					$("#ingest_identifier_alert").children().css("left", left+170);
				}
				$(document).ready(function() {
					checkIdentifier();
					window.onresize = function(e) {
						replaceIdentifierMessage();
					};
				});
			</script>
			<div id="ingest_identifier_alert"></div>
		</div>
		
		<div id="ingest_edit_panel_2">
			<input id="owl_input" type="hidden" name="owl" style="display: none;" />
			<iframe id="rightsFrame" src="<%=request.getContextPath() %>/body/ingest/rights/upload.jsp" style="display: none;">iframe not supported</iframe>
			<div class="title preview_title">
				Rights &gt;<br/>
				<button type="button" onclick="$('#rightsFrame').contents().find('#owlFile').click();">Upload Rights File</button>
			</div>
			<div id="owl_preview_display" class="preview_display">
			<%
			boolean hasRights = false;
			if (action.equals("edit")) {
				if (currentSip.getMets() != null && currentSip.getMets().getAmdSec() != null) {
					for (AmdSecType mdSec : currentSip.getMets().getAmdSec()) {
						if (mdSec.getRightsMD() != null) {
							for (MdSecType rightsMD : mdSec.getRightsMD()) {
								if (rightsMD.getMdWrap() != null) {
									if (rightsMD.getMdWrap().getLABEL() != null && rightsMD.getMdWrap().getLABEL().equals("PPRIGHTS")) {
										if (rightsMD.getMdWrap().getXmlData() != null && rightsMD.getMdWrap().getXmlData().getAny() != null) {
											for (Object any : rightsMD.getMdWrap().getXmlData().getAny()) {
												StringWriter sw = new StringWriter();
												if (any instanceof Node) {
													TransformerFactory.newInstance().newTransformer().transform(new DOMSource(((Node) any)), new StreamResult(sw));
													hasRights = true;
												} else {
													hasRights = false;
												} %>
												<script type="text/javascript">
													$(document).ready(function() {
														$("#owl_input").val('<%=sw.toString().replaceAll("(\\r|\\n)", "") %>');
														if (<%=hasRights %>) { 
															reloadOWL();
														} else {
															LoadXMLString("owl_preview_display", "<error>Rights file not supported...</error>");
														}
													});
												</script>
											<%}
										}
									}
								}
							}
						}
					}
				}
			}
			
			if (!hasRights) { %>
				<span class="subtitle"><br/><br/><br/><br/><br/>P4 supports ISO/IEC 21000-21 (MPEG-21 Media Contract Ontology)</span>
			<%} %>
			</div>
			<script type="text/javascript">
				function reloadOWL() {	
					LoadXMLString("owl_preview_display", $("#owl_input").val());
				}
			</script>
		</div>
		
		<div id="ingest_edit_panel_3">
			<br/>
			<table id="ingest_resources_table" class="coloured">
				<tr><th>#FILE</th><th>MIMETYPE</th><th>LOCATION</th></tr>
				<%
				tcm.reset();
				Iterator<Resource> itResources = currentSip.getResources().iterator();
				int cFile = 0;
				while (itResources.hasNext()) {
					Resource tmpResource = itResources.next();
					Iterator<Location> itLocations = tmpResource.getLocations().iterator();
					int rowspan = tmpResource.getLocations().size();
					%>
					<tr class="<%=tcm.getColouringClass() %>">
						<td rowspan="<%=rowspan %>">
							<span><%=cFile+1 %></span><br/>
							</td>
						<td>
							<input type="hidden" id="mimetype_input" name="resource_mimetype_<%=cFile %>" style="color: black;">
							<input type="text" disabled="disabled" id="mimetype_input2" style="color: black; width: 180px;" />
						</td>
						<%
						boolean newrow = false;
						int cFLocat = 0;
						List<String> fileList = CommonConnection.getAvailableFileList(user.getCurrentP4Service());
						while (itLocations.hasNext()) {
							Location tmpLocation = itLocations.next();
							if (newrow) {%>
					<tr class="<%=tcm.getColouringClass() %>">
							<%} %>
							<td>
								<select name="resource_href_<%=cFile %>_<%=cFLocat %>" class="resource_href_select" style="width: 450px;" onchange="updateMIMEType(this.value);">
									<option value="<%=tmpLocation.getHref() %>"><%=tmpLocation.getHref() %></option>
									<%
									Iterator<String> it = fileList.iterator();
									while (it.hasNext()) {
									String tmp = it.next(); %>
											<option value="<%=tmp %>" <%if (tmpLocation.getHref().equals(tmp)) out.write("selected=\"selected\""); %>><%=tmp %></option>
									<%} %>
								</select>
								<script type="text/javascript">
									function updateMIMEType(filePath) {
										if (filePath.split(".")[1] == "mp4") {
											$("#mimetype_input").val("video/mp4");
											$("#mimetype_input2").val("video/mp4");
										} else {
											$("#mimetype_input").val("application/mxf");
											$("#mimetype_input2").val("application/mxf");
										}
									}
								</script>
							</td>
					</tr>
							<%
						newrow = true;
						cFLocat++;
					}
					tcm.newRow();
					cFile++;
				} %>
			</table>
			<iframe id="masterfileFrame" src="<%=request.getContextPath() %>/body/ingest/masterfile/masterfile.jsp" style="display: none;">iframe not supported</iframe>
			<button type="button" onclick="$('#masterfileFrame').contents().find('#masterFile').click();$('#masterfile_loader').css('display', 'inline');">Upload New MQ File</button>
			<img id="masterfile_loader" src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_orange.gif" style="display: none;"/><br/>
			<span id="masterfile_name"></span>
		</div>
	</form>
<%} %>

<form id="ingest_form" action="<%=request.getContextPath() %>/ingest" method="POST">
	<div id="ingest_edit_panel_4">
		<%
		WfDescriptor descriptor = WorkflowConnection.getWfDescriptor(user.getCurrentP4Service());
		tcm.reset();
		%>
		<br/>
		<div class="panel_container">
			<div class="panel">
				<table class="coloured">
					<tr><td></td><th>WORKFLOW</th><th>Description</th></tr>
					<%for (Workflow workflow : descriptor.getWorkflows().getWorkflow()) {
						if (workflow.getType() != null && workflow.getType().equals(WfType.INGEST)) { %>
							<tr class="<%=tcm.getColouringClass() %>">
								<td><input type="radio" name="wfID" value="<%=workflow.getId() %>" checked="checked"/></td>
								<th><%=workflow.getId() %></th>
								<td><% if (workflow.getDescription() == null) out.write("N/A"); else out.write(workflow.getDescription().getValue()); %></td>
							</tr>
						<%tcm.newRow();
						}
					} %>
				</table>
			</div>
		</div>
	</div>
	
	<div id="ingest_edit_panel_5">
		<img id="sip_preview_loader" class="sip_preview_img" src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_orange.gif" />
		<img id="sip_submit_button" class="sip_preview_img" src="<%=request.getContextPath() %>/resources/ingest/ingest.png" onclick="$('#ingest_form').submit();" />
		<div class="title preview_title">
			Preview &gt;<br/>
			<button type="button" onclick="$('#edit_form').submit();">Download SIP</button>
		</div>
		<div id="sip_preview_display" class="preview_display"></div>
		<input id="sip_input" type="hidden" name="sip" />
	</div>
</form>

<script type="text/javascript">
	function getPreview() {
		$("#sip_preview_loader").css("display", "inline");
		$("#sip_submit_button").css("display", "none");
		$.ajax({
			type: "POST",
			url: "<%=request.getContextPath() %>/ingest/preview",
			data: $("#edit_form").serialize(),
			dataType: "html",
			success: function(data) {
				try {
					setPreview(data);
				} catch (err) {
					console.log(err);
				}
			}
		});
	}
	function setPreview(data) {
		$("#sip_input").val(data);
		LoadXMLString("sip_preview_display", data);
		$("#sip_submit_button").css("display", "inline");
		$("#sip_preview_loader").css("display", "none");
	}
</script>
