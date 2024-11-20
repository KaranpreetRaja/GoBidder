# use latest Kafka image from Bitnami
FROM bitnami/kafka:latest

# Kafka Environment Variables
# Kafka broker ID
ENV KAFKA_BROKER_ID=1
# ZooKeeper connection string
ENV KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181
# Listeners configuration
ENV KAFKA_CFG_LISTENERS=PLAINTEXT://:9092
# Advertised listeners (how other services will connect)
ENV KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092
# Allow automatic topic creation
ENV KAFKA_CFG_AUTO_CREATE_TOPICS_ENABLE=true
# Default replication factor
ENV KAFKA_CFG_DEFAULT_REPLICATION_FACTOR=1
# Number of partitions for auto-created topics
ENV KAFKA_CFG_NUM_PARTITIONS=1

EXPOSE 9092