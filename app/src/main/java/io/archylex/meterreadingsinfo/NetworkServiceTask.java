package io.archylex.meterreadingsinfo;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class NetworkServiceTask extends AsyncTask<NetworkServiceParameters, Void, NetworkServiceParameters> {

    protected NetworkServiceParameters doInBackground(NetworkServiceParameters... params) {
        NetworkServiceParameters nsp = params[0];
        HttpURLConnection client = null;
        NetworkServiceParameters result = new NetworkServiceParameters();

        try {
            URL url = nsp.getUrl() != null ? new URL(nsp.getUrl()) : new URL("https://google.com");

            if (!nsp.getQueryParams().isEmpty()) {
                url = new URL(nsp.getUrl() + "?" + encodeParams(nsp.getQueryParams()));
            }

            client = (HttpURLConnection) url.openConnection();

            if (client != null) {
                client.setRequestMethod(nsp.getHTTPMethod());

                client.setDoInput(true);
                client.setUseCaches(false);
                client.setDoOutput(true);

                if (!nsp.getHeaders().isEmpty()) {
                    for (Map.Entry<String, String> entry : nsp.getHeaders().entrySet()) {
                        client.setRequestProperty(entry.getKey() != null ? entry.getKey() : "", entry.getValue() != null ? entry.getValue() : "");
                    }
                }

                if (!nsp.getBodyParams().isEmpty()) {
                    OutputStream os = client.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(encodeParams(nsp.getBodyParams()));
                    writer.flush();
                    writer.close();
                    os.close();
                }

                if (client.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    result.setHTTPOk(true);

                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }

                    in.close();

                    result.setContent(sb.toString());

                    for (Map.Entry<String, List<String>> entry : client.getHeaderFields().entrySet()) {
                        String key = entry.getKey() == null ? "" : entry.getKey();
                        String value = "";

                        for (String str : entry.getValue())
                            value += str;
                        result.addHeader(key, value);
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            if(client != null)
                client.disconnect();
        }

        return result;
    }

    private static String encodeParams(Map<String, String> bParams) {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        try {

            for (Map.Entry<String, String> entry : bParams.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey() != null ? entry.getKey() : "", "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue() != null ? entry.getValue() : "", "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}