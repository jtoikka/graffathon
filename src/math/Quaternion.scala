package math 

import scala.math.{sin, cos, sqrt}

object Quaternion {
/**
  * Generate quaternion from an axis and an angle.
  */
	def axisAngle(x: Float, y: Float, z: Float, angle: Float): Quaternion = {
		val halfAngle = angle / 2.0f
		val sinAng = sin(halfAngle).toFloat
		Quaternion(x * sinAng, y * sinAng, z * sinAng, cos(halfAngle).toFloat)
	}
}

/**
  * Quaternion 
  */
case class Quaternion(x: Float, y: Float, z: Float, w: Float) {

	def * (b: Quaternion) = {
		Quaternion(
			w * b.x + x * b.w + y * b.z - z * b.y,
			w * b.y + y * b.w + z * b.x - x * b.z,
			w * b.z + z * b.w + x * b.y - y * b.x,
			w * b.w - x * b.x - y * b.y - z * b.z
		)
	}

	def toMat: Mat4 = {
		Mat4(
			1 - 2*y*y - 2*z*z, 2*x*y - 2*w*z,   2*x*z + 2*w*y,     0,
			2*x*y + 2*w*z,     1-2*x*x - 2*z*z, 2*y*z - 2*w*x,     0,
			2*x*z - 2*w*y,     2*y*z + 2*w*x,   1 - 2*x*x - 2*y*y, 0,
			0,                 0,               0,                 1
		)
	}

	def length = sqrt(x*x + y*y + z*z + w*w).toFloat

	def normalize: Quaternion = {
		val len = length
		Quaternion(x/len, y/len, z/len, w/len)
	}
}