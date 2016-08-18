<%
String page_title = "Exchange";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_slider.jsp" %>
<%@ include file="navbar.jsp" %>
<div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Exchange</h3>
        </div>
        <div class="panel-body">
            <fieldset class="form-group">
                <label for="coins">Coins</label>
                <input id="coins" data-slider-id='coinsSlider' type="text" data-slider-min="0" data-slider-max="<%=CharManager.getInstance().getMaxCoinsPossible()%>" data-slider-step="1" data-slider-value="<%=CharManager.getInstance().getCoins()%>"/>
            </fieldset>
            <fieldset class="form-group">
                <label for="skillPoints">Skill Points</label>
                <input id="skillPoints" data-slider-id='skillPointsSlider' type="text" data-slider-min="0" data-slider-max="<%=CharManager.getInstance().getMaxSkillPointsPossible()%>" data-slider-step="1" data-slider-value="<%=CharManager.getInstance().getSkillPoints()%>"/>
            </fieldset>
            <button type="submit" class="btn btn-primary" id="save_btn">Save</button>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_slider.jsp" %>
<script>
            var ratio = <%=CharManager.getInstance().getExchangeRatio()%>;
            var old_coins = <%=CharManager.getInstance().getCoins()%>;
            var old_points = <%=CharManager.getInstance().getSkillPoints()%>;
            var coinsSlider = $('#coins').slider({
                formatter: function(value) {
                    return 'Current value: ' + value;
                }
            }).on("slide", function(){
                var coins = parseInt($('#coins').val());
                var delta_coins = coins - old_coins;
                var delta_points = -(delta_coins / ratio);
                skillPointsSlider.setValue(old_points + delta_points);
            }).data('slider');
            $('#coinsSlider').css("width", "100%");
            var skillPointsSlider = $('#skillPoints').slider({
                formatter: function(value) {
                    return 'Current value: ' + value;
                }
            }).on("slide", function(){
                var points = parseInt($('#skillPoints').val());
                var delta_points = points - old_points;
                var delta_coins = -(delta_points * ratio);
                coinsSlider.setValue(old_coins + delta_coins);
            }).data('slider');
            $('#skillPointsSlider').css("width", "100%");

            $("#save_btn").click(function(){
                $.post(getAPIURL("api/char/exchange"), { 'coins': coinsSlider.getValue(), 'points': skillPointsSlider.getValue() }, function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                    }
                    else {
                        showDanger(msg);
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
