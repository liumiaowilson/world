<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="org.wilson.world.quiz.*" %>
<%@ page import="java.util.*" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="Content-Type" content="text/html charset=UTF-8">

        <%
        String quiz_result = (String)request.getSession().getAttribute("world-public-quiz-result");
        if(quiz_result == null) {
            quiz_result = "";
        }
        else {
            request.getSession().removeAttribute("world-public-quiz-result");
        }
        String error = null;
        Quiz quiz = null;
        int id = -1;
        String id_str = request.getParameter("id");
        try {
            id = Integer.parseInt(id_str);
        }
        catch(Exception e) {
        }
        quiz = QuizDataManager.getInstance().getQuiz(id);
        if(quiz == null) {
            error = "Quiz is not found";
        }

        String title = "Quiz";
        if(quiz != null) {
            title = quiz.getName();
        }
        %>
        <title><%=title%></title>
    </head>

    <body>
        <%
        if(error != null) {
        %>
        <%=error%>
        <%
        }
        else {
        %>
        <%=quiz_result%> <br/>
        <form action="<%=basePath%>/api/quiz_data/done" method="post">
            <input type="hidden" name="id" value="<%=id%>"/>
            <%
            QuizDataManager.getInstance().clearQuizPaper();
            QuizPaper paper = QuizDataManager.getInstance().getQuizPaper(quiz);
            List<QuizItem> quizItems = quiz.getQuizItems();
            for(QuizItem quizItem : quizItems) {
            %>
            <%=quizItem.question%><br/>
            <%
                for(QuizItemOption quizItemOption : quizItem.options) {
            %>
            <label><input type="checkbox" value="<%=quizItem.id%>_<%=quizItemOption.id%>" id="selection" name="selection"/><%=quizItemOption.answer%></label><br/>
            <%
                }
            %>
            <hr/>
            <%
            }
            %>
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="Done"/>
        </form>
        <%
        }
        %>
    </body>
    <script>
    </script>
</html>
