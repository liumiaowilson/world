<%@ page import="org.wilson.world.proxy.*" %>
<%
String page_title = "Dynamic Proxy List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Dynamic Proxy List</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Host</th>
                    <th>Port</th>
                    <th>Echo Time</th>
                    <th>External IP</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<DynamicProxy> proxies = ProxyManager.getInstance().getDynamicProxies();
                for(DynamicProxy proxy : proxies) {
                %>
                <tr>
                    <td><%=proxy.host%></td>
                    <td><%=proxy.port%></td>
                    <td><%=proxy.getEchoTimeDisplay()%></td>
                    <td><%=proxy.getExternalIPDisplay()%></td>
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
