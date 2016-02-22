package clustering

//spark core
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
//vector/matrix API
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.{Matrix, Matrices}
import org.apache.spark.mllib.linalg.distributed.{IndexedRow, IndexedRowMatrix, RowMatrix}
import org.apache.spark.mllib.linalg.distributed.{CoordinateMatrix, MatrixEntry, BlockMatrix}
//utils for loading from a file
import org.apache.spark.mllib.util.MLUtils


/**
 * Created by m102417 on 2/19/16.
 *
 * A key part of machine learning on spark is:
 * org.apache.spark.mllib.linalg
 *
 * This class gives some examples of using this library
 *
 * http://spark.apache.org/docs/latest/mllib-data-types.html
 *
 */
object VectorExamples {

  def main(args: Array[String]) {
    //need a spark context
    val conf = new SparkConf().setMaster("local").setAppName("Vector Examples")
    val sc = new SparkContext(conf)

    //MLlib supports local vectors and matrices stored on a single machine, as well as distributed matrices
    // backed by one or more RDDs. Local vectors and local matrices are simple data models that serve as public interfaces.
    // The underlying linear algebra operations are provided by Breeze and jblas.
    // A training example used in supervised learning is called a “labeled point” in MLlib.

    // ********************************************
    println("Local Vectors")
    // ********************************************
    //A local vector has integer-typed and 0-based indices and double-typed values, stored on a single machine.
    // MLlib supports two types of local vectors: dense and sparse.
    // A dense vector is backed by a double array representing its entry values,
    // while a sparse vector is backed by two parallel arrays: indices and values.
    // For example,
    //      a vector (1.0, 0.0, 3.0) can be represented in dense format as [1.0, 0.0, 3.0] or,
    //      in sparse format as (3, [0, 2], [1.0, 3.0]), where 3 is the size of the vector.
    // Create a dense vector (1.0, 0.0, 3.0).
    val dv: Vector = Vectors.dense(1.0, 0.0, 3.0)
    println(dv)
    // Create a sparse vector (1.0, 0.0, 3.0) by specifying its indices and values corresponding to nonzero entries.
    val sv1: Vector = Vectors.sparse(3, Array(0, 2), Array(1.0, 3.0))
    println(sv1)
    // Create a sparse vector (1.0, 0.0, 3.0) by specifying its nonzero entries.
    val sv2: Vector = Vectors.sparse(3, Seq((0, 1.0), (2, 3.0)))
    println(sv2)

    // ********************************************
    println("Labeled Point")
    // ********************************************
    // A labeled point is a local vector, either dense or sparse, associated with a label/response.
    // In MLlib, labeled points are used in supervised learning algorithms.
    // We use a double to store a label, so we can use labeled points in both regression and classification.
    // For binary classification, a label should be either 0 (negative) or 1 (positive).
    // For multiclass classification, labels should be class indices starting from zero
    // Create a labeled point with a positive label and a dense feature vector.
    val pos = LabeledPoint(1.0, Vectors.dense(1.0, 0.0, 3.0))
    println(pos)
    // Create a labeled point with a negative label and a sparse feature vector.
    val neg = LabeledPoint(0.0, Vectors.sparse(3, Array(0, 2), Array(1.0, 3.0)))
    println(neg)

    // ********************************************
    println("Loading Data")
    // ********************************************
    val libsvmfile = "../TechnicalReplicatesVCFSVM081015/vcf_train"; //make sure you run the setup in this directory to get the file you need!
    val examples: RDD[LabeledPoint] = MLUtils.loadLibSVMFile(sc, libsvmfile)
    println(examples.first())

    // ********************************************
    // ********************************************
    println("Matrix Types...")
    // ********************************************
    // ********************************************


    // ********************************************
    println("Local Matrix")
    // ********************************************
    // A local matrix has integer-typed row and column indices and double-typed values, stored on a single machine.
    // MLlib supports dense matrices, whose entry values are stored in a single double array in column-major order,
    // and sparse matrices, whose non-zero entry values are stored in the Compressed Sparse Column (CSC) format in column-major order.
    // For example, the following dense matrix is stored in a one-dimensional array:
    // [1.0, 3.0, 5.0, 2.0, 4.0, 6.0]
    // with the matrix size (3, 2).
    //
    // The base class of local matrices is Matrix, and we provide two implementations:
    // DenseMatrix, and SparseMatrix. We recommend using the factory methods implemented in Matrices to create local matrices.
    // Remember, local matrices in MLlib are stored in column-major order. -i.e. in memory consecutive elements of the columns are contiguous.

    // Create a dense matrix ((1.0, 2.0), (3.0, 4.0), (5.0, 6.0))
    val dm: Matrix = Matrices.dense(3, 2, Array(1.0, 3.0, 5.0, 2.0, 4.0, 6.0))

    // Create a sparse matrix ((9.0, 0.0), (0.0, 8.0), (0.0, 6.0))
    val sm: Matrix = Matrices.sparse(3, 2, Array(0, 1, 3), Array(0, 2, 1), Array(9, 6, 8))

    // ********************************************
    // ********************************************
    println("Distributed Matrix Types...")
    // ********************************************
    // ********************************************
    // A distributed matrix has long-typed row and column indices and double-typed values,
    // stored distributively in one or more RDDs.
    // It is very important to choose the right format to store large and distributed matrices.
    // Converting a distributed matrix to a different format may require a global shuffle, which is quite expensive.
    // Three types of distributed matrices have been implemented so far.  They are as follows:
    // ********************************************
    println("Distributed Matrix")
    println("   Row Matrix")
    // ********************************************
    // A RowMatrix is a row-oriented distributed matrix without meaningful row indices,
    // e.g., a collection of feature vectors. It is backed by an RDD of its rows, where each row is a local vector.
    // We assume that the number of columns is not huge for a RowMatrix
    // so that a single local vector can be reasonably communicated to the driver and can also be
    // stored / operated on using a single node.

    val rows: RDD[Vector] = examples.map(x => x.features) //map the labeled points to a vector RDD
    // Create a RowMatrix from an RDD[Vector].
    val mat: RowMatrix = new RowMatrix(rows)

    // Get its size.
    val m = mat.numRows()
    val n = mat.numCols()
    println("m="+m + " n="+n)

    // QR decomposition
    val qrResult = mat.tallSkinnyQR(true)
    println(qrResult)

    // ********************************************
    println("Distributed Matrix")
    println("   Indexed Row Matrix")
    // ********************************************
    // An IndexedRowMatrix is similar to a RowMatrix but with meaningful row indices.
    // It is backed by an RDD of indexed rows, so that each row is represented by its index (long-typed) and a local vector.
    // An IndexedRowMatrix can be created from an RDD[IndexedRow] instance, where IndexedRow is a wrapper over (Long, Vector).
    // An IndexedRowMatrix can be converted to a RowMatrix by dropping its row indices.
    val rdd :RDD[IndexedRow] = examples.map(x => x.features).zipWithIndex().map(x => IndexedRow(x._2,x._1))
    println(rdd.first())

    //quick note on the above conversion and indexed RDDs, which is confusing...
    //Given: rdd = (a,b,c)
    //val withIndex = rdd.zipWithIndex // ((a,0),(b,1),(c,2))
    //To lookup an element by index, this form is not useful. First we need to use the index as key:
    //val indexKey = withIndex.map{case (k,v) => (v,k)}  //((0,a),(1,b),(2,c))
    //Now, it's possible to use the 'lookup' action in PairRDD to find an element by key:
    //val b = indexKey.lookup(1) // Array(b)
    //If you're expecting to use lookup often on the same RDD, I'd recommend to cache the indexKey RDD to improve performance.

    // Create an IndexedRowMatrix from an RDD[IndexedRow].
    val mat2: IndexedRowMatrix = new IndexedRowMatrix(rdd)

    // Get its size.
    val m2 = mat2.numRows()
    val n2 = mat2.numCols()
//
    // Drop its row indices.
    val rowMat: RowMatrix = mat2.toRowMatrix()

    // ********************************************
    println("Distributed Matrix")
    println("   CoordinateMatrix")
    // ********************************************
    // A CoordinateMatrix is a distributed matrix backed by an RDD of its entries.
    // Each entry is a tuple of (i: Long, j: Long, value: Double), where i is the row index, j is the column index,
    // and value is the entry value. A CoordinateMatrix should be used only when both dimensions of the matrix are huge
    // and the matrix is very sparse.
    // great example:
    // http://stackoverflow.com/questions/33558755/matrix-multiplication-in-apache-spark
    val entries = sc.parallelize(Seq(
      (0, 0, 3.0), (2, 0, -5.0), (3, 2, 1.0),
      (4, 1, 6.0), (6, 2, 2.0), (8, 1, 4.0))
    ).map{case (i, j, v) => MatrixEntry(i, j, v)}

    val coordinateMatrix = new CoordinateMatrix(entries, 9, 3)
    // Create a CoordinateMatrix from an RDD[MatrixEntry].
    val m3: CoordinateMatrix = new CoordinateMatrix(entries)
    println(m3.entries.first())

    // Get its size.
    val mr = m3.numRows()
    val nr = m3.numCols()

    // Convert it to an IndexRowMatrix whose rows are sparse vectors.
    val indexedRowMatrix = m3.toIndexedRowMatrix()
    val p = indexedRowMatrix.rows;
    println(p.first());

    // ********************************************
    println("Distributed Matrix")
    println("   BlockMatrix")
    // ********************************************
    //A BlockMatrix is a distributed matrix backed by an RDD of MatrixBlocks,
    // where a MatrixBlock is a tuple of ((Int, Int), Matrix),
    //   where the (Int, Int) is the index of the block, and
    //   Matrix is the sub-matrix at the given index with size rowsPerBlock x colsPerBlock.
    // BlockMatrix supports methods such as add and multiply with another BlockMatrix.
    // BlockMatrix also has a helper function validate which can be used to check whether the BlockMatrix is set up properly.

    // A BlockMatrix can be most easily created from an IndexedRowMatrix or CoordinateMatrix by calling: toBlockMatrix.
    // toBlockMatrix creates blocks of size 1024 x 1024 by default.
    // Users may change the block size by supplying the values through toBlockMatrix(rowsPerBlock, colsPerBlock).

    // Transform the CoordinateMatrix to a BlockMatrix
    val matA: BlockMatrix = m3.toBlockMatrix().cache()

    // Validate whether the BlockMatrix is set up properly. Throws an Exception when it is not valid.
    // Nothing happens if it is valid.
    matA.validate()

    // Calculate A^T A.
    val ata = matA.transpose.multiply(matA)
    println(ata.blocks.first())

    println("Finished")
  }

}
