import org.apache.flink.api.common.functions.FlatMapFunction
import org.apache.flink.api.java.DataSet
import org.apache.flink.api.java.ExecutionEnvironment
import org.apache.flink.api.java.tuple.Tuple2
import org.apache.flink.util.Collector


    fun main() { // set up the batch execution environment
        val env = ExecutionEnvironment.getExecutionEnvironment()
        val text: DataSet<String> = env.fromElements(
                "Who's there?",
                "I think I hear them. Stand, ho! Who's there?")
        val wordCounts: DataSet<Tuple2<String, Int>> = text
                .flatMap<Tuple2<String, Int>>(LineSplitter())
                .groupBy(0)
                .sum(1)
        wordCounts.print()
    }

    class LineSplitter : FlatMapFunction<String, Tuple2<String, Int>?> {
        override fun flatMap(line: String, out: Collector<Tuple2<String, Int>?>) {
            for (word in line.split(" ").toTypedArray()) {
                out.collect(Tuple2(word, 1))
            }
        }
    }
