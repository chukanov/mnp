<%@ page import="com.eyeline.mnp.Storage" %>
<%@ page import="com.eyeline.mnp.Mno" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="utils.jsp"%>
<%
    Storage mnpStorage = (Storage) application.getAttribute("mnp-storage");

    String eventType = request.getParameter("event.type");
    String input = null;
    if (eventType.equals("text")) {
        input = request.getParameter("event.text");
    } else if (eventType.equals("contact")){
        input = request.getParameter("event.msisdn");
    }

    List<String> msisdn = toMsisdnList(input);

    Map<String, Mno> res = new HashMap<String, Mno>();
    if (msisdn!=null) {
        for (String m: msisdn) {
            try {
                Mno mno = mnpStorage.lookup(m);
                res.put(mno.getId(), mno);
            } catch (Exception e) {
                //to do log
            }
        }
    }
%><page version="2.0">
    <%
        if (input.equals("/help")) {
    %><div><b>Я бот, который умеет определять оператора и регион абонента по номеру телефона (пока только мобильные РФ).</b><br/>
    Телефон можно задать с 7, без нее, с 8 и т.д. Как и любой робот, я обращаю внимание только на цифры.<br/>
    Примеры: <i>9139367911, 8(913)936-7911, +7 913 936 79 11</i><br/>
    Можно задать сразу несколько номеров через запятую, перенос строки или точку с запятой.
    <br/><br/>
    <b>Этот бот разработан с помощью</b> https://miniApps.pro, <b>ведущей платформы для бот-коммерции.</b><br/>
    <br/>
    Буду благодарен, если оцените меня по ссылке: https://telegram.me/storebot?start=mnprobot<br/>
</div><%
    }
%>
    <div>
        <% if (msisdn==null || msisdn.size() == 0) { %>
        Введите один или несколько номеров телефонов через запятую или выберите контакт из адресной книги.<br/>Вызов справки: /help
        <% } else if (res!=null){
            if (res.size()>0) {
                for (Map.Entry<String,Mno> entry: res.entrySet()){
                    String m = entry.getKey();
                    Mno mno = entry.getValue();
                    %><%=formatPhone(m)%> это <%=buildMnoString(mno)%>
        <%      }
            } else {
            %>Введите один или несколько номеров телефонов через запятую или выберите контакт из адресной книги.<br/>Вызов справки: /help<%
            }
        } else {
            %>Прошу прощения, наблюдаются временные технические неполадки. Уверен, они ненадолго. Повторите запрос через пару минут.<%
        }
        %>
    </div>
    <div>
        <input navigationId="submit" name="phone"/>
    </div>
    <navigation id="submit">
        <link accesskey="1" pageId="index.jsp">Ok</link>
    </navigation>
</page>
