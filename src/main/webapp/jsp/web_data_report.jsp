<%@ page import="org.wilson.world.web.*" %>
<%
String page_title = "Web Data Size Report";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Web Data Size Report</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Ratio(Per Hour)</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<DataSizeReportInfo> infos = WebManager.getInstance().getDataSizeReport();
                for(DataSizeReportInfo info : infos) {
                %>
                <tr>
                    <td><%=info.name%></td>
                    <td><%=info.ratio%></td>
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
