<%
String page_title = "Hunger Random";
%>
<%@ include file="header.jsp" %>
<%
Hunger hunger = HungerManager.getInstance().randomHunger();
if(hunger == null) {
    response.sendRedirect("hunger_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Hunger Random</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <p><b><%=hunger.name%></b></p>
            <p><%=FormatUtils.toHtml(hunger.content)%></p>
        </div>
    </div>
</div>
<button type="button" class="btn btn-default" id="url_back_btn">Back</button>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
