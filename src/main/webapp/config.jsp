<%@ page import="java.util.*" %>
<%@ page import="org.wilson.world.manager.*" %>
<%
String from_url = "config.jsp";
%>
<%@ include file="header.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Configuration</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<String> confignames = ConfigManager.getInstance().getConfigNames();
                Collections.sort(confignames);
                for(String name : confignames) {
                %>
                <tr>
                    <td><%=name%></td>
                    <td><%=String.valueOf(ConfigManager.getInstance().getConfig(name))%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_scripts.jsp" %>
<%@ include file="footer.jsp" %>
