import org.apache.flink.api.common.functions.FlatMapFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.java.tuple.Tuple2
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.util.Collector
import org.apache.kafka.clients.consumer.ConsumerConfig
import java.util.*


    fun main() {
        // Create execution environment
        val env = StreamExecutionEnvironment.getExecutionEnvironment()

        // Properties
        val props = Properties()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = Commons.EXAMPLE_KAFKA_SERVER
        props[ConsumerConfig.GROUP_ID_CONFIG] = "FlinkConsumerGroup"
        val messageStream: DataStream<String> = env.addSource(FlinkKafkaConsumer(Commons.EXAMPLE_KAFKA_TOPIC, SimpleStringSchema(), props))


        // Split up the lines in pairs (2-tuples) containing: (word,1)
        messageStream.flatMap<Tuple2<String, Int>>(Tokenizer()) // group by the tuple field "0" and sum up tuple field "1"
                .keyBy(0)
                .sum(1)
                .print()
        try {
            env.execute()
        } catch (e: Exception) {
            e.stackTrace
            error("An error occurred.")
        }
    }

    class Tokenizer : FlatMapFunction<String, Tuple2<String, Int>?> {
        override fun flatMap(value: String, out: Collector<Tuple2<String, Int>?>) {
            // normalize and split the line
            val tokens = value.toLowerCase().split("\\W+").toTypedArray()

            // emit the pairs
            for (token in tokens) {
                if (token.isNotEmpty()) {
                    out.collect(Tuple2(token, 1))
                }
            }
        }
    }
