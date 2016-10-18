<%
String page_title = "Sign Edit";
%>
<%@ include file="header.jsp" %>
<%
Sign sign = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
sign = SignManager.getInstance().getSign(id);
if(sign == null) {
    response.sendRedirect("sign_list.jsp");
    return;
}
TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=sign.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=sign.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="meaning">Meaning</label>
        <textarea class="form-control" id="meaning" rows="5" maxlength="400" placeholder="Enter detailed description" disabled><%=sign.meaning%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="step">Step</label>
        <input type="text" class="form-control" id="step" maxlength="20" placeholder="Enter step" value="<%=sign.step%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="time">Last Reviewed</label>
        <input type="text" class="form-control" id="time" maxlength="20" placeholder="Enter time" value="<%=TimeUtils.toDateString(sign.time, tz)%>" disabled>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary disabled" id="save_btn">Save</button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteSign()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteSign() {
                bootbox.confirm("Are you sure to delete this sign?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/sign/delete?id=" + id), function(data){
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
