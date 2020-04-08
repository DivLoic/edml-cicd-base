package fr.xebia.gbildi.cicd

import java.time.Duration

import com.spotify.zoltar.Model
import com.spotify.zoltar.tf.{TensorFlowLoader, TensorFlowModel}
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.JavaConverters._

/**
 * Created by loicmdivad.
 */
class SavedModelCliSpec extends AnyFlatSpec with Matchers with GivenWhenThen {

  val modelPath: String = Thread.currentThread().getContextClassLoader.getResource("model_v0").getPath

  val modelOptions: TensorFlowModel.Options = TensorFlowModel.Options.builder().tags("serve" :: Nil asJava).build()

  "extractGraph" should "extract the tensorflow graph definition from the .pb files" in {

    Given("a model serialized in protobuf")
    val loader = TensorFlowLoader.create(Model.Id.create("model_v0"), modelPath, modelOptions, "predict")
    val model = loader.get(Duration.ofSeconds(20))

    When("the model is parsed by the function extractGraph")
    val result = SavedModelCli.extractGraph(model, "./arbitrary/model/path/")

    Then("a TFSavedModel is returned and contains the models informations")

    result.version shouldBe "model_v0"
    result.inputs should have length 6
    result.inputs.find(_.name == "pickup_zone_name").get.`type` shouldBe "DT_STRING"
    result.inputs.find(_.name == "dropoff_zone_name").get.`type` shouldBe "DT_STRING"
    result.inputs.find(_.name == "passenger_count").get.`type` shouldBe "DT_FLOAT"
    result.inputs.find(_.name == "dayofweek").get.`type` shouldBe "DT_INT64"
    result.inputs.find(_.name == "hourofday").get.`type` shouldBe "DT_INT64"
    result.inputs.find(_.name == "uuid").get.`type` shouldBe "DT_STRING"

    result.gcs_path shouldBe "./arbitrary/model/path/"
    result.output.name shouldBe "predictions"
    result.output.`type` shouldBe "DT_FLOAT"
  }
}
