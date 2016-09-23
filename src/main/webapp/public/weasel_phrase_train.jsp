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

        <title>Weasel Phrase Train</title>
    </head>

    <body>
        <%
        WeaselPhrase phrase = WeaselPhraseManager.getInstance().randomWeaselPhrase();
        if(phrase != null) {
            String hint = "";
            Emotion emotion = EmotionManager.getInstance().randomEmotion();
            if(emotion != null) {
                hint = emotion.name;
            }
        %>
        <p><b><%=phrase.pattern%></b></p>
        <p><i><%=phrase.usage%></i></p>
        <hr/>
        <span style="background-color: #5bc0de"><%=hint%></span>
        <hr/>
        <%
        } else {
        %>
        No weasel phrase could be found.
        <%
        }
        %>
        <form action="<%=basePath%>/api/weasel_phrase/train_public" method="post">
            <textarea name="examples" style="width: 100%" rows=5></textarea>
            Key: <input type="password" name="key"/><br/>
            <br/>
            <input type="submit" value="Save"/>
        </form>
    </body>
</html>
