<%@ page import="org.wilson.world.quiz.*" %>
<%
String page_title = "Test Quiz";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Test Quiz</h3>
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
                List<Quiz> quizes = QuizDataManager.getInstance().getTestQuizes();
                for(Quiz quiz : quizes) {
                %>
                <tr>
                    <td><a href="javascript:jumpTo('quiz_paper.jsp?id=<%=quiz.getId()%>')"><%=quiz.getId()%></a></td>
                    <td><%=quiz.getName()%></td>
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
