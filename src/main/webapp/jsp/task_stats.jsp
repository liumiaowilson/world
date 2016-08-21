<%
String page_title = "Task Statistics";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Task Statistics</h3>
    </div>
    <div class="panel-body">
        <div id="task_types">
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Task Attr Contexts</h3>
    </div>
    <div class="panel-body">
        <div id="task_attr_contexts">
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Task Attr Types</h3>
    </div>
    <div class="panel-body">
        <div id="task_attr_types">
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
            $(document).ready(function(){
                $('#task_types').highcharts({
                    chart: {
                        type: 'pie'
                    },
                    title: {
                        text: 'Task Types'
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
                        name: 'Task Types',
                        colorByPoint: true,
                        data: [
                            <%
                            Map<String, Double> all = TaskManager.getInstance().getTaskTypeStats();
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

                $('#task_attr_contexts').highcharts({
                    chart: {
                        type: 'pie'
                    },
                    title: {
                        text: 'Task Attr Contexts'
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
                        name: 'Task Attr Contexts',
                        colorByPoint: true,
                        data: [
                            <%
                            all = TaskManager.getInstance().getTaskAttrContextStats();
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

                $('#task_attr_types').highcharts({
                    chart: {
                        type: 'pie'
                    },
                    title: {
                        text: 'Task Attr Types'
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
                        name: 'Task Attr Types',
                        colorByPoint: true,
                        data: [
                            <%
                            all = TaskManager.getInstance().getTaskAttrTypeStats();
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
