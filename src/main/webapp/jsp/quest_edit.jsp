<%
String page_title = "Quest Edit";
%>
<%@ include file="header.jsp" %>
<%
Quest quest = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
quest = QuestManager.getInstance().getQuest(id);
if(quest == null) {
    response.sendRedirect("quest_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=quest.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="defId">Quest Def</label>
        <select class="combobox form-control" id="defId">
            <option></option>
            <%
            List<QuestDef> defs = QuestDefManager.getInstance().getQuestDefs();
            Collections.sort(defs, new Comparator<QuestDef>(){
                public int compare(QuestDef d1, QuestDef d2) {
                    return d1.name.compareTo(d2.name);
                }
            });
            for(QuestDef def : defs) {
                String selected = "";
                if(def.id == quest.defId) {
                    selected = "selected";
                }
            %>
            <option value="<%=def.id%>" <%=selected%>><%=def.name%></option>
            <%
            }
            %>
        </select>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=quest.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="5" maxlength="400" placeholder="Enter detailed description" required><%=quest.content%></textarea>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteQuest()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteQuest() {
                bootbox.confirm("Are you sure to delete this quest?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/quest/delete?id=" + id), function(data){
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
                        $.post(getAPIURL("api/quest/update"), { id: $('#id').val(), name: $('#name').val(), content: $('#content').val(), 'defId': $('#defId').val()}, function(data) {
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
