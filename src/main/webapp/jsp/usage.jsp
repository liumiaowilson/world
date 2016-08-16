<%
String page_title = "Usage";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<input type="hidden" id="isOpenShiftApp" value="<%=ConfigManager.getInstance().isOpenShiftApp()%>"/>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">System Info</h3>
    </div>
    <div class="panel-body">
        <table id="sys_info_table" class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Up Time</td>
                    <%
                    long upTime = ConsoleManager.getInstance().getUpTime();
                    %>
                    <td><%=TimeUtils.getTimeReadableString(upTime)%></td>
                </tr>
                <tr>
                    <td>Max Memory</td>
                    <%
                    long maxMemory = ConsoleManager.getInstance().maxMemory();
                    %>
                    <td><%=SizeUtils.getSizeReadableString(maxMemory)%></td>
                </tr>
                <tr>
                    <td>Total Memory</td>
                    <%
                    long totalMemory = ConsoleManager.getInstance().totalMemory();
                    %>
                    <td><%=SizeUtils.getSizeReadableString(totalMemory)%></td>
                </tr>
                <tr>
                    <td>Used Memory</td>
                    <%
                    long usedMemory = ConsoleManager.getInstance().usedMemory();
                    %>
                    <td><%=SizeUtils.getSizeReadableString(usedMemory)%></td>
                </tr>
                <tr>
                    <td>Free Memory</td>
                    <%
                    long freeMemory = ConsoleManager.getInstance().freeMemory();
                    %>
                    <td><%=SizeUtils.getSizeReadableString(freeMemory)%></td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Storage</h3>
    </div>
    <div class="panel-body">
        <div id="storage">
        </div>
        <button type="button" class="btn btn-danger ladda-button" data-style="slide-left" id="delete_logs_btn"><span class="ladda-label">Delete Logs</span></button>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Memory</h3>
    </div>
    <div class="panel-body">
        <div id="memory">
        </div>
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
        <button type="button" class="btn btn-info ladda-button" data-style="slide-left" id="release_memory_btn"><span class="ladda-label">Release Memory</span></button>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
            $(document).ready(function(){
                var ld = $('#delete_logs_btn').ladda();
                var lr = $('#release_memory_btn').ladda();

                if($('#isOpenShiftApp').val() != "true") {
                    showInfo("Usage data here is fake for non-openshift app hosting.");
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

                $('#memory').highcharts({
                    chart: {
                        type: 'pie'
                    },
                    title: {
                        text: 'Memory Usage'
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
                        name: 'Usage',
                        colorByPoint: true,
                        data: [{
                            <%
                            double [] memory_usage = ConsoleManager.getInstance().getMemoryUsageDisplay();
                            %>
                            name: 'Free',
                            y: <%=memory_usage[1]%>
                            }, {
                            name: 'Used',
                            y: <%=memory_usage[0]%>
                            }]
                    }]
                });

                $('#delete_logs_btn').click(function(){
                    bootbox.confirm("Are you sure to delete all the logs?", function(result){
                        if(result) {
                            ld.ladda('start');
                            $.get(getAPIURL("api/console/delete_logs"), function(data){
                                var status = data.result.status;
                                var msg = data.result.message;
                                if("OK" == status) {
                                    showSuccess(msg);
                                    ld.ladda('stop');
                                    jumpCurrent();
                                }
                                else {
                                    showDanger(msg);
                                    ld.ladda('stop');
                                }
                            });
                        }
                    });
                });

                $('#release_memory_btn').click(function(){
                    lr.ladda('start');
                    $.get(getAPIURL("api/console/release_memory"), function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                            lr.ladda('stop');
                            jumpCurrent();
                        }
                        else {
                            showDanger(msg);
                            lr.ladda('stop');
                        }
                    });
                });
            });
</script>
<%@ include file="footer.jsp" %>
