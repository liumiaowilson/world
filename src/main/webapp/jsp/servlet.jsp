<%@ page import="org.wilson.world.servlet.*" %>
<%
String page_title = "Active Servlet";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Active Servlet</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Pattern</th>
                    <th>Class</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<ActiveServlet> servlets = ServletManager.getInstance().getActiveServlets();
                Collections.sort(servlets, new Comparator<ActiveServlet>() {
                    public int compare(ActiveServlet s1, ActiveServlet s2) {
                        return s1.getName().compareTo(s2.getName());
                    }
                });
                for(ActiveServlet servlet : servlets) {
                %>
                <tr>
                    <td><%=servlet.getName()%></td>
                    <td><%=servlet.getPattern()%></td>
                    <td><%=servlet.getClass().getCanonicalName()%></td>
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
