package fr.xebia.gbildi.cicd

import cats.implicits._
import fr.xebia.gbildi.{TFInOperation, TFOutOperation}

import scala.sys.process._
import scala.util.{Failure, Success, Try}

/**
 * Created by loicmdivad.
 */
object SavedModelCli {

  def execute(modelPath: String): Try[String] =

    Try(s"saved_model_cli show --dir $modelPath --all" !!)

  def parseExecution(output: String): Unit = {

  }

  def parseOutput(execOutput: String): Try[TFOutOperation] = {
    val it = execOutput.linesIterator

    for(line <- it) {
      if ("^\\s*outputs\\['(.*)'\\] tensor_info:$".r.pattern.matcher(line).matches()) {
        val dtype = it.next()
        val shape = it.next()
        val name = it.next()

        val output: Try[TFOutOperation] = for {

          inType <- Try("dtype: (\\w*)".r.findAllIn(dtype).group(1))
          inName <- Try("name: (\\w*)".r.findAllIn(name).group(1))

        } yield TFOutOperation(inName, inType)

        return output

      }
    }

    Failure(new Exception("No output bloc was found"))
  }

  def parseInputs(execOutput: String): Try[List[TFInOperation]] = {

    val it = execOutput.linesIterator

    it.foldLeft(List.empty[Try[TFInOperation]])( (acc, line) => {

      if ("^\\s*inputs\\['(.*)'\\] tensor_info:$".r.pattern.matcher(line).matches()) {
        val dtype = it.next()
        val shape = it.next()
        val name = it.next()

        val input: Try[TFInOperation] = for {

          inType <- Try("dtype: (\\w*)".r.findAllIn(dtype).group(1))
          inName <- Try("name: (\\w*)".r.findAllIn(name).group(1))

        } yield TFInOperation(inName, inType)

        acc :+ input

      }  else acc

    })
      .sequence
      .flatMap(inputs => if(inputs.isEmpty) Failure(new Exception("No input bloc was found")) else Success(inputs))
  }

}
