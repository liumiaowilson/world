<%@ page import="org.wilson.world.today.*" %>
<%
String page_title = "Today";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<%
List<TodayContentProvider> providers = TodayManager.getInstance().getTodayContentProviders();
for(TodayContentProvider provider : providers) {
    String content = provider.getContent(request);
    if(content != null) {
%>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title"><%=provider.getName()%></h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <%=content%>
        </div>
    </div>
</div>
<%
    }
}
%>
<button type="button" class="btn btn-primary" id="continue_btn">Continue</button>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $("#continue_btn").click(function(){
                    jumpBack();
                });
            });
</script>
<%@ include file="footer.jsp" %>
