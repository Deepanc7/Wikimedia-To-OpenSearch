package io.producer.com;

import okhttp3.*;

import java.io.IOException;
import okio.BufferedSource;

public class WikimediaStreamProcessor {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String STREAM_URL = "https://stream.wikimedia.org/v2/stream/recentchange";

    public void startStream(KafkaProducerService producerService) {
        Request request = new Request.Builder().url(STREAM_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.err.println("Unexpected response code: " + response.code());
                    return;
                }

                try (ResponseBody responseBody = response.body()) {
                    if (responseBody != null) {
                        BufferedSource source = responseBody.source();
                        StringBuilder eventData = new StringBuilder();

                        while (!source.exhausted()) {
                            String line = source.readUtf8Line();
                            eventData.append(line.trim());
                            String messages = eventData.toString();

                            try {
                                if (!messages.isEmpty()) {
                                    System.out.println("Sending to Kafka: " + messages);
                                    producerService.sendToKafka(messages);
                                }
                            } catch (Exception e) {
                                System.err.println("Error processing concatenated data (not valid JSON): " + e.getMessage());
                                System.err.println("Problematic line: " + messages);  // Log problematic line
                            }

                            eventData.setLength(0);  // Reset eventData
                        }
                    }
                }
            }
        });
    }
}
