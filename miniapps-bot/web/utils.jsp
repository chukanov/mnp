<%@ page import="java.util.List" %>
<%@ page import="java.util.LinkedHashSet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    public static List<String> toMsisdnList(String input) {
        if (input==null) return new ArrayList<String>();
        String[] inputs = input.split("[;\\,,\n]");
        LinkedHashSet<String> result = new LinkedHashSet<String>(input.length());
        for (String in: inputs) {
            String x = toMsisdn(in);
            if (x!=null) {
                result.add(x);
            }
        }
        return new ArrayList<String>(result);
    }

    private String toName(String id) {
        if (id.startsWith("ru.mts")) return "МТС";
        if (id.startsWith("ru.bee")) return "Билайн";
        if (id.startsWith("ru.mega")) return "Мегафон";
        if (id.startsWith("ru.tele2")) return "Tele2";
        if (id.startsWith("ru.gtel")) return "Глобал-Телеком";
        if (id.startsWith("ru.motiv")) return "Мотив";
        return "";
    }

    private String buildMnoString(Mno mno) {
        String id = this.toName(mno.getId());
        String ul = mno.getTitle();
        String region = mno.getArea();

        String out = "";
        if (StringUtils.isNotBlank(id)) {
            out += "<b>"+id+"</b>";
        }
        if (StringUtils.isNotBlank(ul)) {
            if (StringUtils.isNotBlank(out)) out+=", ";
            out += "<i>"+StringUtils.capitalize(ul)+"</i>";
        }
        if (StringUtils.isNotBlank(region)) {
            if (StringUtils.isNotBlank(out)) out+=", ";
            out += region;
        }
        if (StringUtils.isBlank(out)) out="неизвестный мне оператор. Вы точно ввели российский мобильный номер?<br/>Вызов справки: /help";
        return out;
    }

    private static String toMsisdn(String input) {
        if (input==null) return null;
        String msisdn = input.replaceAll("[\\D]", "");
        if (msisdn.length()<10) return null;
        if (msisdn.length()>10){
            msisdn = msisdn.substring(msisdn.length()-10, msisdn.length());
        }
        if (msisdn.length()!=10){
            return null;
        } else {
            return "7"+msisdn;
        }
    }

    private String formatPhone(String msisdn) {
        java.text.MessageFormat phoneMsgFmt=new java.text.MessageFormat("({0})-{1}-{2}");
        //suposing a grouping of 3-3-4
        String[] phoneNumArr={
                msisdn.substring(1, 4),
                msisdn.substring(4,7),
                msisdn.substring(7)
        };
        return phoneMsgFmt.format(phoneNumArr);
    }
%>