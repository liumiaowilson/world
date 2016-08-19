<%@ page import="org.wilson.world.quiz.*" %>
<%
String page_title = "Quiz Paper";
%>
<%@ include file="header.jsp" %>
<%
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
    response.sendRedirect("quiz_data_list.jsp");
    return;
}
QuizPaper paper = QuizDataManager.getInstance().getQuizPaper(quiz);
QuizItem item = null;
String action = request.getParameter("action");
if("next".equals(action)) {
    item = paper.next();
}
else {
    item = paper.current();
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Quiz Paper</h3>
    </div>
    <div class="panel-body">
        <%
        if(item != null) {
        %>
        <div class="well"><%=item.question%></div>
        <%
            if("Single".equals(item.mode.name())) {
                for(QuizItemOption option : item.options) {
        %>
        <div class="radio">
            <label><input type="radio" name="answer" value="<%=option.id%>"><%=option.answer%></label>
        </div>
        <%
                }
            }
            else if("Multiple".equals(item.mode.name())) {
                for(QuizItemOption option : item.options) {
        %>
        <div class="checkbox">
            <label><input type="checkbox" value="<%=option.id%>"><%=option.answer%></label>
        </div>
        <%
                }
            }
        }
        else {
            String paper_status = "";
            if("NotStarted".equals(paper.getStatus())) {
                paper_status = "Click Next to start the quiz.";
            }
            else if("Finished".equals(paper.getStatus())) {
                QuizResult result = quiz.process(paper);
                paper_status = result.message;
            }
        %>
        <div class="well"><%=paper_status%></div>
        <%
        }
        %>
        <%
        if(!"Finished".equals(paper.getStatus())) {
        %>
        <button type="button" class="btn btn-primary" id="next_btn">Next</button>
        <button type="button" class="btn btn-default" id="refresh_btn">Refresh</button>
        <%
        }
        %>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                $('#next_btn').click(function(){
                    <%
                    if(item != null) {
                    %>
                        var ids = [];
                        <%
                        if("Single".equals(item.mode.name())) {
                        %>
                        ids.push($('input[type="radio"]:checked').val());
                        <%
                        }
                        else if("Multiple".equals(item.mode.name())) {
                        %>
                        $('input[type=checkbox]').each(function () {
                            if (this.checked) {
                                ids.push(this.value);
                            }
                        });
                        <%
                        }
                        %>
                        $.post(getAPIURL("api/quiz_data/do_quiz"), { 'id': <%=id%>, 'itemId': <%=item.id%>, 'selection': ids.join(',') }, function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                jumpTo("quiz_paper.jsp?id=<%=id%>&action=next");
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    <%
                    }
                    else {
                    %>
                    jumpTo("quiz_paper.jsp?id=<%=id%>&action=next");
                    <%
                    }
                    %>
                });
                $('#refresh_btn').click(function(){
                    jumpTo("quiz_paper.jsp?id=<%=id%>");
                });
            });
</script>
<%@ include file="footer.jsp" %>
