import java.io.Serializable
import java.util
import java.util.Random

import org.apache.spark.{SparkContext, SparkConf}

/**
 * A very simple (and probably not 100% correct) neural network implementation.
 *
 * The goal is to allow for distributed training via Spark (if that is possible)...
 *
 * There are two included scenarios (see below...), sin() and XOR.
 *
 * @param inputs # if input values
 * @param layersDef neurons in the layers
 */
class NN(inputs: Int, layersDef: Array[Int]) extends Serializable {

  val random: Random = new Random()

  val layers: Seq[NNLayer] = {
    var inputNum = inputs

    layersDef.foldLeft(Seq[NNLayer]())((a,b) => {
      val returnVal = a :+ new NNLayer(inputNum, b, random)

      inputNum = b

      returnVal
    })
  }

  /**
   * Train given a training set and expected results.
   *
   * @param input training data
   * @param expected expected results
   * @param learningRate
   * @return returns the actual output array, as well as the error array
   */
  def train(input: Array[Float], expected: Array[Float], learningRate: Float) = {
    val output = run(input)

    val outputError = output.zip(expected).foldLeft(Seq[Float]())((err, t) => err :+ (t._1 - t._2))

    layers.foldRight(outputError)((layer, err) => layer.backprop(err, learningRate))

    (output, outputError)
  }

  /**
   * Run input through the (hopefully trained) network and return the results.
   *
   * @param input
   * @return
   */
  def run(input: Array[Float]): Seq[Float] = {
    layers.foldLeft(input)((in, layer) => layer.run(in))
  }

}

/**
 * A Layer on the neural network.
 *
 * @param _inputs
 * @param _outputs
 * @param random
 */
class NNLayer(val _inputs: Int, val _outputs: Int, random: Random) extends Serializable {

  var inputs: Array[Float] = Array.fill(_inputs + 1)(0f)
  var outputs: Array[Float] = Array.fill(_outputs)(0f)
  var weights: Array[Array[Float]] = {
    Array.fill(inputs.size)(
      Array.fill(outputs.size)(random.nextFloat()))
  }

  /**
   * Backpropogate and update weights.
   *
   * @param error
   * @param learningRate
   * @return
   */
  def backprop(error: Seq[Float], learningRate: Float): Seq[Float] = {

    val inputError = Array.fill(inputs.size)(0f)

    for (i <- 0 until outputs.size) {
      val err = error(i)
      val out = outputs(i)

      val d = err * out * (1 - out)

      for (j <- 0 until inputs.size) {
        inputError(j) = weights(j)(i) * d

        val dw = -inputs(j) * d * learningRate

        weights(j)(i) += dw
      }
    }

    inputError
  }
  
  /**
   * Run the layer with the given input.
   * @param input
   * @return
   */
  def run(input: Array[Float]): Array[Float] = {
    outputs = Array.fill(outputs.size)(0f)

    inputs = input :+ 1f

    for (j <- 0 until outputs.size) {
      var activation  = 0f
      for (i <- 0 until inputs.size) {
        activation += weights(i)(j) * inputs(i)
      }

      outputs(j) = sigmoid(activation)
    }

    outputs
  }

  def sigmoid(x: Float): Float = {
    (1.0 / (1.0 + math.pow(math.E, -x))).toFloat
  }

}

/**
 * Launcher for a few test cases.
 *
 * Each test case can either be ran via Spark or not.
 *
 * Currently with Spark enabled the error is high, I'm not sure
 * how distributed training could work yet...
 */
object app {

  // set up Spark context
  val conf = new SparkConf().setMaster("local").setAppName("My App")
  val sc = new SparkContext(conf)

  /**
   * Run a scenario.
   *
   * @param args
   */
  def main(args: Array[String]) {
    time(doRunSin)
    //time(doRunXOR)
  }

  /**
   * Simulation to learn the sin() function.
   */
  def doRunSin {
    val trainingN = 500
    val random = new Random()

    val train = Array.fill(trainingN)(Array(random.nextFloat()))
    val res = train.map(x => Array(math.sin(x(0)).toFloat))

    val mlp = new NN(1, Array(3, 1))

    // # of epochs
    val en = 100

    for (e <- 0 until en) {
      val trainingSets_ = train.zip(res)

      // Spark
      //val trainingSets = sc.parallelize(trainingSets_, 2)

      // Non-Spark
      val trainingSets = trainingSets_

      printf("%d epoch\n", e + 1)

      trainingSets.foreach((trainingSet) => {

        println(util.Arrays.toString(trainingSet._1))

        val error = mlp.train(trainingSet._1, trainingSet._2, .1f)

        printf("Given %.3f, Expected %.3f, Got %.3f, Error %.3f\n",
          trainingSet._1(0),
          trainingSet._2(0),
          error._1(0),
          error._2(0))

        println()
      })
    }
  }

  /**
   * Simulation to learn XOR.
   */
  def doRunXOR {
    val train = Array(
      Array[Float](0, 0),
      Array[Float](0, 1),
      Array[Float](1, 0),
      Array[Float](1, 1))

    val res = Array(Array[Float](0), Array[Float](1), Array[Float](1), Array[Float](0))
    val mlp = new NN(2, Array(3, 1))

    // # of epochs
    val en = 5000

    for (e <- 0 until en) {
      val trainingSets_ = train.zip(res)

      // Spark
      //val trainingSets = sc.parallelize(trainingSets_, 2)

      // Non-Spark
      val trainingSets = trainingSets_

      printf("%d epoch\n", e + 1)

      trainingSets.foreach((trainingSet) => {

        val error = mlp.train(trainingSet._1, trainingSet._2, .25f)

        printf("Given (%.3f, %.3f), Expected %.3f, Got %.3f, Error %.3f\n",
          trainingSet._1(0),
          trainingSet._1(1),
          trainingSet._2(0),
          error._1(0),
          error._2(0))

        println()
      })
    }
  }

  def time[A](a: => A) = {
    val now = System.currentTimeMillis
    val result = a
    val millis = (System.currentTimeMillis - now)
    println("%d milliseconds".format(millis))
    result
  }
}