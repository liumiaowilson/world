<%
String page_title = "Idea Edit";
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
}
idea = IdeaManager.getInstance().getIdea(id);
if(idea == null) {
    response.sendRedirect("idea_list.jsp");
    return;
}
boolean marked = MarkManager.getInstance().isMarked("idea", String.valueOf(idea.id));
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=idea.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=idea.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=idea.content%></textarea>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="convertIdea()">To Task</a></li>
                <li role="separator" class="divider"></li>
                <%
                if(marked) {
                %>
                <li><a href="javascript:void(0)" onclick="unmarkIdea()">Unmark</a></li>
                <%
                }
                else {
                %>
                <li><a href="javascript:void(0)" onclick="markIdea()">Mark</a></li>
                <%
                }
                %>
                <li role="separator" class="divider"></li>
                <li><a href="javascript:void(0)" onclick="splitIdea()">Split</a></li>
                <%
                boolean hasMarked = MarkManager.getInstance().hasMarked("idea");
                String disabled = (hasMarked ? "" : "disabled");
                %>
                <li class="<%=disabled%>"><a href="javascript:void(0)" onclick="mergeIdea()">Merge</a></li>
                <li role="separator" class="divider"></li>
                <li><a href="javascript:void(0)" onclick="deleteIdea()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function convertIdea() {
                var id = $('#id').val();
                $.get(getAPIURL("api/task/convert?id=" + id), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        var task_id = data.result.data.id;
                        jumpTo("task_edit.jsp?id=" + task_id);
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }
            function splitIdea() {
                var id = $('#id').val();
                jumpTo("idea_split.jsp?id=" + id);
            }
            function mergeIdea() {
                var id = $('#id').val();
                jumpTo("idea_merge.jsp?id=" + id);
            }
            function markIdea() {
                var id = $('#id').val();
                $.get(getAPIURL("api/item/mark?type=idea&id=" + id), function(data){
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
            function unmarkIdea() {
                var id = $('#id').val();
                $.get(getAPIURL("api/item/unmark?type=idea&id=" + id), function(data){
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
            function deleteIdea() {
                bootbox.confirm("Are you sure to delete this idea?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/idea/delete?id=" + id), function(data){
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
                        var content = $('#content').val();
                        if(!content) {
                            content = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/idea/update"), { id: $('#id').val(), name: $('#name').val(), content: $('#content').val()}, function(data) {
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
