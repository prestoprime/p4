<%request.setAttribute("title", "Search"); %>
<%@page import="eu.prestoprime.search.util.Schema"%>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/body/access/search/search.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/resources/XMLdisplay/XMLdisplay.css" />
<link media="all" type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.17/themes/base/jquery-ui.css" rel="stylesheet">

<script type="text/javascript" src="<%=request.getContextPath() %>/resources/XMLdisplay/XMLdisplay.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/resources/jquery.ui.core.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/resources/jquery.ui.widget.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/resources/jquery.ui.position.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/resources/jquery.ui.autocomplete.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/resources/jquery.ui.datepicker.js"></script>
<div id="top_menu">
	<ul>
		<li id="top_menu_1" onclick="javascript:changeSearchPanel(1)"><a>Quick</a></li>
		<!--<li id="top_menu_2" onclick="javascript:changeSearchPanel(2)"><a>OAI-PMH</a></li>-->
		<li id="top_menu_3" onclick="javascript:changeSearchPanel(3)"><a>Advanced</a></li>
	</ul>
</div>
<div id="search_panel_1">
	<div id="dc_search_fields" class="search_fields">
		<img id="dc_search_loader" src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_orange.gif" align="right"/><br/>
		<form>
			<div class="search_field">
				title<br/><input type="text" id="title_dc_search_field" onkeyup="javascript:searchDCrecords()" size="20" />
			</div>
			<div class="search_field">
				description<br/><input type="text" id="description_dc_search_field" onkeyup="javascript:searchDCrecords()" size="20" />
			</div>
			<div class="search_field">
				format<br/><input type="text" id="format_dc_search_field" onkeyup="javascript:searchDCrecords()" size="20" />
			</div>
			<div class="search_field">
				identifier<br/><input type="text" id="identifier_dc_search_field" onkeyup="javascript:searchDCrecords()" size="20" />
			</div>
		</form>
		<script type="text/javascript">
			var rid = 0;
			function searchDCrecords() {
				$("#dc_search_loader").css("visibility", "visible");
				var title = $("#title_dc_search_field").val();
				var description = $("#description_dc_search_field").val();
				var format = $("#format_dc_search_field").val();
				var identifier = $("#identifier_dc_search_field").val();
				rid = rid + 1;
				var path = "<%=request.getContextPath() %>/access/search/dc?title="+title+"&description="+description+"&format="+format+"&identifier="+identifier+"&rid=p4guidcrid"+rid;
				path = path.replace(" ", "%20");
				$.get(path, function(data) {
					if (data.indexOf("p4guidcrid"+rid) != -1) {
						$("#dc_search_results").html(data);
						try {
							var script = data.split("<script type=\"text/javascript\">")[1].split("</script")[0];
							eval(script);
						} catch (err) {
							
						}
						$("#dc_search_loader").css("visibility", "hidden");
					}
				}, "html");
			}
			$(document).ready(function() {
				//the function below initializes the quick search
			searchDCrecords();
  			});
			
			var id_array_thumb;
			var id_array_dc;
			function processNextThumb() {
				var tmp_id = id_array_thumb.shift();
				if (tmp_id != undefined) {
					var path = "<%=request.getContextPath() %>/body/access/search/DC/getThumb.jsp";
					$.get(path, {"id": tmp_id}, function(data) {
						$("#"+tmp_id+"_thumb").html(data);
						processNextThumb();
					});
				}
			}
			function processNextDC() {
				var tmp_id = id_array_dc.shift();
				if (tmp_id != undefined) {
					var path = "<%=request.getContextPath() %>/body/access/search/DC/getDCfields.jsp";
					$.get(path, {"id": tmp_id}, function(data) {
						$("#"+tmp_id+"_dc").html(data);
						processNextDC();
					});
				}
			}
			function startAjaxDownloads(ids) {
				id_array_thumb = ids.slice(0);
				id_array_dc = ids.slice(0);
				for (var i = 0; i < 10; i++) {
					processNextThumb();
					processNextDC();
				}
			}
		</script>
	</div>
	<div id="dc_search_results" class="search_results">
	
	</div>
</div>
<!-- 
<div id="search_panel_2">
	<div id="oai_search_fields" class="search_fields">
		<img id="oai_search_loader" src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_orange.gif" align="right" style="visibility: hidden;"/><br/>
		<form action="javascript:toggleManualOAISearchField()">
			<div id="manual_oai_search_fields" class="search_field" style="margin-left: 50px;">
				<input type="submit" value="OAI-PMH Query"/>
				<input type="text" id="manual_oai_search_field" size="80" value="verb=ListRecords&metadataPrefix=oai_dc"/>
			</div>
		</form>
		<script type="text/javascript">
			function toggleManualOAISearchField() {
				var mosf = $("#manual_oai_search_field");
				if (mosf.css("display") == "none") {
					mosf.css("display", "inline");
					mosf.focus();
				} else {
					mosf.css("display", "none");
					var loader = $("#oai_search_loader");
					loader.css("visibility", "visible");
					
					var path = mosf.val();
					path = path.replace(/&/g, "--");
					path = "<%=request.getContextPath() %>/access/search/oaipmh?params=" + path;
					$.get(path, function(data) {
						LoadXMLString("oai_pmh_search_results", data);
						loader.css("visibility", "hidden");
					}, "html");
				}
			}
		//	toggleManualOAISearchField();
		</script>
	</div>
	<div id="oai_pmh_search_results" class="search_results"></div>
</div>
-->

<%! //generate non-removable default searchWidgets on Server side
private String getSearchWidget(int nr, Schema.searchField selectedSf){
	StringBuilder sb = new StringBuilder();
	sb.append("<div class=\"search_field\" id=\"searchWidget_" + nr + "\">");
	sb.append("<select id=\"select_" + nr + "\" style=\"float:left;\">");

	for(Schema.searchField sf : Schema.searchField.values() ){
		if(!sf.getType().equals(Schema.FieldType.TINT)){ //slider not implemented yet
			sb.append("<option value=\"");
			sb.append(sf.getUrlParam());
			sb.append("\""); 
			sb.append(" data-type=\"" + sf.getType() + "\"");
			if(sf.equals(selectedSf)){
				sb.append(" selected=\"selected\"");
			}
			sb.append(">");
			sb.append(sf.getLabel());
			sb.append("</option>");
		}
	}
	sb.append("</select>");
	sb.append("<br/>");
	sb.append("<div id=\"input_div_" + nr + "\">");
	sb.append("<input type=\"text\" id=\"input_" + nr +"\" size=\"20\" />");
	sb.append("</div>");
	sb.append("</div>");
	return sb.toString();
}			
%>
<div id="search_panel_3">
	<script type="text/javascript">
	var searchWidgetCount = 3;
	var format = 'yy-mm-dd'; //for date searches
	var searchFields = new Object();
	<%
	    int i = 0;
	    for(Schema.searchField sf : Schema.searchField.values() ){
	    	if(sf.equals(Schema.searchField.TITLE) 
	    		|| sf.equals(Schema.searchField.CREATOR) 
	    		|| sf.equals(Schema.searchField.DESC)){
	    		out.println("	searchFields[" + i + "] = {urlParam:'" + sf.getUrlParam() + "',label:'" + sf.getLabel() + "',type:'" + sf.getType().toString() + "', selected:true}");
	    	} else { 
	    		out.println("	searchFields[" + i + "] = {urlParam:'" + sf.getUrlParam() + "',label:'" + sf.getLabel() + "',type:'" + sf.getType().toString() + "', selected:false}"); 
	    	}
	    	i++;
	    }
	    out.println("	var searchFieldCount = " + i + ";");
	%>
	function getSearchWidget(isRemovable){
	 	var nr = searchWidgetCount;
		var div = $('<div>').attr('class', 'search_field').attr('id', 'searchWidget_' + nr).attr('style', 'display: none;');
		var select = $('<select>').attr('style', 'float:left;').attr('id', 'select_' + nr);
		var selectedField = 0;
		for(var i = 0; i < searchFieldCount; i++ ){
			if(searchFields[i].type != 'TINT'){
				var opt = $('<option>').attr('value', searchFields[i].urlParam).attr('data-type', searchFields[i].type).text(searchFields[i].label);
				if(i+1 == nr){
					opt.attr('selected', 'selected');
					selectedField = i;
				}
				select.append(opt);
			}
		}
		div.append(select);
		
		if(isRemovable){
			var a = $('<a>').attr('href', '#').attr('id', 'remove_' + nr).attr('class', 'adv_search_remove').text('×');
			div.append(a);
		}
		div.append($('<br/>'));
		var input = '';
		var inputDiv = $('<div>').attr('id', 'input_div_' + nr);
		if(searchFields[selectedField].type == 'TDATE'){
			input = $('<input>')
			.attr('type', 'text')
			.attr('data-type', searchFields[selectedField].type)
			.attr('id', 'input_a_' + nr)
			.attr('size', '7')
			.datepicker({ dateFormat: format });
			var span = $('<span>').text('to');
			input2 = $('<input>')
			.attr('type', 'text')
			.attr('data-type', searchFields[selectedField].type)
			.attr('id', 'input_b_' + nr)
			.attr('class', 'hasDatePicker')
			.attr('size', '7')
			.datepicker({ dateFormat: format });
			
			inputDiv.append(input).append(span).append(input2);
		} else {
	    	input = $('<input>').attr('type', 'text').attr('id', 'input_' + nr).attr('size', '20');
	    	inputDiv.append(input);
		}
		
		div.append(inputDiv);
		return div;
	}			
	
	function search(paramMap) {
		var path = '<%=request.getContextPath()%>/access/search/index';
		var encodedParams = $.param(paramMap);
		$.get(path, encodedParams, 
			function(data) {
				$("#advanced_search_results").html(data);
				$("#advanced_search_loader").css("visibility", "hidden");
			}, "html");
	}
	function searchSimple(query){
		var parameters = {from : 0, resultCount : 10, sortAsc : true, searchType: 'quick'}
		var term = null;
		$("#advanced_search_loader").css("visibility", "visible");
		if (query == null || query == '') {
			term = $("#quick_search_field").val();
		} else {
			term = query;
		}
		parameters['term'] = term;
		search(parameters);
	}
	function searchAdvanced() {
		var dateSuffix = 'T00:00:00Z';
		var paramMap = {from : 0, resultCount : 10, sortAsc : true, searchType: 'adv'};
		//build a paramMap here with values and call
		for(var j = 1; j <= searchWidgetCount; j++){
			var key = $("#select_" + j + " option:selected").val();
			var value = null;
			if($("#select_" + j + " option:selected").attr('data-type') == 'TDATE'){
				var from = $("#input_a_" + j).val();
				var to = $("#input_b_" + j).val();
				if (from != null && from != ''){ from += dateSuffix; } 
				else {from = '';}
				if (to != null && to != ''){ to += dateSuffix;}
				else{ to = '';}
				if(from != '' || to != ''){
					value = from + '#' + to;
				}
			} else {
				value = $("#input_" + j).val();
			}
			//put the filter query as value here. 
			if(value != null && value != ''){				
				//if key (fieldname) is not yet contained then add it. Otherwise concat with ' '. 
				if(paramMap[key] === undefined || paramMap[key] == '' || paramMap[key]==null){
					paramMap[key] = value;
				} else {
					paramMap[key] = paramMap[key].concat(' ', value);
				}
			}
		}
		search(paramMap);
	}
	function getAutoComplete(term){
		var alt = [];
		if(term != null && term.replace(' ', '') != ''){
  			var path = '<%=request.getContextPath()%>/access/search/suggest';
  			
			$.ajax({
				url: path,
				data: {term : encodeURIComponent(term)},
				async: false,
				dataType: 'json',
				success: function(data) {
					var tmp;
					if(typeof data.suggestions.alternatives == 'undefined'){ //happens if there are several suggestions (suggestions is mapped to an array)
						tmp = data.suggestions[data.suggestions.length - 1].alternatives;
					} else {  //only one suggestion
						tmp = data.suggestions.alternatives;
					}
					if(typeof tmp == 'string'){ 
						//if there is a single result put this to 1-sized array or else jquery will suggest single letters
						alt[0] = tmp;
					} else {
						alt = tmp;
					}
				}
			});
		}
		return alt;
	}

	function switchSearch(number) {		
		var n = 2;
		for (var t = 1; t <= n; t++) {
			$("#adv_search_"+t).css("display", "none");
		}
		$("#adv_search_"+number).css("display", "inline");
	}

	//onload handling
	$(function(){
		switchSearch(1);
		searchSimple('');
		//handle events for quick search field
		$('#quick_search_field').bind({
			submit: function(event){
				searchSimple();
			}
		});
		//handle onclick event on addWidget button -> advanced search
		$('a#add').bind({
			click: function(event){
				searchWidgetCount++;
				$(getSearchWidget(true)).appendTo("#searchWidget_container").show('fast'); //appendTo("#adv_search_form").show('fast');
			}
		});
		
		//handle onclick event on removeWidget button -> advanced search
		$('.adv_search_remove').live(
			'click', function(event){
				var nr = (event.target.id).substring(7);
				$('#searchWidget_' + nr).hide('fast', function(){ $(this).remove();});
				searchWidgetCount--;
			}
		);
		$('select[id^="select_"]').live(
			'change', function(event){
				var nr = (event.target.id).substring(7);
				//get type
				var type = $('option:selected', this).attr('data-type');
				//build input
				var input;
				var input2;
				var inputDiv = $('<div>').attr('id', 'input_div_' + nr);
				if(type == 'TDATE'){
					input = $('<input>')
						.attr('type', 'text')
						.attr('data-type', type)
						.attr('id', 'input_a_' + nr)
						.attr('size', '7')
						.datepicker({ dateFormat: format });
 					var span = $('<span>').text('to');
					input2 = $('<input>')
						.attr('type', 'text')
						.attr('data-type', type)
						.attr('id', 'input_b_' + nr)
						.attr('class', 'hasDatePicker')
						.attr('size', '7')
						.datepicker({ dateFormat: format });
					inputDiv.append(input).append(span).append(input2);
				} else if(type == 'TINT'){
//						input = $('<div style="width:196px;margin-top:10px;margin-bottom:10px;margin-left:8px; margin-right:8px;">')
// 							.attr('type', 'range')
// 							.attr('data-type', type)
// 							.attr('id', 'input_' + nr)
// 							.attr('size', '20')
//							.slider({ range: true });
//						inputDiv.append(input);
				} else {
					input = $('<input>').attr('type', 'text')
						.attr('data-type', type)
						.attr('id', 'input_' + nr)
						.attr('size', '20');
					inputDiv.append(input);
				}
				//set html
				$('#input_div_' + nr).replaceWith(inputDiv);
			}	
		);
		$('select[id^="input_"]').live(
			'submit', function(event){
				searchAdvanced();
			}	
		);
		$( "#quick_search_field" ).autocomplete({
			minLength: 2,
			delay: 1000,
			source: function(request, response){
				response(getAutoComplete(request.term));
			}
		});
		$('.refine-link, .refine-link-selected, .facet-link, .facet-link-selected').live( 
			'click', function() {
	        $.get( $(this).attr('href'), function(msg) {
	        	$('div .adv_search_results').html(msg);
	    	});
	    	return false; // don't follow the link!
		});
	});
	</script>
	<div id="form_div" style="width:200px;">
	<div id="adv_search_1" class="search_fields">
		<form action="javascript:searchSimple()">
		<div id="search_menu">
		<ul>
			<li id="search_menu_1" class="search_menu_current" onclick="javascript:switchSearch(1)" >
			<a>Simple</a>
			</li>
			<li id="search_menu_2" class="" onclick="javascript:switchSearch(2)">
			<a>Detail</a>
			</li>
		</ul>
		</div>
			<div class="search_field">
				Multifield<br/><input type="text" id="quick_search_field" size="20"/>
			</div>
			<input type="submit" value=" Search ">
		</form>
		<img id="advanced_search_loader" src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_orange.gif" align="right" style="visibility:hidden;"/>
	</div>
	<div id="adv_search_2" class="search_fields" style="display:none;">
		<form id="adv_search_form" action="javascript:searchAdvanced()">
			<div id="search_menu">
				<ul>
				<li id="search_menu_1" class="" onclick="javascript:switchSearch(1)">
				<a>Simple</a>
				</li>
				<li id="search_menu_2" class="search_menu_current" onclick="javascript:switchSearch(2)">
				<a>Detail</a>
				</li>
				</ul>
			</div>
			<div id="searchWidget_container">
				<%= getSearchWidget(1, Schema.searchField.TITLE) %>
				<%= getSearchWidget(2, Schema.searchField.CREATOR) %>
				<%= getSearchWidget(3, Schema.searchField.DESC) %>
			</div>
			<a href="#" id="add" title="Add search field">+</a>
			<input type="submit" value=" Search ">
		</form>
		
		<img id="advanced_search_loader" src="<%=request.getContextPath() %>/resources/loaders/ajax_loader_orange.gif" align="right" style="visibility:hidden;"/>
	</div>
	</div>
	<div id="advanced_search_results" class="adv_search_results"></div>
</div>
<script type="text/javascript">
	function changeSearchPanel(number) {		
		var n = 3;
		for (var t = 1; t <= n; t++) {
			$("#top_menu_"+t).removeClass("top_menu_current");
			$("#search_panel_"+t).css("display", "none");
		}
		$("#top_menu_"+number).addClass("top_menu_current");
		$("#search_panel_"+number).css("display", "inline");
	}
	changeSearchPanel(1); //default search panel
</script>
