<%
String page_title = "Role Random";
%>
<%@ include file="header.jsp" %>
<%
Role role = RoleManager.getInstance().randomRole();
if(role == null) {
    response.sendRedirect("role_overview.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Role Random</h3>
    </div>
    <div class="panel-body">
        <%
        List<RoleDetail> details = RoleDetailManager.getInstance().getRoleDetails(role.id);
        Collections.sort(details, new Comparator<RoleDetail>(){
            public int compare(RoleDetail r1, RoleDetail r2) {
                return r1.name.compareTo(r2.name);
            }
        });
        for(RoleDetail detail : details) {
        %>
        <div class="well">
            <p><b><%=detail.name%></b></p>
            <p><%=detail.content%></p>
        </div>
        <%
        }
        %>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
