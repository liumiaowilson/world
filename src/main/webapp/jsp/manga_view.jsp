<%
String page_title = "Manga View";

int pages = MangaManager.getInstance().getCurrentPages();
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Manga View</h3>
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
                    <img src="<%=basePath%>/servlet/image?path=manga/<%=i%>.jpg" alt="<%=i%>.jpg">
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
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
