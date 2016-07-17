<%
String from_url = "task_attr_rule_new.jsp";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="name">Name</label>
        <select class="combobox form-control" id="name" required autofocus>
            <option></option>
            <%
            List<String> availableNames = TaskAttrRuleManager.getInstance().getAvailableNames();
            Collections.sort(availableNames);
            for(String availableName : availableNames) {
            %>
            <option value="<%=availableName%>"><%=availableName%></option>
            <%
            }
            %>
        </select>
    </fieldset>
    <fieldset class="form-group">
        <label for="priority">Priority</label>
        <input type="number" class="form-control" id="priority" maxlength="20" placeholder="Enter priority" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="policy">Policy</label>
        <select class="combobox form-control" id="policy" required>
            <option></option>
            <option value="normal">normal</option>
            <option value="reversed">reversed</option>
        </select>
        <small class="text-muted">"normal" means smaller goes first. "reversed" means bigger goes first.</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="impl">Implementation</label>
        <input type="text" class="form-control" id="impl" maxlength="50" placeholder="Enter implementation">
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_new_btn"><span class="ladda-label">Save And New</span></button>
        <button type="button" class="btn btn-default" id="view_all_btn">Back</button>
    </div>
</form>
<input type="hidden" id="create_new" value="false"/>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                var l = $('#save_btn').ladda();
                var ln = $('#save_new_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var policy = $('#policy').val();
                        if(!policy) {
                            policy = 'normal';
                        }

                        var flag = $('#create_new').val();
                        if("true" == flag) {
                            ln.ladda('start');
                        }
                        else if("false" == flag) {
                            l.ladda('start');
                        }
                        $.post("api/task_attr_rule/create", { name: $('#name').val(), 'policy': policy, 'priority': $('#priority').val(), 'impl': $('#impl').val()}, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                if("true" == flag) {
                                    ln.ladda('stop');
                                    window.location.href = "task_attr_rule_new.jsp";
                                }
                                else if("false" == flag) {
                                    l.ladda('stop');
                                    window.location.href = "task_attr_rule_list.jsp";
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

                $('#view_all_btn').click(function(){
                    window.location.href = "task_attr_rule_list.jsp";
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
