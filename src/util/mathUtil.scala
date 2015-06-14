package src.util

import processing.core.PShape
import processing.core.PVector
import src.math.Vec3


/**
 * @author Toni
 */
object mathUtil {
  def degToRad(d: Float): Float = {
    ((Math.PI/180) * d).toFloat
  }
  def radToDeg(r: Float): Float = {
    ((r/Math.PI) * 180f).toFloat
  }
  
  def pVecToVec(p: PVector) = {
    new Vec3(p.x, p.y, p.z)
  }

}