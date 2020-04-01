package fr.xebia.gbildi.cicd

import com.spotify.zoltar.tf.TensorFlowModel
import fr.xebia.gbildi.{TFInOperation, TFOutOperation, TFSavedModel}

import scala.collection.JavaConverters._

/**
 * Created by loicmdivad.
 */
object SavedModelCli {

  def extractGraph(model: TensorFlowModel, path: String): TFSavedModel = {
    val signature = model.signatureDefinition()

    val inputInfo: Seq[TFInOperation] = signature.getInputsMap.asScala.toSeq.map { case (key, info) =>
      TFInOperation(key, info.getDtype.toString)
    }

    val outputInfo: TFOutOperation = signature.getOutputsMap.asScala.toSeq.map { case (key, info) =>
      TFOutOperation(key, info.getDtype.toString)
    }.head

    TFSavedModel(model.id().value(), path, inputInfo, outputInfo)
  }
}
