object Commons {
    val EXAMPLE_KAFKA_TOPIC = if (System.getenv("EXAMPLE_KAFKA_TOPIC") != null) System.getenv("EXAMPLE_KAFKA_TOPIC") else "example"
    val EXAMPLE_KAFKA_SERVER = if (System.getenv("EXAMPLE_KAFKA_SERVER") != null) System.getenv("EXAMPLE_KAFKA_SERVER") else "localhost:9092"
    val EXAMPLE_ZOOKEEPER_SERVER = if (System.getenv("EXAMPLE_ZOOKEEPER_SERVER") != null) System.getenv("EXAMPLE_ZOOKEEPER_SERVER") else "localhost:2181"
}