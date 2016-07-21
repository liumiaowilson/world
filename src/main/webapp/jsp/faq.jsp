<%
String page_title = "FAQ";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">FAQ</h3>
    </div>
    <div class="panel-body">
        <div class="alert alert-warning" role="alert">
            <strong>After a long idle time, "communications link failure" exceptions are thrown when doing database-related operations.</strong><br/>
            This is caused by MySQL wait_timeout(about 8 hours). Currently no best solution to tackle this issue. The workaround is to re-do the operation again and it will succeed.
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
