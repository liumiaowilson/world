<%
String page_title = "Article Speed Metrics";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Article Speed Metrics</h3>
    </div>
    <div class="panel-body">
        <table class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <%
                int [] array = ArticleManager.getInstance().getArrayOfWPM();
                if(array != null) {
                %>
                <tr>
                    <td>Average Speed(Word Per Minute)</td>
                    <td><%=array[0]%></td>
                </tr>
                <tr>
                    <td>Minimum Speed(Word Per Minute)</td>
                    <td><%=array[1]%></td>
                </tr>
                <tr>
                    <td>Maximum Speed(Word Per Minute)</td>
                    <td><%=array[2]%></td>
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
