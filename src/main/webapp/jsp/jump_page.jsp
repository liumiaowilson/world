<%
String page_title = "Jump Page";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Jump Page</h3>
    </div>
    <div class="panel-body">
        <fieldset class="form-group">
            <label for="page">Page</label>
            <input type="text" class="form-control" id="page" maxlength="50" placeholder="Enter page" required autofocus>
        </fieldset>
        <div class="btn-group">
            <button type="button" class="btn btn-primary" id="jump_btn">Jump</button>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $('#jump_btn').click(function(){
                    $.get(getAPIURL("api/menu/get?id=" + $('#page').val()), function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                            var script = data.result.data.$;
                            eval(script);
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });
            });
</script>
<%@ include file="footer.jsp" %>
