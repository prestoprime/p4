<%@page import="eu.prestoprime.p4gui.connection.AccessConnection"%>
<%@page import="java.util.ArrayList"%>
<%@page import="eu.prestoprime.p4gui.util.parse.Frame"%>
<%@page import="java.util.Iterator"%>

<jsp:useBean id="user" class="eu.prestoprime.p4gui.model.User" scope="session" />
<jsp:useBean id="frames" class="java.util.ArrayList" scope="request" />
<jsp:useBean id="segmentEnable" class="java.lang.String" scope="request" />

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/body/access/viewer/keyframes/keyframes.css" />

<div id="keyframes">
	<ul id="keyframes_container">
		<%
		int interval = frames.size() / 200;//set max keyframes number
		int keyframes = 0;
		Iterator<Frame> it = frames.iterator();
		while (it.hasNext()) {
			Frame tmpFrame = it.next();
			if (interval != 0 && (keyframes % interval) != 0) {//don't show
				keyframes ++;
				continue;
			}
			int fps = tmpFrame.getFrameRate();
			int frame = tmpFrame.getFrame();
			int hours = frame / (3600 * fps);
			frame = frame % (3600 * fps);
			int minutes = frame / (60 * fps);
			frame = frame % (60 * fps);
			int seconds = frame / fps;
			frame = frame % fps;
			%>
			<li>
				<div id="keyframe_<%=tmpFrame.getFrame() %>" class="keyframe" style="background: url('<%=tmpFrame.getPath() %>') no-repeat center center; background-size: cover; -webkit-background-size: contain; -moz-background-size: cover; -o-background-size: contain;">
					<span class="keyframe_control" style="float: left;" onclick="setBegin(<%=tmpFrame.getFrame() %>, '<%=tmpFrame.getPath() %>');">begin</span>
					<span class="keyframe_control" style="float: right;" onclick="setEnd(<%=tmpFrame.getFrame() + 124 %>, '<%=tmpFrame.getPath() %>');">end</span>
					<span class="keyframe_play" onclick="setPreviewOffset(<%=tmpFrame.getFrame() / fps %>)">
						<img src="<%=request.getContextPath() %>/body/access/viewer/keyframes/play.png" />
					</span>
					<span class="keyframe_time"><%=hours %>:<%=minutes %>:<%=seconds %>.<%=frame %></span>
				</div>
			</li>
			<%
			keyframes++;
		} %>
	</ul>
</div>

<script type="text/javascript">
	// keyframes scrolling
	var keyframe_scrolling_enabled = true;
	$(function(){
		var div = $('#keyframes');
		var divWidth = div.width();
		var ul = $('#keyframes_container');
		var lastLi = ul.find('li:last-child');
		var ulPadding = 15;
	
		div.css({overflow: 'hidden'});
		
		div.mousemove(function(e){
			if (keyframe_scrolling_enabled) {
				var ulWidth = lastLi[0].offsetLeft + lastLi.outerWidth() + ulPadding;	
				var left = (e.pageX - div.offset().left) * (ulWidth-divWidth) / divWidth;
				div.scrollLeft(left);
			}
		});
		$(window).keydown(function(event){
		    keyframe_scrolling_enabled = false;
		});
		$(window).keyup(function(event){
			keyframe_scrolling_enabled = true;
		});
	});
	
	// keyframes preview offset
	var video = document.getElementById("preview_video");
	function setPreviewOffset(o) {
		video.currentTime = o;
		video.play();
	}
</script>

<%if (true) { %>
	<script type="text/javascript">
		// keyframes selection
		var begin, end;
		function setBegin(i, p) {
			if (end == undefined) {
				end = i+124;
			}
			if (i <= end) {
				begin = i;
			}
			updateKeyframesSelection();
		}
		function setEnd(i, p) {
			if (begin == undefined) {
				begin = i-124;
			}
			if (i >= begin) {
				end = i;
			}
			updateKeyframesSelection();
		}
		function clearKeyframesSelection() {
			begin = undefined;
			end = undefined;
		}
		function updateKeyframesSelection() {
			$(".keyframe").css("border", "4px solid white");
			if (begin != undefined && end != undefined) {
				for (var i = begin; i <= end; i++) {
					try {
						$("#keyframe_"+i).css("border", "4px solid #20A386");
					} catch (e) {
						console.log(e);
					};
				}
				$("#segment_form").css("display", "inline");
				var d = Math.floor((end-begin)/25);
				if (d > 60) {
					d = Math.floor(d/60);
					s = d + " min";
				} else {
					s = d + " sec";
				}
				$("#segment_form_submit").val("Extract " + s);
				$("#start_frame_field").val(""+begin);
				$("#stop_frame_field").val(""+end);
			} else {
				$("#segment_form").css("display", "none");
				$("#start_frame_field").val("0");
				$("#stop_frame_field").val("0");
			}
		}
		updateKeyframesSelection();
	</script>
<%} else {%>
	<script type="text/javascript">
	$(document).ready(function (){
		$(".keyframe_control").css("visibility", "hidden");
	});
	</script>
<%} %>
