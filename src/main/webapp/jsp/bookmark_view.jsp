<%
String page_title = "Bookmark View";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
List<String> groups = BookmarkManager.getInstance().getBookmarkGroups();
Collections.sort(groups);
for(String group : groups) {
%>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title"><%=group%></h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <%
            List<Bookmark> bookmarks = BookmarkManager.getInstance().getBookmarksByGroup(group);
            Collections.sort(bookmarks, new Comparator<Bookmark>(){
                public int compare(Bookmark b1, Bookmark b2) {
                    return b1.name.compareTo(b2.name);
                }
            });
            for(Bookmark bookmark : bookmarks) {
            %>
            <a href="<%=bookmark.url%>" class="list-group-item"><%=bookmark.name%></a>
            <%
            }
            %>
        </div>
    </div>
</div>
<%
}
%>
<%@ include file="import_script.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
