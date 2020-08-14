import com.esotericsoftware.minlog.Log.info
import kafka.admin.AdminUtils
import kafka.admin.RackAwareMode
import kafka.utils.ZkUtils
import kafka.utils.`ZKStringSerializer$`
import org.I0Itec.zkclient.ZkClient
import org.I0Itec.zkclient.ZkConnection
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*
import java.util.concurrent.ExecutionException


    fun main() {
        // Create topic
        createTopic()
        val words = arrayOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten")
        val ran = Random(System.currentTimeMillis())
        val producer = createProducer()
        val EXAMPLE_PRODUCER_INTERVAL = if (System.getenv("EXAMPLE_PRODUCER_INTERVAL") != null)
            System.getenv("EXAMPLE_PRODUCER_INTERVAL").toInt() else 1000
        try {
            while (true) {
                val word = words[ran.nextInt(words.size)]
                val uuid = UUID.randomUUID().toString()
                val record = ProducerRecord(Commons.EXAMPLE_KAFKA_TOPIC, uuid, word)
                val metadata = producer.send(record).get()
                info("Sent (${uuid}, ${word}) to topic ${Commons.EXAMPLE_KAFKA_TOPIC} @ ${metadata.timestamp()}.")
                Thread.sleep(EXAMPLE_PRODUCER_INTERVAL.toLong())
            }
        } catch (e: InterruptedException) {
            e.stackTrace
            error("An error occurred.")
        } catch (e: ExecutionException) {
            e.stackTrace
            error("An error occurred.")
        } finally {
            producer.flush()
            producer.close()
        }
    }

    private fun createProducer(): Producer<String, String> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = Commons.EXAMPLE_KAFKA_SERVER
        props[ProducerConfig.CLIENT_ID_CONFIG] = "KafkaProducerExample"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        return KafkaProducer(props)
    }

    private fun createTopic() {
        val sessionTimeoutMs = 10 * 1000
        val connectionTimeoutMs = 8 * 1000
        val zkClient = ZkClient(
                Commons.EXAMPLE_ZOOKEEPER_SERVER,
                sessionTimeoutMs,
                connectionTimeoutMs,
                `ZKStringSerializer$`.`MODULE$`)
        val isSecureKafkaCluster = false
        val zkUtils = ZkUtils(zkClient, ZkConnection(Commons.EXAMPLE_ZOOKEEPER_SERVER), isSecureKafkaCluster)
        val partitions = 1
        val replication = 1

        // Add topic configuration here
        val topicConfig = Properties()
        if (!AdminUtils.topicExists(zkUtils, Commons.EXAMPLE_KAFKA_TOPIC)) {
            AdminUtils.createTopic(zkUtils, Commons.EXAMPLE_KAFKA_TOPIC, partitions, replication, topicConfig, RackAwareMode.`Safe$`.`MODULE$`)
            info("Topic ${Commons.EXAMPLE_KAFKA_TOPIC} created.")
        } else {
            info("Topic ${Commons.EXAMPLE_KAFKA_TOPIC} already exists.")
        }
        zkClient.close()
    }
