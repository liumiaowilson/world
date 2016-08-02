<%
String page_title = "Expense Item Edit";
%>
<%@ include file="header.jsp" %>
<%
ExpenseItem expense_item = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
expense_item = ExpenseItemManager.getInstance().getExpenseItem(id);
if(expense_item == null) {
    response.sendRedirect("expense_item_list.jsp");
    return;
}
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=expense_item.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=expense_item.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="type">Type</label>
        <select class="combobox form-control" id="type" required>
            <option></option>
            <%
            List<String> expenseTypes = ExpenseItemManager.getInstance().getTypes();
            for(String expenseType : expenseTypes) {
                String selectedStr = expenseType.equals(expense_item.type) ? "selected" : "";
            %>
            <option value="<%=expenseType%>" <%=selectedStr%>><%=expenseType%></option>
            <%
            }
            %>
        </select>
    </fieldset>
    <fieldset class="form-group">
        <label for="description">Description</label>
        <textarea class="form-control" id="description" rows="5" maxlength="200" placeholder="Enter detailed description"><%=expense_item.description%></textarea>
    </fieldset>
    <fieldset class="form-group">
        <label for="amount">Amount</label>
        <input type="number" class="form-control" id="amount" maxlength="20" placeholder="Enter amount" value="<%=expense_item.amount%>" required>
    </fieldset>
    <div class="form-group">
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteExpenseItem()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
<%@ include file="import_script.jsp" %>
<script>
            function deleteExpenseItem() {
                bootbox.confirm("Are you sure to delete this expense item?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/expense_item/delete?id=" + id), function(data){
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
                $('.combobox').combobox();
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
                        $.post(getAPIURL("api/expense_item/update"), { id: $('#id').val(), name: $('#name').val(), description: $('#description').val(), 'type': $('#type').val(), 'amount': $('#amount').val()}, function(data) {
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
