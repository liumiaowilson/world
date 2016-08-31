<%
String page_title = "Algorithm Edit";
%>
<%@ include file="header.jsp" %>
<%
Algorithm algorithm = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
algorithm = AlgorithmManager.getInstance().getAlgorithm(id);
if(algorithm == null) {
    response.sendRedirect("algorithm_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=algorithm.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=algorithm.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <div class="form-group">
        <label for="problemId">Problem</label>
        <select class="combobox form-control" id="problemId">
            <option></option>
            <%
            List<AlgorithmProblem> problems = AlgorithmProblemManager.getInstance().getAlgorithmProblems();
            Collections.sort(problems, new Comparator<AlgorithmProblem>(){
                public int compare(AlgorithmProblem p1, AlgorithmProblem p2) {
                    return p1.name.compareTo(p2.name);
                }
            });
            for(AlgorithmProblem problem : problems) {
                String selectedStr = algorithm.problemId == problem.id ? "selected" : "";
            %>
            <option value="<%=problem.id%>" <%=selectedStr%>><%=problem.name%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=algorithm.description%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="impl">Implementation</label>
        <div class="form-control" id="impl" required><%=algorithm.impl%></div>
    </fieldset>
    <div id="output" class="well" style="display: none">
    </div>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="run_btn">Run</button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteAlgorithm()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var editor = ace.edit("impl");
            editor.setTheme("ace/theme/monokai");
            editor.getSession().setMode("ace/mode/java");
            $("#impl").css("width", "100%").css("height", "500");

            $('#run_btn').click(function(){
                $.post(getAPIURL("api/algorithm/run"), { 'problemId': $('#problemId').val(), 'impl': editor.getValue() }, function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    var data = data.result.data;
                    if("OK" == status) {
                        $('#output').show();
                        $('#output').empty();
                        if(data.isSuccessful) {
                            $('#output').append("<div class=\"alert alert-success\" role=\"alert\">Success</div>");
                        }
                        else {
                            $('#output').append("<div class=\"alert alert-danger\" role=\"alert\">Failure</div>");
                            var result = "<table class=\"table table-striped table-bordered\"><thead><tr><th>Name</th><th>Value</th></tr></thead><tbody>";
                            if(!data.message) {
                                data.message = "";
                            }
                            result += "<tr><td>Log</td><td>" + data.message.replace(/\n/g, "<br/>") + "</td></tr>";
                            if(!data.expected) {
                                data.expected = "";
                            }
                            result += "<tr><td>Expected</td><td>" + data.expected.replace(/\n/g, "<br/>") + "</td></tr>";
                            if(!data.real) {
                                data.real = "";
                            }
                            result += "<tr><td>Real</td><td>" + data.real.replace(/\n/g, "<br/>") + "</td></tr>";
                            result += "</tbody></table>";
                            $('#output').append(result);
                        }
                        scrollToBottom();
                    }
                    else {
                        showDanger(msg);
                    }
                });
            });

            function deleteAlgorithm() {
                bootbox.confirm("Are you sure to delete this algorithm?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/algorithm/delete?id=" + id), function(data){
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
                        $.post(getAPIURL("api/algorithm/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), 'problemId': $('#problemId').val(), 'impl': editor.getValue() }, function(data) {
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
