<%@ page import="org.wilson.world.query.*" %>

        <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
        <link href="../css/ie10-viewport-bug-workaround.css" rel="stylesheet">

        <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
            <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->
    </head>

    <body>
        <script>
            function jumpTo(relative_url) {
                if(relative_url.includes("?")) {
                    relative_url = relative_url + "&_request_end_time=" + $('#request_end_time').val() + "&_request_start_time=" + new Date().getTime();
                }
                else {
                    relative_url = relative_url + "?_request_end_time=" + $('#request_end_time').val() + "&_request_start_time=" + new Date().getTime();
                }
                window.location.href = $('#basePath').val() + "/jsp/" + relative_url;
            }

            function getAPIURL(relative_url) {
                return $('#basePath').val() + "/" + relative_url;
            }

            function jumpBack() {
                window.location.href = "<%=URLManager.getInstance().getLastUrl()%>";
            }

            function jumpCurrent() {
                window.location.href = "<%=URLManager.getInstance().getCurrentUrl()%>";
            }
        </script>
        <input type="hidden" id="basePath" value="<%=basePath%>"/>
        <input type="hidden" id="tmp_value_holder" value=""/>
        <input type="hidden" id="request_end_time" value=""/>
        <div id="notesDialog" class="modal fade" role="dialog">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h4 class="modal-title">Notes</h4>
                    </div>
                    <div class="modal-body">
                        <%
                        String notes = NotesManager.getInstance().getNotes();
                        if(notes == null) {
                            notes = "";
                        }
                        %>
                        <textarea id="notesContent" style="width:100%" rows="5" maxlength="400" placeholder="Enter notes" autofocus><%=notes%></textarea>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" onclick="saveNotes()">Save</button>
                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
        <div id="wordLookupDialog" class="modal fade" role="dialog">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h4 class="modal-title">Word Lookup</h4>
                    </div>
                    <div class="modal-body">
                        <fieldset class="form-group">
                            <label for="dialog_word">Word</label>
                            <input type="text" class="form-control" id="dialog_word" maxlength="50" placeholder="Enter word" required autofocus>
                        </fieldset>
                        <div id="dialog_explanation" class="well">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" onclick="lookupWord()">Look Up</button>
                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
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
                            <%=MenuManager.getInstance().generateNavbar()%>
                        </ul>
                    </div><!--/.nav-collapse -->
                </div>
            </nav>
        </div> <!-- /container -->

        <div class="container">
            <div class="row">
                <nav class="col-sm-2">
                    <ul class="nav nav-pills nav-stacked" data-spy="affix" data-offset-top="205">
                        <%=MenuManager.getInstance().generateToolbar()%>
                    </ul>
                </nav>
                <div class="col-sm-10">
                    <div class="page-header">
                        <h1><%=page_title%></h1>
                    </div>
                    <div class="alert alert-success" role="alert" id="alert_success" style="display:none"></div>
                    <div class="alert alert-info" role="alert" id="alert_info" style="display:none"></div>
                    <div class="alert alert-warning" role="alert" id="alert_warning" style="display:none"></div>
                    <div class="alert alert-danger" role="alert" id="alert_danger" style="display:none"></div>
                    <div class="alert alert-info" role="alert" id="alert_ajax" style="display:none"></div>
