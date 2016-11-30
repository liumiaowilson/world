<%
String page_title = "Form Result";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Form Result</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <%
            String result = (String)request.getSession().getAttribute("form_result");
            request.getSession().removeAttribute("form_result");
            %>
            <%=FormatUtils.toHtml(result)%>
        </div>
    </div>
</div>
<button type="button" class="btn btn-default" id="url_back_btn">Back</button>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
