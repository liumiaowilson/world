<%
String page_title = "Activity Random";
%>
<%@ include file="header.jsp" %>
<%
Activity activity = ActivityManager.getInstance().randomActivity();
if(activity == null) {
    response.sendRedirect("activity_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Activity Random</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <p><b><%=activity.name%></b></p>
            <p><%=FormatUtils.toHtml(activity.content)%></p>
        </div>
    </div>
</div>
<button type="button" class="btn btn-default" id="url_back_btn">Back</button>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
