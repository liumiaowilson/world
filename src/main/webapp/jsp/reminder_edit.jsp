<%
String page_title = "Reminder Edit";
%>
<%@ include file="header.jsp" %>
<%
Reminder reminder = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
reminder = ReminderManager.getInstance().getReminder(id);
if(reminder == null) {
    response.sendRedirect("reminder_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=reminder.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=reminder.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="message">Message</label>
        <textarea class="form-control" id="message" rows="5" maxlength="200" placeholder="Enter message"><%=reminder.message%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="hours">Hours</label>
        <input type="number" class="form-control" id="hours" placeholder="Enter hours" value="<%=reminder.hours%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="minutes">Minutes</label>
        <input type="number" class="form-control" id="minutes" placeholder="Enter minutes" value="<%=reminder.minutes%>" required>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteReminder()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteReminder() {
                bootbox.confirm("Are you sure to delete this reminder?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/reminder/delete?id=" + id), function(data){
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
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var message = $('#message').val();
                        if(!message) {
                            message = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/reminder/update"), { id: $('#id').val(), name: $('#name').val(), message: $('#message').val(), 'hours': $('#hours').val(), 'minutes': $('#minutes').val() }, function(data) {
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
