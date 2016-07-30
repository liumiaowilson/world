<%
String page_title = "Quest New";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
int id = 0;
try {
    id = Integer.parseInt(request.getParameter("id"));
}
catch(Exception e){}
%>
<form id="form" data-toggle="validator" role="form">
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
            QuestDef selectedDef = null;
            for(QuestDef def : defs) {
                String selected = "";
                if(def.id == id) {
                    selected = "selected";
                    selectedDef = def;
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
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=selectedDef == null ? "" : selectedDef.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="5" maxlength="400" placeholder="Enter detailed description"><%=selectedDef == null ? "" : selectedDef.content%></textarea>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_new_btn"><span class="ladda-label">Save And New</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<input type="hidden" id="create_new" value="false"/>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $('.combobox').combobox();
                var l = $('#save_btn').ladda();
                var ln = $('#save_new_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var content = $('#content').val();
                        if(!content) {
                            content = $('#name').val();
                        }

                        var flag = $('#create_new').val();
                        if("true" == flag) {
                            ln.ladda('start');
                        }
                        else if("false" == flag) {
                            l.ladda('start');
                        }
                        $.post(getAPIURL("api/quest/create"), { name: $('#name').val(), 'content': content, 'defId': $('#defId').val()}, function(data) {
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
