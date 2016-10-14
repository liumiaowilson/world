<%
String page_title = "Profile View";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Profiles</h3>
    </div>
    <div class="panel-body">
        <div class="list-group">
            <%
            if(ProfileManager.getInstance().hasBigFiveProfile()) {
            %>
            <a href="javascript:jumpTo('profile_view_bigfive.jsp')" class="list-group-item">Profile Big Five</a>
            <%
            }
            %>
            <%
            if(ProfileManager.getInstance().hasSmalleyProfile()) {
            %>
            <a href="javascript:jumpTo('profile_view_smalley.jsp')" class="list-group-item">Profile Smalley</a>
            <%
            }
            %>
            <%
            if(ProfileManager.getInstance().hasPCompassProfile()) {
            %>
            <a href="javascript:jumpTo('profile_view_pcompass.jsp')" class="list-group-item">Profile Personality Compass</a>
            <%
            }
            %>
            <%
            if(ProfileManager.getInstance().hasPColorProfile()) {
            %>
            <a href="javascript:jumpTo('profile_view_pcolor.jsp')" class="list-group-item">Profile Personality Color</a>
            <%
            }
            %>
            <a href="javascript:jumpTo('profile_view_detail.jsp')" class="list-group-item">Profile Details</a>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
