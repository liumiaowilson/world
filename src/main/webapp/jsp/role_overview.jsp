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
                    <th>Action</th>
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
                    <td>
                        <div class="btn-group">
                            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                Action <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu">
                                <li><a href="javascript:jumpTo('role_view.jsp?id=<%=role.id%>')">View</a></li>
                                <li><a href="javascript:jumpTo('role_update.jsp?id=<%=role.id%>')">Edit</a></li>
                            </ul>
                        </div>
                    </td>
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
