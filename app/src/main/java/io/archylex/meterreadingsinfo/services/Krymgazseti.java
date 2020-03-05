package io.archylex.meterreadingsinfo.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.archylex.meterreadingsinfo.NetworkServiceParameters;
import io.archylex.meterreadingsinfo.NetworkServiceTask;

public class Krymgazseti {
    private static String host_url = "https://lkk.crimeagasnet.ru/";
    private static Map<String, String> info;
    private static String sessionID = null;

    public Krymgazseti(String login, String password) {
        info = new LinkedHashMap<>();

        try {
            sessionID = getSession(login, password);

            if (sessionID != null) {
                getInfo(sessionID);
                getDateOfLastPayment(sessionID);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static String getSession(String login, String password) throws ExecutionException, InterruptedException {
        NetworkServiceTask asyncTask = new NetworkServiceTask();
        NetworkServiceParameters nsp = new NetworkServiceParameters();
        String sessid = null;

        nsp.setUrl(host_url);

        nsp.setHTTPMethod("POST");

        nsp.addHeader("Content-Type", "application/x-www-form-urlencoded");
        nsp.addHeader("Accept", "text/html");

        nsp.addBodyParam("mail", login);
        nsp.addBodyParam("password", password);
        nsp.addBodyParam("submit", "");

        asyncTask.execute(nsp);

        for (Map.Entry<String, String> entry : asyncTask.get().getHeaders().entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Set-Cookie")) {
                Pattern pattern = Pattern.compile("PHPSESSID=(.*?);");
                Matcher matcher = pattern.matcher(entry.getValue());

                while (matcher.find()) {
                    sessid = matcher.group(1);
                }
            }
        }

        return sessid;
    }

    private static void getInfo(String sessionId) throws ExecutionException, InterruptedException {
        NetworkServiceTask asyncTask = new NetworkServiceTask();
        NetworkServiceParameters nsp = new NetworkServiceParameters();

        nsp.setUrl(host_url);

        nsp.setHTTPMethod("POST");

        nsp.addHeader("Accept", "text/html");
        nsp.addHeader("Cookie", "PHPSESSID=" + sessionId);

        nsp.addQueryParam("page", "abon_book");
        nsp.addQueryParam("search", "abon");

        asyncTask.execute(nsp);

        String content = asyncTask.get().getContent();

        Pattern pattern = Pattern.compile("<input name=\"(.*?)\" type=\"hidden\" value=\"(.*?)\"/>");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            info.put(matcher.group(1), matcher.group(2));
        }

        pattern = Pattern.compile("<h3><i class=\"fa fa-rub\"></i> (.*?)</h3>");
        matcher = pattern.matcher(content);
        String sign_minus = "";

        while (matcher.find()) {
            if (matcher.group(1).equalsIgnoreCase("Переплата")) {
                sign_minus = "-";
                break;
            }
        }

        pattern = Pattern.compile("<strong >(\\d+[.]\\d{2})<span style=\"font-size: 25px;\"> руб.</span>");
        matcher = pattern.matcher(content);

        if (matcher.find())
            info.put("bill", sign_minus + matcher.group(1));

        pattern = Pattern.compile("<td rowspan=\"3\" style=\"text-align: right;\">(\\d{1},\\d{4})</td>");
        matcher = pattern.matcher(content);

        if (matcher.find())
            info.put("last_price", matcher.group(1));

        pattern = Pattern.compile("uegh_id=\"(\\d+)\"");
        matcher = pattern.matcher(content);

        if (matcher.find())
            info.put("uegh_id", matcher.group(1));
    }

    //
    private static void getDateOfLastPayment(String sessionId) throws ExecutionException, InterruptedException {
        NetworkServiceTask asyncTask = new NetworkServiceTask();
        NetworkServiceParameters nsp = new NetworkServiceParameters();

        nsp.setUrl(host_url);

        nsp.setHTTPMethod("POST");

        nsp.addHeader("Accept", "text/html");
        nsp.addHeader("Cookie", "PHPSESSID=" + sessionId);

        nsp.addQueryParam("page", "pokazaniya");

        asyncTask.execute(nsp);

        String content = asyncTask.get().getContent();

        Pattern pattern = Pattern.compile("<td>(\\d{2}-\\d{2}-\\d{4})</td><td>" + getRecentMeterReading() + "</td><td>Обработано</td>");
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            info.put("last_reading_date", matcher.group(1));
        }
    }

    public static String getLastReadingDate() {
        return info.get("last_reading_date") != null ? info.get("last_reading_date").replace("-", ".") : "";
    }

    public static String getSubscriberNumber() {
        return info.get("nom") != null ? info.get("nom").replaceAll("\\s+", "") : "";
    }

    public static String getOrganization() {
        return info.get("uegh_name") != null ? info.get("uegh_name") : "";
    }

    public static String getOwner() {
        if (info.get("f") != null && info.get("i") != null && info.get("o") != null)
            return info.get("f") + " " + info.get("i") + " " + info.get("o");

        return "";
    }

    public static Float getBill() {
        if (info.get("bill") != null)
            return Float.valueOf(info.get("bill").replace(",", "."));

        return 0.00F;
    }

    private static String getUserId() {
        return info.get("id_user") != null ? info.get("id_user") : "";
    }

    public static Integer getRecentMeterReading() {
        if (info.get("pokaz_last") != null)
            return Integer.valueOf(info.get("pokaz_last"));

        return 0;
    }

    public static String getAddress() {
        return info.get("adres") != null ? info.get("adres") : "";
    }

    public static Float getLastPrice() {
        return info.get("last_price") != null ? Float.valueOf(info.get("last_price").replace(",", ".")) : 0.00F;
    }

    private static String getOrganizationId() {
        return  info.get("uegh_id") != null ? info.get("uegh_id") : "";
    }

    public static boolean meterReading(String num) {
        boolean sended = false;

        try {
            if (sessionID != null)
                sended = sendAmountOfGas(num, sessionID);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return sended;
    }

    private static boolean sendAmountOfGas(String num, String sessionId) throws ExecutionException, InterruptedException {
        NetworkServiceTask asyncTask = new NetworkServiceTask();
        NetworkServiceParameters nsp = new NetworkServiceParameters();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        nsp.setUrl(host_url);

        nsp.setHTTPMethod("POST");

        nsp.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        nsp.addHeader("x-requested-with", "XMLHttpRequest");
        nsp.addHeader("Cookie", "PHPSESSID=" + sessionId);

        nsp.addBodyParam("ajax", "add_pokaz");
        nsp.addBodyParam("user_id", getUserId());
        nsp.addBodyParam("uegh_id", getOrganizationId());
        nsp.addBodyParam("nom", getSubscriberNumber());
        nsp.addBodyParam("pokaz", num);
        nsp.addBodyParam("data_pokaz", dateFormat.format(date));

        asyncTask.execute(nsp);

        return asyncTask.get().getHTTPOk();
    }
}