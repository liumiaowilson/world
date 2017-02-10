<%@ page contentType="text/html; charset=UTF-8"%>
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
        <meta http-equiv="Content-Type" content="text/html charset=UTF-8">

        <title>Debug</title>
    </head>

    <body>
        <h3>Request Parameters</h3>
        <table>
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
                <%
                Map<String, String[]> parameters = request.getParameterMap();
                for(Map.Entry<String, String[]> entry : parameters.entrySet()) {
                    String name = entry.getKey();
                    String [] values = entry.getValue();
                    StringBuilder sb = new StringBuilder();
                    for(int i = 0; i < values.length; i++) {
                        sb.append(values[i]);
                        if(i != values.length - 1) {
                            sb.append(",");
                        }
                    }
                %>
                <tr>
                    <td><%=name%></td>
                    <td><%=sb.toString()%></td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
        <hr/>
    </body>
</html>
