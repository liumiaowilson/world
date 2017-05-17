<%
String page_title = "Pagelet Cached Edit";
%>
<%@ include file="header.jsp" %>
<%
Pagelet pagelet = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
pagelet = PageletManager.getInstance().getCachedPagelets().get(id);
if(pagelet == null) {
    response.sendRedirect("pagelet_cached_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=pagelet.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=pagelet.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="title">Title</label>
        <input type="text" class="form-control" id="title" maxlength="100" placeholder="Enter title" value="<%=pagelet.title%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="target">Target</label>
        <input type="text" class="form-control" id="target" maxlength="200" placeholder="Enter target" value="<%=pagelet.target%>">
    </fieldset>
    <div class="form-group">
        <label for="type">Type</label>
        <select class="combobox form-control" id="type">
            <option></option>
            <%
            List<String> types = PageletManager.getInstance().getPageletTypes();
            for(String type : types) {
                String selectedStr = (type.equals(pagelet.type) ? "selected" : "");
            %>
            <option value="<%=type%>" <%=selectedStr%>><%=type%></option>
            <%
            }
            %>
        </select>
    </div>
    <fieldset class="form-group">
        <label for="serverCode">Server Code</label>
        <div class="form-control" id="serverCode"><%=FormatUtils.escapeHtml(pagelet.serverCode == null ? "" : pagelet.serverCode)%></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="css">CSS</label>
        <div class="form-control" id="css"><%=FormatUtils.escapeHtml(pagelet.css == null ? "" : pagelet.css)%></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="html">HTML</label>
        <div class="form-control" id="html"><%=FormatUtils.escapeHtml(pagelet.html == null ? "" : pagelet.html)%></div>
    </fieldset>
    <fieldset class="form-group">
        <label for="clientCode">Client Code</label>
        <div class="form-control" id="clientCode"><%=FormatUtils.escapeHtml(pagelet.clientCode == null ? "" : pagelet.clientCode)%></div>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary" id="save_btn" disabled>Save</button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deletePagelet()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_code_editor.jsp" %>
<script>
            var serverCode = ace.edit("serverCode");
            serverCode.setTheme("ace/theme/monokai");
            serverCode.getSession().setMode("ace/mode/javascript");
            $("#serverCode").css("width", "100%").css("height", "400");

            var css = ace.edit("css");
            css.setTheme("ace/theme/monokai");
            css.getSession().setMode("ace/mode/css");
            $("#css").css("width", "100%").css("height", "400");

            var html = ace.edit("html");
            html.setTheme("ace/theme/monokai");
            html.getSession().setMode("ace/mode/html");
            $("#html").css("width", "100%").css("height", "400");

            var clientCode = ace.edit("clientCode");
            clientCode.setTheme("ace/theme/monokai");
            clientCode.getSession().setMode("ace/mode/javascript");
            $("#clientCode").css("width", "100%").css("height", "400");

            function deletePagelet() {
                bootbox.confirm("Are you sure to delete this cached pagelet?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/pagelet/delete_cache?id=" + id), function(data){
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
</script>
<%@ include file="footer.jsp" %>
