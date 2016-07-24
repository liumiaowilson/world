<%@ page import="org.wilson.world.status.*" %>
<%
String page_title = "Status From System";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Status From System</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<IStatus> statuses = StatusManager.getInstance().getSystemStatuses();
                Collections.sort(statuses, new Comparator<IStatus>(){
                    public int compare(IStatus s1, IStatus s2) {
                        return s1.getName().compareTo(s2.getName());
                    }
                });
                for(IStatus status : statuses) {
                %>
                <tr>
                    <td><%=status.getName()%></td>
                    <td><%=status.getDescription()%></td>
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
