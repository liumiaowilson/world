<%
String page_title = "Romance Train";
%>
<%@ include file="header.jsp" %>
<%
List<RomanceFactor> factors = RomanceFactorManager.getInstance().randomRomanceFactors();
if(factors == null || factors.isEmpty()) {
    response.sendRedirect("romance_factor_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Romance Train</h3>
    </div>
    <div class="panel-body">
        <%
        Collections.sort(factors, new Comparator<RomanceFactor>(){
            public int compare(RomanceFactor f1, RomanceFactor f2) {
                return f1.name.compareTo(f2.name);
            }
        });
        for(RomanceFactor factor : factors) {
        %>
        <span class="label label-info"><%=factor.name%></span>
        <%
        }
        %>
        <fieldset class="form-group">
            <label for="outcome">Romance Outcome</label>
            <textarea class="form-control" id="outcome" rows="5" maxlength="400" placeholder="Enter detailed description"></textarea>
        </fieldset>
        <div class="progress">
            <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%"></div>
        </div>
    </div>
</div>
<div class="form-group">
    <button type="button" class="btn btn-primary disabled" id="accept_btn">Accept</button>
    <button type="button" class="btn btn-default disabled" id="discard_btn">Discard</button>
</div>
<%@ include file="import_script.jsp" %>
<script>
var debug = <%=ConfigManager.getInstance().isInDebugMode()%>;
var period = 1000;
if(debug) {
    period = 10;
}
function stop() {
    $('#outcome').prop('disabled', true);
    $('#accept_btn').removeClass('disabled');
    $('#discard_btn').removeClass('disabled');

    $('#discard_btn').click(function(){
        $.get(getAPIURL("api/romance/discard"), function(data){
            var status = data.result.status;
            var msg = data.result.message;
            if("OK" == status) {
                showSuccess(msg);
                jumpBack();
            }
            else {
                showDanger(msg);
            }
        });
    });

    $('#accept_btn').click(function(){
        $.post(getAPIURL("api/romance/accept"), { 'content': $('#outcome').val() }, function(data){
            var status = data.result.status;
            var msg = data.result.message;
            if("OK" == status) {
                showSuccess(msg);
                jumpTo("romance_new.jsp");
            }
            else {
                showDanger(msg);
            }
        });
    });
}

$(document).ready(function(){
    var progress = setInterval(function() {
        var bar = $('.progress-bar');
        var value = parseInt(bar.attr("aria-valuenow"));
        if(value >= 101) {
            clearInterval(progress);
            stop();
        }
        else {
            value = value + 1;
            bar.attr("aria-valuenow", value);
            bar.css("width", value + "%");
        }
    }, period);
});
</script>
<%@ include file="footer.jsp" %>
