<%
String page_title = "Error Info Edit";
%>
<%@ include file="header.jsp" %>
<%
ErrorInfo error_info = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
error_info = ErrorInfoManager.getInstance().getErrorInfo(id);
if(error_info == null) {
    response.sendRedirect("error_info_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=error_info.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" value="<%=error_info.name%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="trace">Trace</label>
        <div class="well">
            <%
            String [] lines = error_info.trace;
            for(String line : lines) {
            %>
            <%=line%><br/>
            <%
            }
            %>
        </div>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn" disabled><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteErrorInfo()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteErrorInfo() {
                var id = $('#id').val();
                $.get(getAPIURL("api/error_info/delete?id=" + id), function(data){
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
</script>
<%@ include file="footer.jsp" %>
