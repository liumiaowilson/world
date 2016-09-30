<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
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

        <title>Contact</title>
    </head>

    <body>
        <%=ContactManager.getInstance().getLastUpdatedContactDisplay()%>
        <form action="<%=basePath%>/api/contact/update_public" method="post">
            <table>
                <tr>
                    <td>Contact</td>
                    <td>
                        <select name="id">
                            <option></option>
                            <%
                            List<Contact> contacts = ContactManager.getInstance().getContacts();
                            Collections.sort(contacts, new Comparator<Contact>(){
                                public int compare(Contact c1, Contact c2) {
                                    return c1.name.compareTo(c2.name);
                                }
                            });
                            for(Contact contact : contacts) {
                            %>
                            <option value="<%=contact.id%>"><%=contact.name%></option>
                            <%
                            }
                            %>
                        </select>
                    </td>
                </tr>
            </table>
            <textarea name="content" style="width: 100%" rows=5></textarea>
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="Save"/>
        </form>
    </body>
</html>
