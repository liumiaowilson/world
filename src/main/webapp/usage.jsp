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
                            int [] storage_usage = ConsoleManager.getInstance().getStorageUsage();
                            %>
                            name: 'Free',
                            y: <%=storage_usage[1] - storage_usage[0]%>
                            }, {
                            name: 'Used',
                            y: <%=storage_usage[0]%>
                            }]
                    }]
                });
            });
</script>
<%@ include file="footer.jsp" %>
