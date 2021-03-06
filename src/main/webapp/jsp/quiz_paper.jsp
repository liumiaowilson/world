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
    Map<String, String[]> parameters = request.getParameterMap();
    paper.setParameters(parameters);
    item = paper.current();
}

int current = paper.getCurrent();
int total = paper.getNumOfItems();
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <%
        if(item != null) {
        %>
        <h3 class="panel-title">Quiz Paper [<%=quiz.getName()%> <%=current + 1%> / <%=total%>]</h3>
        <%
        }
        else {
        %>
        <h3 class="panel-title">Quiz Paper [<%=quiz.getName()%>]</h3>
        <%
        }
        %>
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
            if(quiz.getQuizItems().isEmpty()) {
                paper_status = "Quiz has no content.";
            }
            else if("NotStarted".equals(paper.getStatus())) {
                paper_status = "Click Next to start quiz [" + quiz.getName() + "].";
            }
            else if("Finished".equals(paper.getStatus())) {
                QuizResult result = quiz.process(paper);
                if(result != null) {
                    paper_status = result.message;
                }
                else {
                    paper_status = "The quiz has been done.";
                }
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
        else {
        %>
        <button type="button" class="btn btn-primary" id="redo_btn">Redo</button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <%
        }
        %>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%
String redo_url = QuizDataManager.getInstance().getRedoUrl();
if(redo_url == null) {
    redo_url = "javascript:jumpTo('quiz_paper.jsp?id=" + id + "')";
}
%>
<script>
            $('#redo_btn').click(function(){
                eval("<%=redo_url%>");
            });

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
