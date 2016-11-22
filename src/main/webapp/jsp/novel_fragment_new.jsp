<%@ page import="org.wilson.world.novel.*" %>
<%
String page_title = "Novel Fragment New";
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
    <div class="form-group">
        <label for="stageId">Stage</label>
        <select class="combobox form-control" id="stageId" required>
            <option></option>
            <%
            List<NovelStage> stages = NovelStageManager.getInstance().getNovelStages();
            Collections.sort(stages, new Comparator<NovelStage>(){
                public int compare(NovelStage s1, NovelStage s2) {
                    return s1.name.compareTo(s2.name);
                }
            });
            for(NovelStage stage : stages) {
            %>
            <option value="<%=stage.id%>"><%=stage.name%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="condition">Condition</label>
        <div class="form-control" id="condition"></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="preCode">Pre Code</label>
        <div class="form-control" id="preCode"></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="10" placeholder="Enter detailed description"></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="postCode">Post Code</label>
        <div class="form-control" id="postCode"></div>
    </fieldset>
    <div class="form-group">
        <label for="image">Image</label>
        <select class="combobox form-control" id="image">
            <option></option>
            <%
            List<String> imageNames = NovelConfig.getInstance().getImageRefNames();
            Collections.sort(imageNames);
            for(String imageName : imageNames) {
            %>
            <option value="<%=imageName%>"><%=imageName%></option>
            <%
            }
            %>
        </select>
    </div>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_new_btn"><span class="ladda-label">Save And New</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<input type="hidden" id="create_new" value="false"/>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var condition = ace.edit("condition");
            condition.setTheme("ace/theme/monokai");
            condition.getSession().setMode("ace/mode/javascript");
            $("#condition").css("width", "100%").css("height", "100");

            var preCode = ace.edit("preCode");
            preCode.setTheme("ace/theme/monokai");
            preCode.getSession().setMode("ace/mode/javascript");
            $("#preCode").css("width", "100%").css("height", "100");

            var postCode = ace.edit("postCode");
            postCode.setTheme("ace/theme/monokai");
            postCode.getSession().setMode("ace/mode/javascript");
            $("#postCode").css("width", "100%").css("height", "100");

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
                        $.post(getAPIURL("api/novel_fragment/create"), { name: $('#name').val(), 'content': content, 'stageId': $('#stageId').val(), 'condition': condition.getValue(), 'preCode': preCode.getValue(), 'postCode': postCode.getValue(), 'image': $('#image').val() }, function(data) {
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
