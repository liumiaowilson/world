<%
String page_title = "Clip View";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_video.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Clip View</h3>
    </div>
    <div class="panel-body">
        <%
        String name = ClipManager.getInstance().getCurrent();
        if(name == null) {
        %>
        <div class="well">
            Clip is not found.
        </div>
        <%
        }
        else {
        %>
        <video controls>
            <source src="<%=basePath%>/servlet/video?path=<%=name%>" type="video/mp4">
            Your browser does not support the video tag.
        </video>
        <%
        }
        %>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_video.jsp" %>
<script>
            plyr.setup();
</script>
<%@ include file="footer.jsp" %>
