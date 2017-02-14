<%@ page import="org.wilson.world.reference.*" %>
<%
String page_title = "Reference Provider";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Reference Provider</h3>
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
                List<ReferenceProvider> providers = ReferenceManager.getInstance().getReferenceProviders();
                Collections.sort(providers, new Comparator<ReferenceProvider>() {
                    public int compare(ReferenceProvider s1, ReferenceProvider s2) {
                        return s1.getName().compareTo(s2.getName());
                    }
                });
                for(ReferenceProvider provider : providers) {
                %>
                <tr>
                    <td><%=provider.getName()%></td>
                    <td><%=provider.getClass().getCanonicalName()%></td>
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
