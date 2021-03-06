<%
String page_title = "Reaction Edit";
%>
<%@ include file="header.jsp" %>
<%
Reaction reaction = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
reaction = ReactionManager.getInstance().getReaction(id);
if(reaction == null) {
    response.sendRedirect("reaction_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=reaction.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=reaction.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="condition">Condition</label>
        <textarea class="form-control" id="condition" rows="5" maxlength="200" placeholder="Enter condition" required><%=reaction.condition%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="result">Result</label>
        <textarea class="form-control" id="result" rows="5" maxlength="200" placeholder="Enter result" required><%=reaction.result%></textarea>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteReaction()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteReaction() {
                bootbox.confirm("Are you sure to delete this reaction?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/reaction/delete?id=" + id), function(data){
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
                        var condition = $('#condition').val();
                        if(!condition) {
                            condition = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/reaction/update"), { id: $('#id').val(), name: $('#name').val(), condition: $('#condition').val(), 'result': $('#result').val() }, function(data) {
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
