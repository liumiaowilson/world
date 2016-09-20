<%
String page_title = "Period New";
TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datepicker.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <div class="form-group">
        <label for="status">Status</label>
        <select class="combobox form-control" id="status">
            <option></option>
            <%
            List<String> statuses = PeriodManager.getInstance().getPeriodStatuses();
            for(String status : statuses) {
            %>
            <option value="<%=status%>"><%=status%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="time">Time</label>
        <input type="text" class="form-control datepicker" id="time" placeholder="Enter time" value="<%=TimeUtils.toDateString(new Date(), tz)%>" required>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_new_btn"><span class="ladda-label">Save And New</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<input type="hidden" id="create_new" value="false"/>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datepicker.jsp" %>
<script>
            $(document).ready(function(){
                $('.combobox').combobox();
                $('.datepicker').datepicker({
                    format: 'yyyy-mm-dd'
                });
                var l = $('#save_btn').ladda();
                var ln = $('#save_new_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();

                        var flag = $('#create_new').val();
                        if("true" == flag) {
                            ln.ladda('start');
                        }
                        else if("false" == flag) {
                            l.ladda('start');
                        }
                        $.post(getAPIURL("api/period/create"), { status: $('#status').val(), 'time': $('#time').val() }, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                if("true" == flag) {
                                    ln.ladda('stop');
                                    jumpCurrent();
                                }
                                else if("false" == flag) {
                                    l.ladda('stop');
                                    jumpBack();
                                }
                            }
                            else {
                                showDanger(msg);
                                if("true" == flag) {
                                    ln.ladda('stop');
                                }
                                else if("false" == flag) {
                                    l.ladda('stop');
                                }
                            }
                        }, "json");
                    }
                });

                $('#save_btn').click(function(){
                    $('#create_new').val("false");
                    $('#form').submit();
                });

                $('#save_new_btn').click(function(){
                    $('#create_new').val("true");
                    $('#form').submit();
                });
            });
</script>
<%@ include file="footer.jsp" %>
