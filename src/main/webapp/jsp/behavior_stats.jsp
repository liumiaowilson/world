<%@ page import="org.wilson.world.behavior.*" %>
<%
String page_title = "Behavior Statistics";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Behavior Statistics</h3>
    </div>
    <div class="panel-body">
        <div id="behavior_type">
        </div>
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Average Period</th>
                    <th>Last Occurrence</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<BehaviorFrequency> freqs = BehaviorManager.getInstance().getBehaviorFrequencies();
                Collections.sort(freqs, new Comparator<BehaviorFrequency>(){
                    public int compare(BehaviorFrequency f1, BehaviorFrequency f2) {
                        return f1.name.compareTo(f2.name);
                    }
                });
                for(BehaviorFrequency freq : freqs) {
                %>
                <tr>
                    <td><%=freq.name%></td>
                    <td><%=TimeUtils.getTimeReadableString(freq.period)%></td>
                    <td><%=freq.lastStr%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
            $(document).ready(function(){
                $('#behavior_type').highcharts({
                    chart: {
                        type: 'pie'
                    },
                    title: {
                        text: 'Behavior Types in One Month'
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
                        name: 'Behavior Types',
                        colorByPoint: true,
                        data: [
                            <%
                            Map<String, Double> all = BehaviorManager.getInstance().getBehaviorStats();
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
            });
</script>
<%@ include file="footer.jsp" %>
