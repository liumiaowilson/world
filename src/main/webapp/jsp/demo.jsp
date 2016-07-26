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
                    <td id="hp_a">
                        <div class="progress">
                            <div class="progress-bar progress-bar-danger" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div>
                        </div>
                    </td>
                    <td id="hp_b">
                        <div class="progress">
                            <div class="progress-bar progress-bar-danger" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div>
                        </div>
                    </td>
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
            var hp = message.source.hp;
            var max_hp = message.source.maxHp;
            var hp_pct = (hp * 100 / max_hp).toFixed(2);
            $('#hp_' + data[message.source.name] + ' .progress-bar').attr("aria-valuemax", max_hp).attr("aria-valuenow", hp).css("width", hp_pct + "%").text(hp + "/" + max_hp);
            $('#speed_' + data[message.source.name]).text(message.source.speed);
            $('#strength_' + data[message.source.name]).text(message.source.strength);
        }
        if(message.target) {
            if(!data[message.target.name]) {
                data[message.target.name] = 'b';
            }
            $('#name_' + data[message.target.name]).text(message.target.name);
            var hp = message.target.hp;
            var max_hp = message.target.maxHp;
            var hp_pct = (hp * 100 / max_hp).toFixed(2);
            $('#hp_' + data[message.target.name] + ' .progress-bar').attr("aria-valuemax", max_hp).attr("aria-valuenow", hp).css("width", hp_pct + "%").text(hp + "/" + max_hp);
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
