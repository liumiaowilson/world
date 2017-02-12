<%
String page_title = "Rewrite Edit";
%>
<%@ include file="header.jsp" %>
<%
Rewrite rewrite = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
rewrite = RewriteManager.getInstance().getRewrite(id);
if(rewrite == null) {
    response.sendRedirect("rewrite_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=rewrite.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=rewrite.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <div class="form-group">
        <label for="regex">Regex</label>
        <select class="combobox form-control" id="regex" required>
            <%
            List<String> options = new ArrayList<String>();
            options.add("true");
            options.add("false");
            for(String option : options) {
                String selectedStr = (option.equals(rewrite.regex) ? "selected" : "");
            %>
            <option value="<%=option%>" <%=selectedStr%>><%=option%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="fromUrl">From Url</label>
        <input type="text" class="form-control" id="fromUrl" maxlength="100" placeholder="Enter fromUrl" value="<%=rewrite.fromUrl%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="toUrl">To Url</label>
        <input type="text" class="form-control" id="toUrl" maxlength="100" placeholder="Enter toUrl" value="<%=rewrite.toUrl%>" required>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteRewrite()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteRewrite() {
                bootbox.confirm("Are you sure to delete this rewrite?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/rewrite/delete?id=" + id), function(data){
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

                        l.ladda('start');
                        $.post(getAPIURL("api/rewrite/update"), { id: $('#id').val(), name: $('#name').val(), regex: $('#regex').val(), fromUrl: $("#fromUrl").val(), toUrl: $("#toUrl").val() }, function(data) {
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
