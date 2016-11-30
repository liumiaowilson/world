<%@ page import="org.wilson.world.menu.*" %>
<%
String page_title = "Menu Provider";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Menu Provider</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th># of Menu Items</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<JumpPageMenuItemProvider> providers = MenuManager.getInstance().getJumpPageMenuItemProviders();
                Collections.sort(providers, new Comparator<JumpPageMenuItemProvider>(){
                    public int compare(JumpPageMenuItemProvider p1, JumpPageMenuItemProvider p2) {
                        return p1.getName().compareTo(p2.getName());
                    }
                });
                for(JumpPageMenuItemProvider provider : providers) {
                %>
                <tr>
                    <td><%=provider.getName()%></td>
                    <td><%=provider.getSingleMenuItems().size()%></td>
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
