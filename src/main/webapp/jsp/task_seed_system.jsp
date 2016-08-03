<%@ page import="org.wilson.world.task.*" %>
<%
String page_title = "System Task Generator";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">System Task Generator</h3>
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
                Map<String, TaskGenerator> map = TaskSeedManager.getInstance().getTaskGenerators();
                List<String> keys = new ArrayList<String>(map.keySet());
                Collections.sort(keys);
                for(String key : keys) {
                %>
                <tr>
                    <td><%=key%></td>
                    <td><%=map.get(key).getClass().getCanonicalName()%></td>
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
