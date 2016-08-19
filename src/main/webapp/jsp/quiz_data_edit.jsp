<%
String page_title = "Quiz Data Edit";
%>
<%@ include file="header.jsp" %>
<%
QuizData quiz_data = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
quiz_data = QuizDataManager.getInstance().getQuizData(id);
if(quiz_data == null) {
    response.sendRedirect("quiz_data_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=quiz_data.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=quiz_data.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=quiz_data.description%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="processor">Processor</label>
        <input type="text" class="form-control" id="processor" maxlength="50" placeholder="Enter processor" value="<%=quiz_data.processor%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <div class="form-control" id="content"><%=quiz_data.content%></div>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="validate_btn">Validate</button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="jumpTo('quiz_paper.jsp?id=<%=id%>')">Do Quiz</a></li>
                <li role="separator" class="divider"></li>
                <li><a href="javascript:void(0)" onclick="deleteQuizData()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var editor = ace.edit("content");
            editor.setTheme("ace/theme/monokai");
            editor.getSession().setMode("ace/mode/json");
            $("#content").css("width", "100%").css("height", "500");

            function deleteQuizData() {
                bootbox.confirm("Are you sure to delete this quiz data?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/quiz_data/delete?id=" + id), function(data){
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

            $('#validate_btn').click(function(){
                $.post(getAPIURL("api/quiz_data/validate_content"), { 'content': editor.getValue() }, function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                    }
                    else {
                        showDanger(msg);
                    }
                });
            })

            $(document).ready(function(){
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
                        $.post(getAPIURL("api/quiz_data/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), 'processor': $('#processor').val(), 'content': editor.getValue() }, function(data) {
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
