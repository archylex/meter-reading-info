package io.archylex.meterreadingsinfo;

import java.util.LinkedHashMap;
import java.util.Map;

public class NetworkServiceParameters {
    private static String url;
    private static String method;
    private Map<String, String> headers = new LinkedHashMap<>();
    private Map<String, String> query_params = new LinkedHashMap<>();
    private Map<String, String> body_params = new LinkedHashMap<>();
    private static String content;
    private boolean httpOk = false;

    public NetworkServiceParameters() {

    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHTTPMethod(String method) {
        this.method = method.toUpperCase();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void addQueryParam(String key, String value) {
        this.query_params.put(key, value);
    }

    public void addBodyParam(String key, String value) {
        this.body_params.put(key, value);
    }

    public void setHTTPOk(boolean ok) {
        this.httpOk = ok;
    }

    public String getContent() {
        return this.content;
    }

    public String getUrl() {
        return this.url;
    }

    public String getHTTPMethod() {
        return this.method;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public Map<String, String> getQueryParams() {
        return this.query_params;
    }

    public Map<String, String> getBodyParams() {
        return this.body_params;
    }

    public boolean getHTTPOk() {
        return httpOk;
    }

    public void removeHeader(String key) {
        this.headers.remove(key);
    }

    public void removeBodyParam(String key) {
        this.body_params.remove(key);
    }
}