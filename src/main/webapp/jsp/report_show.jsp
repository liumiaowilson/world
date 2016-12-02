<%@ page import="org.wilson.world.report.*" %>
<%@ include file="header.jsp" %>
<%
ReportBuilder builder = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
builder = ReportManager.getInstance().getReportBuilder(id);
if(builder == null) {
    response.sendRedirect("report_list.jsp");
    return;
}

ReportData data = builder.build();
if(data == null) {
    response.sendRedirect("report_list.jsp");
    return;
}
String page_title = data.getTitle();
List<List<String>> rows = data.getRows();
if(rows.isEmpty()) {
    response.sendRedirect("report_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title"><%=page_title%></h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <%
                    List<String> header = rows.get(0);
                    for(String headerItem : header) {
                    %>
                    <th><%=headerItem%></th>
                    <%
                    }
                    %>
                </tr>
            </thead>
            <tbody>
                <%
                for(int i = 1; i < rows.size(); i++) {
                    List<String> row = rows.get(i);
                %>
                <tr>
                    <%
                    for(String rowItem : row) {
                    %>
                    <td><%=rowItem%></td>
                    <%
                    }
                    %>
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
