<%
String page_title = "Query Edit";
%>
<%@ include file="header.jsp" %>
<%
Query query = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
query = QueryManager.getInstance().getQuery(id);
if(query == null) {
    response.sendRedirect("query_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=query.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=query.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="impl">Implementation</label>
        <input type="text" class="form-control" id="impl" maxlength="50" placeholder="Enter implementation" value="<%=query.impl%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="idExpr">ID Cell Expression</label>
        <textarea class="form-control" id="idExpr" rows="5" maxlength="200" placeholder="Enter ID cell expression" required><%=query.idExpr%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="nameExpr">Name Cell Expression</label>
        <textarea class="form-control" id="nameExpr" rows="5" maxlength="200" placeholder="Enter name cell expression" required><%=query.nameExpr%></textarea>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteQuery()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteQuery() {
                bootbox.confirm("Are you sure to delete this query?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/query/delete?id=" + id), function(data){
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

                        l.ladda('start');
                        $.post(getAPIURL("api/query/update"), { id: $('#id').val(), name: $('#name').val(), impl: $('#impl').val(), idExpr: $('#idExpr').val(), nameExpr: $('#nameExpr').val()}, function(data) {
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
