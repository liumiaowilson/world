<%
String page_title = "How-to Train";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">How-to Train</h3>
    </div>
    <div class="panel-body">
        <div id="title">
        </div>
        <div id="answer" class="well" style="display:none">
        </div>
        <fieldset class="form-group">
            <label for="content">Solution</label>
            <textarea class="form-control" id="content" rows="5" maxlength="400" placeholder="Enter detailed description"></textarea>
        </fieldset>
        <div class="progress">
            <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%"></div>
        </div>
    </div>
</div>
<button type="button" class="btn btn-primary disabled" id="done_btn">Done</button>
<button type="button" class="btn btn-default disabled" id="show_btn">Show</button>
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
                    $.post(getAPIURL("api/how_to/train"), { 'content': $('#content').val() }, function(data){
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

                $('#show_btn').removeClass('disabled');
                $('#show_btn').click(function(){
                    $('#answer').show();
                });
            }
            $(document).ready(function(){
                $.get(getAPIURL("api/how_to/random"), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);

                        var name = data.result.data.name;
                        var url = data.result.data.url;
                        var answer = data.result.data.answer;
                        $('#title').append("<h3>" + name + "</h3>");
                        $('#answer').append(answer);
                        $('#answer').append("<a href='" + url + "'>More</a>");
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
