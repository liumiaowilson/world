<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_timecircles.jsp" %>
<%@ include file="navbar.jsp" %>
<div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Demo</h3>
        </div>
        <div class="panel-body">
            <div id="demo"></div>
            <button class="btn btn-success" id="start_btn">Start</button>
            <button class="btn btn-danger" id="stop_btn">Stop</button>
            <button class="btn btn-info" id="check_btn">Check</button>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_timecircles.jsp" %>
<script>
            $('#start_btn').click(function(){
                $('#demo').TimeCircles().start();
            });
            $('#stop_btn').click(function(){
                $('#demo').TimeCircles().stop();
            });
            $('#check_btn').click(function(){
                alert($('#demo').TimeCircles().getTime());
            });
</script>
<%@ include file="footer.jsp" %>
