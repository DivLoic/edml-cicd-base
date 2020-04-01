package fr.xebia.gbildi.cicd

import java.time.Duration

import com.spotify.zoltar.Model
import com.spotify.zoltar.tf.{TensorFlowLoader, TensorFlowModel}
import fr.xebia.gbildi.{ModelKey, TFSavedModel}
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import org.slf4j.LoggerFactory
import org.tensorflow.TensorFlow
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.collection.JavaConverters._

/**
 * Created by loicmdivad.
 */
object ModelPublisher extends App {

  val logger = LoggerFactory.getLogger(getClass)

  ConfigSource.default.load[PublisherConfig].map { publisherConfig =>

    logger info s"TensorFlow version ${TensorFlow.version()}"

    val modelTags = publisherConfig.tensorflowClient.tags.asJava
    val modelPath = publisherConfig.tensorflowClient.model.path
    val modelOpts = TensorFlowModel.Options.builder().tags(modelTags).build()
    val modelId = Model.Id.create(publisherConfig.tensorflowClient.model.version)

    val loader: TensorFlowLoader = TensorFlowLoader.create(modelId, modelPath, modelOpts, "predict")

    val model: TensorFlowModel = loader.get(Duration.ofSeconds(10))

    logger info s"Model version: ${publisherConfig.tensorflowClient.model.version} is loaded!"

    val mapConfig = publisherConfig.kafkaClient.toMap
    val properties = publisherConfig.kafkaClient.toProps

    val keyModelSerializer = new SpecificAvroSerializer[ModelKey]()
    val valueModelSerializer = new SpecificAvroSerializer[TFSavedModel]()

    valueModelSerializer.configure(mapConfig.asJava, false)
    keyModelSerializer.configure(mapConfig.asJava, true)

    val producer = new KafkaProducer[ModelKey, TFSavedModel](properties, keyModelSerializer, valueModelSerializer)

    logger info s"Parsing model description for ${publisherConfig.tensorflowClient.model.version}"

    val key = ModelKey(publisherConfig.tensorflowClient.model.name)
    val value: TFSavedModel = SavedModelCli.extractGraph(model, modelPath)

    val record = new ProducerRecord(publisherConfig.modelTopic, key, value)

    logger info s"Producer Record publication in ${publisherConfig.modelTopic} topic"
    producer.send(record, new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit =
        Option(exception).map { _ =>
          logger error("Fail to produce the model!", exception)
          exception.printStackTrace()
          sys.exit(1)
        }.getOrElse {
          logger info "Successfully produce the model:"
          logger info
            s""" Metadata:
               | topic: ${metadata.topic}
               | partition: ${metadata.partition}
               | offset: ${metadata.offset}
               | timestamp: ${metadata.timestamp} """.stripMargin.replace("\n", "")
          metadata.toString
        }
    })

    producer.flush()
    producer.close()

  }.left.map { failures =>
    failures.toList.foreach(failure => logger.error(s"Fail to parse configuration: ${failure.description}"))
    sys.exit(1)
  }
}

