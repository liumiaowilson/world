<%
String page_title = "Web Data";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Web Data</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Size</th>
                </tr>
            </thead>
            <tbody>
                <%
                Map<String, Integer> data = WebManager.getInstance().getDataSizeStats();
                List<String> keys = new ArrayList<String>(data.keySet());
                Collections.sort(keys);
                for(String key : keys) {
                %>
                <tr>
                    <td><%=key%></td>
                    <td><%=data.get(key)%></td>
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
