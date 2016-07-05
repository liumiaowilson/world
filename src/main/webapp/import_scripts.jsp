        </div>
        <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
        <script src="js/ie10-viewport-bug-workaround.js"></script>
        <script src="js/jquery-2.2.4.min.js"></script>
        <script src="js/bootstrap.min.js"></script>
        <script src="js/validator.min.js"></script>
        <script src="js/jquery.dataTables.min.js"></script>
        <script src="js/dataTables.bootstrap.min.js"></script>
        <script src="js/dataTables.buttons.min.js"></script>
        <script src="js/buttons.bootstrap.min.js"></script>
        <script src="js/spin.min.js"></script>
        <script src="js/ladda.min.js"></script>
        <script src="js/ladda.jquery.min.js"></script>
        <script src="js/bootbox.min.js"></script>
        <script src="js/highcharts.js"></script>

        <script>
                $.ajaxSetup({
                    beforeSend: function(xhr) {
                        $('#alert_success').hide();
                        $('#alert_info').hide();
                        $('#alert_warning').hide();
                        $('#alert_danger').hide();
                    }
                });
        </script>
