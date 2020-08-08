import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaInputDStream
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.apache.spark.streaming.kafka010.ConsumerStrategies
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies
import scala.Tuple2
import java.util.*


fun main() {
    // Configure Spark to connect to Kafka running on local machine
    val kafkaParams: MutableMap<String, Any> =
        HashMap()
    kafkaParams[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = Commons.EXAMPLE_KAFKA_SERVER
    kafkaParams[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
    kafkaParams[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
    kafkaParams[ConsumerConfig.GROUP_ID_CONFIG] = "SparkConsumerGroup"

    //Configure Spark to listen messages in topic test
    val topics: Collection<String> = listOf(Commons.EXAMPLE_KAFKA_TOPIC)
    val conf: SparkConf = SparkConf().setMaster("local[2]").setAppName("SparkConsumerApplication")

    //Read messages in batch of 30 seconds
    val jssc = JavaStreamingContext(conf, Durations.seconds(5))

    // Start reading messages from Kafka and get DStream
    val stream: JavaInputDStream<ConsumerRecord<String, String>> = KafkaUtils.createDirectStream(
        jssc, LocationStrategies.PreferConsistent(),
        ConsumerStrategies.Subscribe(topics, kafkaParams)
    )

    // Read value of each message from Kafka and return it

    // Read value of each message from Kafka and return it
    val lines = stream.map { kafkaRecord -> kafkaRecord.value() }

    // Break every message into words and return list of words
    val words = lines.flatMap { line -> listOf(*line.split(" ").toTypedArray()).iterator() }

    // Take every word and return Tuple with (word,1)
    val wordMap = words.mapToPair { word -> Tuple2(word, 1) }

    // Count occurrence of each word
    val wordCount= wordMap?.reduceByKey{ first, second -> first + second }

    //Print the word count
    wordCount?.print()
    jssc.start()
    try {
        jssc.awaitTermination()
    } catch (e: InterruptedException) {
        e.stackTrace
        error("An error occurred.")
    }
}
