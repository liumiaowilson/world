<%
String page_title = "Sleep Exercise";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_timecircles.jsp" %>
<%@ include file="navbar.jsp" %>
<div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Sleep Exercise</h3>
        </div>
        <div class="panel-body">
            <%
            if(SleepManager.getInstance().isInSleep()) {
                TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
                String startTime = SleepManager.getInstance().getStartTimeDisplay(null, tz);
            %>
            <div id="stopwatch" data-date="<%=startTime%>"></div>
            <div id="end_form">
                <fieldset class="form-group">
                    <label for="quality">Quality</label>
                    <input type="number" class="form-control" id="quality" placeholder="Enter quality" value="50" required>
                </fieldset>
                <fieldset class="form-group">
                    <label for="dreams">Num Of Dreams</label>
                    <input type="number" class="form-control" id="dreams" placeholder="Enter dreams" value="0" required>
                </fieldset>
            </div>
            <button class="btn btn-success" id="start_btn" style="display:none">Start</button>
            <button class="btn btn-danger" id="end_btn">End</button>
            <%
            }
            else {
            %>
            <div id="stopwatch"></div>
            <div id="end_form" style="display:none">
                <fieldset class="form-group">
                    <label for="quality">Quality</label>
                    <input type="number" class="form-control" id="quality" placeholder="Enter quality" value="50" required>
                </fieldset>
                <fieldset class="form-group">
                    <label for="dreams">Num Of Dreams</label>
                    <input type="number" class="form-control" id="dreams" placeholder="Enter dreams" value="0" required>
                </fieldset>
            </div>
            <button class="btn btn-success" id="start_btn">Start</button>
            <button class="btn btn-danger" id="end_btn" style="display:none">End</button>
            <%
            }
            %>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_timecircles.jsp" %>
<script>
            <%
            if(SleepManager.getInstance().isInSleep()) {
            %>
            $('#stopwatch').TimeCircles();
            <%
            }
            %>
            $('#start_btn').click(function(){
                $.get(getAPIURL("api/sleep/start_sleep"), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        $('#stopwatch').TimeCircles().start();
                        $('#start_btn').hide();
                        $('#end_form').show();
                        $('#end_btn').show();
                    }
                    else {
                        showDanger(msg);
                    }
                });
            });
            $('#end_btn').click(function(){
                $.post(getAPIURL("api/sleep/end_sleep"), { 'quality': $('#quality').val(), 'dreams': $('#dreams').val() }, function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        $('#stopwatch').TimeCircles().stop();
                        $('#end_btn').hide();
                    }
                    else {
                        showDanger(msg);
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
