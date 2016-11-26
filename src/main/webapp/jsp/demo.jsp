<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_terminal.jsp" %>
<%@ include file="navbar.jsp" %>
<div id="term_demo"/>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_terminal.jsp" %>
<script>
$('#term_demo').terminal(function(command, term) {
    if (command !== '') {
        try {
            $.get(getAPIURL("api/demo/names"), function(data){
                term.echo(new String(data));
            });
        } catch(e) {
            term.error(new String(e));
        }
    } else {
       term.echo('');
    }
}, {
    greetings: 'Javascript Interpreter',
    name: 'js_demo',
    height: 200,
    prompt: 'js> '
});
</script>
<%@ include file="footer.jsp" %>
