<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Notes</title>
    </head>

    <body>
        <form action="api/notes/send_notes" method="post">
            <%
            String notes = NotesManager.getInstance().getNotes();
            if(notes == null) {
                notes = "";
            }
            %>
            <textarea name="notes" style="width: 100%" rows=10><%=notes%></textarea>
            <br/>
            <input type="submit" value="Send"/>
        </form>
    </body>
</html>
