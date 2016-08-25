<%
String page_title = "Statistics";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Statistics</h3>
    </div>
    <div class="panel-body">
        <div id="pie_chart">
        </div>
        <div id="trend">
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
            $(document).ready(function(){
                $('#pie_chart').highcharts({
                    chart: {
                        type: 'pie'
                    },
                    title: {
                        text: 'Expenses in One Month'
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
                        name: 'Expenses',
                        colorByPoint: true,
                        data: [
                            <%
                            Map<String, Double> all = ExpenseItemManager.getInstance().getExpenseItemStats();
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

                $.get(getAPIURL("api/expense_item/trend"), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        $('#trend').highcharts({
                            chart: {
                                zoomType: 'x'
                            },
                            title: {
                                text: 'Trend'
                            },
                            xAxis: {
                                type: 'datetime'
                            },
                            yAxis: {
                                title: {
                                    text: 'Amount'
                                }
                            },
                            legend: {
                                enabled: false
                            },
                            plotOptions: {
                                area: {
                                    fillColor: {
                                        linearGradient: {
                                            x1: 0,
                                            y1: 0,
                                            x2: 0,
                                            y2: 1
                                        },
                                        stops: [
                                            [0, Highcharts.getOptions().colors[0]],
                                            [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                                        ]
                                    },
                                    marker: {
                                        radius: 2
                                    },
                                    lineWidth: 1,
                                    states: {
                                        hover: {
                                            lineWidth: 1
                                        }
                                    },
                                    threshold: null
                                }
                            },

                            series: [{
                                type: 'area',
                                name: 'Expense Item Trend',
                                data: eval(msg)
                            }]
                        });
                    }
                    else {
                        showDanger(msg);
                    }
                }, "json");
            });
</script>
<%@ include file="footer.jsp" %>
