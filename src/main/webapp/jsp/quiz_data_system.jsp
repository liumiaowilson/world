<%@ page import="org.wilson.world.quiz.*" %>
<%
String page_title = "Quiz Data System";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Quiz Data System</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Class</th>
                </tr>
            </thead>
            <tbody>
                <%
                List<Quiz> quizes = QuizDataManager.getInstance().getQuizes();
                List<Quiz> systemQuizes = new ArrayList<Quiz>();
                for(Quiz quiz : quizes) {
                    if(quiz.getId() < 0) {
                        systemQuizes.add(quiz);
                    }
                }
                Collections.sort(systemQuizes, new Comparator<Quiz>(){
                    public int compare(Quiz q1, Quiz q2) {
                        return q1.getName().compareTo(q2.getName());
                    }
                });
                for(Quiz systemQuiz : systemQuizes) {
                %>
                <tr>
                    <td><%=systemQuiz.getId()%></td>
                    <td><%=systemQuiz.getName()%></td>
                    <td><%=systemQuiz.getClass().getCanonicalName()%></td>
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
