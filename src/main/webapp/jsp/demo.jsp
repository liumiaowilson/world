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
        <div id="message_panel" class="well">
        </div>
        <button type="button" class="btn btn-default" id="play_btn">Play</button>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
    function replayGame(list) {
        $('#message_panel').empty();
        var progress = setInterval(function(){
            if(list.length == 0) {
                clearInterval(progress);
            }
            else {
                var message = list.shift();
                var content = message.message;
                $('#message_panel').append("<span>" + content + "</span><br/>");
            }
        }, 1000);
    }
    $(function() {
        $('#play_btn').click(function(){
            $.post(getAPIURL("api/game/start"), {}, function(data) {
                var status = data.result.status;
                var msg = data.result.message;
                if("OK" == status) {
                    replayGame(data.result.list);
                }
                else {
                    showDanger(msg);
                }
            }, "json");
        });
    });
</script>
<%@ include file="footer.jsp" %>
