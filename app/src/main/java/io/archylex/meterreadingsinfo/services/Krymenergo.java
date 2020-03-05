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

public class Krymenergo {
    private static String login_host = "https://www.mega-billing.ru/ru";
    private static String main_host = "https://mega-billing.ru/ru";
    private static Map<String, String> info;
    private static NetworkServiceParameters tmp_nsp;

    public Krymenergo(String login, String password) {
        info = new LinkedHashMap<>();
        tmp_nsp = new NetworkServiceParameters();

        try {
            getSession(login, password);
            connectToSubscriberNumber(getSubscriberNumber());
            getInfo();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static String getOwner() {
        return info.get("owner") != null ? info.get("owner") : "";
    }

    public static String getAddress() {
        return info.get("address") != null ? info.get("address") : "";
    }

    public static String getSaldo() {
        return info.get("saldo") != null ? info.get("saldo") : "";
    }

    public static String getSubscriber() {
        return info.get("subscriber_number") != null ? info.get("subscriber_number") : "";
    }

    public static String getOrganization() {
        return info.get("organization") != null ? info.get("organization") : "";
    }

    public static String getPayment() {
        return info.get("payment") != null ? info.get("payment") : "";
    }

    public static String getPaymentDate() {
        return info.get("payment_date") != null ? info.get("payment_date") : "";
    }

    public static String getLastReading() {
        return info.get("reading") != null ? info.get("reading") : "";
    }

    public static String getReadingDate() {
        return info.get("reading_date") != null ? info.get("reading_date") : "";
    }

    private void getInfo() throws ExecutionException, InterruptedException {
        tmp_nsp.setHTTPMethod("GET");
        tmp_nsp.setUrl(main_host + "/summary");

        NetworkServiceParameters response = sendRequest();

        if (response.getContent() != null) {
            Pattern pattern = Pattern.compile("/ru/multi/readings/create/(\\d+).?\"");
            Matcher matcher = pattern.matcher(response.getContent());
            if (matcher.find())
                this.info.put("user_id", matcher.group(1));

            pattern = Pattern.compile("<td>(.*) руб от ([0-9 .]+).г");
            matcher = pattern.matcher(response.getContent());
            while (matcher.find()) {
                this.info.put("payment", matcher.group(1));
                this.info.put("payment_date", matcher.group(2));
            }

            pattern = Pattern.compile("<th>Потребитель<..th>.n\\s+<td>(.*?)<..?td>");
            matcher = pattern.matcher(response.getContent());
            if (matcher.find())
                this.info.put("owner", matcher.group(1));

            pattern = Pattern.compile("<th>Адрес<..th>.n\\s+<td>(.*\\d+)<..?td>");
            matcher = pattern.matcher(response.getContent());
            if (matcher.find())
                this.info.put("address", matcher.group(1));

            String sign = "-";
            pattern = Pattern.compile("Задолженность, руб");
            matcher = pattern.matcher(response.getContent());
            if (matcher.find()) {
                sign = "";
            }

            pattern = Pattern.compile("<span class=..saldo-value..>(.*)<..span>");
            matcher = pattern.matcher(response.getContent());
            if (matcher.find()) {
                String str = matcher.group(1).replace(",", ".");
                str = sign + str.replace(" ", "");
                this.info.put("saldo", str);
            }

            pattern = Pattern.compile("<b>(\\d+).кВт.ч<..b>.на.(\\d{2}.\\d{2}.\\d{4})");
            matcher = pattern.matcher(response.getContent());
            if (matcher.find()) {
                this.info.put("reading", matcher.group(1));
                this.info.put("reading_date", matcher.group(2));
                System.out.println("");
            }
        }

        tmp_nsp.removeHeader("X-Requested-With");

        response = sendRequest();

        if (response.getContent() != null) {
            Pattern pattern = Pattern.compile("<h1 class=\"content-title\">Лицевой счет № (\\d+)</h1>");
            Matcher matcher = pattern.matcher(response.getContent());

            if (matcher.find())
                this.info.put("subscriber_number", matcher.group(1));

            pattern = Pattern.compile("<div class=\"timestamp\">(.*?)&nbsp;");
            matcher = pattern.matcher(response.getContent());

            if (matcher.find())
                this.info.put("organization", matcher.group(1));

            tmp_nsp.addHeader("X-Requested-With", "XMLHttpRequest");
        }
    }

    public boolean submitReading(String num) {
        boolean sended = false;

        try {
            sended = sendAmountofEnergy(num);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return sended;
    }

    private boolean sendAmountofEnergy(String num) throws ExecutionException, InterruptedException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        tmp_nsp.setUrl(login_host + "/multi/readings/create");

        tmp_nsp.setHTTPMethod("POST");

        tmp_nsp.addBodyParam("utf8", "✓");
        tmp_nsp.addBodyParam("readings[1]", num);
        tmp_nsp.addBodyParam("reading_day", dateFormat.format(date));
        tmp_nsp.addBodyParam("counterid", this.info.get("user_id"));
        tmp_nsp.addBodyParam("scale", "5");
        tmp_nsp.addBodyParam("zonecount", "1");

        NetworkServiceParameters response = sendRequest();

        if (response.getHTTPOk())
            return true;
        else
            return false;
    }

    // GET CSRF TOKEN
    private String getToken() throws ExecutionException, InterruptedException {
        tmp_nsp.setHTTPMethod("GET");

        NetworkServiceParameters response = sendRequest();

        String content = response.getContent();

        if (content != null) {
            Pattern pattern = Pattern.compile("<meta name=\"csrf-token\" content=\"(.*?)\" />");
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    private boolean getSession(String login, String password) throws ExecutionException, InterruptedException {
        tmp_nsp.setUrl(login_host + "/login");

        String token = getToken();

        tmp_nsp.setHTTPMethod("POST");

        tmp_nsp.addHeader("Content-Type", "application/x-www-form-urlencoded");

        tmp_nsp.addBodyParam("utf8", "✓");
        tmp_nsp.addBodyParam("username", login);
        tmp_nsp.addBodyParam("password", password);
        tmp_nsp.addBodyParam("authenticity_token", token);

        NetworkServiceParameters response = sendRequest();

        tmp_nsp.setContent(response.getContent());

        return response.getHTTPOk();
    }

    // GET SUBSCRIBER NUMBER
    private String getSubscriberNumber() {
        if (tmp_nsp.getContent() != null) {
            Pattern pattern = Pattern.compile("<a href=\"https://www.mega-billing.ru/ru/accounts/(\\d+)\" class=\"acc-connect\">\\s+<img alt=\"ГУП РК «Крымэнерго»\"");
            Matcher matcher = pattern.matcher(tmp_nsp.getContent());

            if (matcher.find())
                return matcher.group(1);
        }

        return null;
    }

    // CONNECT TO SUBSCRIBER NUMBER
    private boolean connectToSubscriberNumber(String id) throws ExecutionException, InterruptedException {
        tmp_nsp.setUrl(main_host + "/accounts/" + id);

        tmp_nsp.addHeader("X-CSRF-Token", getToken());
        tmp_nsp.addHeader("X-Requested-With", "XMLHttpRequest");

        tmp_nsp.removeBodyParam("utf8");
        tmp_nsp.removeBodyParam("username");
        tmp_nsp.removeBodyParam("password");
        tmp_nsp.removeBodyParam("authenticity_token");

        NetworkServiceParameters response = sendRequest();

        if (response.getContent() != null) {
            Pattern pattern = Pattern.compile("window.location.href = 'https://www.mega-billing.ru/ru/summary';");
            Matcher matcher = pattern.matcher(response.getContent());

            if (matcher.find())
                return true;
        }

        return false;
    }

    private NetworkServiceParameters sendRequest() throws ExecutionException, InterruptedException {
        NetworkServiceTask asyncTask = new NetworkServiceTask();

        asyncTask.execute(tmp_nsp);

        NetworkServiceParameters response = asyncTask.get();

        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Set-Cookie")) {
                tmp_nsp.addHeader("Cookie", entry.getValue());
            }

            if (entry.getKey().equalsIgnoreCase("ETag")) {
                tmp_nsp.addHeader("ETag", entry.getValue());
            }
        }

        return response;
    }
}