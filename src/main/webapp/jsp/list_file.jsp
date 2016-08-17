<%@ page import="org.wilson.world.console.*" %>
<%
String page_title = "Data File";
String data_path = request.getParameter("path");
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Data File</h3>
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
                List<FileInfo> infos = ConsoleManager.getInstance().listFiles(data_path);
                for(FileInfo info : infos) {
                %>
                <tr>
                    <%
                    if(info.name.endsWith("/")) {
                    %>
                    <td><a href="javascript:jumpTo('list_file.jsp?path=<%=info.path%>')"><%=info.name%></a></td>
                    <%
                    }
                    else {
                    %>
                    <td><%=info.name%></td>
                    <%
                    }
                    %>
                    <td><%=info.size%></td>
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
