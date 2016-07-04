<%
String from_url = "index.jsp";
%>
<%@ include file="header.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Quick Links</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <a href="idea_new.jsp" class="list-group-item active">New Idea</a>
            <a href="idea_list.jsp" class="list-group-item">Todo</a>
        </div>
    </div>
</div>
<%@ include file="import_scripts.jsp" %>
<%@ include file="footer.jsp" %>
