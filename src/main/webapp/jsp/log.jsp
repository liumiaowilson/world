<%
String page_title = "Log";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Logs</h3>
    </div>
    <div class="panel-body">
        <form id="form" data-toggle="validator" role="form" method="POST" action="/jsp/log.jsp">
            <fieldset class="form-group">
                <label for="keyword">Keyword</label>
                <input type="text" class="form-control" id="keyword" name="keyword" placeholder="Enter keyword" required autofocus>
            </fieldset>
            <div class="btn-group">
                <button type="submit" class="btn btn-primary" id="search_btn">Search</button>
                <button type="button" class="btn btn-info" id="download_btn">Download</button>
            </div>
        </form>
        <hr/>
        <div class="well">
            <%
            String keyword = request.getParameter("keyword");
            String log = ConsoleManager.getInstance().getLogs(keyword);
            log = log.replaceAll("\n", "<br/>");
            %>
            <%=log%>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                var l = $('#download_btn').ladda();

                $('#download_btn').click(function(){
                    window.location.href = getAPIURL("api/console/download_log");
                });
            });
</script>
<%@ include file="footer.jsp" %>
