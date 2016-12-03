<%@ page import="org.wilson.world.proxy.*" %>
<%
String page_title = "Dynamic Proxy Provider";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Dynamic Proxy Provider</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th># of Dynamic Proxies</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<DynamicProxyProvider> providers = ProxyManager.getInstance().getDynamicProxyProviders();
                Collections.sort(providers, new Comparator<DynamicProxyProvider>(){
                    public int compare(DynamicProxyProvider p1, DynamicProxyProvider p2) {
                        return Integer.compare(p1.getId(), p2.getId());
                    }
                });
                for(DynamicProxyProvider provider : providers) {
                %>
                <tr>
                    <td><%=provider.getId()%></td>
                    <td><%=provider.getName()%></td>
                    <td><%=provider.getProxies().size()%></td>
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
