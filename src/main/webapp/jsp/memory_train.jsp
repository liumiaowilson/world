<%
String page_title = "Memory Train";
%>
<%@ include file="header.jsp" %>
<%
List<String> pieces = MemoryManager.getInstance().getPieces();
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Memory Train</h3>
    </div>
    <div class="panel-body">
        <%
        if(!pieces.isEmpty()) {
        %>
        <div id="memory">
        <%
            for(String piece : pieces) {
        %>
            <span class="label label-info"><%=piece%></span>
        <%
            }
        %>
        <hr/>
        </div>
        <fieldset id="recall_fs" class="form-group" style="display:none">
            <label for="content">Words</label>
            <textarea class="form-control" id="content" rows="5" maxlength="400" placeholder="Enter a list of words separated by comma."></textarea>
            <small class="text-muted">Use your memory to recall the words you have just seen.</small>
        </fieldset>
        <div class="progress">
            <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%"></div>
        </div>
        <%
        }
        else {
        %>
        <div class="alert alert-danger" role="alert">
            No pieces could be found.
        </div>
        <%
        }
        %>
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
    $('#memory').hide();
    $('#recall_fs').show();
    $('#done_btn').removeClass('disabled');

    $('#done_btn').click(function(){
        var list = [];
        $('div#memory span.label-info').each(function(){
            list.push($(this).text());
        });
        $.post(getAPIURL("api/memory/train"), { 'content': $('#content').val(), 'old': list.join(",") }, function(data){
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
