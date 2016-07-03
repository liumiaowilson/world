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

        <title>Sign In</title>

        <!-- Bootstrap core CSS -->
        <link href="css/bootstrap.min.css" rel="stylesheet">

        <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
        <link href="css/ie10-viewport-bug-workaround.css" rel="stylesheet">

        <!-- Custom styles for this template -->
        <link href="css/signin.css" rel="stylesheet">

        <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
            <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->
    </head>

    <body>
        <%
        String from_url = request.getParameter("from");
        if(from_url == null) {
            from_url = "index.jsp";
        }
        %>
        <input type="hidden" id="from_url" value="<%=from_url%>"/>
        <div class="container">
            <form class="form-signin" id="signin_form" data-toggle="validator" role="form">
                <h2 class="form-signin-heading">Wilson's World</h2>
                <div class="form-group">
                    <label for="username" class="sr-only">Username</label>
                    <input type="text" id="username" class="form-control" placeholder="Username" required autofocus>
                </div>
                <div class="form-group">
                    <label for="password" class="sr-only">Password</label>
                    <input type="password" id="password" class="form-control" placeholder="Password" required>
                </div>
                <div class="alert alert-danger" role="alert" id="signin_error">
                </div>
                <div class="form-group">
                    <button class="btn btn-lg btn-primary btn-block" type="submit" id="signin_btn">Sign in</button>
                </div>
            </form>
        </div> <!-- /container -->

        <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
        <script src="js/ie10-viewport-bug-workaround.js"></script>
        <script src="js/jquery-2.2.4.min.js"></script>
        <script src="js/bootstrap.min.js"></script>
        <script src="js/validator.min.js"></script>
        <script>
$(document).ready(function(){
    $('#signin_error').hide();

    $('#signin_form').validator().on('submit', function (e) {
        if (e.isDefaultPrevented()) {
            // handle the invalid form...
        } else {
            e.preventDefault();
            $('#signin_error').hide();
            $.post("api/security/login", { username: $('#username').val(), password: $('#password').val()}, function(data) {
                var status = data.result.status;
                if("OK" == status) {
                    var from_url = $('#from_url').val();
                    window.location.href = from_url;
                }
                else {
                    var msg = data.result.message;
                    $('#signin_error').text(msg);
                    $('#signin_error').show();
                }
            }, "json");
        }
    })
});
        </script>
    </body>
</html>
