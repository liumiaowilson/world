<%@ page import="org.wilson.world.profile.*" %>
<%
String page_title = "Profile View Smalley";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Smalley Personality</h3>
    </div>
    <div class="panel-body">
        <%
        SmalleyProfile smalleyProfile = ProfileManager.getInstance().getSmalleyProfile();
        String smalleyType = smalleyProfile.getType();
        %>
        <h4>You are a(n) <b><%=smalleyType%></b></h4>
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Aspect</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
                <%
                SmalleyPersonalityInterpretation spi = ProfileManager.getInstance().getSmalleyPersonalityInterpretation(smalleyType);
                for(Map.Entry<String, String> entry : spi.aspects.entrySet()) {
                %>
                <tr>
                    <td><%=entry.getKey()%></td>
                    <td><%=entry.getValue()%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
