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
<script src="js/bootstrap-combobox.js"></script>
<script src="js/bootstrap-notify.min.js"></script>
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

        function showSuccess(msg, html) {
            if(html) {
                $('#alert_success').html(msg);
            }
            else {
                $('#alert_success').text(msg);
            }


            $('#alert_success').show();
            $('#alert_info').hide();
            $('#alert_warning').hide();
            $('#alert_danger').hide();
        }

        function showInfo(msg, html) {
            if(html) {
                $('#alert_info').html(msg);
            }
            else {
                $('#alert_info').text(msg);
            }


            $('#alert_success').hide();
            $('#alert_info').show();
            $('#alert_warning').hide();
            $('#alert_danger').hide();
        }

        function showWarning(msg, html) {
            if(html) {
                $('#alert_warning').html(msg);
            }
            else {
                $('#alert_warning').text(msg);
            }

            $('#alert_success').hide();
            $('#alert_info').hide();
            $('#alert_warning').show();
            $('#alert_danger').hide();
        }

        function showDanger(msg, html) {
            if(html) {
                $('#alert_danger').html(msg);
            }
            else {
                $('#alert_danger').text(msg);
            }


            $('#alert_success').hide();
            $('#alert_info').hide();
            $('#alert_warning').hide();
            $('#alert_danger').show();
        }

        function notifySuccess(msg) {
            notifyMessage(msg, 'success');
        }

        function notifyInfo(msg) {
            notifyMessage(msg, 'info');
        }

        function notifyWarning(msg) {
            notifyMessage(msg, 'warning');
        }

        function notifyDanger(msg) {
            notifyMessage(msg, 'danger');
        }

        function notifyMessage(msg, type) {
            $.notify({
                message: msg,
            },{
                type: type,
                allow_dismiss: true,
                newest_on_top: true,
                placement: {
                    from: "top",
                    align: "right"
                },
                animate: {
                    enter: 'animated fadeInDown',
                    exit: 'animated fadeOutUp'
                },
                offset: 20,
                spacing: 10,
                z_index: 1031,
                delay: 5000,
                timer: 1000,
            });
        }

        $(document).ready(function(){
            var msg = $('#notify_success').val();
            if(msg) {
                notifySuccess(msg);
            }

            msg = $('#notify_info').val();
            if(msg) {
                notifyInfo(msg);
            }

            msg = $('#notify_warning').val();
            if(msg) {
                notifyWarning(msg);
            }

            msg = $('#notify_danger').val();
            if(msg) {
                notifyDanger(msg);
            }
        });
</script>
