<%
String from_url = "idea_new_batch.jsp";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="5" maxlength="200" placeholde="Enter detailed description"></textarea>
    </fieldset>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="view_all_btn">Back</button>
        <button type="button" class="btn btn-default" id="left_btn">
            <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
        </button>
        <button type="button" class="btn btn-default" id="plus_btn">
            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
        </button>
        <button type="button" class="btn btn-default" id="ok_btn">
            <span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
        </button>
        <button type="button" class="btn btn-default" id="cancel_btn">
            <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
        </button>
        <button type="button" class="btn btn-default" id="minus_btn">
            <span class="glyphicon glyphicon-minus" aria-hidden="true"></span>
        </button>
        <button type="button" class="btn btn-default" id="right_btn">
            <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
        </button>
    </div>
</form>
<input type="hidden" id="create_new" value="false"/>
<%@ include file="import_script.jsp" %>
<script>
            $(document).ready(function(){
                var l = $('#save_btn').ladda();
                var list = [];
                var index = list.length;

                function syncButtons() {
                    //left button
                    if(index <= 0 || index >= list.length) {
                        $('#left_btn').addClass('disabled');
                    }
                    else {
                        $('#left_btn').removeClass('disabled');
                    }

                    //right button
                    if(index < 0 || index >= list.length - 1) {
                        $('#right_btn').addClass('disabled');
                    }
                    else {
                        $('#right_btn').removeClass('disabled');
                    }

                    //plus button
                    if(index != list.length - 1) {
                        $('#plus_btn').addClass('disabled');
                    }
                    else {
                        $('#plus_btn').removeClass('disabled');
                    }

                    //minus button
                    if(index != list.length - 1) {
                        $('#minus_btn').addClass('disabled');
                    }
                    else {
                        $('#minus_btn').removeClass('disabled');
                    }

                    //cancel button
                    if(index != list.length) {
                        $('#cancel_btn').addClass('disabled');
                    }
                    else {
                        $('#cancel_btn').removeClass('disabled');
                    }
                }

                syncButtons();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var name = $('#name').val();
                        var content = $('#content').val();
                        if(!content) {
                            content = name;
                        }

                        var found = false;
                        for(var i in list) {
                            if(list[i].name == name && list[i].content == content) {
                                found = true;
                                break;
                            }
                        }
                        if(!found) {
                            list.push({'name': name, 'content': content});
                        }

                        l.ladda('start');
                        $.post("api/idea/batch_create", { 'ideas': JSON.stringify(list) }, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                l.ladda('stop');
                                window.location.href = "idea_list.jsp";
                            }
                            else {
                                showDanger(msg);
                                l.ladda('stop');
                            }
                        }, "json");
                    }
                });

                $('#view_all_btn').click(function(){
                    window.location.href = "idea_list.jsp";
                });

                $('#save_btn').click(function(){
                    $('#form').submit();
                });

                $('#plus_btn').click(function(){
                    resetAlerts();
                    if(index == list.length - 1) {
                        index = index + 1;
                        $('#name').val("");
                        $('#content').val("");
                    }

                    syncButtons();
                });

                $('#ok_btn').click(function(){
                    resetAlerts();
                    var name = $('#name').val();
                    if(!name) {
                        showDanger("Name should be provided.");
                        return;
                    }
                    var content = $('#content').val();
                    if(!content) {
                        content = name;
                    }
                    var idea = { 'name': name, 'content': content };
                    if(index == list.length) {
                        list.push(idea);
                        showSuccess("Idea has been added to this area.");
                    }
                    else {
                        list[index] = idea;
                        showSuccess("Idea has been updated in this area.");
                    }

                    syncButtons();
                });

                $('#cancel_btn').click(function(){
                    resetAlerts();
                    if(index == list.length) {
                        index = index - 1;
                        if(index >= 0) {
                            $('#name').val(list[index].name);
                            $('#content').val(list[index].content);
                        }
                        else {
                            index = 0;
                            $('#name').val("");
                            $('#content').val("");
                        }
                    }

                    syncButtons();
                });

                $('#left_btn').click(function(){
                    resetAlerts();
                    index = index - 1;
                    if(index < 0) {
                        index = 0;
                    }
                    $('#name').val(list[index].name);
                    $('#content').val(list[index].content);

                    syncButtons();
                });

                $('#right_btn').click(function(){
                    resetAlerts();
                    index = index + 1;
                    if(index >= list.length) {
                        index = list.length - 1;
                    }
                    $('#name').val(list[index].name);
                    $('#content').val(list[index].content);

                    syncButtons();
                });

                $('#minus_btn').click(function(){
                    resetAlerts();
                    if(index == list.length - 1) {
                        list.splice(index, 1);
                        index = index - 1;
                        if(index >= 0) {
                            $('#name').val(list[index].name);
                            $('#content').val(list[index].content);
                        }
                        else {
                            index = 0;
                            $('#name').val("");
                            $('#content').val("");
                        }
                        showSuccess("Idea has been deleted from this area.");
                    }

                    syncButtons();
                });
            });
</script>
<%@ include file="footer.jsp" %>
