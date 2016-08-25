<%
String page_title = "Page Visit";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Page Visit</h3>
    </div>
    <div class="panel-body">
        <div id="page_visit">
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
            $(document).ready(function(){
                $('#page_visit').highcharts({
                    chart: {
                        type: 'pie'
                    },
                    title: {
                        text: 'Page Visit'
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
                        name: 'Page Visit',
                        colorByPoint: true,
                        data: [
                            <%
                            Map<String, Double> all = ConsoleManager.getInstance().getPageVisitStats();
                            for(Map.Entry<String, Double> entry : all.entrySet()) {
                                String url = entry.getKey();
                                double pct = entry.getValue();
                            %>
                            {
                                name: '<%=url%>',
                                y: <%=pct%>
                            },
                            <%
                            }
                            %>
                        ]
                    }]
                });
            });
</script>
<%@ include file="footer.jsp" %>
