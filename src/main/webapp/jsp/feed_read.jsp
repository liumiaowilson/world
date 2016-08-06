<%@ page import="org.wilson.world.feed.*" %>
<%
String page_title = "Feed Read";
%>
<%@ include file="header.jsp" %>
<%
FeedInfo feed_info = FeedManager.getInstance().randomFeedInfo();
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Feed Read</h3>
    </div>
    <div class="panel-body">
        <%
        if(feed_info != null) {
        %>
        <div id="feed_info">
            <div class="embed-responsive embed-responsive-16by9">
                <iframe class="embed-responsive-item" src="<%=feed_info.url%>"></iframe>
            </div>
        </div>
        <%
        }
        else {
        %>
        <div class="alert alert-danger" role="alert">
            No feed info could be found.
        </div>
        <%
        }
        %>
    </div>
</div>
<div class="form-group">
    <button type="button" class="btn btn-primary" id="next_btn">Next</button>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $('#next_btn').click(function(){
                jumpTo('feed_train.jsp');
            });
</script>
<%@ include file="footer.jsp" %>
