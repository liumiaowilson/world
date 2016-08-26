<%
String page_title = "Article Speed Train";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<input type="hidden" id="article_id" value=""/>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Article Speed Train</h3>
    </div>
    <div class="panel-body">
        <input type="hidden" id="start_time" value=""/>
        <div id="content" class="well">
        </div>
        <div class="form-group">
            <button type="button" class="btn btn-primary disabled" id="done_btn">Done</button>
            <button type="button" class="btn btn-default disabled" id="save_btn">Save</button>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/article/random"), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        $('#article_id').val(data.result.data.id);
                        var title = data.result.data.title;
                        var html = data.result.data.html;
                        $('#content').append("<div class=\"alert alert-warning\" role=\"alert\">This article is going to take you " + data.result.data.expectedTime + " minute(s).</div>");
                        $('#content').append("<h3>" + title + "</h3>");
                        $('#content').append(html);
                        $('#start_time').val(new Date().getTime());

                        $('#done_btn').removeClass("disabled");
                        $('#done_btn').click(function(){
                            var end_time = new Date().getTime();
                            $.post(getAPIURL("api/article/train_speed"), { 'startTime': $('#start_time').val(), 'endTime': end_time, 'title': $('#content h3').text() }, function(data){
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

                        $('#save_btn').removeClass("disabled");
                        $('#save_btn').click(function(){
                            bootbox.prompt({
                                title: "Enter the name you want to save as.",
                                callback: function(result) {
                                    if(result) {
                                        $.post(getAPIURL("api/article/save"), { 'id': $('#article_id').val(), 'name': result }, function(data){
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
                    }
                    else {
                        showDanger(msg);
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
