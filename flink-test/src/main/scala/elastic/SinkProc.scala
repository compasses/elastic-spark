package elastic

import java.net.{InetAddress, InetSocketAddress}

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig
import org.apache.flink.streaming.util.serialization.SimpleStringSchema
import java.util

import scala.collection.mutable

/**
  * Created by I311352 on 3/30/2017.
  */
object SinkProc extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  // checkpointing is required for exactly-once or at-least-once guarantees
  //env.enableCheckpointing(...)

  val connectionConfig = new RMQConnectionConfig.Builder()
    .setHost("10.128.165.206")
    .setPort(5672)
    .setUserName("guest")
    .setPassword("guest")
    .build()

  val stream = env
    .addSource(new RMQSource[String](
      connectionConfig,            // config for the RabbitMQ connection
      "ElasticRabbit",                 // name of the RabbitMQ queue to consume
      true,                        // use correlation ids; can be false if only at-least-once is required
      new SimpleStringSchema))     // deserialization schema to turn messages into Java objects
    .setParallelism(1)               // non-parallel source is only required for exactly-once

  val config = new util.HashMap[String, String]
  config.put("cluster.name", "SAP-AnyWhere-ES-EShop")

  val transports = new util.ArrayList[String]
  transports.add(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9300))
  transports.add(new InetSocketAddress(InetAddress.getByName("10.2.3.1"), 9300));

  //stream.addSink()
}
