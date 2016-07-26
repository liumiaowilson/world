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
        <table id="char_table" class="table table-bordered table-striped" style="display:none">
            <thead>
                <tr>
                    <th>Name</th>
                    <th id="name_a"></th>
                    <th id="name_b"></th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>HP</td>
                    <td id="hp_a"></td>
                    <td id="hp_b"></td>
                </tr>
                <tr>
                    <td>Speed</td>
                    <td id="speed_a"></td>
                    <td id="speed_b"></td>
                </tr>
                <tr>
                    <td>Strength</td>
                    <td id="strength_a"></td>
                    <td id="strength_b"></td>
                </tr>
            </tbody>
        </table>
        <div id="message_panel" class="well">
        </div>
        <button type="button" class="btn btn-default" id="play_btn">Play</button>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
    var data = {};
    function sendMessage(message) {
        if(message.source) {
            if(!data[message.source.name]) {
                data[message.source.name] = 'a';
            }
            $('#name_' + data[message.source.name]).text(message.source.name);
            $('#hp_' + data[message.source.name]).text(message.source.hp);
            $('#speed_' + data[message.source.name]).text(message.source.speed);
            $('#strength_' + data[message.source.name]).text(message.source.strength);
        }
        if(message.target) {
            if(!data[message.target.name]) {
                data[message.target.name] = 'b';
            }
            $('#name_' + data[message.target.name]).text(message.target.name);
            $('#hp_' + data[message.target.name]).text(message.target.hp);
            $('#speed_' + data[message.target.name]).text(message.target.speed);
            $('#strength_' + data[message.target.name]).text(message.target.strength);
        }
        var content = message.message;
        $('#message_panel').append("<span>" + content + "</span><br/>");
    }
    function replayGame(list) {
        $('#message_panel').empty();
        $('#char_table').show();
        var message = list.shift();
        sendMessage(message);
        var progress = setInterval(function(){
            if(list.length == 0) {
                clearInterval(progress);
            }
            else {
                var message = list.shift();
                sendMessage(message);
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
