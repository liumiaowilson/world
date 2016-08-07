<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_datepicker.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Demo</h3>
    </div>
    <div class="panel-body">
        <input type="text" class="form-control datepicker"/>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_datepicker.jsp" %>
<script>
            $(document).ready(function(){
                $('.datepicker').datepicker({
                    format: 'yyyy-mm-dd'
                });
            });
</script>
<%@ include file="footer.jsp" %>
