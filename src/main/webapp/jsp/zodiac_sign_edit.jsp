<%
String page_title = "Zodiac Sign Edit";
%>
<%@ include file="header.jsp" %>
<%
ZodiacSign zodiac_sign = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
zodiac_sign = ZodiacSignManager.getInstance().getZodiacSign(id);
if(zodiac_sign == null) {
    response.sendRedirect("zodiac_sign_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=zodiac_sign.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" placeholder="Enter name" value="<%=zodiac_sign.name%>" disabled>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <table class="table table-striped table-bordered">
        <thead>
            <tr>
                <th>Name</th>
                <th>Value</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>Range</td>
                <td><%=zodiac_sign.fromMonth%>/<%=zodiac_sign.fromDay%> to <%=zodiac_sign.toMonth%>/<%=zodiac_sign.toDay%></td>
            </tr>
            <tr>
                <td>Strengths</td>
                <td><%=zodiac_sign.strengths%></td>
            </tr>
            <tr>
                <td>Weaknesses</td>
                <td><%=zodiac_sign.weaknesses%></td>
            </tr>
            <tr>
                <td>Likes</td>
                <td><%=zodiac_sign.likes%></td>
            </tr>
            <tr>
                <td>Dislikes</td>
                <td><%=zodiac_sign.dislikes%></td>
            </tr>
            <tr>
                <td>Compatibility</td>
                <td><%=zodiac_sign.compatibility%></td>
            </tr>
            <tr>
                <td>Partnership</td>
                <td><%=zodiac_sign.partnership%></td>
            </tr>
        </tbody>
    </table>
    <div class="well">
        <p><b><%=zodiac_sign.description%></b></p>
    </div>
    <div class="form-group">
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
