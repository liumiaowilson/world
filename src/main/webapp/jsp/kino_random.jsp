<%
String page_title = "Kino Random";
%>
<%@ include file="header.jsp" %>
<%
Kino kino = KinoManager.getInstance().randomKino();
if(kino == null) {
    response.sendRedirect("kino_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Kino Random</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <p><b><%=kino.name%></b></p>
            <p><%=kino.content%></p>
        </div>
    </div>
</div>
<button type="button" class="btn btn-default" id="url_back_btn">Back</button>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
