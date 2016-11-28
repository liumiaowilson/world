<%@ page import="org.wilson.world.menu.*" %>
<%
String page_title = "Jump Page Ext Menu List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Jump Page Ext Menu List</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Link</th>
                </tr>
            </thead>
            <tbody>
                <%
                Map<String, MenuItem> items = MenuManager.getInstance().getJumpPageExtMenuItems();
                List<String> keys = new ArrayList<String>();
                keys.addAll(items.keySet());
                Collections.sort(keys);
                for(String key : keys) {
                    MenuItem item = items.get(key);
                %>
                <tr>
                    <td><%=key%></td>
                    <td><%=item.link%></td>
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
