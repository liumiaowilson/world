<%
String page_title = "Novel Document View";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Novel Document View</h3>
    </div>
    <div class="panel-body">
        <div id="content" class="well">
        </div>
    </div>
</div>
<fieldset class="form-group">
    <label for="comment">Comment</label>
    <textarea class="form-control" id="comment" rows="5" maxlength="200" placeholder="Enter comment"></textarea>
</fieldset>
<button type="button" class="btn btn-primary" id="save_btn">Save</button>
<button type="button" class="btn btn-default" id="url_back_btn">Back</button>
<input type="hidden" id="docId" value=""/>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $.get(getAPIURL("api/novel_document/random"), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);

                        var html = data.result.data.content;
                        $('#content').append(html);

                        $('#docId').val(data.result.data.id);
                    }
                    else {
                        showDanger(msg);
                    }
                });
            });

            $('#save_btn').click(function(){
                $.post(getAPIURL("api/novel_document/comment"), { 'comment': $('#comment').val(), 'docId': $('#docId').val() }, function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        $('#comment').val('');
                    }
                    else {
                        showDanger(msg);
                    }
                });
            });
</script>
<%@ include file="footer.jsp" %>
