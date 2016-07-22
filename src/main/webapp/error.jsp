<%@ page isErrorPage="true" import="java.io.*" %>
<%@ page import="org.wilson.world.manager.*" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
        <meta name="description" content="">
        <meta name="author" content="">
        <!--<link rel="icon" href="../../favicon.ico">-->

        <title>Welcome</title>

        <!-- Bootstrap core CSS -->
        <link href="<%=basePath%>/css/bootstrap.min.css" rel="stylesheet">

        <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
        <link href="<%=basePath%>/css/ie10-viewport-bug-workaround.css" rel="stylesheet">

        <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
            <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->
    </head>

    <body>
        <div class="container">
            <div class="page-header">
                <h1>Error</h1>
            </div>
            <div class="alert alert-danger" role="alert">
                Something is wrong with the request. Please see the <a href="log.jsp">logs</a>.
            </div>
            <%
            if(ConfigManager.getInstance().isInDebugMode()) {
            %>
            <div class="well">
                <%
                exception.printStackTrace(new java.io.PrintWriter(out));
                %>
            </div>
            <%
            }
            %>
        </div> <!-- /container -->

        <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
        <script src="<%=basePath%>/js/ie10-viewport-bug-workaround.js"></script>
        <script src="<%=basePath%>/js/jquery-2.2.4.min.js"></script>
        <script src="<%=basePath%>/js/bootstrap.min.js"></script>
    </body>
</html>
