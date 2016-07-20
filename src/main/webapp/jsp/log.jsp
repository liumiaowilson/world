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
        <button type="button" class="btn btn-info ladda-button" data-style="slide-left" id="download_btn"><span class="ladda-label">Download</span></button>
        <hr/>
        <div class="well">
            <%
            if(!ConfigManager.getInstance().isOpenShiftApp()) {
            %>
            This feature is only enabled on openshift hosted apps.
            <%
            }
            else {
                String log = ConsoleManager.getInstance().run("tail -200 ../app-root/logs/jbossews.log");
                log = log.replaceAll("\n", "<br/>");
            %>
            <%=log%>
            <%
            }
            %>
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
