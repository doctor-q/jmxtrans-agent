package org.jmxtrans.agent.elastic;

import org.jmxtrans.agent.AbstractOutputWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.jmxtrans.agent.util.ConfigurationUtils.getInt;
import static org.jmxtrans.agent.util.ConfigurationUtils.getString;


public class ElasticsearchOutputWriter extends AbstractOutputWriter {

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private static final String ELASTICSEARCH_HOST = "elasticsearchHost";
    private static final String ELASTICSEARCH_HOST_DEFAULT_VALUE = "localhost";

    private static final String ELASTICSEARCH_PORT = "elasticsearchPort";
    private static final int ELASTICSEARCH_PORT_DEFAULT_VALUE = 9200;

    private static final String ELASTICSEARCH_INDEX = "elasticsearchIndex";
    private static final String ELASTICSEARCH_INDEX_DEFAULT_VALUE = "jmxtrans-%{yyyy.MM.dd}";

    private IndexNameBuilder indexNameBuilder;
    private String host;

    private ElasticClient elasticClient;

    private ThreadLocal<Map<String, Object>> documents = new ThreadLocal<Map<String, Object>>() {

        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>();
        }
    };

    @Override
    public void postConstruct(@Nonnull Map<String, String> settings) {
        super.postConstruct(settings);

        try {
            String elasticSearchHost = getString(settings, ELASTICSEARCH_HOST, ELASTICSEARCH_HOST_DEFAULT_VALUE);
            int elasticSearchPort = getInt(settings, ELASTICSEARCH_PORT, ELASTICSEARCH_PORT_DEFAULT_VALUE);
            try {
                logger.info("Connection to http://{}:{}" + elasticSearchHost + ":" + elasticSearchPort);
                elasticClient = new ElasticClient(elasticSearchHost, elasticSearchPort);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (logger.isLoggable(getTraceLevel())) {
                String msg = String.format("ElasticSearchOutputWriter is configured with elasticHost=%s, elasticPort=%s", elasticSearchHost, elasticSearchPort);
                logger.log(getTraceLevel(), msg);
            }

            String indexNamePattern = getString(settings, ELASTICSEARCH_INDEX, ELASTICSEARCH_INDEX_DEFAULT_VALUE);
            indexNameBuilder = new IndexNameBuilder(indexNamePattern);

            try {
                host = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preDestroy() {
        elasticClient.close();
    }

    @Override
    public void writeQueryResult(@Nonnull String name, String type, Object value) throws IOException {
        Map<String, Object> currentThreadDocuments = documents.get();
        currentThreadDocuments.put(name, value);
    }

    @Override
    public void writeInvocationResult(@Nonnull String invocationName, Object value) throws IOException {
        writeQueryResult(invocationName, null, value);
    }

    @Override
    public void postCollect() throws IOException {
        Date timestamp = new Date();
        Map<String, Object> document = documents.get();
        document.put("@timestamp", new SimpleDateFormat(TIMESTAMP_FORMAT).format(timestamp));
        if (host != null) {
            document.put("host", host);
        }
        elasticClient.index(document, indexNameBuilder.build(timestamp));
        documents.remove();
    }
}
