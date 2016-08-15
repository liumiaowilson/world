<%
String page_title = "Porn Train";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Porn Train</h3>
    </div>
    <div class="panel-body">
        <input type="hidden" id="porn_id" value=""/>
        <div id="image">
        </div>
        <fieldset class="form-group">
            <label for="description">Description</label>
            <textarea class="form-control" id="description" rows="5" maxlength="400" placeholder="Enter detailed description."></textarea>
            <small class="text-muted">Try your best to describe this image.</small>
        </fieldset>
        <div class="progress">
            <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%"></div>
        </div>
    </div>
</div>
<div class="form-group">
    <button type="button" class="btn btn-primary disabled" id="done_btn">Done</button>
    <button type="button" class="btn btn-default" id="save_btn">Save</button>
</div>
<%@ include file="import_script.jsp" %>
<script>
var debug = <%=ConfigManager.getInstance().isInDebugMode()%>;
var period = 1000;
if(debug) {
    period = 10;
}
function stop() {
    $('#description').prop('disabled', true);
    $('#done_btn').removeClass('disabled');

    $('#done_btn').click(function(){
        $.post(getAPIURL("api/porn/train"), { 'description': $('#description').val() }, function(data){
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
    $.get(getAPIURL("api/porn/random"), function(data){
        var status = data.result.status;
        var msg = data.result.message;
        if("OK" == status) {
            showSuccess(msg);

            $('#porn_id').val(data.result.data.id);
            $('#image').append('<img src="<%=basePath%>/servlet/image?path=image.jpg" alt="porn_image"/>');
            $('#save_btn').click(function(){
                bootbox.prompt({
                    title: "Enter the name you want to save as.",
                    callback: function(result) {
                        var id = $('#porn_id').val();
                        if(result) {
                            $.post(getAPIURL("api/porn/save"), { 'id': $('#porn_id').val(), 'name': result }, function(data){
                                var status = data.result.status;
                                var msg = data.result.message;
                                if("OK" == status) {
                                    showSuccess(msg);
                                }
                                else {
                                    showDanger(msg);
                                }
                            });
                        }
                    }
                });
            });

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
