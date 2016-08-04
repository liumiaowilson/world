<%
String page_title = "Account Edit";
%>
<%@ include file="header.jsp" %>
<%
Account account = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
account = AccountManager.getInstance().getAccount(id);
if(account == null) {
    response.sendRedirect("account_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=account.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=account.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="identifier">Identifier</label>
        <input type="text" class="form-control" id="identifier" maxlength="50" placeholder="Enter identifier" value="<%=account.identifier%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=account.description%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="amount">Amount</label>
        <input type="number" class="form-control" id="amount" placeholder="Enter amount" value="<%=account.amount%>" required>
    </fieldset>
    <fieldset class="form-group">
        <label for="updatedTime">Last updated time</label>
        <%
        TimeZone tz = (TimeZone)request.getSession().getAttribute("world-timezone");
        String updatedTime = AccountManager.getInstance().getUpdatedTime(tz);
        %>
        <input type="text" class="form-control" id="updatedTime" maxlength="20" value="<%=updatedTime%>" disabled>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteAccount()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteAccount() {
                bootbox.confirm("Are you sure to delete this account?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/account/delete?id=" + id), function(data){
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
                        var description = $('#description').val();
                        if(!description) {
                            description = $('#name').val();
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/account/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), 'identifier': $('#identifier').val(), 'amount': $('#amount').val()}, function(data) {
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
            });
</script>
<%@ include file="footer.jsp" %>
