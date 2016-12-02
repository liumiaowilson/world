<%@ page import="org.wilson.world.report.*" %>
<%
String page_title = "Report List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Report List</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<ReportBuilder> builders = ReportManager.getInstance().getReportBuilders();
                Collections.sort(builders, new Comparator<ReportBuilder>(){
                    public int compare(ReportBuilder r1, ReportBuilder r2) {
                        return Integer.compare(r1.getId(), r2.getId());
                    }
                });
                for(ReportBuilder builder : builders) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('report_show.jsp?id=<%=builder.getId()%>')"><%=builder.getId()%></a></td>
                    <td><%=builder.getName()%></td>
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
