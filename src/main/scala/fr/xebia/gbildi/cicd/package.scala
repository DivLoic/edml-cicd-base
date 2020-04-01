package fr.xebia.gbildi

import java.util.Properties

import com.typesafe.config.Config

import scala.collection.JavaConverters._

/**
 * Created by loicmdivad.
 */
package object cicd {

  implicit class configOps(config: Config) {

    def toMap: Map[String, AnyRef] = config
      .entrySet()
      .asScala
      .map(pair => (pair.getKey, config.getAnyRef(pair.getKey)))
      .toMap

    def toProps: Properties = {
      val properties = new Properties()
      properties.putAll(config.toMap.asJava)
      properties
    }
  }

  implicit class propertiesOps(map: Map[String, AnyRef]) {
    def toProps: Properties = {
      val properties = new Properties()
      properties.putAll(map.asJava)
      properties
    }
  }

}
