<%@ page import="org.wilson.world.manga.*" %>
<%
String page_title = "Manga Edit";
%>
<%@ include file="header.jsp" %>
<%
Manga manga = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
manga = MangaManager.getInstance().getManga(id);
if(manga == null) {
    response.sendRedirect("manga_list.jsp");
    return;
}
List<String> urls = MangaManager.getInstance().getImageUrls(manga);
int pages = urls.size();
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Manga Edit</h3>
    </div>
    <div class="panel-body">
        <div id="manga" class="carousel slide" data-ride="carousel">
            <!-- Indicators -->
            <ol class="carousel-indicators">
                <%
                for(int i = 0; i < pages; i++) {
                    String active = i == 0 ? "class='active'" : "";
                %>
                <li data-target="#manga" data-slide-to="<%=i%>" <%=active%>></li>
                <%
                }
                %>
            </ol>

            <!-- Wrapper for slides -->
            <div class="carousel-inner" role="listbox">
                <%
                for(int i = 1; i <= pages; i++) {
                    String active = i == 1 ? "active" : "";
                %>
                <div class="item <%=active%>">
                    <img src="<%=urls.get(i - 1)%>" alt="<%=i%>.jpg">
                </div>
                <%
                }
                %>
            </div>

            <!-- Left and right controls -->
            <a class="left carousel-control" href="#manga" role="button" data-slide="prev">
                <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                <span class="sr-only">Previous</span>
            </a>
            <a class="right carousel-control" href="#manga" role="button" data-slide="next">
                <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
                <span class="sr-only">Next</span>
            </a>
        </div>
    </div>
</div>
<div class="form-group">
    <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    <div class="btn-group">
        <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            Action <span class="caret"></span>
        </button>
        <ul class="dropdown-menu">
            <li><a href="javascript:void(0)" onclick="deleteManga()">Delete</a></li>
        </ul>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            function deleteManga() {
                bootbox.confirm("Are you sure to delete this manga?", function(result){
                    if(result) {
                        $.get(getAPIURL("api/manga/delete?id=<%=manga.id%>"), function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                jumpBack();
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            }
</script>
<%@ include file="footer.jsp" %>
