<%@ page import="org.wilson.world.chart.*" %>
<%
String page_title = "Storage Statistics";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
List<PieChartData> dataList = StorageManager.getInstance().getStorageUsagePieChartDatas();
Collections.sort(dataList, new Comparator<PieChartData>(){
    public int compare(PieChartData d1, PieChartData d2) {
        return d1.getTitle().compareTo(d2.getTitle());
    }
});
for(PieChartData data : dataList) {
%>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title"><%=data.getTitle()%></h3>
    </div>
    <div class="panel-body">
        <div id="<%=data.getName()%>">
        </div>
        <button type="button" class="btn btn-default" id="url_back_btn">Download</button>
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
            for(PieChartData data : dataList) {
            %>
                $('#<%=data.getName()%>').highcharts({
                    chart: {
                        type: 'pie'
                    },
                    title: {
                        text: '<%=data.getTitle()%>'
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
                        name: '<%=data.getName()%>',
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
            <%
            }
            %>
            });
</script>
<%@ include file="footer.jsp" %>
