<%@ page import="org.wilson.world.food.*" %>
<%
String page_title = "Food View";
%>
<%@ include file="header.jsp" %>
<%
FoodInfo food_info = FoodManager.getInstance().randomFoodInfo();
%>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Food View</h3>
    </div>
    <div class="panel-body">
        <%
        if(food_info != null) {
        %>
        <div id="food_info">
            <div class="embed-responsive embed-responsive-16by9">
                <iframe class="embed-responsive-item" src="<%=food_info.url%>"></iframe>
            </div>
        </div>
        <%
        }
        else {
        %>
        <div class="alert alert-danger" role="alert">
            No food info could be found.
        </div>
        <%
        }
        %>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<script>
</script>
<%@ include file="footer.jsp" %>
