<%
String page_title = "Story View";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<input type="hidden" id="story_id" value=""/>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Story View</h3>
    </div>
    <div class="panel-body">
        <div id="content" class="well">
        </div>
    </div>
</div>
<button type="button" class="btn btn-default disabled" id="save_btn">Save</button>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/story/random"), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);

                        $('#story_id').val(data.result.data.id);
                        var title = data.result.data.title;
                        var html = data.result.data.html;
                        $('#content').append("<h3>" + title + "</h3>");
                        $('#content').append(html);
                        $('#save_btn').removeClass("disabled");
                        $('#save_btn').click(function(){
                            bootbox.prompt({
                                title: "Enter the name you want to save as.",
                                callback: function(result) {
                                    if(result) {
                                        $.post(getAPIURL("api/story/save"), { 'id': $('#story_id').val(), 'name': result }, function(data){
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
