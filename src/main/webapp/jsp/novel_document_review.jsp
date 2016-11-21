<%
String page_title = "Novel Document Review";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
String docId = request.getParameter("docId");
if(docId == null) {
    docId = "";
}
%>
<fieldset class="form-group">
    <label for="id">Novel Document ID</label>
    <input type="text" class="form-control" id="id" placeholder="Enter id" value="<%=docId%>" required autofocus>
    <small class="text-muted">Novel document ID is like "[role_id]-[fragment_id]-..."</small>
</fieldset>
<div class="form-group">
    <button type="button" class="btn btn-primary" id="review_btn">Review</button>
</div>
<div id="content" class="well">
</div>
<button type="button" class="btn btn-default" id="url_back_btn">Back</button>
<%@ include file="import_script.jsp" %>
<script>
            function reviewDoc() {
                $.get(getAPIURL("api/novel_document/review?id=" + $('#id').val()), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);

                        $('#content').empty();
                        var html = data.result.data.$;
                        $('#content').append(html);
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }

            $('#review_btn').click(function(){
                reviewDoc();
            });

            $(document).ready(function(){
                <%
                if(docId != null && !"".equals(docId.trim())) {
                %>
                reviewDoc();
                <%
                }
                %>
            });
</script>
<%@ include file="footer.jsp" %>
