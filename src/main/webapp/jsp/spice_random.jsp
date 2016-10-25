<%
String page_title = "Spice Random";
%>
<%@ include file="header.jsp" %>
<%
Spice spice = SpiceManager.getInstance().randomSpice();
if(spice == null) {
    response.sendRedirect("spice_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Spice Random</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <p><b><%=spice.name%></b></p>
            <p><%=spice.content%></p>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
