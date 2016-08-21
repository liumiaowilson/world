<%
String page_title = "FAQ";
%>
<%@ include file="header.jsp" %>
<%@ include file="import_css.jsp" %>
<%@ include file="navbar.jsp" %>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">FAQ</h3>
    </div>
    <div class="panel-body">
        <div class="alert alert-warning" role="alert">
            <strong>After a long idle time, "communications link failure" exceptions are thrown when doing database-related operations.</strong><br/>
            This is caused by MySQL wait_timeout(about 8 hours). Currently no best solution to tackle this issue. The workaround is to re-do the operation again and it will succeed.
        </div>
        <div class="alert alert-warning" role="alert">
            <strong>Can tasks have multiple attributes of the same name?</strong><br/>
            Currently this is NOT allowed. Please make sure to avoid this, otherwise unexpected issues might happen.
        </div>
        <div class="alert alert-warning" role="alert">
            <strong>Is there any translation or dictionary supported?</strong><br/>
            No. The translation or dictionary is not easy to implement, and it is convenient to check words online.
        </div>
        <div class="alert alert-warning" role="alert">
            <strong>How can I analyze memory leak?</strong><br/>
            Click 'Dump Heap' button to download the heap snapshot and run 'jhat path_to_dump_file'. Visit 'localhost:7000' to check details by default.
        </div>
        <div class="alert alert-warning" role="alert">
            <strong>How is Java memory pool divided?</strong><br/>
            <b>Heap Memory</b><br/>
            <p><b>PS Eden Space</b>: The pool from which memory is initially allocated for most objects.</p>
            <p><b>PS Survivor Space</b>: The pool containing objects that have survived the garbage collection of the Eden space.</p>
            <p><b>PS Old Gen</b>: The pool containing objects that have existed for some time in the survivor space.</p>
            <b>Non-Heap Memory</b><br/>
            <p><b>PS Perm Gen</b>: The pool containing all the reflective data of the virtual machine itself, such as class and method objects.</p>
            <p><b>Code Cache</b>: The HotSpot Java VM also includes a code cache, containing memory that is used for compilation and storage of native code.</p>
        </div>
        <%
        List<Faq> faqs = FaqManager.getInstance().getFaqs();
        Collections.sort(faqs, new Comparator<Faq>(){
            public int compare(Faq f1, Faq f2) {
                return f1.name.compareTo(f2.name);
            }
        });
        for(Faq faq : faqs) {
        %>
        <div class="alert alert-warning" role="alert">
            <strong><%=faq.question%></strong><br/>
            <%=faq.answer%>
        </div>
        <%
        }
        %>
    </div>
</div>
<%@ include file="import_script.jsp" %>
<%@ include file="footer.jsp" %>
