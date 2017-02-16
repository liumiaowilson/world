<%@ page import="org.wilson.world.menu.*" %>
<%
String page_title = "Active Toolbar";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Active Toolbar</h3>
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
                List<ActiveToolbar> toolbars = MenuManager.getInstance().getActiveToolbars();
                Collections.sort(toolbars, new Comparator<ActiveToolbar>() {
                    public int compare(ActiveToolbar s1, ActiveToolbar s2) {
                        return s1.getName().compareTo(s2.getName());
                    }
                });
                for(ActiveToolbar toolbar : toolbars) {
                %>
                <tr>
                    <td><%=toolbar.getName()%></td>
                    <td><%=toolbar.getClass().getCanonicalName()%></td>
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
