<%@ page import="org.wilson.world.chart.*" %>
<%@ include file="header.jsp" %>
<%
ChartProvider provider = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
provider = ChartManager.getInstance().getChartProvider(id);
if(provider == null) {
    response.sendRedirect("char_list.jsp");
    return;
}

ChartData data = provider.getChartData();
String chart_name = data.getName();
String page_title = data.getTitle();
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title"><%=page_title%></h3>
    </div>
    <div class="panel-body">
        <div id="<%=chart_name%>">
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
            $(document).ready(function(){
                $('#<%=chart_name%>').highcharts({
                    chart: {
                        type: 'pie'
                    },
                    title: {
                        text: '<%=page_title%>'
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
                        name: '<%=chart_name%>',
                        colorByPoint: true,
                        data: [
                            <%
                            Map<String, Double> all = (Map<String, Double>)data.getData();
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
