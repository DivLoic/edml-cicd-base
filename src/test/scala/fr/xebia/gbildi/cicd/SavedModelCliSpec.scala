package fr.xebia.gbildi.cicd

import fr.xebia.gbildi.{TFInOperation, TFOutOperation}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Try}

/**
 * Created by loicmdivad.
 */
class SavedModelCliSpec extends AnyFlatSpec with Matchers {

  "execute" should "trigger the saved_model_cli command" in {
    val path = Thread.currentThread().getContextClassLoader.getResource("../resources/model_v0").getPath
    val result: Try[String] = SavedModelCli.execute(path)

    result.get.stripMargin shouldBe
      """
        |MetaGraphDef with tag-set: 'serve' contains the following SignatureDefs:
        |
        |signature_def['predict']:
        |  The given SavedModel SignatureDef contains the following input(s):
        |    inputs['dayofweek'] tensor_info:
        |        dtype: DT_INT64
        |        shape: (-1)
        |        name: dayofweek:0
        |    inputs['dropoff_zone_name'] tensor_info:
        |        dtype: DT_STRING
        |        shape: (-1)
        |        name: dropoff_zone_name:0
        |    inputs['hourofday'] tensor_info:
        |        dtype: DT_INT64
        |        shape: (-1)
        |        name: hourofday:0
        |    inputs['passenger_count'] tensor_info:
        |        dtype: DT_FLOAT
        |        shape: (-1)
        |        name: passenger_count:0
        |    inputs['pickup_zone_name'] tensor_info:
        |        dtype: DT_STRING
        |        shape: (-1)
        |        name: pickup_zone_name:0
        |    inputs['uuid'] tensor_info:
        |        dtype: DT_STRING
        |        shape: (-1)
        |        name: uuid:0
        |  The given SavedModel SignatureDef contains the following output(s):
        |    outputs['predictions'] tensor_info:
        |        dtype: DT_FLOAT
        |        shape: (-1, 1)
        |        name: add:0
        |  Method name is: tensorflow/serving/predict
        |""".stripMargin
  }

  "parseInputs" should "extract input operations from the output execution" in {
    val givenInputName1 = "SuperCoolInput1"
    val givenInputType1 = "DT_FLOAT"
    val givenInputName2 = "SuperCoolInput2"
    val givenInputType2 = "DT_INT64"

    val givenSavedModel =
      s"""
         |MetaGraphDef with tag-set: 'serve' contains the following SignatureDefs:
         |
         |signature_def['predict']:
         |  The given SavedModel SignatureDef contains the following input(s):
         |    inputs['pickup_zone_name'] tensor_info:
         |        dtype: $givenInputType1
         |        shape: (-1)
         |        name: $givenInputName1:0
         |    inputs['uuid'] tensor_info:
         |        dtype: $givenInputType2
         |        shape: (-1)
         |        name: $givenInputName2:0
         |  The given SavedModel SignatureDef contains the following output(s):
         |    outputs['predictions'] tensor_info:
         |        dtype: DT_FLOAT
         |        shape: (-1, 1)
         |        name: add:0
         |  Method name is: tensorflow/serving/predict
         |""".stripMargin

    val result = SavedModelCli.parseInputs(givenSavedModel)

    result.get.head shouldBe TFInOperation("SuperCoolInput1", "DT_FLOAT")
    result.get(1) shouldBe TFInOperation("SuperCoolInput2", "DT_INT64")
  }

  it should "fail to extract inputs if no inputs blocs are present" in {
    val givenSavedModel =
      s"""
         |MetaGraphDef with tag-set: 'serve' contains the following SignatureDefs:
         |
         |signature_def['predict']:
         |  The given SavedModel SignatureDef contains the following input(s):
         |  The given SavedModel SignatureDef contains the following output(s):
         |    outputs['predictions'] tensor_info:
         |        dtype: DT_FLOAT
         |        shape: (-1, 1)
         |        name: add:0
         |  Method name is: tensorflow/serving/predict
         |""".stripMargin

    val result = SavedModelCli.parseInputs(givenSavedModel)

    result shouldBe a[Failure[Exception]]
  }

  "parseOutput" should "extract the 1st output operation from the output execution" in {
    val givenOutputName = "AwesomeName"
    val givenOutputType = "DT_FLOAT"
    val givenSavedModel =
      s"""
        |MetaGraphDef with tag-set: 'serve' contains the following SignatureDefs:
        |
        |signature_def['predict']:
        |  The given SavedModel SignatureDef contains the following input(s):
        |    inputs['dayofweek'] tensor_info:
        |        dtype: DT_INT64
        |        shape: (-1)
        |        name: dayofweek:0
        |  The given SavedModel SignatureDef contains the following output(s):
        |    outputs['predictions'] tensor_info:
        |        dtype: $givenOutputType
        |        shape: (-1, 1)
        |        name: $givenOutputName:0
        |  Method name is: tensorflow/serving/predict
        |""".stripMargin

    val result: Try[TFOutOperation] = SavedModelCli.parseOutput(givenSavedModel)

    result.get shouldBe TFOutOperation("AwesomeName", "DT_FLOAT")
  }

  it should "fail to extract output if no outputs bloc is present" in {
    val givenSavedModel =
      s"""
         |MetaGraphDef with tag-set: 'serve' contains the following SignatureDefs:
         |
         |signature_def['predict']:
         |  The given SavedModel SignatureDef contains the following input(s):
         |    inputs['dayofweek'] tensor_info:
         |        dtype: DT_INT64
         |        shape: (-1)
         |        name: dayofweek:0
         |""".stripMargin

    val result: Try[TFOutOperation] = SavedModelCli.parseOutput(givenSavedModel)

    result shouldBe a[Failure[Exception]]
  }
}
