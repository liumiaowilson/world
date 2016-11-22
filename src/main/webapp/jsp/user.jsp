<%
String page_title = "User";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">User Information</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th style="width:40%">Attribute</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>User Name</td>
                    <td><%=(String)session.getAttribute("world-user")%></td>
                </tr>
                <tr>
                    <td>User Level</td>
                    <td><span class="badge"><%=ExpManager.getInstance().getLevel()%></span></td>
                </tr>
                <tr>
                    <td>User Karma</td>
                    <td>
                        <span style="color:BlueViolet"><%=CharManager.getInstance().getKarma()%></span>
                        <%
                        if(CharManager.getInstance().canRedeemKarma()) {
                        %>
                        <button type="button" class="btn btn-info btn-xs" id="redeem_karma_btn">Redeem</button>
                        <%
                        }
                        %>
                    </td>
                </tr>
                <tr>
                    <td>User Coins</td>
                    <td><span style="color:orange"><%=CharManager.getInstance().getCoins()%></span></td>
                </tr>
                <tr>
                    <td>Skill Points</td>
                    <td><span style="color:CornflowerBlue"><%=CharManager.getInstance().getSkillPoints()%></span></td>
                </tr>
                <tr>
                    <td>User Kills</td>
                    <td><span style="color:red"><%=CharManager.getInstance().getKills()%></span></td>
                </tr>
                <tr>
                    <td>Status</td>
                    <%
                    List<StatusEffect> statusEffects = CharManager.getInstance().getValidStatusEffects();
                    StringBuffer statusSB = new StringBuffer();
                    for(int i = 0; i < statusEffects.size(); i++) {
                        StatusEffect statusEffect = statusEffects.get(i);
                        String icon = statusEffect.status.getIcon();
                        if(icon == null || "".equals(icon.trim())) {
                            statusSB.append(statusEffect.status.getName());
                        }
                        else {
                            statusSB.append("<img src='");
                            statusSB.append(basePath);
                            statusSB.append("/images/status/");
                            statusSB.append(icon);
                            statusSB.append("' alt='");
                            statusSB.append(statusEffect.status.getName());
                            statusSB.append("' data-toggle='tooltip' title='");
                            statusSB.append(statusEffect.status.getName());
                            statusSB.append("'/>");
                        }
                        if(i != statusEffects.size() - 1) {
                            statusSB.append(",");
                        }
                    }
                    %>
                    <td><%=statusSB.toString()%></td>
                </tr>
                <tr>
                    <td>Level Experience</td>
                    <td>
                        <div class="progress">
                            <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="<%=ExpManager.getInstance().getCurrentLevelExperiencePercentage()%>" aria-valuemin="0" aria-valuemax="100" style="width: <%=ExpManager.getInstance().getCurrentLevelExperiencePercentage()%>%"><%=ExpManager.getInstance().getLevelInfo()%></div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>To Next Level Experience</td>
                    <td><%=ExpManager.getInstance().getToNextLevelExp()%></td>
                </tr>
                <tr>
                    <td>HP</td>
                    <td>
                        <div class="progress">
                            <div class="progress-bar progress-bar-danger" role="progressbar" aria-valuenow="<%=CharManager.getInstance().getCurrentHPPercentage()%>" aria-valuemin="0" aria-valuemax="100" style="width: <%=CharManager.getInstance().getCurrentHPPercentage()%>%"><%=CharManager.getInstance().getHP()%>/<%=CharManager.getInstance().getMaxHP()%></div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>MP</td>
                    <td>
                        <div class="progress">
                            <div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="<%=CharManager.getInstance().getCurrentMPPercentage()%>" aria-valuemin="0" aria-valuemax="100" style="width: <%=CharManager.getInstance().getCurrentMPPercentage()%>%"><%=CharManager.getInstance().getMP()%>/<%=CharManager.getInstance().getMaxMP()%></div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>Stamina</td>
                    <td>
                        <div class="progress">
                            <div class="progress-bar progress-bar-warning" role="progressbar" aria-valuenow="<%=CharManager.getInstance().getCurrentStaminaPercentage()%>" aria-valuemin="0" aria-valuemax="100" style="width: <%=CharManager.getInstance().getCurrentStaminaPercentage()%>%"><%=CharManager.getInstance().getStamina()%>/<%=CharManager.getInstance().getMaxStamina()%></div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>Speed</td>
                    <td><%=CharManager.getInstance().getSpeed()%></td>
                </tr>
                <tr>
                    <td>Strength</td>
                    <td><%=CharManager.getInstance().getStrength()%></td>
                </tr>
                <tr>
                    <td>Construction</td>
                    <td><%=CharManager.getInstance().getConstruction()%></td>
                </tr>
                <tr>
                    <td>Dexterity</td>
                    <td><%=CharManager.getInstance().getDexterity()%></td>
                </tr>
                <tr>
                    <td>Intelligence</td>
                    <td><%=CharManager.getInstance().getIntelligence()%></td>
                </tr>
                <tr>
                    <td>Charisma</td>
                    <td><%=CharManager.getInstance().getCharisma()%></td>
                </tr>
                <tr>
                    <td>Willpower</td>
                    <td><%=CharManager.getInstance().getWillpower()%></td>
                </tr>
                <tr>
                    <td>Luck</td>
                    <td><%=CharManager.getInstance().getLuck()%></td>
                </tr>
                <tr>
                    <td>Balance</td>
                    <td><a href="javascript:jumpTo('balance.jsp')">Balance</a></td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Status Effects</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Icon</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Remaining Time</th>
                </tr>
            </thead>
            <tbody>
                <%
                for(StatusEffect effect : statusEffects) {
                    String icon = effect.status.getIcon();
                    String effectName = effect.status.getName();
                %>
                <tr>
                    <%
                    if(icon != null) {
                    %>
                    <td><img src="<%=basePath%>/images/status/<%=icon%>" alt="<%=effectName%>" data-toggle="tooltip" title="<%=effectName%>"/></td>
                    <%
                    }
                    else {
                    %>
                    <td></td>
                    <%
                    }
                    %>
                    <td><%=effectName%></td>
                    <td><%=effect.status.getDescription()%></td>
                    <%
                    String effectRemainingTime = CharManager.getInstance().getStatusRemainingTime(effect);
                    if(effectRemainingTime == null) {
                        effectRemainingTime = "";
                    }
                    %>
                    <td><%=effectRemainingTime%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $('#redeem_karma_btn').click(function(){
                $.get(getAPIURL("api/char/redeem_karma"), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        jumpCurrent();
                    }
                    else {
                        showDanger(msg);
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
