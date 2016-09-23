<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="org.wilson.world.quiz.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Quiz List</title>
    </head>
        <%
        List<Quiz> quizes = QuizDataManager.getInstance().getPublicQuizes();
        Collections.sort(quizes, new Comparator<Quiz>(){
            public int compare(Quiz q1, Quiz q2) {
                return q1.getName().compareTo(q2.getName());
            }
        });
        for(Quiz quiz : quizes) {
        %>
        <a href="quiz.jsp?id=<%=quiz.getId()%>"><%=quiz.getName()%></a><br/>
        <%
        }
        %>
    <body>

    </body>
    <script>
    </script>
</html>
