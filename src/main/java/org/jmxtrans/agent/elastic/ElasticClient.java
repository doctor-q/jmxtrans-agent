package org.jmxtrans.agent.elastic;

import org.jmxtrans.agent.util.StandardCharsets2;
import org.jmxtrans.agent.util.io.IoUtils;
import org.jmxtrans.agent.util.logging.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ElasticClient {
    private final Logger logger = Logger.getLogger(getClass().getName());

    private String host;
    private int port;

    private String currentIndex;
    private HttpURLConnection conn;

    public ElasticClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void index(Map<String, Object> document, String index) {
        try {
            createAndConfigureConnection(index);
            String json = mapToJsonString(document);
            byte[] toSendBytes = json.getBytes(StandardCharsets2.UTF_8);
            conn.setRequestProperty("Content-Length", Integer.toString(toSendBytes.length));
            try (OutputStream os = conn.getOutputStream()) {
                os.write(toSendBytes);
                os.flush();
            }
            int code = conn.getResponseCode();
            if (code >= 400 || code < 200) {
                String message = conn.getResponseMessage();
                logger.warning("Index error. code : " + code + ", message: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createAndConfigureConnection(String index) throws IOException {
        // new index new connection
        if (currentIndex == null || !index.equals(currentIndex)) {
            currentIndex = index;
            if (conn != null) {
                IoUtils.closeQuietly(conn);
            }
            URL url = new URL(String.format("http://%s:%s/%s/_doc/", host, port, index));
            logger.info("Index document in " + url.getPath());
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");
        }
    }

    public void close() {
        if (conn != null) {
            IoUtils.closeQuietly(conn);
        }
    }


    private String mapToJsonString(Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('{').append("\n");
        int size = map.size();
        int i = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            stringBuilder.append('"').append(key).append('"').append(":");
            if (value instanceof Number) {
                stringBuilder.append(value);
            } else {
                stringBuilder.append('"').append(value.toString()).append('"');
            }
            if (i < size - 1) {
                stringBuilder.append(',');
            }
            stringBuilder.append('\n');
            i++;
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
