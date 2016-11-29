<%@ page import="org.wilson.world.chart.*" %>
<%
String page_title = "Chart List";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Chart List</h3>
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
                List<ChartProvider> providers = ChartManager.getInstance().getChartProviders();
                Collections.sort(providers, new Comparator<ChartProvider>(){
                    public int compare(ChartProvider p1, ChartProvider p2) {
                        return p1.getName().compareTo(p2.getName());
                    }
                });
                for(ChartProvider provider : providers) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('chart_view.jsp?id=<%=provider.getId()%>')"><%=provider.getId()%></a></td>
                    <td><%=provider.getName()%></td>
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
