package math

/**
  * Mat3 utility functions.
  */
object Mat3 {
/**
  * Generate 3x3 zero matrix.
  */
  def apply(): Mat3 = {
    Mat3(0, 0, 0, 0, 0, 0, 0, 0, 0)
  }

/**
  * Generate 3x3 identity matrix.
  */
  val identity = {
    Mat3(
      1, 0, 0,
      0, 1, 0,
      0, 0, 1
    )
  }
}

/**
  * 3x3 matrix class
  */
case class Mat3(
    x0: Float, y0: Float, z0: Float,
    x1: Float, y1: Float, z1: Float,
    x2: Float, y2: Float, z2: Float
  ) {
  private val storage = Array[Vec3](
    Vec3(x0, x1, x2),
    Vec3(y0, y1, y2),
    Vec3(z0, z1, z2)
  )

  val size = 9

/**
  * Matrix as array, rows first.
  */
  def asArray: Array[Float] = {
    (for (vector <- storage) yield List(vector.x, vector.y, vector.z)).flatten
  }

/**
  * Get value at index.
  */
  def apply(index: Int): Vec3 = {
    if (index >= storage.size) {
      throw new IndexOutOfBoundsException
    }
    storage(index)
  }

/**
  * Get matrix row.
  */ 
  def row(index: Int): Vec3 = {
    if (index >= storage.size) {
      throw new IndexOutOfBoundsException
    }
    Vec3(storage(0).x, storage(1).y, storage(2).z)
  }

/**
  * Matrix multiplication with other 3x3 matrix.
  */
  def *(other: Mat3): Mat3 = {
    val row0 = this.row(0)
    val row1 = this.row(1)
    val row2 = this.row(2)
    Mat3(
      row0.dot(other(0)), row0.dot(other(1)), row0.dot(other(2)),
      row1.dot(other(0)), row1.dot(other(1)), row1.dot(other(2)),
      row2.dot(other(0)), row2.dot(other(1)), row2.dot(other(2))
    )
  }

/**
  * Matrix multiplication with length 3 vector.
  */
  def *(other: Vec3): Vec3 = {
    val row0 = this.row(0)
    val row1 = this.row(1)
    val row2 = this.row(2)
    Vec3(
      row0.dot(other), row1.dot(other), row2.dot(other)
    )
  }
}
