package flink.elastic

import java.net.{InetAddress, InetSocketAddress}
import java.util

import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.elasticsearch2.{ElasticsearchSink, ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig
import org.apache.flink.streaming.util.serialization.SimpleStringSchema
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests
import org.apache.flink.streaming.api.scala._


/**
  * Created by I311352 on 3/30/2017.
  */
class SinkProc {

}

object SinkProc extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment

  val connectionConfig = new RMQConnectionConfig.Builder()
    .setHost("10.128.165.206")
    .setPort(5672)
    .setUserName("guest")
    .setPassword("guest")
    .setVirtualHost("/")
    .build()

  val stream = env
    .addSource(new RMQSource[String](
      connectionConfig,            // config for the RabbitMQ connection
      "ElasticRabbit",                 // name of the RabbitMQ queue to consume
      true,                        // use correlation ids; can be false if only at-least-once is required
      new SimpleStringSchema))     // deserialization schema to turn messages into Java objects
    .setParallelism(1)               // non-parallel source is only required for exactly-once

  val config = new java.util.HashMap[String, String]
  //config.put("cluster.name", "SAP-AnyWhere-ES-EShop")

  val transports = new util.ArrayList[InetSocketAddress]
  transports.add(new InetSocketAddress(InetAddress.getByName("localhost"), 9300))

  stream.addSink(new ElasticsearchSink[String](config, transports, new ElasticsearchSinkFunction[String]{
    def createIndexRequest(element: String): IndexRequest = {
      val json = new util.HashMap[String, AnyRef]
      json.put("data", element)
      Requests.indexRequest.index("my-index").`type`("my-type").source(json)
    }

    override def process(element: String, ctx: RuntimeContext, indexer: RequestIndexer) {
      indexer.add(createIndexRequest(element))
    }
  }));

  env.execute()
}