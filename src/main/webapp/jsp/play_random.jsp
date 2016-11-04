<%
String page_title = "Play Random";
%>
<%@ include file="header.jsp" %>
<%
Play play = PlayManager.getInstance().randomPlay();
if(play == null) {
    response.sendRedirect("play_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Play Random</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <p><b><%=play.name%></b></p>
            <p><i><%=play.source%></i></p>
            <p><%=play.content%></p>
        </div>
    </div>
</div>
<button type="button" class="btn btn-default" id="url_back_btn">Back</button>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
