<%
String page_title = "Weasel Phrase Edit";
%>
<%@ include file="header.jsp" %>
<%
WeaselPhrase weasel_phrase = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
weasel_phrase = WeaselPhraseManager.getInstance().getWeaselPhrase(id);
if(weasel_phrase == null) {
    response.sendRedirect("weasel_phrase_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=weasel_phrase.id%>" disabled>
    </fieldset>
    <div class="well">
        <p><b><%=weasel_phrase.pattern%></b></p>
        <p><i><%=weasel_phrase.usage%></i></p>
    </div>
    <div class="form-group">
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
