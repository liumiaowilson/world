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
                            <li class="active"><a href="javascript:jumpTo('../index.jsp')">Home</a></li>
                            <li class="dropdown">
                                <a href="javascript:void(0)" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Items <span class="caret"></span></a>
                                <ul class="dropdown-menu multi-level">
                                    <li class="dropdown-submenu">
                                        <a href="javascript:void(0)">Efficiency</a>
                                        <ul class="dropdown-menu">
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Idea</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('idea_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('idea_new.jsp')">New</a></li>
                                                    <li><a href="javascript:jumpTo('idea_new_batch.jsp')">Batch New</a></li>
                                                    <li><a href="javascript:jumpTo('post_process.jsp')">Process Posts</a></li>
                                                    <li><a href="javascript:randomIdea()">Random</a></li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">View</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('idea_stats.jsp')">Statistics</a></li>
                                                        </ul>
                                                    </li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Iterator</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:startIdeaIterator()">Start</a></li>
                                                            <li><a href="javascript:stopIdeaIterator()">Stop</a></li>
                                                        </ul>
                                                    </li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Task</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('task_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('task_new.jsp')">New</a></li>
                                                    <li><a href="javascript:randomTask()">Random</a></li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">View</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('task_todo.jsp')">Todos</a></li>
                                                            <li><a href="javascript:jumpTo('task_queue.jsp')">Queue</a></li>
                                                            <li><a href="javascript:jumpTo('task_graph.jsp')">Graph</a></li>
                                                            <li><a href="javascript:jumpTo('task_project.jsp')">Project</a></li>
                                                            <li><a href="javascript:jumpTo('calendar.jsp')">Calendar</a></li>
                                                        </ul>
                                                    </li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Iterator</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:startTaskIterator()">Start</a></li>
                                                            <li><a href="javascript:stopTaskIterator()">Stop</a></li>
                                                        </ul>
                                                    </li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Context</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('context_list.jsp')">List All</a></li>
                                                            <li><a href="javascript:jumpTo('context_new.jsp')">New</a></li>
                                                        </ul>
                                                    </li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Task Seed</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('task_seed_list.jsp')">User-Defined</a></li>
                                                            <li><a href="javascript:jumpTo('task_seed_new.jsp')">New</a></li>
                                                            <li><a href="javascript:jumpTo('task_seed_system.jsp')">System-Defined</a></li>
                                                        </ul>
                                                    </li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Task Follower</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('task_follower_list.jsp')">User-Defined</a></li>
                                                            <li><a href="javascript:jumpTo('task_follower_new.jsp')">New</a></li>
                                                            <li><a href="javascript:jumpTo('task_follower_show.jsp')">System-Defined</a></li>
                                                        </ul>
                                                    </li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Task Template</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('task_template_info_list.jsp')">List All</a></li>
                                                        </ul>
                                                    </li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Task Attr Def</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('task_attr_def_list.jsp')">List All</a></li>
                                                            <li><a href="javascript:jumpTo('task_attr_def_new.jsp')">New</a></li>
                                                        </ul>
                                                    </li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Task Attr Rule</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('task_attr_rule_list.jsp')">List All</a></li>
                                                            <li><a href="javascript:jumpTo('task_attr_rule_new.jsp')">New</a></li>
                                                        </ul>
                                                    </li>
                                                </ul>
                                            </li>
                                        </ul>
                                    </li>
                                    <li class="dropdown-submenu">
                                        <a href="javascript:void(0)">PIM</a>
                                        <ul class="dropdown-menu">
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Account</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('account_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('account_new.jsp')">New</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Checklist</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('checklist_overview.jsp')">Overview</a></li>
                                                    <li role="separator" class="divider"></li>
                                                    <li><a href="javascript:jumpTo('checklist_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('checklist_new.jsp')">New</a></li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Checklist Def</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('checklist_def_list.jsp')">List All</a></li>
                                                            <li><a href="javascript:jumpTo('checklist_def_new.jsp')">New</a></li>
                                                        </ul>
                                                    </li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Contact</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('contact_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('contact_new.jsp')">New</a></li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Contact Attr Def</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('contact_attr_def_list.jsp')">List All</a></li>
                                                            <li><a href="javascript:jumpTo('contact_attr_def_new.jsp')">New</a></li>
                                                        </ul>
                                                    </li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Detail</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('detail_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('detail_new.jsp')">New</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Document</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('document_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('document_new.jsp')">New</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Expense</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('expense_item_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('expense_item_new.jsp')">New</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Goal</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('goal_track.jsp')">Track</a></li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Goal Def</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('goal_def_list.jsp')">List All</a></li>
                                                            <li><a href="javascript:jumpTo('goal_def_new.jsp')">New</a></li>
                                                        </ul>
                                                    </li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Habit</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('habit_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('habit_new.jsp')">New</a></li>
                                                    <li><a href="javascript:jumpTo('habit_trace_check.jsp')">Check</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Health</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('bio_curve.jsp')">Bio Curves</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Humor</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('humor_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('humor_new.jsp')">New</a></li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Humor Pattern</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('humor_pattern_list.jsp')">List All</a></li>
                                                            <li><a href="javascript:jumpTo('humor_pattern_new.jsp')">New</a></li>
                                                        </ul>
                                                    </li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Journal</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('journal_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('journal_new.jsp')">New</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Quest</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('quest_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('quest_new.jsp')">New</a></li>
                                                    <li><a href="javascript:jumpTo('quest_info.jsp')">Quest Info</a></li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Query Def</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('quest_def_list.jsp')">List All</a></li>
                                                            <li><a href="javascript:jumpTo('quest_def_new.jsp')">New</a></li>
                                                        </ul>
                                                    </li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Quote</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('quote_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('quote_new.jsp')">New</a></li>
                                                    <li><a href="javascript:randomQuote()">Random</a></li>
                                                </ul>
                                            </li>
                                        </ul>
                                    </li>
                                    <li class="dropdown-submenu">
                                        <a href="javascript:void(0)">Gym</a>
                                        <ul class="dropdown-menu">
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Article</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('article_speed_train.jsp')">Train</a></li>
                                                    <li><a href="javascript:jumpTo('article_speed_metrics.jsp')">Metrics</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Beauty</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('beauty_train.jsp')">Train</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Creativity</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('creativity_train.jsp')">Train</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Face</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('face.jsp')">Train</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Fashion</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('fashion_train.jsp')">Train</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Feed</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('feed_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('feed_new.jsp')">New</a></li>
                                                    <li><a href="javascript:jumpTo('feed_read.jsp')">Train</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Flash Card</a>
                                                <ul class="dropdown-menu">
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Set</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('flashcard_set_list.jsp')">List All</a></li>
                                                            <li><a href="javascript:jumpTo('flashcard_set_new.jsp')">New</a></li>
                                                        </ul>
                                                    </li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Card</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('flashcard_list.jsp')">List All</a></li>
                                                            <li><a href="javascript:jumpTo('flashcard_new.jsp')">New</a></li>
                                                        </ul>
                                                    </li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">How-To</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('howto_train.jsp')">Train</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Image</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('image_train.jsp')">Train</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Imagination</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('imagination_item_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('imagination_item_new.jsp')">New</a></li>
                                                    <li><a href="javascript:jumpTo('imagination_item_train.jsp')">Train</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Memory</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('memory_train.jsp')">Train</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Porn</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('porn_train.jsp')">Train</a></li>
                                                    <li><a href="javascript:jumpTo('porn_gallery.jsp')">Gallery</a></li>
                                                    <li role="separator" class="divider"></li>
                                                    <li><a href="javascript:jumpTo('porn_config.jsp')">Config</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Quiz</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('quiz_data_list.jsp')">User-Defined</a></li>
                                                    <li><a href="javascript:jumpTo('quiz_data_new.jsp')">New</a></li>
                                                    <li><a href="javascript:jumpTo('quiz_data_system.jsp')">System-Defined</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Romance</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('romance_train.jsp')">Train</a></li>
                                                    <li role="separator" class="divider"></li>
                                                    <li><a href="javascript:jumpTo('romance_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('romance_new.jsp')">New</a></li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Romance Factor</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('romance_factor_list.jsp')">List All</a></li>
                                                            <li><a href="javascript:jumpTo('romance_factor_new.jsp')">New</a></li>
                                                        </ul>
                                                    </li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Scenario</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('scenario_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('scenario_new.jsp')">New</a></li>
                                                    <li><a href="javascript:jumpTo('scenario_read.jsp')">Train</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Word</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('word_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('word_new.jsp')">New</a></li>
                                                </ul>
                                            </li>
                                        </ul>
                                    </li>
                                    <li class="dropdown-submenu">
                                        <a href="javascript:void(0)">Relax</a>
                                        <ul class="dropdown-menu">
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Clip</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('clip_view.jsp')">View</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Manga</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('manga_view.jsp')">View</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Novel</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('novel_view.jsp')">View</a></li>
                                                    <li><a href="javascript:jumpTo('novel_gallery.jsp')">Gallery</a></li>
                                                </ul>
                                            </li>
                                        </ul>
                                    </li>
                                    <li class="dropdown-submenu">
                                        <a href="javascript:void(0)">System</a>
                                        <ul class="dropdown-menu">
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Action</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('action_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('action_new.jsp')">New</a></li>
                                                    <li><a href="javascript:jumpTo('extension_point_list.jsp')">Extension Point</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Asset</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('storage_asset_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('storage_asset_new.jsp')">New</a></li>
                                                    <li class="dropdown-submenu">
                                                        <a href="javascript:void(0)">Storage</a>
                                                        <ul class="dropdown-menu">
                                                            <li><a href="javascript:jumpTo('storage_list.jsp')">List All</a></li>
                                                            <li><a href="javascript:jumpTo('storage_new.jsp')">New</a></li>
                                                        </ul>
                                                    </li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Festival</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('festival_data_list.jsp')">User-Defined</a></li>
                                                    <li><a href="javascript:jumpTo('festival_data_new.jsp')">New</a></li>
                                                    <li><a href="javascript:jumpTo('festival_data_system.jsp')">System-Defined</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Hopper</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('hopper_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('hopper_new.jsp')">New</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Query</a>
                                                <ul class="dropdown-menu">
                                                    <%
                                                    List<QueryProcessor> nav_processors = QueryManager.getInstance().getQueryProcessors();
                                                    Collections.sort(nav_processors, new Comparator<QueryProcessor>(){
                                                        public int compare(QueryProcessor p1, QueryProcessor p2) {
                                                            return p1.getName().compareTo(p2.getName());
                                                        }
                                                    });
                                                    for(QueryProcessor nav_processor : nav_processors) {
                                                        if(!nav_processor.isQuickLink()) {
                                                            continue;
                                                        }
                                                    %>
                                                    <li><a href="javascript:jumpTo('query_execute.jsp?id=<%=nav_processor.getID()%>')"><%=nav_processor.getName()%></a></li>
                                                    <%
                                                    }
                                                    %>
                                                    <li role="separator" class="divider"></li>
                                                    <li><a href="javascript:jumpTo('query_list.jsp')">User-Defined</a></li>
                                                    <li><a href="javascript:jumpTo('query_new.jsp')">New</a></li>
                                                    <li><a href="javascript:jumpTo('query_processor.jsp')">System-Defined</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Variable</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('variable_list.jsp')">List All</a></li>
                                                    <li><a href="javascript:jumpTo('variable_new.jsp')">New</a></li>
                                                    <li><a href="javascript:jumpTo('script_var.jsp')">Script Variable</a></li>
                                                </ul>
                                            </li>
                                        </ul>
                                    </li>
                                    <li role="separator" class="divider"></li>
                                    <li class="dropdown-submenu">
                                        <a href="javascript:void(0)">Tools</a>
                                        <ul class="dropdown-menu">
                                            <li><a href="javascript:jumpTo('today.jsp')">Today</a></li>
                                            <li role="separator" class="divider"></li>
                                            <li><a href="javascript:jumpTo('item_search.jsp')">Search</a></li>
                                            <li><a href="javascript:openNotesDialog()">Notes</a></li>
                                            <li role="separator" class="divider"></li>
                                            <li><a href="javascript:jumpTo('color.jsp')">Color</a></li>
                                            <li><a href="javascript:jumpTo('word_lookup.jsp')">Word Lookup</a></li>
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
                                    <li><a href="javascript:jumpTo('web_job_info.jsp')">Web Jobs</a></li>
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
                                    <li class="dropdown-submenu">
                                        <a href="javascript:void(0)">Usage</a>
                                        <ul class="dropdown-menu">
                                            <li><a href="javascript:jumpTo('usage.jsp')">Statistics</a></li>
                                            <li><a href="javascript:jumpTo('memory.jsp')">Memory Monitor</a></li>
                                            <li><a href="javascript:jumpTo('list_file.jsp?path=.')">Data File</a></li>
                                        </ul>
                                    </li>
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
                                    <li class="dropdown-submenu">
                                        <a href="javascript:void(0)">Context</a>
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
                                        </ul>
                                    </li>
                                    <li class="dropdown-submenu">
                                        <a href="javascript:void(0)">RPG</a>
                                        <ul class="dropdown-menu">
                                            <li><a href="javascript:jumpTo('user.jsp')">User</a></li>
                                            <li><a href="javascript:jumpTo('inventory_item_list.jsp')">Inventory</a></li>
                                            <li><a href="javascript:jumpTo('user_skill_list.jsp')">User Skill</a></li>
                                            <li><a href="javascript:jumpTo('npc.jsp')">NPC</a></li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Shop</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('shop_item_list.jsp')">Buy</a></li>
                                                    <li><a href="javascript:jumpTo('shop_item_sell.jsp')">Sell</a></li>
                                                    <li><a href="javascript:jumpTo('exchange.jsp')">Exchange</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Trainer</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('trainer_skill_learn.jsp')">Learn</a></li>
                                                    <li><a href="javascript:jumpTo('trainer_skill_upgrade.jsp')">Upgrade</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Status</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('status_list.jsp')">User-Defined</a></li>
                                                    <li><a href="javascript:jumpTo('status_new.jsp')">New</a></li>
                                                    <li><a href="javascript:jumpTo('status_system.jsp')">System-Defined</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">User Item</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('user_item_data_list.jsp')">User-Defined</a></li>
                                                    <li><a href="javascript:jumpTo('user_item_data_new.jsp')">New</a></li>
                                                    <li><a href="javascript:jumpTo('user_item_system.jsp')">System-Defined</a></li>
                                                </ul>
                                            </li>
                                            <li class="dropdown-submenu">
                                                <a href="javascript:void(0)">Skill</a>
                                                <ul class="dropdown-menu">
                                                    <li><a href="javascript:jumpTo('skill_data_list.jsp')">User-Defined</a></li>
                                                    <li><a href="javascript:jumpTo('skill_data_new.jsp')">New</a></li>
                                                    <li><a href="javascript:jumpTo('skill_system.jsp')">System-Defined</a></li>
                                                </ul>
                                            </li>
                                        </ul>
                                    </li>
                                    <li class="dropdown-submenu">
                                        <a href="javascript:void(0)">FAQ</a>
                                        <ul class="dropdown-menu">
                                            <li><a href="javascript:jumpTo('faq.jsp')">FAQ</a></li>
                                            <li role="separator" class="divider"></li>
                                            <li><a href="javascript:jumpTo('faq_list.jsp')">User-Defined</a></li>
                                            <li><a href="javascript:jumpTo('faq_new.jsp')">New</a></li>
                                        </ul>
                                    </li>
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
                            <%
                            String modeStatus = NotifyManager.getInstance().getModeStatus();
                            if(modeStatus == null) {
                                modeStatus = "";
                            }
                            %>
                            <li><a href="javascript:void(0)"><%=modeStatus%></a></li>
                        </ul>
                    </div><!--/.nav-collapse -->
                </div>
            </nav>
        </div> <!-- /container -->

        <div class="container">
            <div class="row">
                <nav class="col-sm-2">
                    <ul class="nav nav-pills nav-stacked" data-spy="affix" data-offset-top="205">
                        <li><a href="javascript:jumpTo('today.jsp')">Today</a></li>
                        <li><a href="javascript:openWordLookupDialog()">Word Lookup</a></li>
                        <li><a href="javascript:openNotesDialog()">Notes</a></li>
                        <li><a href="javascript:jumpTo('idea_new_batch.jsp')">New Idea</a></li>
                        <li><a href="javascript:jumpTo('task_new.jsp')">New Task</a></li>
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
