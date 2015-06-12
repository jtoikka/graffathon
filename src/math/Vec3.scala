package math

object Vec3 {
  def apply(v: Vec2, f: Float): Vec3 = {
    Vec3(v.x, v.y, f)
  }
}

/**
  * 3 length vector
  */
case class Vec3(x: Float, y: Float, z: Float) {
  def +(other: Vec3) = 
    Vec3(this.x + other.x, this.y + other.y, this.z + other.z)
  
  def -(other: Vec3) = 
    Vec3(this.x - other.x, this.y - other.y, this.z - other.z)
  
  def neg() = 
    Vec3(-this.x, -this.y, -this.z)
  
  def *(other: Vec3) =
    Vec3(this.x * other.x, this.y * other.y, this.z * other.z)
  
  def *(value: Float) =
    Vec3(this.x * value, this.y * value, this.z * value)
  
  def /(other: Vec3) =
    Vec3(this.x / other.x, this.y / other.y, this.z / other.z)
  
  def /(value: Float) =
    Vec3(this.x / value, this.y / value, this.z / value)
  
  def dot(other: Vec3): Float = 
    this.x * other.x + this.y * other.y + this.z * other.z
    
  def cross(other: Vec3) = 
   Vec3(
     this.y * other.z - this.z * other.y,
     this.z * other.x - this.x * other.z,
     this.x * other.y - this.y * other.x
   )

  lazy val abs = Vec3(x.abs, y.abs, z.abs)
  
  lazy val length = Math.pow(this.dot(this), 0.5).floatValue()

  lazy val lengthSquared = this.dot(this)
  
  lazy val normalize = this / length
  
  lazy val xx = Vec2(x, x)
  lazy val xy = Vec2(x, y)
  lazy val xz = Vec2(z, z)
  
  lazy val yx = Vec2(y, x)
  lazy val yy = Vec2(y, y)
  lazy val yz = Vec2(y, z)
  
  lazy val zx = Vec2(z, x)
  lazy val zy = Vec2(z, y)
  lazy val zz = Vec2(z, z)
  
  override def toString() = {
    "x: " + x + " y: " + y + " z: " + z
  }
}