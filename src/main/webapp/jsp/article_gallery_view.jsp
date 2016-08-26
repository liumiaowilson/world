<%@ page import="org.wilson.world.article.*" %>
<%
String page_title = "Article Gallery View";
%>
<%@ include file="header.jsp" %>
<%
ArticleItem article_item = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
article_item = ArticleManager.getInstance().getArticleItem(id);
if(article_item == null) {
    response.sendRedirect("article_gallery.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Article Gallery View</h3>
    </div>
    <div class="panel-body">
        <div id="content" class="well">
        </div>
    </div>
</div>
<button type="button" class="btn btn-default" id="url_back_btn">Back</button>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/article/get?id=<%=article_item.id%>"), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);

                        var title = data.result.data.title;
                        var html = data.result.data.html;
                        $('#content').append("<h3>" + title + "</h3>");
                        $('#content').append(html);
                    }
                    else {
                        showDanger(msg);
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
