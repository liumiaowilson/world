<%@ page import="java.lang.management.*" %>
<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Dropdown button</h3>
        </div>
        <div class="panel-body">
        </div>
    </div>
    <div class="btn-group">
        <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            Action <span class="caret"></span>
        </button>
        <ul class="dropdown-menu">
            <li><a href="javascript:void(0)">Try</a></li>
            <li class="dropdown-submenu">
                <a href="javascript:void(0)">View</a>
                <ul class="dropdown-menu">
                    <li><a href="javascript:jumpTo('idea_stats.jsp')">Statistics</a></li>
                </ul>
            </li>
        </ul>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
