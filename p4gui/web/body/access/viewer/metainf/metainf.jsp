<%@page import="eu.prestoprime.p4gui.connection.AccessConnection"%>
<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/body/access/viewer/metainf/metainf.css" />

<%String id = request.getParameter("id"); %>

<div id="top_menu">
	<ul>
		<li id="top_menu_1" onclick="setMetainfPanel(1)"><a>General Info</a></li>
		<li id="top_menu_6" onclick="setMetainfPanel(6)"><a>Resources</a></li>
		<li id="top_menu_2" onclick="setMetainfPanel(2)" style="display: none;"><a>File Section</a></li>
		<li id="top_menu_4" onclick="setMetainfPanel(4)"><a>Actions</a></li>
 		<li id="top_menu_5" onclick="setMetainfPanel(5)" style="display: none;"><a>Rights</a></li>
 		<li id="top_menu_7" onclick="setMetainfPanel(7)"><a>Events</a></li>
		<li id="top_menu_3" onclick="setMetainfPanel(3)"><a>Source</a></li>
	</ul>
</div>

<!-- general info -->
<div id="metainf_panel_1" class="metainf_panel">
	<img src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_orange.gif"/>
</div>

<!-- filesec -->
<div id="metainf_panel_6" class="metainf_panel">
	<img src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_orange.gif"/>
</div>

<!-- resources -->
<div id="metainf_panel_2" class="metainf_panel">
	<img src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_orange.gif"/>
</div>

<!-- actions -->
<div id="metainf_panel_4" class="metainf_panel">
	<img src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_orange.gif"/>
</div>

<!-- rights -->
<div id="metainf_panel_5" class="metainf_panel">
	<img src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_orange.gif"/>
</div>

<!-- events -->
<div id="metainf_panel_7" class="metainf_panel">
	<img src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_orange.gif"/>
</div>

<!-- source -->
<div id="metainf_panel_3" class="metainf_panel">
	<img src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_orange.gif"/>
</div>

<script type="text/javascript">
	function changeMetainfPanel(i) {
		var n = $("#top_menu > ul > li").size();
		for (var t = 1; t <= n; t++) {
			document.getElementById("top_menu_"+t).setAttribute("class", "");
			document.getElementById("metainf_panel_"+t).style.display = 'none';
		}
		document.getElementById("top_menu_"+i).setAttribute("class", "top_menu_current");
		document.getElementById("metainf_panel_"+i).style.display = 'inline';
	}
	
	function setMetainfPanel(i) {
		if (document.getElementById("metainf_panel_"+i).title != "loaded") {
			var path="<%=request.getContextPath() %>/access/viewer/metainf/"+i;
			$.get(path, {id: "<%=id %>"}, function(data) {
				document.getElementById("metainf_panel_"+i).innerHTML = data;
				try {
					var scripts = data.split("<script type=\"text/javascript\">");
					var j;
					for (j = 1; j <= scripts.length; j++) {
						var script = scripts[j].split("</script")[0];
						eval(script);
					}
				} catch (err) {
					console.log(err);
				}
				document.getElementById("metainf_panel_"+i).title = "loaded";
			}, "html");
		}
		changeMetainfPanel(i);
		updateKeyframesSelection();
	}
	
	function updateMIMEType(filePath) {
		if (filePath.split(".")[1] == "mp4") {
			$("#mimetype_input").val("video/mp4");
			$("#mimetype_input2").val("video/mp4");
		} else {
			$("#mimetype_input").val("application/mxf");
			$("#mimetype_input2").val("application/mxf");
		}
	}
	
	$(document).ready(function() {
		var et = "<p><a class=\"coloured\" href=\"<%=request.getContextPath() %>/access/viewer/preview\" target=\"_blank\">";
		et += "Download DIP";
		et += "</a></p>";
		$("#metainf_panel_3").et(et, "click", {color: "sand"});
		
		setMetainfPanel("1");
		
		<%if (AccessConnection.checkDataTypeAvailability(user.getCurrentP4Service(), id, "rights")!= null) { %>
			$("#top_menu_5").css("display", "inline");
		<%} %>
		<%if (AccessConnection.getPreviewsPath(user.getCurrentP4Service(), id).size() != 0) { %>
			$("#top_menu_2").css("display", "inline");
		<%} %>
	});
</script>