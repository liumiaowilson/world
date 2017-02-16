<%@ page import="org.wilson.world.menu.*" %>
<%
String page_title = "Active Menu";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Active Menu</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Class</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<ActiveMenu> menus = MenuManager.getInstance().getActiveMenus();
                Collections.sort(menus, new Comparator<ActiveMenu>() {
                    public int compare(ActiveMenu s1, ActiveMenu s2) {
                        return s1.getName().compareTo(s2.getName());
                    }
                });
                for(ActiveMenu menu : menus) {
                %>
                <tr>
                    <td><%=menu.getName()%></td>
                    <td><%=menu.getClass().getCanonicalName()%></td>
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
