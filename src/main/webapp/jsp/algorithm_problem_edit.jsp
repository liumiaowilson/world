<%
String page_title = "Algorithm Problem Edit";
%>
<%@ include file="header.jsp" %>
<%
AlgorithmProblem algorithm_problem = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
algorithm_problem = AlgorithmProblemManager.getInstance().getAlgorithmProblem(id);
if(algorithm_problem == null) {
    response.sendRedirect("algorithm_problem_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=algorithm_problem.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=algorithm_problem.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=algorithm_problem.description%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="interfaceDef">Interface</label>
        <div class="form-control" id="interfaceDef" required><%=algorithm_problem.interfaceDef%></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="dataset">Data Set</label>
        <div class="form-control" id="dataset" required><%=algorithm_problem.dataset%></div>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteAlgorithmProblem()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var interfaceDefEditor = ace.edit("interfaceDef");
            interfaceDefEditor.setTheme("ace/theme/monokai");
            interfaceDefEditor.getSession().setMode("ace/mode/java");
            $("#interfaceDef").css("width", "100%").css("height", "200");

            var datasetEditor = ace.edit("dataset");
            datasetEditor.setTheme("ace/theme/monokai");
            datasetEditor.getSession().setMode("ace/mode/json");
            $("#dataset").css("width", "100%").css("height", "500");

            function deleteAlgorithmProblem() {
                bootbox.confirm("Are you sure to delete this algorithm problem?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/algorithm_problem/delete?id=" + id), function(data){
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
                        var description = $('#description').val();
                        if(!description) {
                            description = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/algorithm_problem/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), 'interfaceDef': interfaceDefEditor.getValue(), 'dataset': datasetEditor.getValue() }, function(data) {
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
