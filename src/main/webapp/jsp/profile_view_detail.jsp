<%
String page_title = "Profile View Detail";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Profile Details</h3>
    </div>
    <div class="panel-body">
        <%
        List<String> types = ProfileDetailManager.getInstance().getProfileDetailTypes();
        for(String type : types) {
        %>
        <div class="well">
            <p><b><%=type%></b></p>
            <%
            List<ProfileDetail> details = ProfileDetailManager.getInstance().getProfileDetailsOfType(type);
            for(ProfileDetail detail : details) {
            %>
            <p><%=detail.content%></p>
            <%
            }
            %>
        </div>
        <%
        }
        %>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
