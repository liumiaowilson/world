<%
String page_title = "Novel Role in Document Stats";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Novel Roles in Documents</h3>
    </div>
    <div class="panel-body">
        <div id="roles">
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
            $(document).ready(function(){
                $('#roles').highcharts({
                    chart: {
                        type: 'pie'
                    },
                    title: {
                        text: 'Novel Roles in Documents'
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
                        name: 'Roles',
                        colorByPoint: true,
                        data: [
                            <%
                            Map<String, Double> all = NovelDocumentManager.getInstance().getNovelRoleInDocStats();
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
