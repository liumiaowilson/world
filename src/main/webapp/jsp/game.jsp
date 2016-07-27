<%
String page_title = "Game";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
String id = request.getParameter("id");
String type = request.getParameter("type");
%>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Game</h3>
    </div>
    <div class="panel-body">
        <table id="char_table" class="table table-bordered table-striped" style="display:none">
            <thead>
                <tr>
                    <th style="width:30%">Name</th>
                    <th id="name_a" style="width:35%"></th>
                    <th id="name_b" style="width:35%"></th>
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
                    <td>MP</td>
                    <td id="mp_a">
                        <div class="progress">
                            <div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div>
                        </div>
                    </td>
                    <td id="mp_b">
                        <div class="progress">
                            <div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div>
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
                <tr>
                    <td>Construction</td>
                    <td id="construction_a"></td>
                    <td id="construction_b"></td>
                </tr>
                <tr>
                    <td>Dexterity</td>
                    <td id="dexterity_a"></td>
                    <td id="dexterity_b"></td>
                </tr>
                <tr>
                    <td>Intelligence</td>
                    <td id="intelligence_a"></td>
                    <td id="intelligence_b"></td>
                </tr>
                <tr>
                    <td>Charisma</td>
                    <td id="charisma_a"></td>
                    <td id="charisma_b"></td>
                </tr>
                <tr>
                    <td>Willpower</td>
                    <td id="willpower_a"></td>
                    <td id="willpower_b"></td>
                </tr>
                <tr>
                    <td>Luck</td>
                    <td id="luck_a"></td>
                    <td id="luck_b"></td>
                </tr>
            </tbody>
        </table>
        <div id="message_panel" class="well">
            Press 'Start' to engage in the game.
        </div>
        <button type="button" class="btn btn-primary" id="start_btn">Start</button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
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
            var mp = message.source.mp;
            var max_mp = message.source.maxMp;
            var mp_pct = (mp * 100 / max_mp).toFixed(2);
            $('#mp_' + data[message.source.name] + ' .progress-bar').attr("aria-valuemax", max_mp).attr("aria-valuenow", mp).css("width", mp_pct + "%").text(mp + "/" + max_mp);
            $('#speed_' + data[message.source.name]).text(message.source.speed);
            $('#strength_' + data[message.source.name]).text(message.source.strength);
            $('#construction_' + data[message.source.name]).text(message.source.construction);
            $('#dexterity_' + data[message.source.name]).text(message.source.dexterity);
            $('#intelligence_' + data[message.source.name]).text(message.source.intelligence);
            $('#charisma_' + data[message.source.name]).text(message.source.charisma);
            $('#willpower_' + data[message.source.name]).text(message.source.willpower);
            $('#luck_' + data[message.source.name]).text(message.source.luck);
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
            var mp = message.target.mp;
            var max_mp = message.target.maxMp;
            var mp_pct = (mp * 100 / max_mp).toFixed(2);
            $('#mp_' + data[message.target.name] + ' .progress-bar').attr("aria-valuemax", max_mp).attr("aria-valuenow", mp).css("width", mp_pct + "%").text(mp + "/" + max_mp);
            $('#speed_' + data[message.target.name]).text(message.target.speed);
            $('#strength_' + data[message.target.name]).text(message.target.strength);
            $('#construction_' + data[message.target.name]).text(message.target.construction);
            $('#dexterity_' + data[message.target.name]).text(message.target.dexterity);
            $('#intelligence_' + data[message.target.name]).text(message.target.intelligence);
            $('#charisma_' + data[message.target.name]).text(message.target.charisma);
            $('#willpower_' + data[message.target.name]).text(message.target.willpower);
            $('#luck_' + data[message.target.name]).text(message.target.luck);
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
        $('#start_btn').click(function(){
            $.get(getAPIURL("api/game/start?id=<%=id%>&type=<%=type%>"), function(data) {
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
