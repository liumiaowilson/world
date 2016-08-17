<%
String page_title = "How-to Train";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">How-to Train</h3>
    </div>
    <div class="panel-body">
        <div id="content" class="well">
        </div>
    </div>
</div>
<button type="button" class="btn btn-default" id="save_btn">Save</button>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/how_to/random"), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);

                        var name = data.result.data.name;
                        var url = data.result.data.url;
                        var answer = data.result.data.answer;
                        $('#content').append("<h3>" + name + "</h3>");
                        $('#content').append(answer);
                        $('#content').append("<a href='" + url + "'>More</a>");
                        $('#save_btn').click(function(){
                            //TODO
                        });
                    }
                    else {
                        showDanger(msg);
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
