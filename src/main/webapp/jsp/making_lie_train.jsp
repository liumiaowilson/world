<%
String page_title = "Making Lie Train";
%>
<%@ include file="header.jsp" %>
<%
WeaselPhrase phrase = WeaselPhraseManager.getInstance().randomWeaselPhrase();
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Making Lie Train</h3>
    </div>
    <div class="panel-body">
        <div id="sentence" class="well">
        </div>
        <fieldset class="form-group">
            <label for="content">Make Lies</label>
            <textarea class="form-control" id="content" rows="5" maxlength="400" placeholder="Enter detailed content."></textarea>
            <small class="text-muted">Try your best to make more content.</small>
        </fieldset>
        <div class="progress">
            <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%"></div>
        </div>
    </div>
</div>
<div class="form-group">
    <button type="button" class="btn btn-primary disabled" id="done_btn">Done</button>
</div>
<%@ include file="import_script.jsp" %>
<script>
var debug = <%=ConfigManager.getInstance().isInDebugMode()%>;
var period = 1000;
if(debug) {
    period = 10;
}
function stop() {
    $('#content').prop('disabled', true);
    $('#done_btn').removeClass('disabled');

    $('#done_btn').click(function(){
        $.post(getAPIURL("api/sentence/train_making_lie"), { 'content': $('#content').val() }, function(data){
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
}

$(document).ready(function(){
    $.get(getAPIURL("api/sentence/random"), function(data){
        var status = data.result.status;
        var msg = data.result.message;
        if("OK" == status) {
            showSuccess(msg);
            $('#sentence').empty();
            var from = data.result.data.from;
            var sentence = data.result.data.sentence;
            $('#sentence').append("<p><b>" + from + "</b></p>");
            $('#sentence').append("<p>" + sentence + "</p>");

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
        }
        else {
            showDanger(msg);
        }
    });
});
</script>
<%@ include file="footer.jsp" %>
