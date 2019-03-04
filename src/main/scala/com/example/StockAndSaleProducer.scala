package com.example

import java.util.Properties

import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroSerializer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import scala.util.Random

object StockAndSaleProducer extends App {
    val props = {
        val p = new Properties()
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer])
        p.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "10000")
        p.put(ProducerConfig.LINGER_MS_CONFIG, "100")
        p.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000")
        p.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "10000")
        p.put(ProducerConfig.RETRIES_CONFIG, "0")
        p.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081")
        p
    }

    val stockProducer = new KafkaProducer[String, Stock](props)
    val saleProducer = new KafkaProducer[String, Sale](props)

    (1 to 10)
        .map(i => Stock("" + i, Random.nextInt(10) + 1))
        .foreach(stock => stockProducer.send(new ProducerRecord("stocks", stock.id, stock)))
    (1 to 10)
        .map(i => Sale("" + i, Random.nextInt(3) + 1))
        .foreach(sale => saleProducer.send(new ProducerRecord("sales", sale.id, sale)))

    stockProducer.close()
    saleProducer.close()

    println("Stocks and sales sent")
}
