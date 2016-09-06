<%
String page_title = "Article Read Train";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Article Read Train</h3>
    </div>
    <div class="panel-body">
        <div id="content" class="well">
        </div>
        <fieldset class="form-group">
            <label for="summary">Summary</label>
            <textarea class="form-control" id="summary" rows="5" maxlength="400" placeholder="Enter summary."></textarea>
            <small class="text-muted">Try your best to summarize the section.</small>
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
    $('#summary').prop('disabled', true);
    $('#done_btn').removeClass('disabled');

    $('#done_btn').click(function(){
        $.post(getAPIURL("api/article/train_read"), { 'summary': $('#summary').val() }, function(data){
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
    $.get(getAPIURL("api/article/random_section"), function(data){
        var status = data.result.status;
        var msg = data.result.message;
        if("OK" == status) {
            showSuccess(msg);
            var title = data.result.data.title;
            var html = data.result.data.html;
            $('#content').append("<h3>" + title + "</h3>");
            $('#content').append(html);

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
