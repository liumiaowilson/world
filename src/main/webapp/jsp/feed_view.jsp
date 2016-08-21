<%@ page import="org.wilson.world.feed.*" %>
<%
String page_title = "Feed View";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Feed View</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <%
            List<FeedInfo> feed_infos = FeedManager.getInstance().randomFeedInfos();
            for(FeedInfo feed_info : feed_infos) {
            %>
            <a href="<%=feed_info.url%>"><%=feed_info.title%></a><br/>
            <%
            }
            %>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
