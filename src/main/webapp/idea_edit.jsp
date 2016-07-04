<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%
String from_url = "idea_edit.jsp";
%>
<%@ include file="header.jsp" %>
<%
Idea idea = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
    idea = new Idea();
}
idea = IdeaManager.getInstance().getIdea(id);
if(idea == null) {
    idea = new Idea();
}
%>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=idea.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" placeholder="Enter name" value="<%=idea.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="5" placeholde="Enter detailed description" required><%=idea.content%></textarea>
    </fieldset>
    <div class="alert alert-danger" role="alert" id="error">
    </div>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-primary" id="view_all_btn">View All</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="removeIdea()">Remove</a></li>
                <%--<li role="separator" class="divider"></li>--%>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_scripts.jsp" %>
<script>
            function removeIdea() {
                bootbox.confirm("Are you sure to remove this idea?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get("api/idea/remove?id=" + id, function(data){
                            window.location.href = "idea_list.jsp";
                        });
                    }
                });
            }
            $(document).ready(function(){
                $('#error').hide();
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        $('#error').hide();
                        l.ladda('start');
                        $.post("api/idea/update", { id: $('#id').val(), name: $('#name').val(), content: $('#content').val()}, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                $('#alert_success').text(msg);
                                $('#alert_success').show();
                                l.ladda('stop');
                            }
                            else {
                                $('#alert_danger').text(msg);
                                $('#alert_danger').show();
                                l.ladda('stop');
                            }
                        }, "json");
                    }
                });

                $('#view_all_btn').click(function(){
                    window.location.href = "idea_list.jsp";
                });
            });
</script>
<%@ include file="footer.jsp" %>
