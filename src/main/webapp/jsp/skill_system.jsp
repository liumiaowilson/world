<%@ page import="org.wilson.world.skill.*" %>
<%
String page_title = "System Skill";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">System Skill</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<Skill> skills = SkillDataManager.getInstance().getSystemSkills();
                Collections.sort(skills, new Comparator<Skill>(){
                    public int compare(Skill s1, Skill s2) {
                        return s1.getName().compareTo(s2.getName());
                    }
                });
                for(Skill skill : skills) {
                %>
                <tr>
                    <td><%=skill.getId()%></td>
                    <td><%=skill.getName()%></td>
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
