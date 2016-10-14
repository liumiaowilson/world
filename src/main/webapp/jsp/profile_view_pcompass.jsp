<%@ page import="org.wilson.world.profile.*" %>
<%
String page_title = "Profile View Personality Compass";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Personality Compass</h3>
    </div>
    <div class="panel-body">
        <%
        PCompassProfile pcompassProfile = ProfileManager.getInstance().getPCompassProfile();
        String dominantTypeStr = pcompassProfile.getDominantType();
        PersonalityCompassType dominantType = ProfileManager.getInstance().getPersonalityCompassType(dominantTypeStr);
        String subDominantTypeStr = pcompassProfile.getSubDominantType();
        PersonalityCompassType subDominantType = ProfileManager.getInstance().getPersonalityCompassType(subDominantTypeStr);
        %>
        <h4>You are a(n) <b><%=dominantTypeStr%> - <%=subDominantTypeStr%></b></h4>
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Aspect</th>
                    <th><%=dominantTypeStr%></th>
                    <th><%=subDominantTypeStr%></th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Definition</td>
                    <td><%=dominantType.definition%></td>
                    <td><%=subDominantType.definition%></td>
                </tr>
                <tr>
                    <td>Motto</td>
                    <td><%=dominantType.motto%></td>
                    <td><%=subDominantType.motto%></td>
                </tr>
                <tr>
                    <td>Great Strength</td>
                    <td><%=dominantType.strength%></td>
                    <td><%=subDominantType.strength%></td>
                </tr>
                <tr>
                    <td>Basic Weakness</td>
                    <td><%=dominantType.weakness%></td>
                    <td><%=subDominantType.weakness%></td>
                </tr>
                <tr>
                    <td>Fundamental Aptitude</td>
                    <td><%=dominantType.aptitude%></td>
                    <td><%=subDominantType.aptitude%></td>
                </tr>
                <tr>
                    <td>Priority</td>
                    <td><%=dominantType.priority%></td>
                    <td><%=subDominantType.priority%></td>
                </tr>
                <tr>
                    <td>Motivation</td>
                    <td><%=dominantType.motivation%></td>
                    <td><%=subDominantType.motivation%></td>
                </tr>
                <tr>
                    <td>Pet Peeve</td>
                    <td><%=dominantType.pet_peeve%></td>
                    <td><%=subDominantType.pet_peeve%></td>
                </tr>
                <tr>
                    <td>Work Style</td>
                    <td><%=dominantType.work_style%></td>
                    <td><%=subDominantType.work_style%></td>
                </tr>
                <tr>
                    <td>Main Work Competency</td>
                    <td><%=dominantType.work_competency%></td>
                    <td><%=subDominantType.work_competency%></td>
                </tr>
                <tr>
                    <td>Pace</td>
                    <td><%=dominantType.pace%></td>
                    <td><%=subDominantType.pace%></td>
                </tr>
                <tr>
                    <td>image</td>
                    <td><%=dominantType.image%></td>
                    <td><%=subDominantType.image%></td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
