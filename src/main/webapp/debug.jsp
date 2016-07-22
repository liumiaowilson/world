<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Http Debug</title>
    </head>

    <body>
        <table>
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <%
                Map<String, String[]> map = request.getParameterMap();
                List<String> names = new ArrayList<String>(map.keySet());
                Collections.sort(names);
                for(String name : names) {
                    String [] values = map.get(name);
                %>
                <tr>
                    <td><%=name%></td>
                    <td><%=Arrays.asList(values).toString()%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </body>
</html>
