# kafka-streams-scala-avro-schema-registry

From 2 avro schemas (Sale.avsc and Stock.avsc), the compilation generates the `case class` which inherits `SpecificRecord` to be handled by the Kafka Avro Serde.

This project depends upon:

```
"io.confluent" % "kafka-avro-serializer" % "5.1.2"
"io.confluent" % "kafka-streams-avro-serde" % "5.1.2"
```


There is a typical producer: `StockAndSaleProducer` to generate some sales and stocks.

There is a Kafka Streams: `StockWithSales` which is a sample Kafka Streams Scala implementation, using Kafka Avro Serdes.