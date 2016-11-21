<%@ page import="org.wilson.world.novel.*" %>
<%
String page_title = "Novel Role Stats";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
List<NovelRoleReport> reports = NovelRoleManager.getInstance().buildReports();
for(NovelRoleReport report : reports) {
%>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title"><%=report.title%></h3>
    </div>
    <div class="panel-body">
        <div id="<%=report.name%>">
        </div>
    </div>
</div>
<%
}
%>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
            $(document).ready(function(){
            <%
            for(NovelRoleReport report : reports) {
            %>
                $('#<%=report.name%>').highcharts({
                    chart: {
                        type: 'pie'
                    },
                    title: {
                        text: '<%=report.title%>'
                    },
                    plotOptions: {
                        series: {
                            dataLabels: {
                                enabled: true,
                                format: '{point.name}: {point.y:.1f}%'
                            }
                        }
                    },
                    series: [{
                        name: '<%=report.name%>',
                        colorByPoint: true,
                        data: [
                            <%
                            Map<String, Double> all = report.data;
                            for(Map.Entry<String, Double> entry : all.entrySet()) {
                                String type = entry.getKey();
                                double pct = entry.getValue();
                            %>
                            {
                                name: '<%=type%>',
                                y: <%=pct%>
                            },
                            <%
                            }
                            %>
                        ]
                    }]
                });
            <%
            }
            %>
            });
</script>
<%@ include file="footer.jsp" %>
