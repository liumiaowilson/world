<%@ page import="org.wilson.world.manga.*" %>
<%
String page_title = "Manga Report";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Manga Report</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Completeness(%)</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<Manga> mangas = MangaManager.getInstance().getMangas();
                Collections.sort(mangas, new Comparator<Manga>(){
                    public int compare(Manga m1, Manga m2) {
                        return m1.name.compareTo(m2.name);
                    }
                });
                for(Manga manga : mangas) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('manga_edit.jsp?id=<%=manga.id%>')"><%=manga.id%></a></td>
                    <td><%=manga.name%></td>
                    <td><%=MangaManager.getInstance().getCompletePercentage(manga)%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
