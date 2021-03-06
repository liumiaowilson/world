<%
String page_title = "Balance";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_slider.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Balance</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Train Balance</td>
                    <td>
                        <b>In</b>&nbsp;&nbsp;<input id="train_slider" type="text" data-slider-min="-<%=BalanceManager.getInstance().getTrainBalanceLimit()%>" data-slider-max="<%=BalanceManager.getInstance().getTrainBalanceLimit()%>" data-slider-step="1" data-slider-value="<%=BalanceManager.getInstance().getTrainBalance()%>" data-slider-enabled="false"/>&nbsp;&nbsp;<b>Out</b>
                    </td>
                </tr>
                <tr>
                    <td>Energy Balance</td>
                    <td>
                        <b>Negative</b>&nbsp;&nbsp;<input id="energy_slider" type="text" data-slider-min="-<%=BalanceManager.getInstance().getEnergyBalanceLimit()%>" data-slider-max="<%=BalanceManager.getInstance().getEnergyBalanceLimit()%>" data-slider-step="1" data-slider-value="<%=BalanceManager.getInstance().getEnergyBalance()%>" data-slider-enabled="false"/>&nbsp;&nbsp;<b>Positive</b>
                    </td>
                </tr>
                <tr>
                    <td>Idea-Task Balance</td>
                    <td><%=BalanceManager.getInstance().getIdeaTaskBalance()%></td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_slider.jsp" %>
<script>
            var trainSlider = $('#train_slider').slider();
            var energySlider = $('#energy_slider').slider();
</script>
<%@ include file="footer.jsp" %>
