<%
String page_title = "Today";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Word Of The Day</h3>
    </div>
    <div class="panel-body">
        <div class="well">
            <%
            String wotd = (String)WebManager.getInstance().get("word_of_the_day");
            if(wotd == null) {
            %>
            Not found
            <%
            }
            else {
            %>
            <a href="http://www.merriam-webster.com/dictionary/<%=wotd%>"><%=wotd%></a>
            <%
            }
            %>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
