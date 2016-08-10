<%
String page_title = "Demo";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Demo</h3>
    </div>
    <div class="panel-body">
        <div id="manga" class="carousel slide" data-ride="carousel">
            <!-- Indicators -->
            <ol class="carousel-indicators">
                <li data-target="#manga" data-slide-to="0" class="active"></li>
                <li data-target="#manga" data-slide-to="1"></li>
            </ol>

            <!-- Wrapper for slides -->
            <div class="carousel-inner" role="listbox">
                <div class="item active">
                    <img src="<%=basePath%>/servlet/image?path=manga/1.jpg" alt="1.jpg">
                </div>
                <div class="item">
                    <img src="<%=basePath%>/servlet/image?path=manga/2.jpg" alt="2.jpg">
                </div>
            </div>

            <!-- Left and right controls -->
            <a class="left carousel-control" href="#manga" role="button" data-slide="prev">
                <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                <span class="sr-only">Previous</span>
            </a>
            <a class="right carousel-control" href="#manga" role="button" data-slide="next">
                <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
                <span class="sr-only">Next</span>
            </a>
        </div>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_highcharts.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
