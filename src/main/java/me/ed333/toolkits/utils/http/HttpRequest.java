package me.ed333.toolkits.utils.http;

import me.ed333.toolkits.utils.http.HttpRequestCfg;
import me.ed333.toolkits.utils.http.HttpResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {
    private HttpRequestCfg cfg;
    private HttpHeader header = new HttpHeader();

    public HttpRequest() { cfg = new HttpRequestCfg(); }
    public HttpRequest(HttpRequestCfg cfg) {
        this.cfg = cfg;
    }

    public void setHeader(HttpHeader header) {
        this.header = header;
    }

    public HttpResponse send(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        cfg.setToConnection(connection);
        header.addToConnection(connection);
        // 读取响应
        return HttpResponse.fromHttpURLConnection(connection);
    }
}

