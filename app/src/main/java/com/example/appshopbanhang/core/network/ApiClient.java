package com.example.appshopbanhang.core.network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ApiClient {
    private static final int CONNECT_TIMEOUT_MS = 15000;
    private static final int READ_TIMEOUT_MS = 20000;

    public JSONArray getArray(String path) throws Exception {
        String body = request("GET", path, null, "application/json");
        return new JSONArray(body);
    }

    public JSONObject getObject(String path) throws Exception {
        String body = request("GET", path, null, "application/json");
        return new JSONObject(body);
    }

    public JSONObject postJson(String path, JSONObject json) throws Exception {
        String body = request("POST", path, json.toString(), "application/json; charset=UTF-8");
        return new JSONObject(body);
    }

    public JSONObject putJson(String path, JSONObject json) throws Exception {
        String body = request("PUT", path, json.toString(), "application/json; charset=UTF-8");
        return new JSONObject(body);
    }

    public void delete(String path) throws IOException {
        request("DELETE", path, null, "application/json");
    }

    public JSONObject multipart(
            String method,
            String path,
            Map<String, String> fields,
            String fileField,
            String fileName,
            String mimeType,
            byte[] fileBytes
    ) throws Exception {
        String boundary = "----AppShopBanHang" + System.currentTimeMillis();
        HttpURLConnection connection = openConnection(method, path);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setDoOutput(true);

        try (OutputStream output = connection.getOutputStream()) {
            for (Map.Entry<String, String> field : fields.entrySet()) {
                writeTextPart(output, boundary, field.getKey(), field.getValue());
            }
            if (fileBytes != null && fileBytes.length > 0) {
                writeFilePart(output, boundary, fileField, fileName, mimeType, fileBytes);
            }
            output.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        }

        String body = readResponse(connection);
        return new JSONObject(body);
    }

    public static String query(Map<String, String> params) {
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            if (query.length() > 0) {
                query.append('&');
            }
            query.append(urlEncode(entry.getKey()));
            query.append('=');
            query.append(urlEncode(entry.getValue()));
        }
        return query.toString();
    }

    private String request(String method, String path, String body, String contentType) throws IOException {
        HttpURLConnection connection = openConnection(method, path);
        connection.setRequestProperty("Accept", "application/json");
        if (body != null) {
            connection.setRequestProperty("Content-Type", contentType);
            connection.setDoOutput(true);
            try (OutputStream output = connection.getOutputStream()) {
                output.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }
        return readResponse(connection);
    }

    private HttpURLConnection openConnection(String method, String path) throws IOException {
        URL url = new URL(ApiConfig.getBaseUrl() + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
        connection.setReadTimeout(READ_TIMEOUT_MS);
        connection.setRequestMethod(method);
        return connection;
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        int code = connection.getResponseCode();
        InputStream input = code >= 200 && code < 300
                ? connection.getInputStream()
                : connection.getErrorStream();
        String body = input == null ? "" : readFully(input);
        if (code < 200 || code >= 300) {
            throw new IOException("HTTP " + code + ": " + body);
        }
        return body;
    }

    private static String readFully(InputStream input) throws IOException {
        try (InputStream stream = input; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = stream.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            return output.toString(StandardCharsets.UTF_8.name());
        }
    }

    private static void writeTextPart(OutputStream output, String boundary, String name, String value) throws IOException {
        output.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        output.write((value == null ? "" : value).getBytes(StandardCharsets.UTF_8));
        output.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private static void writeFilePart(
            OutputStream output,
            String boundary,
            String name,
            String fileName,
            String mimeType,
            byte[] bytes
    ) throws IOException {
        output.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + fileName + "\"\r\n")
                .getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        output.write(bytes);
        output.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (Exception exception) {
            return value;
        }
    }
}
