<%
String page_title = "Contact New";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_editable_table.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="5" maxlength="200" placeholder="Enter detailed description"></textarea>
    </fieldset>
    <div class="form-group">
        <label for="attr_table">Attributes</label>
        <table id="attr_table" class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Value</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
        <button type="button" class="btn btn-default" id="add_btn">
            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
        </button>
        <button type="button" class="btn btn-default" id="delete_btn">
            <span class="glyphicon glyphicon-minus" aria-hidden="true"></span>
        </button>
    </div>
    <div class="form-group">
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-primary ladda-button" data-style="slide-left" id="save_new_btn"><span class="ladda-label">Save And New</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
    </div>
</form>
<input type="hidden" id="create_new" value="false"/>
<%@ include file="import_script.jsp" %>
<%@ include file="import_script_editable_table.jsp" %>
<script>
            var attr_defs = {
            <%
            List<String> contactAttrDefNames = ContactAttrDefManager.getInstance().getContactAttrNames();
            Collections.sort(contactAttrDefNames);
            for(String contactAttrDefName : contactAttrDefNames) {
                String type = ContactAttrDefManager.getInstance().getContactAttrType(contactAttrDefName);
            %>
                '<%=contactAttrDefName%>': '<%=type%>',
            <%
            }
            %>
            };
            var contacts = {
            <%
            List<Contact> contacts = ContactManager.getInstance().getContacts();
            Collections.sort(contacts, new Comparator<Contact>(){
                public int compare(Contact t1, Contact t2) {
                    return t1.name.compareTo(t2.name);
                }
            });
            for(Contact t : contacts) {
            %>
                '<%=t.id%>': '<%=t.name%>',
            <%
            }
            %>
            };
            var attr_name_source = [];
            for(var i in attr_defs) {
                attr_name_source.push({value: i, text: i});
            }
            var contact_source = [];
            for(var i in contacts) {
                contact_source.push({id: i, text: contacts[i]});
            }

            function setEditor(obj, newValue) {
                var newType = attr_defs[newValue];
                if("DateTime" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'combodate',
                        template: 'YYYY MMM D HH:mm',
                        format: 'YYYY-MM-DD HH:mm',
                        combodate: {
                            maxYear: new Date().getFullYear(),
                            smartDays: true,
                            minuteStep: 1
                        }
                    });
                }
                else if("Date" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'combodate',
                        template: 'YYYY MMM D',
                        format: 'YYYY-MM-DD',
                        combodate: {
                            maxYear: new Date().getFullYear(),
                            smartDays: true
                        }
                    });
                }
                else if("Boolean" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'select',
                        source: [
                            { value: 'true', text: 'true' },
                            { value: 'false', text: 'false' },
                        ]
                    });
                }
                else if("Integer" == newType || "Long" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'text',
                    });
                }
                else if("Contact" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'select2',
                        placeholder: 'Choose Contact',
                        source: contact_source
                    });
                }
                else {
                    obj.editable();
                }
            }

            function configTable() {
                var obj = $('#attr_table td[id="name"]');
                obj.editable({
                    type: 'select',
                    source: attr_name_source,
                    success: function(response, newValue) {
                        var obj = $(this).closest('tr').find('td#value');
                        setEditor(obj, newValue);
                    }
                });
                $('#attr_table tbody tr').each(function(){
                    var name = $(this).find('#name').text();
                    setEditor($(this).find('#value'), name);
                });

                $('.del_attr_btn').click(function(){
                    $(this).closest("tr").remove();
                });
            }

            function replaceRows(attrs) {
                $('#attr_table tbody tr').remove();
                addRows(attrs);
            }

            function addRows(attrs) {
                for(var i in attrs) {
                    $('#attr_table').append('<tr><td id="name" data-type="select">' + i + '</td><td id="value">' + attrs[i] + '</td><td><button type="button" class="btn btn-warning btn-xs del_attr_btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button></td></tr>');
                }
                configTable();
            }

            $(document).ready(function(){
                $('.combobox').combobox();
                var l = $('#save_btn').ladda();
                var ln = $('#save_new_btn').ladda();
                $.fn.editable.defaults.mode = 'inline';
                configTable();

                $('#form').validator().on('submit', function (e) {
                    if (e.isDefaultPrevented()) {
                        // handle the invalid form...
                    } else {
                        e.preventDefault();
                        var content = $('#content').val();
                        if(!content) {
                            content = $('#name').val();
                        }

                        var attrs = [];
                        var validation = "";
                        $('#attr_table tbody tr').each(function(){
                            $this = $(this);
                            var name = $this.find("#name").text();
                            var value = $this.find("#value").text();
                            if(!name) {
                                validation = "Attribute name should be provided.";
                                return;
                            }
                            if(name.length > 20) {
                                validation = "Attribute name length should be less than 20.";
                                return;
                            }
                            if(!value) {
                                validation = "Attribute value should be provided.";
                                return;
                            }
                            if(value.length > 200) {
                                validation = "Attribute value length should be less than 200.";
                                return;
                            }
                            attrs.push({'name': name, 'value': value});
                        });
                        if(validation) {
                            showDanger(validation);
                            return;
                        }

                        var flag = $('#create_new').val();
                        if("true" == flag) {
                            ln.ladda('start');
                        }
                        else if("false" == flag) {
                            l.ladda('start');
                        }
                        $.post(getAPIURL("api/contact/create"), { name: $('#name').val(), 'content': content, 'attrs': JSON.stringify(attrs)}, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                if("true" == flag) {
                                    ln.ladda('stop');
                                    jumpCurrent();
                                }
                                else if("false" == flag) {
                                    l.ladda('stop');
                                    jumpBack();
                                }
                            }
                            else {
                                showDanger(msg);
                                if("true" == flag) {
                                    ln.ladda('stop');
                                }
                                else if("false" == flag) {
                                    l.ladda('stop');
                                }
                            }
                        }, "json");
                    }
                });

                $('#save_btn').click(function(){
                    $('#create_new').val("false");
                    $('#form').submit();
                });

                $('#save_new_btn').click(function(){
                    $('#create_new').val("true");
                    $('#form').submit();
                });

                $('#add_btn').click(function(){
                    addRows({ 'attr name': '0' })
                });

                $('#delete_btn').click(function(){
                    $('#attr_table tbody tr:last').remove();
                });
            });
</script>
<%@ include file="footer.jsp" %>
