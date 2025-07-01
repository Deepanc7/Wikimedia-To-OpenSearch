# Wikimedia to OpenSearch Data Pipeline

A real-time data streaming pipeline built with Apache Kafka that ingests live Wikimedia recent changes data and indexes it into OpenSearch for analysis and visualization.

## 🏗️ Architecture Overview

This project implements a robust data pipeline with the following components:

```
Wikimedia Recent Changes Stream → Kafka Producer → Kafka Topic → Kafka Consumer → OpenSearch
```

- **Data Source**: Wikimedia Recent Changes Event Stream (`https://stream.wikimedia.org/v2/stream/recentchange`)
- **Message Broker**: Apache Kafka for reliable data streaming
- **Data Sink**: OpenSearch for indexing and search capabilities
- **Language**: Java with Kafka Client APIs

## 🚀 Features

- **Real-time Data Ingestion**: Continuous streaming of Wikimedia recent changes
- **Fault Tolerance**: Kafka's built-in replication and durability guarantees
- **Scalable Architecture**: Horizontally scalable producer and consumer components
- **JSON Processing**: Structured data transformation and indexing
- **Monitoring**: Built-in logging and error handling
- **Configurable**: Easy-to-modify configuration for different environments

## 📋 Prerequisites

Before running this project, ensure you have:

- Java 11
- Apache Kafka
- OpenSearch
- Docker (optional, for containerized deployment)

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/Deepanc7/Wikimedia-To-OpenSearch.git
cd Wikimedia-To-OpenSearch
```

### 2. Start Kafka Infrastructure
```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka Server
bin/kafka-server-start.sh config/server.properties

# Create topic for Wikimedia data
bin/kafka-topics.sh --create --topic wikimedia-recent-changes --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

### 3. Start OpenSearch
```bash
# Using Docker
docker run -d \
  --name opensearch \
  -p 9200:9200 \
  -p 9600:9600 \
  -e "discovery.type=single-node" \
  -e "OPENSEARCH_INITIAL_ADMIN_PASSWORD=admin" \
  opensearchproject/opensearch:latest
```

### 4. Build the Project
```bash

# Using Gradle
./gradlew build
```

## 🏃‍♂️ Running the Application

### 1. Start the Wikimedia Producer
```bash
java -cp target/classes:target/lib/* com.wikimedia.producer.WikimediaChangesProducer
```

The producer will:
- Connect to Wikimedia's recent changes stream
- Parse incoming JSON events
- Publish messages to the Kafka topic `wikimedia-recent-changes`

### 2. Start the OpenSearch Consumer
```bash
java -cp target/classes:target/lib/* com.wikimedia.consumer.OpenSearchConsumer
```

The consumer will:
- Subscribe to the Kafka topic
- Process incoming messages
- Index data into OpenSearch
- Handle batch processing for efficiency
