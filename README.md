Kafka streaming with Spark and Flink application
===
Example project on how to use [Apache Kafka](https://kafka.apache.org) and streaming consumers, namely:
- Producer sending random number words to Kafka
- Consumer using Kafka to output received messages
- Streaming Consumer using [Apache Spark](https://spark.apache.org) to count words occurrences
- Streaming Consumer using [Apache Flink](https://flink.apache.org) to count words occurrences 

Requirements
---
- Docker Compose
- Java 8
- Gradle

Run Streaming Job
---
1. Start docker containers
    ```
    docker-compose up -d
    ```
   
2. Run producer -> KafkaProducer.kt
    
    Output should be similar to:
    ```
    [main] INFO  org.davidcampos.kafka.producer.KafkaProducerExample - Sent (74b23319-084c-4309-80a7-c0d6f107a092, eight) to topic example @ 1525127107909.
    ```
3. Run consumer with Spark -> KafkaSparkConsumer.kt
    
    Output should be similar to:
    ```
    (two,3)
    (one,3)
    (nine,5)
    (six,8)
    (three,2)
    (five,2)
    (four,9)
    (seven,3)
    (eight,6)
    (ten,6)
    ```
4. Run consumer with Flink -> KafkaFlinkConsumer.kt
    
    Output should be similar to:
    ```
    1> (ten,85)
    4> (nine,104)
    1> (ten,86)
    4> (five,91)
    4> (one,94)
    4> (six,90)
    1> (three,89)
    4> (six,91)
    4> (five,92)
    ```
   
Run Batch Job
---   

Run job -> BatchJob.kt

   
Kafka Web UI
---
Kafka Manager Web UI available at [http://localhost:9000]().

Spark Web UI
---
Spark Web UI available at [http://localhost:4040]().

Stop
---
1. Stop docker containers
    ```
    docker-compose down
    ```
