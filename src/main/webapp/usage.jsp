<%@ page import="org.wilson.world.manager.*" %>
<%
String from_url = "usage.jsp";
%>
<%@ include file="header.jsp" %>
<input type="hidden" id="isOpenShiftApp" value="<%=ConfigManager.getInstance().isOpenShiftApp()%>"/>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Storage</h3>
    </div>
    <div class="panel-body">
        <div id="storage">
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Memory</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <td>Name</td>
                    <td>Value</td>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Num of Exceeding Memory Limit Hits</td>
                    <td><%=ConsoleManager.getInstance().getNumOfExceedLimitHits()%></td>
                </tr>
                <tr>
                    <td>Num of Out Of Memory Hits</td>
                    <td><%=ConsoleManager.getInstance().getNumOfOutOfMemoryHits()%></td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_scripts.jsp" %>
<script>
            $(document).ready(function(){
                if($('#isOpenShiftApp').val() != "true") {
                    $('#alert_info').text("Usage data here is fake for non-openshift app hosting.");
                    $('#alert_info').show();
                }

                $('#storage').highcharts({
                    chart: {
                        type: 'pie'
                    },
                    title: {
                        text: 'Storage Quota'
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
                        name: 'Quota',
                        colorByPoint: true,
                        data: [{
                            <%
                            double [] storage_usage = ConsoleManager.getInstance().getStorageUsageDisplay();
                            %>
                            name: 'Free',
                            y: <%=storage_usage[1]%>
                            }, {
                            name: 'Used',
                            y: <%=storage_usage[0]%>
                            }]
                    }]
                });
            });
</script>
<%@ include file="footer.jsp" %>
