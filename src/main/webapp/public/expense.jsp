<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="org.wilson.world.expense.*" %>
<%@ page import="java.util.*" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Expense</title>
    </head>

    <body>
        <%
        ExpenseItem item = ExpenseItemManager.getInstance().getLastCreatedExpenseItem();
        String last = null;
        if(item == null) {
            last = "Last item not found.";
        }
        else {
            last = item.name + "[" + item.type + "]:" + item.amount;
        }
        %>
        <%=last%>
        <form action="<%=basePath%>/api/expense_item/create_public" method="post">
            <table>
                <tr>
                    <td>Name</td>
                    <td><input type="text" name="name" id="name"/></td>
                </tr>
                <tr>
                    <td>Type</td>
                    <td>
                        <select name="type" id="type">
                        <option></option>
                        <%
                        List<String> expenseTypes = ExpenseItemManager.getInstance().getTypes();
                        for(String expenseType : expenseTypes) {
                        %>
                        <option value="<%=expenseType%>"><%=expenseType%></option>
                        <%
                        }
                        %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Amount</td>
                    <td>
                        <input type="number" name="amount" id="amount"/>
                    </td>
                </tr>
                <tr>
                    <td>Key</td>
                    <td>
                        <input type="password" name="key"/>
                    </td>
                </tr>
            </table>
            <br/>
            <input type="submit" value="Save"/>
            <hr/>
            <%
            List<ExpenseRecord> records = ExpenseItemManager.getInstance().getTopExpenseRecords(5);
            for(ExpenseRecord record : records) {
            %>
            <a href="javascript:fillValues('<%=record.name%>', '<%=record.type%>', <%=record.amount%>)"><%=record.name%>/<%=record.type%>/<%=record.amount%></a><br/>
            <%
            }
            %>
        </form>
        <script>
                                 function fillValues(name, type, amount) {
                                    document.getElementById("name").value = name;
                                    document.getElementById("type").value = type;
                                    document.getElementById("amount").value = amount;
                                 }
        </script>
    </body>
</html>
