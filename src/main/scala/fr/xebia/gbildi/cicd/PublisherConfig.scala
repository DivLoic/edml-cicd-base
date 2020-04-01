package fr.xebia.gbildi.cicd


import com.typesafe.config.Config
import fr.xebia.gbildi.cicd.PublisherConfig.TensorflowConfig
import fr.xebia.gbildi.cicd.PublisherConfig.TensorflowConfig.Model

import scala.concurrent.duration.FiniteDuration

/**
 * Created by loicmdivad.
 */
case class PublisherConfig(modelTopic: String, kafkaClient: Config, tensorflowClient: TensorflowConfig)

object PublisherConfig {

  case class TensorflowConfig(model: Model, tags: List[String], loadingTimeout: FiniteDuration)

  object TensorflowConfig {
    case class Model(name: String, version: String, path: String)
  }
}
