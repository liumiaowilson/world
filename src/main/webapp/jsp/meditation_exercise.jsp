<%
String page_title = "Meditation Exercise";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_timecircles.jsp" %>
<%@ include file="navbar.jsp" %>
<div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Meditation Exercise</h3>
        </div>
        <div class="panel-body">
            <div id="meditation_stopwatch"></div>
            <button type="button" class="btn btn-primary" id="done_btn">Done</button>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_timecircles.jsp" %>
<script>
            $(document).ready(function(){
                var startTime = new Date().getTime();
                $('#meditation_stopwatch').TimeCircles();

                $('#done_btn').click(function(){
                    var stopTime = new Date().getTime();
                    $('#meditation_stopwatch').TimeCircles().stop();
                    $.post(getAPIURL("api/meditation/create"), { 'time': startTime, 'duration': (stopTime - startTime) }, function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });
            });
</script>
<%@ include file="footer.jsp" %>
