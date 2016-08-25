<%
String page_title = "Response";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Response</h3>
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
                long [] response_stats = ConsoleManager.getInstance().getResponseTimeStats();
                %>
                <tr>
                    <td>Average Response Time</td>
                    <td><%=TimeUtils.getTimeReadableString(response_stats[0])%></td>
                </tr>
                <tr>
                    <td>Min Response Time</td>
                    <td><%=TimeUtils.getTimeReadableString(response_stats[1])%></td>
                </tr>
                <tr>
                    <td>Max Response Time</td>
                    <td><%=TimeUtils.getTimeReadableString(response_stats[2])%></td>
                </tr>
            </tbody>
        </table>
        <div id="response_time_section">
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
            $(document).ready(function(){
                $('#response_time_section').highcharts({
                    chart: {
                        type: 'pie'
                    },
                    title: {
                        text: 'Response Time in Sections'
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
                        name: 'Response Time',
                        colorByPoint: true,
                        data: [
                            <%
                            Map<String, Double> all = ConsoleManager.getInstance().getResponseTimeStatsInSections();
                            for(Map.Entry<String, Double> entry : all.entrySet()) {
                                String section = entry.getKey();
                                double pct = entry.getValue();
                            %>
                            {
                                name: '<%=section%>',
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
