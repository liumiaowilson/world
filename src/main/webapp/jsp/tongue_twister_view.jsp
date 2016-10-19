<%
String page_title = "Tongue Twister View";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Tongue Twister View</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <%
            TongueTwister tt = TongueTwisterManager.getInstance().randomTongueTwister();
            String content = "No tongue twister found";
            if(tt != null) {
                content = tt.content;
            }
            %>
            <%=content%>
        </div>
    </div>
</div>
<button type="button" class="btn btn-default" id="url_back_btn">Back</button>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
