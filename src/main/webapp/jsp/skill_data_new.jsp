<%
String page_title = "Skill Data New";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description"></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="type">Skill Type</label>
        <select class="combobox form-control" id="type" required>
            <option></option>
            <%
            List<String> types = SkillDataManager.getInstance().getSkillTypes();
            Collections.sort(types);
            for(String type : types) {
            %>
            <option value="<%=type%>"><%=type%></option>
            <%
            }
            %>
        </select>
    </fieldset>
    <fieldset class="form-group">
        <label for="scope">Skill Scope</label>
        <select class="combobox form-control" id="scope" required>
            <option></option>
            <%
            List<String> scopes = SkillDataManager.getInstance().getSkillScopes();
            Collections.sort(scopes);
            for(String scope : scopes) {
            %>
            <option value="<%=scope%>"><%=scope%></option>
            <%
            }
            %>
        </select>
    </fieldset>
    <fieldset class="form-group">
        <label for="target">Skill Target</label>
        <select class="combobox form-control" id="target" required>
            <option></option>
            <%
            List<String> targets = SkillDataManager.getInstance().getSkillTargets();
            Collections.sort(targets);
            for(String target : targets) {
            %>
            <option value="<%=target%>"><%=target%></option>
            <%
            }
            %>
        </select>
    </fieldset>
    <fieldset class="form-group">
        <label for="cost">Cost</label>
        <input type="number" class="form-control" id="cost" placeholder="Enter cost" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="cooldown">Cooldown</label>
        <input type="number" class="form-control" id="cooldown" placeholder="Enter cooldown" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="canTrigger">Can Trigger Implementation</label>
        <input type="text" class="form-control" id="canTrigger" maxlength="50" placeholder="Enter can-trigger implementation" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="trigger">Trigger Implementation</label>
        <input type="text" class="form-control" id="trigger" maxlength="50" placeholder="Enter trigger implementation" required>
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
                        var description = $('#description').val();
                        if(!description) {
                            description = $('#name').val();
                        }

                        var flag = $('#create_new').val();
                        if("true" == flag) {
                            ln.ladda('start');
                        }
                        else if("false" == flag) {
                            l.ladda('start');
                        }
                        $.post(getAPIURL("api/skill_data/create"), { name: $('#name').val(), 'description': description, 'type': $('#type').val(), 'scope': $('#scope').val(), 'target': $('#target').val(), 'cost': $('#cost').val(), 'cooldown': $('#cooldown').val(), 'canTrigger': $('#canTrigger').val(), 'trigger': $('#trigger').val()}, function(data) {
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
