<%@ page import="org.wilson.world.test.*" %>
<%
String page_title = "Profile Measure";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Profile Measure</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Big Five Personality</td>
                    <td><a href="<%=QuizDataManager.getInstance().getQuizUrl(BigFivePersonalityQuiz.class)%>">Do Quiz</a></td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
