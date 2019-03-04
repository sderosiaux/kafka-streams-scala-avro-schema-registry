package com.example

import java.util.Properties

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.kstream.{JoinWindows, Printed}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

import scala.collection.JavaConverters._

object StockWithSales extends App {

    val config: Properties = {
        val p = new Properties()
        p.put(StreamsConfig.APPLICATION_ID_CONFIG, "super-app-" + Math.random())
        p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        p
    }

    val serdeConfig = Map(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> "http://localhost:8081").asJava
    implicit val stockSerde: Serde[Stock] = new SpecificAvroSerde[Stock]()
    implicit val saleSerde: Serde[Sale] = new SpecificAvroSerde[Sale]()
    stockSerde.configure(serdeConfig, false)
    saleSerde.configure(serdeConfig, false)

    val sb = new StreamsBuilder
    val stocks: KStream[String, Stock] = sb.stream[String, Stock]("stocks")
    val sales: KStream[String, Sale] = sb.stream[String, Sale]("sales")

    stocks.print(Printed.toSysOut[String, Stock])
    sales.print(Printed.toSysOut[String, Sale])

    val newStocks: KStream[String, Stock] = stocks.leftJoin(sales)((stock, sale) =>
        stock.copy(stock.id, Option(sale).map(sa => stock.quantity - sa.quantity).getOrElse(stock.quantity)),
        JoinWindows.of(java.time.Duration.ofSeconds(5))
    )

    newStocks.through("stocks-after-sales").print(Printed.toSysOut[String, Stock])

    val streams = new KafkaStreams(sb.build(), config)

    streams.cleanUp()
    streams.start()

    sys.ShutdownHookThread {
        streams.close(java.time.Duration.ofSeconds(5))
    }

    Thread.sleep(10000)

}
