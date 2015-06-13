package util

import processing.core.PShape
import processing.core.PVector
import math.Vec3

/**
 * @author Toni
 */
object mathUtil {
  def degToRad(d: Float) = {
    (Math.PI/180) * d
  }
  def radToDeg(r: Float) = {
    (r/Math.PI) * 180f
  }
  
  def pVecToVec(p: PVector) = {
    new Vec3(p.x, p.y, p.z)
  }

}