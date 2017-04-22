<%
String page_title = "Calendar";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_calendar.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Calendar</h3>
    </div>
    <div class="panel-body">
        <div id='calendar'></div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_calendar.jsp" %>
<script>
$(document).ready(function() {
    $('#calendar').fullCalendar({
        header: {
            left: 'prev,next today',
            center: 'title',
            right: 'month,basicWeek,basicDay'
        },
        editable: true,
        eventLimit: true, // allow "more" link when too many events
        events: [
            <%
            TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
            List<CalendarEvent> events = FestivalDataManager.getInstance().getCalendarEvents(tz);
            for(CalendarEvent event : events) {
            %>
            {
                title: "<%=event.title%>",
                start: "<%=event.start%>",
                url: "<%=event.url == null ? "" : event.url%>"
            },
            <%
            }
            %>
        ],
        eventClick: function(event) {
            if(event.url) {
                window.open(event.url);
                return false;
            }
        }
    });
});
</script>
<%@ include file="footer.jsp" %>
