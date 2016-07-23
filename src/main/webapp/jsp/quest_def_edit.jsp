<%
String page_title = "Quest Def Edit";
%>
<%@ include file="header.jsp" %>
<%
QuestDef quest_def = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
quest_def = QuestDefManager.getInstance().getQuestDef(id);
if(quest_def == null) {
    response.sendRedirect("quest_def_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=quest_def.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=quest_def.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=quest_def.content%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="pay">Base Pay</label>
        <input type="number" class="form-control" id="pay" maxlength="20" placeholder="Enter pay" value="<%=quest_def.pay%>" required>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="achieveQuestDef()">Achieve</a></li>
                <li role="separator" class="divider"></li>
                <li><a href="javascript:void(0)" onclick="deleteQuestDef()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function achieveQuestDef() {
                jumpTo("quest_new.jsp?id=<%=quest_def.id%>");
            }
            function deleteQuestDef() {
                bootbox.confirm("Are you sure to delete this quest def?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/quest_def/delete?id=" + id), function(data){
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
                        $.post(getAPIURL("api/quest_def/update"), { id: $('#id').val(), name: $('#name').val(), content: $('#content').val(), 'pay': $('#pay').val()}, function(data) {
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
