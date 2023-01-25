package me.ed333.toolkits.utils.http;

import java.net.HttpURLConnection;
import java.net.ProtocolException;

public class HttpRequestCfg {
    private String method = "GET";
    private int readTimeout = 5000;
    private int connectTimeout = 5000;
    private boolean followRedirects = true;

    public HttpRequestCfg() {}

    public HttpRequestCfg(String method, int readTimeout, int connectTimeout, boolean followRedirects) {
        this.method = method;
        this.readTimeout = readTimeout;
        this.connectTimeout = connectTimeout;
        this.followRedirects = followRedirects;
    }

    public String getMethod() {
        return this.method;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public void setToConnection(HttpURLConnection connection) throws ProtocolException {
        connection.setRequestMethod(method);
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        connection.setInstanceFollowRedirects(followRedirects);
    }
}


