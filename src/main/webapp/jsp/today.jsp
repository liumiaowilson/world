<%@ page import="org.wilson.world.mission.*" %>
<%
String page_title = "Today";
TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Word Of The Day</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <%
            String wotd = (String)WebManager.getInstance().get("word_of_the_day");
            if(wotd == null) {
            %>
            Not found
            <%
            }
            else {
            %>
            <a href="http://www.merriam-webster.com/dictionary/<%=wotd%>"><%=wotd%></a>
            <%
            }
            %>
        </div>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Quote Of The Day</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <%
            String qotd = (String)WebManager.getInstance().get("quote_of_the_day");
            if(qotd == null) {
            %>
            Not found
            <%
            }
            else {
            %>
            <%=qotd%>
            <%
            }
            %>
        </div>
    </div>
</div>
<%
List<Mission> recommendedMissions = MissionManager.getInstance().getRecommendedMissions();
if(!recommendedMissions.isEmpty()) {
%>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Mission Of The Day</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            Easy missions found. <a href="<%=basePath%>/jsp/mission.jsp">Go</a>
        </div>
    </div>
</div>
<%
}
%>
<%
String healthOfToday = HealthManager.getInstance().getSuggestionOfToday(tz);
if(healthOfToday != null && !"".equals(healthOfToday.trim())) {
%>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Health Suggestion Of The Day</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <%=healthOfToday%>
        </div>
    </div>
</div>
<%
}
%>
<button type="button" class="btn btn-primary" id="continue_btn">Continue</button>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $("#continue_btn").click(function(){
                    jumpBack();
                });
            });
</script>
<%@ include file="footer.jsp" %>
