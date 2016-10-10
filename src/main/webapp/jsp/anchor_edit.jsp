<%
String page_title = "Anchor Edit";
%>
<%@ include file="header.jsp" %>
<%
Anchor anchor = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
anchor = AnchorManager.getInstance().getAnchor(id);
if(anchor == null) {
    response.sendRedirect("anchor_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=anchor.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=anchor.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <div class="form-group">
        <label for="type">Type</label>
        <select class="combobox form-control" id="type">
            <option></option>
            <%
            List<String> types = AnchorManager.getInstance().getAnchorTypes();
            for(String type : types) {
                String selectedStr = type.equals(anchor.type) ? "selected" : "";
            %>
            <option value="<%=type%>" <%=selectedStr%>><%=type%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="stimuli">Stimuli</label>
        <textarea class="form-control" id="stimuli" rows="5" maxlength="200" placeholder="Enter stimuli" required><%=anchor.stimuli%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="response">Response</label>
        <textarea class="form-control" id="response" rows="5" maxlength="200" placeholder="Enter response" required><%=anchor.response%></textarea>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteAnchor()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteAnchor() {
                bootbox.confirm("Are you sure to delete this anchor?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/anchor/delete?id=" + id), function(data){
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
                        $.post(getAPIURL("api/anchor/update"), { id: $('#id').val(), name: $('#name').val(), stimuli: $('#stimuli').val(), 'type': $('#type').val(), 'response': $('#response').val() }, function(data) {
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
