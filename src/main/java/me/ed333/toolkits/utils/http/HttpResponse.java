package me.ed333.toolkits.utils.http;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private int statusCode;
    private String body;
    private Map<String, List<String>> headers;

    public HttpResponse(int statusCode, String body, Map<String, List<String>> headers) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getBody() {
        return this.body;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    @Contract("_ -> new")
    public static @NotNull HttpResponse fromHttpURLConnection(@NotNull HttpURLConnection connection) throws IOException {
        int statusCode = connection.getResponseCode();
        InputStream inputStream = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line;
        StringBuilder responseBody = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            responseBody.append(line);
        }
        reader.close();

        return new HttpResponse(statusCode, responseBody.toString(), connection.getHeaderFields());
    }
}
