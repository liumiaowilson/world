</div>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="js/ie10-viewport-bug-workaround.js"></script>
<script src="<%=cm.getConfig("js.jquery.url", "js/jquery-2.2.4.min.js")%>"></script>
<script src="<%=cm.getConfig("js.bootstrap.url", "js/bootstrap.min.js")%>"></script>
<script src="js/validator.min.js"></script>
<script src="js/spin.min.js"></script>
<script src="js/ladda.min.js"></script>
<script src="js/ladda.jquery.min.js"></script>
<script src="js/bootbox.min.js"></script>
<script>
        $.ajaxSetup({
            beforeSend: function(xhr) {
                $('#alert_success').hide();
                $('#alert_info').hide();
                $('#alert_warning').hide();
                $('#alert_danger').hide();
            }
        });

        function resetAlerts() {
            $('#alert_success').hide();
            $('#alert_info').hide();
            $('#alert_warning').hide();
            $('#alert_danger').hide();
        }

        function showSuccess(msg) {
            $('#alert_success').text(msg);

            $('#alert_success').show();
            $('#alert_info').hide();
            $('#alert_warning').hide();
            $('#alert_danger').hide();
        }

        function showInfo(msg) {
            $('#alert_info').text(msg);

            $('#alert_success').hide();
            $('#alert_info').show();
            $('#alert_warning').hide();
            $('#alert_danger').hide();
        }

        function showWarning(msg) {
            $('#alert_warning').text(msg);

            $('#alert_success').hide();
            $('#alert_info').hide();
            $('#alert_warning').show();
            $('#alert_danger').hide();
        }

        function showDanger(msg) {
            $('#alert_danger').text(msg);

            $('#alert_success').hide();
            $('#alert_info').hide();
            $('#alert_warning').hide();
            $('#alert_danger').show();
        }
</script>
