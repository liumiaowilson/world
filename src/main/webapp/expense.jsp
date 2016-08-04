<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="java.util.*" %>
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
        <form action="api/expense_item/create_public" method="post">
            Key: <input type="password" name="key"/><br/>
            Name: <input type="text" name="name"/><br/>
            Type: <select name="type">
                <option></option>
                <%
                List<String> expenseTypes = ExpenseItemManager.getInstance().getTypes();
                for(String expenseType : expenseTypes) {
                %>
                <option value="<%=expenseType%>"><%=expenseType%></option>
                <%
                }
                %>
            </select><br/>
            Amount: <input type="number" name="amount"/><br/>
            <br/>
            <input type="submit" value="Save"/>
        </form>
    </body>
</html>
