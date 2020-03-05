package io.archylex.meterreadingsinfo.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.archylex.meterreadingsinfo.NetworkServiceParameters;
import io.archylex.meterreadingsinfo.NetworkServiceTask;

public class VodaKryma {
    private static String host_url = "http://voda.crimea.ru:5080";
    private static Map<String, String> info;

    public VodaKryma(String login, String password) {
        info = new LinkedHashMap<>();

        try {
            String sessionId = getSessionId();

            if (loginSession(login, password, sessionId)) {
                getSubscriberInfo(sessionId);
                getPaymentInfo(sessionId);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String getSessionId() throws ExecutionException, InterruptedException {
        NetworkServiceTask asyncTask = new NetworkServiceTask();
        NetworkServiceParameters nsp = new NetworkServiceParameters();

        nsp.setUrl(host_url + "/login");
        nsp.setHTTPMethod("POST");

        nsp.addHeader("Content-Type", "application/x-www-form-urlencoded");
        nsp.addHeader("Accept", "text/html");

        asyncTask.execute(nsp);

        for (Map.Entry<String, String> entry : asyncTask.get().getHeaders().entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Set-Cookie")) {
                Pattern pattern = Pattern.compile("session=(.*?);");
                Matcher matcher = pattern.matcher(entry.getValue());

                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }

        return null;
    }

    private static boolean loginSession(String login, String password, String sessionId) throws ExecutionException, InterruptedException {
        NetworkServiceTask asyncTask = new NetworkServiceTask();
        NetworkServiceParameters nsp = new NetworkServiceParameters();

        nsp.setUrl(host_url + "/login");
        nsp.setHTTPMethod("POST");

        nsp.addHeader("Content-Type", "application/x-www-form-urlencoded");
        nsp.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;");
        nsp.addHeader("Cookie", "session=" + sessionId);

        nsp.addBodyParam("login_type", "1");
        nsp.addBodyParam("login", login);
        nsp.addBodyParam("password", password);

        asyncTask.execute(nsp);

        String content = asyncTask.get().getContent();

        if (content != null) {
            Pattern pattern = Pattern.compile("<title>     - Личный кабинет</title>");
            Matcher matcher = pattern.matcher(content);

            if (matcher.find())
                return true;
        }

        return false;
    }

    private static void getSubscriberInfo(String sessionId) throws ExecutionException, InterruptedException, JSONException {
        NetworkServiceTask asyncTask = new NetworkServiceTask();
        NetworkServiceParameters nsp = new NetworkServiceParameters();

        nsp.setUrl(host_url + "/contracts/getContractsObjList/ajax");

        nsp.setHTTPMethod("POST");

        nsp.addHeader("Accept", "application/json");
        nsp.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        nsp.addHeader("Cookie", "session=" + sessionId);
        nsp.addHeader("X-Requested-With", "XMLHttpRequest");

        nsp.addBodyParam("api_version", "2");
        nsp.addBodyParam("jsonData", "{\"id\":\"objects\",\"pActiveOnly\":0,\"without_zipcode\":1}");

        asyncTask.execute(nsp);

        String content = asyncTask.get().getContent();

        if (content != null) {
            JSONObject rjson = new JSONObject(content);
            JSONArray arr = rjson.getJSONArray("data");
            info.put("subscriber_number", arr.getJSONObject(0).getString("contract_number"));
            info.put("address", arr.getJSONObject(0).getString("obj_address"));
        }
    }

    private static void getPaymentInfo(String sessionId) throws  JSONException, ExecutionException, InterruptedException {
        NetworkServiceTask asyncTask = new NetworkServiceTask();
        NetworkServiceParameters nsp = new NetworkServiceParameters();

        nsp.setUrl(host_url + "/user/getMainDebtPayment/ajax");

        nsp.setHTTPMethod("POST");


        nsp.addHeader("Accept", "application/json");
        nsp.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        nsp.addHeader("Cookie", "session=" + sessionId);
        nsp.addHeader("X-Requested-With", "XMLHttpRequest");

        nsp.addBodyParam("api_version", "2");
        nsp.addBodyParam("jsonData", "{}");

        asyncTask.execute(nsp);

        String content = asyncTask.get().getContent();

        if (content != null) {
            JSONObject rjson = new JSONObject(content);
            JSONArray arr = rjson.getJSONArray("data");
            info.put("saldo", arr.getJSONObject(0).getString("OUT_SALDO"));
            info.put("saldo_date", arr.getJSONObject(0).getString("MAX_CALC_PAY"));
            info.put("payment", arr.getJSONObject(0).getString("MAX_INPUT_PAY_SUM"));
            info.put("payment_date", arr.getJSONObject(0).getString("MAX_INPUT_PAY"));
        }
    }

    public static String getAddress() {
        return info.get("address") != null ? info.get("address") : "";
    }

    public static String getSubscriberNumber() {
        return info.get("subscriber_number") != null ? info.get("subscriber_number") : "";
    }

    public static Float getSaldo() {
        return info.get("saldo") != null ? Float.valueOf(info.get("saldo").replace(",", ".")) : 0.00F;
    }

    public static Float getPayment() {
        return info.get("payment") != null ? Float.valueOf(info.get("payment").replace(",", ".")) : 0.00F;
    }

    public static String getSaldoDate() {
        return info.get("saldo_date") != null ? info.get("saldo_date") : "";
    }

    public static String getPaymentDate() {
        return info.get("payment_date") != null ? info.get("payment_date") : "";
    }

    public static String getOrganization() {
        return info.get("subscriber_number") != null ? "ГУП РК «Вода Крыма»" : "";
    }
}