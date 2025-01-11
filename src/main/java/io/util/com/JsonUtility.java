package io.util.com;

public class JsonUtility {

    /**
     * Extracts relevant fields from Kafka record and creates a JSON document.
     *
     * @param event The event string.
     * @param id    The ID string.
     * @param data  The data string.
     * @return JSON document as a string.
     */
    public static String extractJson(String event, String id, String data) {
        // Parse the ID string to construct the message_id array
        String[] idParts = id.replace("id:", "").trim().split(",");
        StringBuilder messageIdArray = new StringBuilder("[");
        for (String idPart : idParts) {
            messageIdArray.append(idPart.trim()).append(",");
        }
        // Remove the trailing comma and close the array
        if (messageIdArray.length() > 1) {
            messageIdArray.setLength(messageIdArray.length() - 1);
        }
        messageIdArray.append("]");

        // Build the final JSON document
        return String.format("""
                {
                    "event_type": "%s",
                    "message_id": %s,
                    "content": %s,
                    "timestamp": "%s"
                }
                """,
                event.replace("event: ", "").trim(),
                messageIdArray.toString(),
                data.replace("data:", "").trim(),
                java.time.Instant.now().toString()
        );
    }
}
