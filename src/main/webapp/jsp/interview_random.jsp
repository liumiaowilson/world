<%
String page_title = "Interview Random";
%>
<%@ include file="header.jsp" %>
<%
Interview interview = InterviewManager.getInstance().randomInterview();
if(interview == null) {
    response.sendRedirect("interview_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Interview Random</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <p><b><%=interview.question%></b></p>
            <p><%=interview.answer%></p>
        </div>
    </div>
</div>
<button type="button" class="btn btn-default" id="url_back_btn">Back</button>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
