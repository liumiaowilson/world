<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Demo</h3>
    </div>
    <div class="panel-body">
        <fieldset class="form-group">
            <label for="description">Description</label>
            <textarea class="form-control" id="description" rows="5" maxlength="400" placeholder="Enter detailed description"></textarea>
            <small class="text-muted">The more detailed the description is, the bigger chance to earn more!</small>
        </fieldset>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
    $(function() {
        $('textarea.form-control').after('<div class="form-group"> <button type="button" class="btn btn-default btn-xs btn_textarea_copy"> <span class="glyphicon glyphicon-copy" aria-hidden="true"></span> </button> <button type="button" class="btn btn-default btn-xs btn_textarea_remove"> <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> </button> </div>');
        $('.btn_textarea_remove').click(function(){
            $(this).parent().prev().val('');
        });
    });
</script>
<%@ include file="footer.jsp" %>
