package io.openSearch.com;

import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OpenSearchService {

    private static final Logger log = LoggerFactory.getLogger(OpenSearchService.class);
    private final RestHighLevelClient client;

    /**
     * OpenSearchService constructor.
     *
     * @param host The OpenSearch host.
     * @param port The OpenSearch port.
     */
    public OpenSearchService(String host, int port) {
        this.client = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, "http")));
    }

    /**
     * Creates the OpenSearch index if it doesn't exist.
     *
     * @param indexName The index name to check and create.
     * @throws IOException If an error occurs when creating the index.
     */
    public void createIndexIfNotExists(String indexName) throws IOException {
        boolean indexExists = client.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
        if (!indexExists) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            log.info("Created OpenSearch index: " + indexName);
        } else {
            log.info("Index already exists: " + indexName);
        }
    }

    /**
     * Bulk index documents into OpenSearch.
     *
     * @param bulkRequest The bulk request containing documents to index.
     * @throws IOException If an error occurs during the bulk index operation.
     */
    public void bulkIndexDocuments(BulkRequest bulkRequest) throws IOException {
        if (bulkRequest.numberOfActions() > 0) {
            client.bulk(bulkRequest, RequestOptions.DEFAULT);
            log.info("Bulk indexed documents into OpenSearch.");
        }
    }

    /**
     * Closes the OpenSearch client connection.
     *
     * @throws IOException If an error occurs while closing the client.
     */
    public void close() throws IOException {
        client.close();
    }
}
