package math

/**
  * 4 length vector
  */
case class Vec4(x: Float, y: Float, z: Float, w: Float) {
  def +(other: Vec4) = 
    Vec4(this.x + other.x, this.y + other.y, this.z + other.z, this.w + other.w)
  
  def -(other: Vec4) = 
    Vec4(this.x - other.x, this.y - other.y, this.z - other.z, this.w - other.w)
  
  def neg() = 
    Vec4(-this.x, -this.y, -this.z, -this.w)
  
  def *(other: Vec4) =
    Vec4(this.x * other.x, this.y * other.y, this.z * other.z, this.w * other.w)
  
  def *(value: Float) =
    Vec4(this.x * value, this.y * value, this.z * value, this.w * value)
  
  def /(other: Vec4) =
    Vec4(this.x / other.x, this.y / other.y, this.z / other.z, this.w / other.w)
  
  def /(value: Float) =
    Vec4(this.x / value, this.y / value, this.z / value, this.w / value)
  
  def dot(other: Vec4): Float = 
    this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w

  def apply(i: Int) = {
    i match {
      case 0 => x
      case 1 => y
      case 2 => z
      case 3 => w
      case _ => 0
    }
  }
  
  def length() = Math.pow(this.dot(this), 0.5).floatValue()
  
  def normalize() = this / length
  
  def xx() = Vec2(x, x)
  def xy() = Vec2(x, y)
  def xz() = Vec2(z, z)
  
  def yx() = Vec2(y, x)
  def yy() = Vec2(y, y)
  def yz() = Vec2(y, z)
  
  def zx() = Vec2(z, x)
  def zy() = Vec2(z, y)
  def zz() = Vec2(z, z)
  
  override def toString() = {
    "x: " + x + " y: " + y + " z: " + z + " w: " + w
  }
}