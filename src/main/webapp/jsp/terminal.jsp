<%
String page_title = "Terminal";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_terminal.jsp" %>
<%@ include file="navbar.jsp" %>
<div id="terminal"/>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_terminal.jsp" %>
<script>
var commands = [
    <%
    List<String> commandNames = CommandManager.getInstance().getCommandNames();
    for(String commandName : commandNames) {
    %>
    "<%=commandName%>",
    <%
    }
    %>
];

$('#terminal').terminal(function(command, term) {
    if (command !== '') {
        try {
            $.post(getAPIURL("api/terminal/execute"), { 'line': command }, function(data){
                var status = data.result.status;
                var msg = data.result.message;
                if("OK" == status) {
                    term.echo(new String(msg));
                }
                else {
                    term.error(new String(msg));
                }
            });
        } catch(e) {
            term.error(new String(e));
        }
    } else {
       term.echo('');
    }
}, {
    greetings: 'Command Terminal',
    name: 'terminal',
    height: 500,
    prompt: 'command> ',
    tabcompletion: true,
    completion: function(terminal, command, callback) {
        callback(commands);
    }
});
</script>
<%@ include file="footer.jsp" %>
