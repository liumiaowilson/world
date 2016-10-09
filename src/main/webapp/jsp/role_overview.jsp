<%
String page_title = "Role Overview";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Role Overview</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Completeness</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<Role> roles = RoleManager.getInstance().getRoles();
                Collections.sort(roles, new Comparator<Role>(){
                    public int compare(Role r1, Role r2) {
                        return r1.name.compareTo(r2.name);
                    }
                });
                for(Role role : roles) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('role_edit.jsp?id=<%=role.id%>')"><%=role.id%></a></td>
                    <td><%=role.name%></td>
                    <td><%=RoleManager.getInstance().getCompletenessDisplay(role)%></td>
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
