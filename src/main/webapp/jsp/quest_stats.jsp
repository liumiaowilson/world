<%@ page import="org.wilson.world.quest.*" %>
<%
String page_title = "Quest Statistics";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Quest Statistics</h3>
    </div>
    <div class="panel-body">
        <div id="quest_type">
        </div>
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Average Period</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<QuestFrequency> freqs = QuestManager.getInstance().getQuestFrequencies();
                Collections.sort(freqs, new Comparator<QuestFrequency>(){
                    public int compare(QuestFrequency f1, QuestFrequency f2) {
                        return f1.name.compareTo(f2.name);
                    }
                });
                for(QuestFrequency freq : freqs) {
                %>
                <tr>
                    <td><%=freq.name%></td>
                    <td><%=TimeUtils.getTimeReadableString(freq.period)%></td>
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
                $('#quest_type').highcharts({
                    chart: {
                        type: 'pie'
                    },
                    title: {
                        text: 'Quest Types in One Month'
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
                        name: 'Quest Types',
                        colorByPoint: true,
                        data: [
                            <%
                            Map<String, Double> all = QuestManager.getInstance().getQuestStats();
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
