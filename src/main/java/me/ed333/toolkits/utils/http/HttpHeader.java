package me.ed333.toolkits.utils.http;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class HttpHeader {
    private Map<String, String> headers = new HashMap<>();

    public void add(String key, String value) {
        this.headers.put(key, value);
    }

    public String get(String key) {
        return this.headers.get(key);
    }

    public void remove(String key) {
        this.headers.remove(key);
    }

    public void addToConnection(HttpURLConnection connection) {
        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }
}
