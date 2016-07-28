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
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-primary" data-style="slide-left" id="reset_btn">Reset</button>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
$(document).ready(function() {
    $('.ladda-button').click(function(){
        $(this).ladda().ladda("start");
    });
    $('#reset_btn').click(function(){
        $('.ladda-button').ladda().ladda("stop");
    });
});
</script>
<%@ include file="footer.jsp" %>
