<%@ page import="org.wilson.world.profile.*" %>
<%
String page_title = "Profile View Personality Color";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Personality Color</h3>
    </div>
    <div class="panel-body">
        <%
        PColorProfile profile = ProfileManager.getInstance().getPColorProfile();
        List<String> names = profile.getTypes();
        List<PersonalityColorType> types = new ArrayList<PersonalityColorType>();
        for(String name : names) {
            PersonalityColorType type = ProfileManager.getInstance().getPersonalityColorType(name);
            types.add(type);
        }
        %>
        <h4>You are a(n) <b><%=profile.getTypeDisplay()%></b></h4>
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Aspect</th>
                    <%
                    for(PersonalityColorType type : types) {
                    %>
                    <th><%=type.name%></th>
                    <%
                    }
                    %>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Strength</td>
                    <%
                    for(PersonalityColorType type : types) {
                    %>
                    <td><%=type.strength%></td>
                    <%
                    }
                    %>
                </tr>
                <tr>
                    <td>Weakness</td>
                    <%
                    for(PersonalityColorType type : types) {
                    %>
                    <td><%=type.weakness%></td>
                    <%
                    }
                    %>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
