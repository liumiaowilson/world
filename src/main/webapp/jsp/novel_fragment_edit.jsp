<%
String page_title = "Novel Fragment Edit";
%>
<%@ include file="header.jsp" %>
<%
NovelFragment novel_fragment = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
novel_fragment = NovelFragmentManager.getInstance().getNovelFragment(id);
if(novel_fragment == null) {
    response.sendRedirect("novel_fragment_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=novel_fragment.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=novel_fragment.name%>" required autofocus>
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
                String selectedStr = novel_fragment.stageId == stage.id ? "selected" : "";
            %>
            <option value="<%=stage.id%>" <%=selectedStr%>><%=stage.name%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="condition">Condition</label>
        <div class="form-control" id="condition"><%=novel_fragment.condition == null ? "" : novel_fragment.condition%></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="preCode">Pre Code</label>
        <div class="form-control" id="preCode"><%=novel_fragment.preCode == null ? "" : novel_fragment.preCode%></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="10" placeholder="Enter detailed description" required><%=novel_fragment.content%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="postCode">Post Code</label>
        <div class="form-control" id="postCode"><%=novel_fragment.postCode == null ? "" : novel_fragment.postCode%></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="image">Image</label>
        <input type="text" class="form-control" id="image" maxlength="100" placeholder="Enter image" value="<%=novel_fragment.image == null ? "" : novel_fragment.image%>">
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteNovelFragment()">Delete</a></li>
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

            function deleteNovelFragment() {
                bootbox.confirm("Are you sure to delete this novel fragment?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/novel_fragment/delete?id=" + id), function(data){
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
                        var content = $('#content').val();
                        if(!content) {
                            content = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/novel_fragment/update"), { id: $('#id').val(), name: $('#name').val(), content: $('#content').val(), 'stageId': $('#stageId').val(), 'condition': condition.getValue(), 'preCode': preCode.getValue(), 'postCode': postCode.getValue(), 'image': $('#image').val() }, function(data) {
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
