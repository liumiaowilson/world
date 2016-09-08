<%
String page_title = "Sleep Edit";
%>
<%@ include file="header.jsp" %>
<%
Sleep sleep = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
sleep = SleepManager.getInstance().getSleep(id);
TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
sleep = SleepManager.getInstance().loadSleep(sleep, tz);
if(sleep == null) {
    response.sendRedirect("sleep_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datetimepicker.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=sleep.id%>" disabled>
    </fieldset>
    <div class="form-group">
        <label>Start Time</label>
        <div class='input-group date' id='startTimeDateTimePicker'>
            <input type='text' class="form-control" value="<%=sleep.startTimeStr%>" required/>
            <span class="input-group-addon">
                <span class="glyphicon glyphicon-calendar"></span>
            </span>
        </div>
    </div>
    <div class="form-group">
        <label>End Time</label>
        <div class='input-group date' id='endTimeDateTimePicker'>
            <input type='text' class="form-control" value="<%=sleep.endTimeStr%>" required/>
            <span class="input-group-addon">
                <span class="glyphicon glyphicon-calendar"></span>
            </span>
        </div>
    </div>
    <fieldset class="form-group">
        <label for="quality">Quality</label>
        <input type="number" class="form-control" id="quality" placeholder="Enter quality" value="<%=sleep.quality%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="dreams">Num Of Dreams</label>
        <input type="number" class="form-control" id="dreams" placeholder="Enter dreams" value="<%=sleep.dreams%>" required>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteSleep()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datetimepicker.jsp" %>
<script>
            function deleteSleep() {
                bootbox.confirm("Are you sure to delete this sleep?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/sleep/delete?id=" + id), function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                jumpBack();
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            }
            $(document).ready(function(){
                $('#startTimeDateTimePicker').datetimepicker();
                $('#endTimeDateTimePicker').datetimepicker();
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        l.ladda('start');
                        $.post(getAPIURL("api/sleep/update"), { id: $('#id').val(), 'startTime': $('#startTimeDateTimePicker input').val(), 'endTime': $('#endTimeDateTimePicker input').val(), 'quality': $('#quality').val(), 'dreams': $('#dreams').val() }, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                l.ladda('stop');
                                jumpBack();
                            }
                            else {
                                showDanger(msg);
                                l.ladda('stop');
                            }
                        }, "json");
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
