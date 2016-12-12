<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_jquery_ui.jsp" %>
<%@ include file="navbar.jsp" %>
<ul id="sortable">
    <li class="ui-state-default">1</li>
    <li class="ui-state-default">2</li>
    <li class="ui-state-default">3</li>
    <li class="ui-state-default">4</li>
    <li class="ui-state-default">5</li>
    <li class="ui-state-default">6</li>
    <li class="ui-state-default">7</li>
    <li class="ui-state-default">8</li>
    <li class="ui-state-default">9</li>
    <li class="ui-state-default">10</li>
    <li class="ui-state-default">11</li>
    <li class="ui-state-default">12</li>
</ul>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_jquery_ui.jsp" %>
<script>
            $(document).ready(function(){
                $( "#sortable" ).sortable();
                $( "#sortable" ).disableSelection();
            });
</script>
<%@ include file="footer.jsp" %>
