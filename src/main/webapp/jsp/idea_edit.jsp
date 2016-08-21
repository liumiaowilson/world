<%@ page import="org.wilson.world.idea.*" %>
<%
String page_title = "Idea Edit";
%>
<%@ include file="header.jsp" %>
<%
Idea idea = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
idea = IdeaManager.getInstance().getIdea(id);
if(idea == null) {
    if(IdeaIterator.getInstance().isEnabled()) {
        String direction = request.getParameter("dir");
        if("prev".equals(direction)) {
            idea = IdeaIterator.getInstance().previous();
        }
        else if("next".equals(direction)) {
            idea = IdeaIterator.getInstance().next();
        }
        else {
            response.sendRedirect("idea_list.jsp");
            return;
        }
    }
    else {
        response.sendRedirect("idea_list.jsp");
        return;
    }
}
if(IdeaIterator.getInstance().isEnabled()) {
    IdeaIterator.getInstance().setIdeaId(id);
}
boolean marked = MarkManager.getInstance().isMarked("idea", String.valueOf(idea.id));
boolean frozen = IdeaManager.getInstance().isFrozen(idea);
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=idea.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=idea.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=idea.content%></textarea>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-default" id="left_btn">
            <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
        </button>
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li class="dropdown-submenu">
                    <a href="javascript:void(0)">To</a>
                    <ul class="dropdown-menu">
                        <%
                        IdeaConverter taskConverter = IdeaConverterFactory.getInstance().getIdeaConverterByName("Task");
                        if(taskConverter != null) {
                        %>
                        <li class="<%=frozen ? "disabled" : ""%>"><a href="javascript:void(0)" onclick="<%=frozen ? "" : "convertIdea('" + taskConverter.getName() + "')"%>"><%=taskConverter.getName()%></a></li>
                        <li role="separator" class="divider"></li>
                        <%
                        }
                        %>
                        <%
                        List<IdeaConverter> converters = IdeaConverterFactory.getInstance().getIdeaConverters();
                        Collections.sort(converters, new Comparator<IdeaConverter>(){
                            public int compare(IdeaConverter c1, IdeaConverter c2) {
                                return c1.getName().compareTo(c2.getName());
                            }
                        });
                        for(IdeaConverter converter : converters) {
                            if(converter == taskConverter) {
                                continue;
                            }
                        %>
                        <li class="<%=frozen ? "disabled" : ""%>"><a href="javascript:void(0)" onclick="<%=frozen ? "" : "convertIdea('" + converter.getName() + "')"%>"><%=converter.getName()%></a></li>
                        <%
                        }
                        %>
                    </ul>
                </li>
                <li role="separator" class="divider"></li>
                <%
                if(marked) {
                %>
                <li class="<%=frozen ? "disabled" : ""%>"><a href="javascript:void(0)" onclick="<%=frozen ? "" : "unmarkIdea()"%>">Unmark</a></li>
                <%
                }
                else {
                %>
                <li class="<%=frozen ? "disabled" : ""%>"><a href="javascript:void(0)" onclick="<%=frozen ? "" : "markIdea()"%>">Mark</a></li>
                <%
                }
                %>
                <li role="separator" class="divider"></li>
                <li class="<%=frozen ? "disabled" : ""%>"><a href="javascript:void(0)" onclick="<%=frozen ? "" : "splitIdea()"%>">Split</a></li>
                <%
                boolean hasMarked = MarkManager.getInstance().hasMarked("idea");
                String disabled = (hasMarked && !frozen ? "" : "disabled");
                String mergeIdeaStr = (hasMarked && !frozen ? "mergeIdea()" : "");
                %>
                <li class="<%=disabled%>"><a href="javascript:void(0)" onclick="<%=mergeIdeaStr%>">Merge</a></li>
                <li role="separator" class="divider"></li>
                <li class="<%=frozen ? "disabled" : ""%>"><a href="javascript:void(0)" onclick="<%=frozen ? "" : "deleteIdea()"%>">Delete</a></li>
            </ul>
        </div>
        <button type="button" class="btn btn-default" id="right_btn">
            <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
        </button>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function convertIdea(type) {
                var id = $('#id').val();
                $.get(getAPIURL("api/idea/convert?id=" + id + "&type=" + type), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        var path = data.result.data.$;
                        jumpTo(path);
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }
            function splitIdea() {
                var id = $('#id').val();
                jumpTo("idea_split.jsp?id=" + id);
            }
            function mergeIdea() {
                var id = $('#id').val();
                jumpTo("idea_merge.jsp?id=" + id);
            }
            function markIdea() {
                var id = $('#id').val();
                $.get(getAPIURL("api/item/mark?type=idea&id=" + id), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        jumpBack();
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }
            function unmarkIdea() {
                var id = $('#id').val();
                $.get(getAPIURL("api/item/unmark?type=idea&id=" + id), function(data){
                    var status = data.result.status;
                    var msg = data.result.message;
                    if("OK" == status) {
                        showSuccess(msg);
                        jumpBack();
                    }
                    else {
                        showDanger(msg);
                    }
                });
            }
            function deleteIdea() {
                bootbox.confirm("Are you sure to delete this idea?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/idea/delete?id=" + id), function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                jumpBack();
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            }
            $(document).ready(function(){
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var content = $('#content').val();
                        if(!content) {
                            content = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/idea/update"), { id: $('#id').val(), name: $('#name').val(), content: $('#content').val()}, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                l.ladda('stop');
                                jumpBack();
                            }
                            else {
                                showDanger(msg);
                                l.ladda('stop');
                            }
                        }, "json");
                    }
                });

                $('#left_btn').click(function(){
                    $.get(getAPIURL("api/idea/prev?id=<%=idea.id%>"), function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                            var prev = parseInt(data.result.data.$);
                            if(prev < 0) {
                                showDanger("Previous idea does not exist.");
                            }
                            else {
                                jumpTo("idea_edit.jsp?id=" + prev);
                            }
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });

                $('#right_btn').click(function(){
                    $.get(getAPIURL("api/idea/next?id=<%=idea.id%>"), function(data){
                        var status = data.result.status;
                        var msg = data.result.message;
                        if("OK" == status) {
                            showSuccess(msg);
                            var next = parseInt(data.result.data.$);
                            if(next < 0) {
                                showDanger("Next idea does not exist.");
                            }
                            else {
                                jumpTo("idea_edit.jsp?id=" + next);
                            }
                        }
                        else {
                            showDanger(msg);
                        }
                    });
                });
            });
</script>
<%@ include file="footer.jsp" %>
