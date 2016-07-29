<%
String page_title = "Script Variable";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Script Variable</h3>
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
                Map<String, Object> bindings = ScriptManager.getInstance().getBindings();
                List<String> keys = new ArrayList<String>(bindings.keySet());
                Collections.sort(keys);
                for(String key : keys) {
                %>
                <tr>
                    <td><%=key%></td>
                    <td><%=String.valueOf(bindings.get(key))%></td>
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
