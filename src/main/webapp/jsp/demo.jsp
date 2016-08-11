<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_video.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Demo</h3>
    </div>
    <div class="panel-body">
        <video controls>
            <source src="<%=basePath%>/servlet/video?path=test.mp4" type="video/mp4">
            Your browser does not support the video tag.
        </video>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_video.jsp" %>
<script>
            plyr.setup();
</script>
<%@ include file="footer.jsp" %>
