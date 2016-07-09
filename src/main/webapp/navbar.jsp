        <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
        <link href="css/ie10-viewport-bug-workaround.css" rel="stylesheet">

        <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
            <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->
    </head>

    <body>
        <div class="container">
            <nav class="navbar navbar-default">
                <div class="container">
                    <div class="navbar-header">
                        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-collapse">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>
                        <%
                        String username = (String)session.getAttribute("world-user");
                        %>
                        <a class="navbar-brand" href="#">Welcome <strong><%=username%></strong></a>
                    </div>
                    <div class="navbar-collapse collapse">
                        <ul class="nav navbar-nav">
                            <li class="active"><a href="index.jsp">Home</a></li>
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Items <span class="caret"></span></a>
                                <ul class="dropdown-menu">
                                    <li><a href="idea_list.jsp">Idea</a></li>
                                </ul>
                            </li>
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Console <span class="caret"></span></a>
                                <ul class="dropdown-menu">
                                    <li><a href="cache.jsp">Cache</a></li>
                                    <li><a href="config.jsp">Configuration</a></li>
                                    <li><a href="database.jsp">Database</a></li>
                                    <li><a href="env.jsp">Environment</a></li>
                                    <li><a href="execute.jsp">Execute SQL</a></li>
                                    <li><a href="log.jsp">Logs</a></li>
                                    <li><a href="management.jsp">Management</a></li>
                                    <li><a href="run.jsp">Run Shell</a></li>
                                    <li><a href="usage.jsp">Usage</a></li>
                                    <li><a href="data.jsp">User Data</a></li>
                                </ul>
                            </li>
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Setting <span class="caret"></span></a>
                                <ul class="dropdown-menu">
                                    <li><a href="signout.jsp">Sign Out</a></li>
                                </ul>
                            </li>
                            <%
                            Date date = new Date();
                            String current_date = FormatUtils.format(date);
                            boolean showCurrentDate = ConfigManager.getInstance().getConfigAsBoolean("header.show.server_time");
                            if(showCurrentDate) {
                            %>
                            <li><a href="#">Server Time: <%=current_date%></a></li>
                            <%
                            }
                            %>
                        </ul>
                    </div><!--/.nav-collapse -->
                </div>
            </nav>
        </div> <!-- /container -->

        <div class="container">
            <div class="alert alert-success" role="alert" id="alert_success" style="display:none"></div>
            <div class="alert alert-info" role="alert" id="alert_info" style="display:none"></div>
            <div class="alert alert-warning" role="alert" id="alert_warning" style="display:none"></div>
            <div class="alert alert-danger" role="alert" id="alert_danger" style="display:none"></div>
