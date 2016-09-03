<%
String page_title = "Stopwatch";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_timecircles.jsp" %>
<%@ include file="navbar.jsp" %>
<div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Stopwatch</h3>
        </div>
        <div class="panel-body">
            <div id="stopwatch"></div>
            <button class="btn btn-success" id="start_btn">Start</button>
            <button class="btn btn-danger" id="stop_btn" style="display:none">Stop</button>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_timecircles.jsp" %>
<script>
            $('#start_btn').click(function(){
                $('#stopwatch').TimeCircles().start();
                $('#start_btn').hide();
                $('#stop_btn').show();
            });
            $('#stop_btn').click(function(){
                $('#stopwatch').TimeCircles().stop();
                $('#stop_btn').hide();
            });
</script>
<%@ include file="footer.jsp" %>
