<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="eu.prestoprime.search.util.Schema.FieldType"%>
<%@page import="eu.prestoprime.p4gui.util.parse.DCField"%>
<%@page import="eu.prestoprime.p4gui.connection.AccessConnection"%>
<%@page import="eu.prestoprime.p4gui.P4GUI"%>
<%@page import="eu.prestoprime.p4gui.util.URLUtils"%>
<%@page import="eu.prestoprime.search.util.Schema" %>
<%@page import="eu.prestoprime.model.search.SearchResults"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.List"%>
<%-- <%@ taglib prefix="p4" uri="WEB-INF/p4Tags.tld" %> --%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />
<%
final String searchUrl = request.getContextPath() + "/access/search/index";
final String viewerUrl = request.getContextPath() + "/access/viewer?id=";

response.setContentType("text/html;charset=UTF-8");

SearchResults res = (SearchResults)request.getAttribute("search_result");
java.util.TreeMap<String, String> urlParamMap = (java.util.TreeMap<String, String>)request.getAttribute("url_param_map");
if(urlParamMap == null) urlParamMap = new TreeMap<String,String>();
java.util.TreeMap<String, String> tmpMap; 
%>

<%!
//TODO custom tags for these scriptlets
private final String getResTableCell(String label, String value){
	if(value != null && !value.equals("null") && !value.isEmpty()){
		return "<tr><td>" + label + "</td><td>" + value + "</td></tr>";
	} else {
		return "";
	}
}
	
private final String getResTableCells(String label, List<String> values){
	final String rowStart = "<tr>";
	final String rowEnd = "</tr>";
	final String start = "<td>";
	final String end = "</td>";
	
	StringBuilder sb = new StringBuilder();
	if(values != null && !values.isEmpty()){
		sb.append(rowStart);
		sb.append(start);
		sb.append(label);
		sb.append(end);
		sb.append(start);
		sb.append(values.get(0));
		sb.append(end);
		sb.append(rowEnd);
		if(values.size() > 1){
			for(int i = 1; i < values.size(); i++){
				sb.append(rowStart);
				sb.append(start);
				sb.append(end);
				sb.append(start);
				sb.append(values.get(i));
				sb.append(end);
				sb.append(rowEnd);
			}
		}
	}
	return sb.toString();
}

private final String getResTableCellsCsv(String label, List<String> values){
	final String rowStart = "<tr>";
	final String rowEnd = "</tr>";
	final String start = "<td>";
	final String end = "</td>";
	String delim = ", ";
	StringBuilder sb = new StringBuilder();
	if(values != null && !values.isEmpty()){
		sb.append(rowStart);
		sb.append(start);
		sb.append(label);
		sb.append(end);
		sb.append(start);
		sb.append(values.get(0));
		if(values.size() > 1){
			for(int i = 1; i < values.size(); i++){
				sb.append(delim);
				sb.append(values.get(i));
			}
		}
		sb.append(end);
		sb.append(rowEnd);
	}
	return sb.toString();
}

private String getPagingDiv(SearchResults res, TreeMap<String, String> urlParamMap, String searchUrl){
	StringBuilder pageDiv = new StringBuilder();
	pageDiv.append("<div id=\"pageDiv\" style=\"float:left;padding-left:10px\">Page: ");
	
	long total = res.getHits().getTotal();
	Long from = res.getParams().getFrom();
	Long resultCount = res.getParams().getResultCount();
	if(from == null) from = new Long(0);
	if(resultCount == null) resultCount = new Long(10);
			
	long nrOfPages = (total % resultCount == 0 ? total/resultCount : total/resultCount + 1);
	long currentPage = (from != 0 ? from/resultCount + 1 : new Long(1));

//		pageDiv.append([[DEBUG: total=" + total + " | from=" + from + " | resultCount=" + resultCount  + " | currentPage=" +currentPage + "]]");
	
	TreeMap<String, String> tmpMap = (TreeMap<String, String>)urlParamMap.clone();
	for(int i = 0; i < nrOfPages; i++){
		tmpMap.put("from", ""+(i*resultCount));
		tmpMap.put("resultCount", ""+resultCount);
		if((i >= (currentPage-5) && i<= (currentPage+3)) || (currentPage <= 4 && i<=9) || (currentPage >= nrOfPages-4 && i>=nrOfPages-9) ){
			String cssClass = (i+1==currentPage) ? "refine-link-selected" : "refine-link";
			pageDiv.append("<a href=\"" + searchUrl + URLUtils.buildUrlParamString(tmpMap) + "\" class=\"" + cssClass + "\">" + (i+1) + "</a>"); 
		}
	}
pageDiv.append("</div>");
return pageDiv.toString();
}

private String getSortDiv(SearchResults res, TreeMap<String,String> urlParamMap, String searchUrl){
	StringBuilder sortDiv = new StringBuilder("<div id=\"sortDiv\" style=\"float:right;\">Sort: ");
	TreeMap<String, String> tmpMap = (TreeMap<String, String>)urlParamMap.clone();
	Schema.P4SortField currentSortField = Schema.P4SortField.get(urlParamMap.get("sortField"));
	boolean currentSortAsc = Boolean.parseBoolean(urlParamMap.get("sortAsc"));
	for(Schema.P4SortField field : Schema.P4SortField.values()){
		tmpMap.put("sortField", field.getFieldName());
		boolean selected = (currentSortField==field);
		tmpMap.put("sortAsc", (selected ? ""+!currentSortAsc : ""+currentSortAsc));
		String label = field.getFieldName().replace("Sort", "");
		label = label.substring(0, 1).toUpperCase() + label.substring(1);
		if(selected){
			if(currentSortAsc){
				label = "▼ " + label;
			} else {
				label = "▲ " + label;
			}
		}
		String cssClass = selected ? "refine-link-selected" : "refine-link";
		sortDiv.append("<a href=\"" + searchUrl + URLUtils.buildUrlParamString(tmpMap) + "\"");
		sortDiv.append(" class=\"" + cssClass + "\">" + label + "</a>");
	}
	sortDiv.append("</div>");
	
	return sortDiv.toString();	
}

private String getFacetsDiv(SearchResults res, TreeMap<String,String> urlParamMap, String searchUrl){
	TreeMap<String, String> tmpMap;
	StringBuilder fDiv = new StringBuilder("<div id=\"facetsDiv\">\n");
	fDiv.append("<table style=\"width:300px;\">\n");
	if(res.getFacets() != null && res.getFacets().getFacet() != null){
		for(SearchResults.Facets.Facet facet : res.getFacets().getFacet()){
			tmpMap = (TreeMap<String, String>)urlParamMap.clone();
			Schema.P4FacetField currFacetField = Schema.P4FacetField.get(facet.getFacetName());
			
			//only display active facets defined in p4core->solr.properties
			if(currFacetField != null){
				fDiv.append("<tr><td style=\"font-weight:bold;\">" + currFacetField.getAssocField().getLabel() + "</td></tr>\n");
				for(SearchResults.Facets.Facet.Value value : facet.getValue()){
					String currFacetParam = currFacetField.getFieldName();
					//if a value for this facet is already contained
					if(urlParamMap.get(currFacetParam) != null && !urlParamMap.get(currFacetParam).isEmpty()){
						if(value.getFilterQuery().isEmpty()){
							tmpMap.remove(currFacetParam);
						} else {
							tmpMap.put(
								currFacetParam,
								value.getFilterQuery());
						}
						
						fDiv.append("<tr>\n");
						fDiv.append("<td>\n");
						if(value.isSelected()){ //if exactly this value, remove filter for this facet							
							fDiv.append("<a href=\"" + searchUrl + URLUtils.buildUrlParamString(tmpMap) 
									+ "\" class=\"facet-link-selected\">" + value.getTerm() + " (" + value.getCount() + ")");
						} else { //if another value for this facet, build a filter query here
							fDiv.append("<a href=\"" + searchUrl + URLUtils.buildUrlParamString(tmpMap) 
									+ "\" class=\"facet-link\">" + value.getTerm() + " (" + value.getCount() + ")");
						}
						
						fDiv.append("</a>\n");
						fDiv.append("</td>\n");
						fDiv.append("</tr>\n");
					} else { //if facet is not yet selected, then add value to map and quote it for exact matches.
						tmpMap.put(
								currFacetParam,
								value.getFilterQuery());
						fDiv.append("<tr>\n");
						fDiv.append("<td>\n");
						fDiv.append("<a href=\"" + searchUrl + URLUtils.buildUrlParamString(tmpMap) 
								+ "\" class=\"facet-link\">" + value.getTerm() + " (" + value.getCount() + ")");
						fDiv.append("</a>\n");
						fDiv.append("</td>\n");
						fDiv.append("</tr>\n");
					}
				}
			}
		}
	}
	fDiv.append("</table>\n");
	fDiv.append("</div>\n");
	return fDiv.toString();
}
%>
<%
if(res != null){
%>
	<div id="resultsDiv">
		<script type="text/javascript">
			function loadThumbs(){
				var path = '<%=request.getContextPath()%>/access/search/thumb';
				$('.ajax_thumb').each(
					function(n){
						var aipId = this.id.split('_')[1];
						if (aipId != undefined) {
						    $.get(path, {"id": aipId}, function(data) {
								if (data != null){
// 									alert('thumb_'+aipId + ' -> src = ' + data);
									$('#thumb_'+aipId+' img').attr('src', data);
								}
							}, "html");
						}
					});
			}
			$(function() {
				loadThumbs();
// 				$('.refine-link, .refine-link-selected, .facet-link, .facet-link-selected').click( function() {
// // 					alert($(this).attr('href'));
//                 	$.get( $(this).attr('href'), function(msg) {
//                 		$('#advanced_search_results', window.parent.document).html(msg);
// 	        		});
//             		return false; // don't follow the link!
// 				});
//             	loadThumbs();
			});
		</script>
		<div id="controls_container">
		<div style="float:left;font-weight:bold">
		<%= res.getHits().getTotal() == 1 ? "1 result" : res.getHits().getTotal() + " results"%></div>
		
		<%= getPagingDiv(res, urlParamMap, searchUrl) %>
		
		<%= getSortDiv(res, urlParamMap, searchUrl) %>
		</div>
		<br/>
<%-- 		<div id="debugDiv"><%= "DEBUG Query='" + res.getParams().getQuery().getAllfields() + "'"%></div> --%>
		<%
		Long from = res.getParams().getFrom();
		if(res.getHits().getTotal() != 0){ 
			for(SearchResults.Hits.Hit hit : res.getHits().getHit()){
				long resNr = Integer.parseInt(hit.getPos()) + 1 + from;
				%>
<!-- 				<div style="border-top-width:1px;border-top-style:solid;border-color:gray;"> -->
				<div class="adv_search_result">
				<div class="ajax_thumb" id="thumb_<%=hit.getId()%>">
					<a class="coloured" href="<%= viewerUrl + hit.getId()%>">
						<img src="/p4gui/resources/access/image-missing.svg"/>
					</a>
				</div>
					<table>
						<tr>
							<td style="width:70px">AIP:</td><td><a class="coloured" href="<%= viewerUrl + hit.getId()%>"><%= hit.getId() %></a></td>
						</tr>
						<%= getResTableCells("Title:", hit.getTitle()) %>
						<%= getResTableCells("Creator:", hit.getCreator()) %>
						<%= getResTableCells("Publisher:", hit.getPublisher()) %>
						<%= getResTableCells("Date:", hit.getDate()) %>
						<%= getResTableCells("Description:", hit.getDescription()) %>
						<%= getResTableCells("Format:", hit.getFormat()) %>
						<%= getResTableCells("Identifier:", hit.getIdentifier()) %>
						<%= getResTableCellsCsv("Subject:", hit.getSubject()) %>
						<%= getResTableCells("Language:", hit.getLanguage()) %>
						<%= getResTableCellsCsv("Keywords:", hit.getUserAnnot()) %>
						<%= getResTableCell("Resolution:", hit.getResolution()) %>
						<%= getResTableCell("Aspect Ratio:", hit.getAspectRat()) %>
						<%= getResTableCell("Duration:", ""+hit.getDuration()) %>
						<%= getResTableCell("Video Codec:", hit.getCodec()) %>
						<%= getResTableCell("Creation Date:", hit.getCreateDate()) %>
					</table>
					<div id="resNr"><%= resNr %></div>
				</div>
				<%
			}
			%>	
		</div>
		<%= getFacetsDiv(res, urlParamMap, searchUrl) %>
	<%
	}
} else {
	out.println("No or invalid query entered");	
}	
%>
