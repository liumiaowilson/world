<%
String page_title = "Scenario Edit";
%>
<%@ include file="header.jsp" %>
<%
Scenario scenario = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
scenario = ScenarioManager.getInstance().getScenario(id);
if(scenario == null) {
    response.sendRedirect("scenario_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=scenario.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=scenario.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="stimuli">Stimuli</label>
        <textarea class="form-control" id="stimuli" rows="5" maxlength="400" placeholder="Enter detailed stimuli" required><%=scenario.stimuli%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="reaction">Reaction</label>
        <textarea class="form-control" id="reaction" rows="5" maxlength="400" placeholder="Enter detailed reaction" required><%=scenario.reaction%></textarea>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteScenario()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteScenario() {
                bootbox.confirm("Are you sure to delete this scenario?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/scenario/delete?id=" + id), function(data){
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
                        var stimuli = $('#stimuli').val();
                        if(!stimuli) {
                            stimuli = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/scenario/update"), { id: $('#id').val(), name: $('#name').val(), stimuli: $('#stimuli').val(), 'reaction': $('#reaction').val()}, function(data) {
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
