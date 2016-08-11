<%
String page_title = "Article Speed Metrics";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Article Speed Metrics</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <%
                int [] array = ArticleManager.getInstance().getArrayOfWPM();
                if(array != null) {
                %>
                <tr>
                    <td>Average Speed(Word Per Minute)</td>
                    <td><%=array[0]%></td>
                </tr>
                <tr>
                    <td>Minimum Speed(Word Per Minute)</td>
                    <td><%=array[1]%></td>
                </tr>
                <tr>
                    <td>Maximum Speed(Word Per Minute)</td>
                    <td><%=array[2]%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
        <div id="container">
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
            $(function () {
                $('#container').highcharts({
                    chart: {
                        type: 'scatter',
                        zoomType: 'xy'
                    },
                    title: {
                        text: 'Article Speed'
                    },
                    xAxis: {
                        title: {
                            enabled: true,
                            text: 'Length (num of words)'
                        },
                        startOnTick: true,
                        endOnTick: true,
                        showLastLabel: true
                    },
                    yAxis: {
                        title: {
                            text: 'Time (minutes)'
                        }
                    },
                    plotOptions: {
                        scatter: {
                            marker: {
                                radius: 5,
                                states: {
                                    hover: {
                                        enabled: true,
                                        lineColor: 'rgb(100,100,100)'
                                    }
                                }
                            },
                            states: {
                                hover: {
                                    marker: {
                                        enabled: false
                                    }
                                }
                            },
                            tooltip: {
                                pointFormat: '{point.x} words, {point.y} minutes'
                            }
                        }
                    },
                    series: [{
                        color: 'rgba(223, 83, 83, .5)',
                        data: [
                            <%
                            Map<Integer, Integer> stats = ArticleManager.getInstance().getSpeedStats();
                            for(Map.Entry<Integer, Integer> entry : stats.entrySet()) {
                            %>
                            [<%=entry.getKey()%>, <%=entry.getValue()%>],
                            <%
                            }
                            %>
                        ]
                    }]
                });
            });
</script>
<%@ include file="footer.jsp" %>
