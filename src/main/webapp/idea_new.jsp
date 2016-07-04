<%
String from_url = "idea_new.jsp";
%>
<%@ include file="header.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" placeholder="Enter name" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="5" placeholde="Enter detailed description" required></textarea>
    </fieldset>
    <div class="alert alert-danger" role="alert" id="error">
    </div>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="view_all_btn">View All</button>
    </div>
</form>
<%@ include file="import_scripts.jsp" %>
<script>
            $(document).ready(function(){
                $('#error').hide();
                var l = $('#save_btn').ladda();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        $('#error').hide();
                        l.ladda('start');
                        $.post("api/idea/create", { name: $('#name').val(), content: $('#content').val()}, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                $('#alert_success').text(msg);
                                $('#alert_success').show();
                                l.ladda('stop');
                            }
                            else {
                                $('#alert_danger').text(msg);
                                $('#alert_danger').show();
                                l.ladda('stop');
                            }
                        }, "json");
                    }
                });

                $('#view_all_btn').click(function(){
                    window.location.href = "idea_list.jsp";
                });
            });
</script>
<%@ include file="footer.jsp" %>
