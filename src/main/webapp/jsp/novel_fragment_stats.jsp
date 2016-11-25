<%@ page import="org.wilson.world.novel.*" %>
<%
String page_title = "Novel Fragment Stats";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Fragments in Stage</h3>
    </div>
    <div class="panel-body">
        <div id="fragments">
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">All Declared Runtime Variables</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Type</th>
                    <th>Values</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<NovelFragmentVarInfo> infos = NovelFragmentManager.getInstance().getAllDeclaredRuntimeVars();
                Collections.sort(infos, new Comparator<NovelFragmentVarInfo>(){
                    public int compare(NovelFragmentVarInfo i1, NovelFragmentVarInfo i2) {
                        return i1.name.compareTo(i2.name);
                    }
                });
                for(NovelFragmentVarInfo info : infos) {
                %>
                <tr>
                    <td><%=info.name%></td>
                    <td><%=info.type%></td>
                    <td><%=info.values%></td>
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
                $('#fragments').highcharts({
                    chart: {
                        type: 'pie'
                    },
                    title: {
                        text: 'Fragments in Stage'
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
                        name: 'Fragments',
                        colorByPoint: true,
                        data: [
                            <%
                            Map<String, Double> all = NovelFragmentManager.getInstance().getStatsByStage();
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
