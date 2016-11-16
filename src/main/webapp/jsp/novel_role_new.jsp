<%
String page_title = "Novel Role New";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_editable_table.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="10" placeholder="Enter detailed description"></textarea>
    </fieldset>
    <div class="form-group">
        <label for="definition">Variables</label>
        <table id="definition" class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<NovelVariable> variables = NovelVariableManager.getInstance().getNovelVariables();
                Collections.sort(variables, new Comparator<NovelVariable>(){
                    public int compare(NovelVariable v1, NovelVariable v2) {
                        return v1.name.compareTo(v2.name);
                    }
                });
                for(NovelVariable variable : variables) {
                %>
                <tr>
                    <td id="name"><%=variable.name%></td>
                    <td id="value"><%=variable.defaultValue%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_new_btn"><span class="ladda-label">Save And New</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<input type="hidden" id="create_new" value="false"/>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_editable_table.jsp" %>
<script>
            $(document).ready(function(){
                $.fn.editable.defaults.mode = 'inline';
                $('#definition tbody td#value').editable();

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

                        var vars = {};
                        $('#definition tbody tr').each(function(){
                            $this = $(this);
                            var name = $this.find("#name").text();
                            var value = $this.find("#value").text();
                            if(!name) {
                                validation = "Variable name should be provided.";
                                return;
                            }
                            if(name.length > 20) {
                                validation = "Variable name length should be less than 20.";
                                return;
                            }
                            if(!value) {
                                validation = "Variable value should be provided.";
                                return;
                            }
                            if(value.length > 200) {
                                validation = "Variable value length should be less than 200.";
                                return;
                            }
                            vars[name] = value;
                        });

                        var flag = $('#create_new').val();
                        if("true" == flag) {
                            ln.ladda('start');
                        }
                        else if("false" == flag) {
                            l.ladda('start');
                        }
                        $.post(getAPIURL("api/novel_role/create"), { name: $('#name').val(), 'description': description, 'definition':  JSON.stringify(vars)}, function(data) {
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
