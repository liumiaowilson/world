<%
String page_title = "Balance Broken";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Balance Broken</h3>
    </div>
    <div class="panel-body">
        <div class="alert alert-warning" role="alert" id="balance_warning">
            <%=BalanceManager.getInstance().check().getMessage()%>
        </div>
    </div>
</div>
<button type="button" class="btn btn-default" id="url_back_btn">Back</button>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
