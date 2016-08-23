<%@ page import="org.wilson.world.countdown.*" %>
<%
String page_title = "Countdown";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Countdown</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Countdown</th>
                </tr>
            </thead>
            <tbody>
                <%
                TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
                List<ICountdown> countdowns = CountdownManager.getInstance().getCountdowns(tz);
                Collections.sort(countdowns, new Comparator<ICountdown>(){
                    public int compare(ICountdown c1, ICountdown c2) {
                        return (int)(c1.getTarget().getTime() - c2.getTarget().getTime());
                    }
                });
                for(ICountdown countdown : countdowns) {
                %>
                <tr>
                    <td><%=countdown.getName()%></td>
                    <td><div id="<%=countdown.getName()%>"></div></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_countdown.jsp" %>
<script>
            <%
            for(ICountdown countdown : countdowns) {
            %>
            $(document.getElementById("<%=countdown.getName()%>")).countdown("<%=TimeUtils.toDateString(countdown.getTarget(), tz, "yyyy/MM/dd")%>", function(event) {
                $(this).html("<b>" + event.strftime('%D days %H:%M:%S') + "</b>");
            });
            <%
            }
            %>
</script>
<%@ include file="footer.jsp" %>
