        <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
        <link href="../css/ie10-viewport-bug-workaround.css" rel="stylesheet">

        <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
            <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->
    </head>

    <body>
        <input type="hidden" id="basePath" value="<%=basePath%>"/>
        <input type="hidden" id="tmp_value_holder" value=""/>
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
                        <a class="navbar-brand" href="javascript:jumpTo('user.jsp')">Welcome <strong><%=username%></strong><span class="badge"><%=ExpManager.getInstance().getLevel()%></span></a>
                    </div>
                    <div class="navbar-collapse collapse">
                        <ul class="nav navbar-nav">
                            <li class="active"><a href="javascript:jumpTo('../index.jsp')">Home</a></li>
                            <li class="dropdown">
                                <a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Items <span class="caret"></span></a>
                                <ul class="dropdown-menu multi-level">
                                    <li><a href="javascript:jumpTo('idea_list.jsp')">Idea</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="javascript:jumpTo('task_list.jsp')">Task</a></li>
                                    <li class="dropdown-submenu">
                                        <a href="javascript:void(0)">Advanced</a>
                                        <ul class="dropdown-menu">
                                            <li><a href="javascript:jumpTo('context_list.jsp')">Context</a></li>
                                            <li><a href="javascript:jumpTo('task_seed_list.jsp')">Task Seed</a></li>
                                            <li><a href="javascript:jumpTo('task_follower_list.jsp')">Task Follower</a></li>
                                            <li><a href="javascript:jumpTo('task_template_info_list.jsp')">Task Template</a></li>
                                            <li><a href="javascript:jumpTo('task_attr_def_list.jsp')">Task Attr Def</a></li>
                                            <li><a href="javascript:jumpTo('task_attr_rule_list.jsp')">Task Attr Rule</a></li>
                                        </ul>
                                    </li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="javascript:jumpTo('quest_list.jsp')">Quest</a></li>
                                    <li><a href="javascript:jumpTo('quest_def_list.jsp')">Quest Def</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="javascript:jumpTo('habit_list.jsp')">Habit</a></li>
                                    <li><a href="javascript:jumpTo('habit_trace_check.jsp')">Habit Check</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="javascript:jumpTo('contact_list.jsp')">Contact</a></li>
                                    <li><a href="javascript:jumpTo('contact_attr_def_list.jsp')">Contact Attr Def</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="javascript:jumpTo('scenario_list.jsp')">Scenario</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="javascript:jumpTo('quote_list.jsp')">Quote</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li class="dropdown-submenu">
                                        <a href="javascript:void(0)">Advanced</a>
                                        <ul class="dropdown-menu">
                                            <li><a href="javascript:jumpTo('action_list.jsp')">Action</a></li>
                                            <li><a href="javascript:jumpTo('extension_point_list.jsp')">Extension Point</a></li>
                                            <li role="separator" class="divider"></li>
                                            <li><a href="javascript:jumpTo('query_list.jsp')">Query</a></li>
                                        </ul>
                                    </li>
                                </ul>
                            </li>
                            <li class="dropdown">
                                <a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Console <span class="caret"></span></a>
                                <ul class="dropdown-menu">
                                    <li><a href="javascript:jumpTo('alert.jsp')">Alerts</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="javascript:jumpTo('config.jsp')">Configuration</a></li>
                                    <li><a href="javascript:jumpTo('env.jsp')">Environment</a></li>
                                    <li><a href="javascript:jumpTo('jobs.jsp')">Jobs</a></li>
                                    <li><a href="javascript:jumpTo('log.jsp')">Logs</a></li>
                                    <li><a href="javascript:jumpTo('management.jsp')">Management</a></li>
                                    <li><a href="javascript:jumpTo('public.jsp')">Public</a></li>
                                    <li><a href="javascript:jumpTo('data.jsp')">User Data</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="javascript:jumpTo('eval.jsp')">Eval Script</a></li>
                                    <li><a href="javascript:jumpTo('execute.jsp')">Execute SQL</a></li>
                                    <li><a href="javascript:jumpTo('run.jsp')">Run Shell</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="javascript:jumpTo('cache.jsp')">Cache</a></li>
                                    <li><a href="javascript:jumpTo('database.jsp')">Database</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="javascript:jumpTo('error_info_list.jsp')">Error Info</a></li>
                                    <li><a href="javascript:jumpTo('stats.jsp')">Statistics</a></li>
                                    <li><a href="javascript:jumpTo('usage.jsp')">Usage</a></li>
                                </ul>
                            </li>
                            <li class="dropdown">
                                <%
                                Context currentContext = ContextManager.getInstance().getCurrentContext();
                                String userHint = "User";
                                String userColor = "black";
                                if(currentContext != null) {
                                    userHint = currentContext.name;
                                    userColor = currentContext.color;
                                }
                                %>
                                <a href="javascript:void(0)" class="dropdown-toggle" style="color: <%=userColor%>" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><%=userHint%><span class="caret"></span></a>
                                <ul class="dropdown-menu">
                                    <%
                                    List<Context> allContexts = ContextManager.getInstance().getContexts();
                                    Collections.sort(allContexts, new Comparator<Context>(){
                                        public int compare(Context c1, Context c2) {
                                            return c1.name.compareTo(c2.name);
                                        }
                                    });
                                    for(Context c : allContexts) {
                                    %>
                                    <li><a href="javascript:setCurrentContext(<%=c.id%>)" style="color: <%=c.color%>"><%=c.name%></a></li>
                                    <%
                                    }
                                    %>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="javascript:jumpTo('user.jsp')">User</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="javascript:jumpTo('status_list.jsp')">Status</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="javascript:jumpTo('faq.jsp')">FAQ</a></li>
                                    <li role="separator" class="divider"></li>
                                    <li><a href="javascript:jumpTo('../signout.jsp')">Sign Out</a></li>
                                </ul>
                            </li>
                            <%
                            Date date = new Date();
                            String current_date = FormatUtils.format(date);
                            boolean showCurrentDate = ConfigManager.getInstance().getConfigAsBoolean("header.show.server_time");
                            if(showCurrentDate) {
                            %>
                            <li><a href="javascript:void(0)">Server Time: <%=current_date%></a></li>
                            <%
                            }
                            %>
                        </ul>
                    </div><!--/.nav-collapse -->
                </div>
            </nav>
        </div> <!-- /container -->

        <div class="container">
            <div class="page-header">
                <h1><%=page_title%></h1>
            </div>
            <div class="alert alert-success" role="alert" id="alert_success" style="display:none"></div>
            <div class="alert alert-info" role="alert" id="alert_info" style="display:none"></div>
            <div class="alert alert-warning" role="alert" id="alert_warning" style="display:none"></div>
            <div class="alert alert-danger" role="alert" id="alert_danger" style="display:none"></div>
            <div class="alert alert-info" role="alert" id="alert_ajax" style="display:none"></div>
