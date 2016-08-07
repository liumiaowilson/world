<%
String page_title = "Contact Edit";
%>
<%
Contact contact = null;
int id = -1;
String id_str = request.getParameter("id");
try {
    id = Integer.parseInt(id_str);
}
catch(Exception e) {
}
contact = ContactManager.getInstance().getContact(id);
if(contact == null) {
    response.sendRedirect("contact_list.jsp");
    return;
}
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="import_css_editable_table.jsp" %>
<%@ include file="navbar.jsp" %>
<form id="form" data-toggle="validator" role="form">
    <fieldset class="form-group">
        <label for="id">ID</label>
        <input type="text" class="form-control" id="id" value="<%=contact.id%>" disabled>
    </fieldset>
    <fieldset class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" maxlength="20" placeholder="Enter name" value="<%=contact.name%>" required autofocus>
        <small class="text-muted">Give a nice and distinct name!</small>
    </fieldset>
    <fieldset class="form-group">
        <label for="content">Content</label>
        <textarea class="form-control" id="content" rows="5" maxlength="200" placeholder="Enter detailed description" required><%=contact.content%></textarea>
    </fieldset>
    <div class="form-group">
        <label for="attr_table">Attributes</label>
        <table id="attr_table" class="table table-striped table-bordered">
            <thead>
                <tr>
                    <th style="display:none">ID</th>
                    <th>Name</th>
                    <th>Value</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                Collections.sort(contact.attrs, new Comparator<ContactAttr>(){
                    public int compare(ContactAttr a1, ContactAttr a2) {
                        return a1.name.compareTo(a2.name);
                    }
                });
                for(int i = 0; i < contact.attrs.size(); i++) {
                    ContactAttr attr = contact.attrs.get(i);
                    String attr_value = (String)ContactAttrManager.getInstance().getRealValue(attr);
                %>
                <tr>
                    <td id="id" style="display:none"><%=attr.id%></td>
                    <td id="name"><%=attr.name%></td>
                    <td id="value"><%=attr_value%></td>
                    <td><button type="button" class="btn btn-warning btn-xs del_attr_btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button></td>
                </tr>
                <%
                }
                %>
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
        <button type="submit" class="btn btn-primary ladda-button" data-style="slide-left" id="save_btn"><span class="ladda-label">Save</span></button>
        <button type="button" class="btn btn-default" id="url_back_btn">Back</button>
        <div class="btn-group">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Action <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li><a href="javascript:void(0)" onclick="deleteContact()">Delete</a></li>
            </ul>
        </div>
    </div>
</form>
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
                    if(obj.text() == "0") {
                        var d = new Date();
                        var dateStr = d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate() + " " + d.getHours() + ":" + d.getMinutes();
                        obj.text(dateStr);
                    }
                    obj.editable("destroy");
                    obj.editable({
                        type: 'combodate',
                        template: 'YYYY MMM D HH:mm',
                        format: 'YYYY-MM-DD HH:mm',
                        combodate: {
                            maxYear: new Date().getFullYear(),
                            minYear: <%=ConfigManager.getInstance().getConfigAsInt("combodate.year.min", 1950)%>,
                            smartDays: true,
                            minuteStep: 1
                        }
                    });
                }
                else if("Date" == newType) {
                    if(obj.text() == "0") {
                        var d = new Date();
                        var dateStr = d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate();
                        obj.text(dateStr);
                    }
                    obj.editable("destroy");
                    obj.editable({
                        type: 'combodate',
                        template: 'YYYY MMM D',
                        format: 'YYYY-MM-DD',
                        combodate: {
                            maxYear: new Date().getFullYear(),
                            minYear: <%=ConfigManager.getInstance().getConfigAsInt("combodate.year.min", 1950)%>,
                            smartDays: true
                        }
                    });
                }
                else if("Boolean" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'select',
                        value: obj.text(),
                        source: [
                            { value: 'true', text: 'true' },
                            { value: 'false', text: 'false' },
                        ]
                    });
                }
                else if("Integer" == newType || "Long" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'number',
                    });
                }
                else if("Contact" == newType) {
                    obj.editable("destroy");
                    obj.editable({
                        type: 'select2',
                        value: obj.val(),
                        placeholder: 'Choose Contact',
                        source: contact_source
                    });
                }
                else {
                    obj.editable();
                }
            }

            function configTable() {
                $('#attr_table td[id="name"]').editable({
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
                    $('#attr_table').append('<tr><td id="id" style="display:none">0</td><td id="name" data-type="select">' + i + '</td><td id="value">' + attrs[i] + '</td><td><button type="button" class="btn btn-warning btn-xs del_attr_btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></button></td></tr>');
                }
                configTable();
            }

            function deleteContact() {
                bootbox.confirm("Are you sure to delete this contact?", function(result){
                    if(result) {
                        var id = $('#id').val();
                        $.get(getAPIURL("api/contact/delete?id=" + id), function(data){
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                jumpBack();
                            }
                            else {
                                showDanger(msg);
                            }
                        });
                    }
                });
            }
            $(document).ready(function(){
                $('.combobox').combobox();
                var l = $('#save_btn').ladda();
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
                            var id = $this.find("#id").text();
                            var name = $this.find("#name").text();
                            var value = $this.find("#value").text();
                            if(!name) {
                                validation = "Attribute name should be provided.";
                                return;
                            }
                            if(!value) {
                                validation = "Attribute value should be provided.";
                                return;
                            }
                            attrs.push({'name': name, 'value': value, 'id': id});
                        });
                        if(validation) {
                            showDanger(validation);
                            return;
                        }

                        l.ladda('start');
                        $.post(getAPIURL("api/contact/update"), { id: $('#id').val(), name: $('#name').val(), content: $('#content').val(), attrs: JSON.stringify(attrs)}, function(data) {
                            var status = data.result.status;
                            var msg = data.result.message;
                            if("OK" == status) {
                                showSuccess(msg);
                                l.ladda('stop');
                                jumpBack();
                            }
                            else {
                                showDanger(msg);
                                l.ladda('stop');
                            }
                        }, "json");
                    }
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
