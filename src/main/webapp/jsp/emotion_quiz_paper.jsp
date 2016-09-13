<%@ page import="org.wilson.world.quiz.*" %>
<%
String page_title = "Emotion Quiz Paper";
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
Emotion emotion = null;
if(item != null) {
    emotion = EmotionManager.getInstance().getEmotion(item.question);
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Emotion Quiz Paper</h3>
    </div>
    <div class="panel-body">
        <%
        if(item != null) {
        %>
        <div>
            <div id="container"/>
        </div>
        <hr/>
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
<%@ include file="import_script_highcharts.jsp" %>
<%
String redo_url = QuizDataManager.getInstance().getRedoUrl();
if(redo_url == null) {
    redo_url = "javascript:jumpTo('emotion_quiz_paper.jsp?id=" + id + "')";
}
%>
<script>
            $('#redo_btn').click(function(){
                eval("<%=redo_url%>");
            });

            $(document).ready(function(){
                <%
                if(emotion != null) {
                %>
                $('#container').highcharts({
                    chart: {
                        polar: true,
                        type: 'line'
                    },
                    title: {
                        text: 'Emotion Structure',
                        x: -80
                    },
                    pane: {
                        size: '80%'
                    },
                    xAxis: {
                        categories: ['Ecstacy', 'Admiration', 'Rage', 'Vigilance', 'Grief', 'Loathing', 'Terror', 'Amazement'],
                        tickmarkPlacement: 'on',
                        lineWidth: 0
                    },
                    yAxis: {
                        gridLineInterpolation: 'polygon',
                        lineWidth: 0,
                        min: -10
                    },
                    tooltip: {
                        shared: true,
                        pointFormat: '<span style="color:{series.color}">{series.name}: <b>{point.y:,.0f}</b><br/>'
                    },
                    legend: {
                        align: 'right',
                        verticalAlign: 'top',
                        y: 70,
                        layout: 'vertical'
                    },
                    series: [{
                        name: 'Unknown',
                        data: [<%=emotion.ecstacy%>,<%=emotion.admiration%>,<%=emotion.rage%>,<%=emotion.vigilance%>,<%=emotion.grief%>,<%=emotion.loathing%>,<%=emotion.terror%>,<%=emotion.amazement%>],
                        pointPlacement: 'on'
                    }]
                });
                <%
                }
                %>

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
                                jumpTo("emotion_quiz_paper.jsp?id=<%=id%>&action=next");
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    <%
                    }
                    else {
                    %>
                    jumpTo("emotion_quiz_paper.jsp?id=<%=id%>&action=next");
                    <%
                    }
                    %>
                });
                $('#refresh_btn').click(function(){
                    jumpTo("emotion_quiz_paper.jsp?id=<%=id%>");
                });
            });
</script>
<%@ include file="footer.jsp" %>
