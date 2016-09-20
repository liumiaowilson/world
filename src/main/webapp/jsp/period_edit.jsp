<%
String page_title = "Period Edit";
TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
%>
<%@ include file="header.jsp" %>
<%
Period period = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
period = PeriodManager.getInstance().getPeriod(id);
if(period == null) {
    response.sendRedirect("period_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datepicker.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=period.id%>" disabled>
    </fieldset>
    <div class="form-group">
        <label for="status">Status</label>
        <select class="combobox form-control" id="status">
            <option></option>
            <%
            List<String> statuses = PeriodManager.getInstance().getPeriodStatuses();
            for(String status : statuses) {
                String selectedStr = status.equals(period.status) ? "selected" : "";
            %>
            <option value="<%=status%>" <%=selectedStr%>><%=status%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="time">Time</label>
        <input type="text" class="form-control datepicker" id="time" placeholder="Enter time" value="<%=TimeUtils.toDateString(period.time, tz)%>" required>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deletePeriod()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datepicker.jsp" %>
<script>
            function deletePeriod() {
                bootbox.confirm("Are you sure to delete this period?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/period/delete?id=" + id), function(data){
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
                $('.combobox').combobox();
                $('.datepicker').datepicker({
                    format: 'yyyy-mm-dd'
                });
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        l.ladda('start');
                        $.post(getAPIURL("api/period/update"), { id: $('#id').val(), status: $('#status').val(), "time": $('#time').val()}, function(data) {
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
