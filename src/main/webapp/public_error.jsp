<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Error</title>
    </head>

    <body>
        <%
        String public_error = (String)request.getSession().getAttribute("world-public-error");
        request.getSession().removeAttribute("world-public-error");
        if(public_error == null) {
            public_error = "Invalid key for public pages";
        }
        %>
        <%=public_error%>
    </body>
</html>
