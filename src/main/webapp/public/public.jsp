<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="org.wilson.world.manager.*" %>
<%@ page import="org.wilson.world.model.*" %>
<%@ page import="org.wilson.world.pagelet.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta http-equiv="Content-Type" content="text/html charset=UTF-8">

        <title>Public Pages</title>
    </head>

    <body>
        <a href="./post.jsp">Post Idea</a><br/>
        <a href="./post_task.jsp">Post Task</a><br/>
        <a href="./notes.jsp">Notes</a><br/>
        <a href="./check.jsp">Check</a><br/>
        <a href="./finish_task.jsp">Outdoor Task</a><br/>
        <hr/>
        <a href="./expense.jsp">Expense</a><br/>
        <a href="./behavior.jsp">Behavior</a><br/>
        <a href="./start_sleep.jsp">Start Sleep</a><br/>
        <a href="./end_sleep.jsp">End Sleep</a><br/>
        <a href="./quest.jsp">Quest</a><br/>
        <hr/>
        <a href="./quiz_list.jsp">Quiz List</a><br/>
        <a href="./weasel_phrase_train.jsp">Weasel Phrase Train</a><br/>
        <a href="./journal.jsp">Journal</a><br/>
        <a href="./emotion.jsp">Emotion</a><br/>
        <a href="./personality.jsp">Personality</a><br/>
        <a href="./view_joke.jsp">Joke</a><br/>
        <a href="./fraud.jsp">Fraud</a><br/>
        <a href="./contact.jsp">Contact</a><br/>
        <hr/>
        <a href="./list_festival.jsp">Festivals</a><br/>
        <a href="./list_idea.jsp">Ideas</a><br/>
        <a href="./list_task.jsp">Tasks</a><br/>
        <a href="./view_artifact.jsp">View Artifact</a><br/>
        <a href="./view_fraud.jsp">View Fraud</a><br/>
        <a href="./view_novel.jsp">View Novel</a><br/>
        <a href="./view_parn.jsp">View Parn</a><br/>
        <a href="./alert.jsp">Alert</a><br/>
        <a href="./cache.jsp">Cache</a><br/>
        <a href="./view_novel_document.jsp">View Novel Document</a><br/>
        <a href="./view_manga.jsp">View Manga</a><br/>
        <a href="./view_image_list.jsp">View Image List</a><br/>
        <hr/>
        <a href="<%=ProxyManager.getInstance().getWebProxyUrl()%>">Web Proxy</a><br/>
        <hr/>
        <%
        List<Pagelet> pagelets = PageletManager.getInstance().getPagelets(PageletType.Public);
        String blacklistStr = ConfigManager.getInstance().getConfig("pagelet.public.blacklist", "");
        List<String> blacklist = new ArrayList<String>();
        for(String item : blacklistStr.split(",")) {
            blacklist.add(item.trim());
        }
        for(Pagelet pagelet : pagelets) {
            if(!blacklist.contains(pagelet.name)) {
        %>
        <a href="./page.jsp?id=<%=pagelet.id%>"><%=pagelet.title%></a><br/>
        <%
            }
        }
        %>
        <hr/>
    </body>
    <script>
    </script>
</html>
