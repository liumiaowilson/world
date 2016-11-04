<%
String page_title = "Chat Random";
%>
<%@ include file="header.jsp" %>
<%
Chat chat = ChatManager.getInstance().randomChat();
if(chat == null) {
    response.sendRedirect("chat_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Chat Random</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <p><b><%=chat.name%></b></p>
            <p><%=FormatUtils.toHtml(chat.content)%></p>
        </div>
    </div>
</div>
<button type="button" class="btn btn-default" id="url_back_btn">Back</button>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
