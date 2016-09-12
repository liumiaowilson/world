<%
String page_title = "Artifact View";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Artifact View</h3>
    </div>
    <div class="panel-body">
        <div id="content" class="well">
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/artifact/random"), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);

                        var html = data.result.data.$;
                        $('#content').append(html);
                    }
                    else {
                        showDanger(msg);
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
