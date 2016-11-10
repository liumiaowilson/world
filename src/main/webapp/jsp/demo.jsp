<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_multiselect.jsp" %>
<%@ include file="navbar.jsp" %>
<select id="example-getting-started" multiple="multiple">
    <option value="cheese">Cheese</option>
    <option value="tomatoes">Tomatoes</option>
    <option value="mozarella">Mozzarella</option>
    <option value="mushrooms">Mushrooms</option>
    <option value="pepperoni">Pepperoni</option>
    <option value="onions">Onions</option>
</select>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_multiselect.jsp" %>
<script>
$(document).ready(function() {
    $('#example-getting-started').multiselect();
});
</script>
<%@ include file="footer.jsp" %>
