<%
String page_title = "Novel Stage Edit";
%>
<%@ include file="header.jsp" %>
<%
NovelStage novel_stage = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
novel_stage = NovelStageManager.getInstance().getNovelStage(id);
if(novel_stage == null) {
    response.sendRedirect("novel_stage_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=novel_stage.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=novel_stage.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=novel_stage.description%></textarea>
    </fieldset>
    <div class="form-group">
        <label for="previousId">Previous Stage</label>
        <select class="combobox form-control" id="previousId">
            <option></option>
            <%
            List<NovelStage> stages = NovelStageManager.getInstance().getNovelStages();
            Collections.sort(stages, new Comparator<NovelStage>(){
                public int compare(NovelStage s1, NovelStage s2) {
                    return s1.name.compareTo(s2.name);
                }
            });
            for(NovelStage stage : stages) {
                String selectedStr = novel_stage.previousId == stage.id ? "selected" : "";
            %>
            <option value="<%=stage.id%>" <%=selectedStr%>><%=stage.name%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="condition">Condition</label>
        <div class="form-control" id="condition"><%=novel_stage.condition == null ? "" : novel_stage.condition%></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="preCode">Pre Code</label>
        <div class="form-control" id="preCode"><%=novel_stage.preCode == null ? "" : novel_stage.preCode%></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="postCode">Post Code</label>
        <div class="form-control" id="postCode"><%=novel_stage.postCode == null ? "" : novel_stage.postCode%></div>
    </fieldset>
    <div class="form-group">
        <label for="status">Status</label>
        <select class="combobox form-control" id="status">
            <option></option>
            <%
            List<String> statuses = NovelStageManager.getInstance().getStatuses();
            for(String status : statuses) {
                String selectedStr = status.equals(novel_stage.status) ? "selected" : "";
            %>
            <option value="<%=status%>" <%=selectedStr%>><%=status%></option>
            <%
            }
            %>
        </select>
    </div>
    <div class="form-group">
        <label for="image">Image</label>
        <select class="combobox form-control" id="image">
            <option></option>
            <%
            List<String> imageNames = ImageManager.getInstance().getImageRefNames();
            Collections.sort(imageNames);
            for(String imageName : imageNames) {
                String selectedStr = imageName.equals(novel_stage.image) ? "selected" : "";
            %>
            <option value="<%=imageName%>" <%=selectedStr%>><%=imageName%></option>
            <%
            }
            %>
        </select>
    </div>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteNovelStage()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
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

            function deleteNovelStage() {
                bootbox.confirm("Are you sure to delete this novel stage?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/novel_stage/delete?id=" + id), function(data){
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
                $('.combobox').combobox();

                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var description = $('#description').val();
                        if(!description) {
                            description = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/novel_stage/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), 'previousId': $('#previousId').val(), 'status': $('#status').val(), 'image': $('#image').val(), 'condition': condition.getValue(), 'preCode': preCode.getValue(), 'postCode': postCode.getValue() }, function(data) {
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
