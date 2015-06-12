package math

/**
  * 2 length vector.
  */
case class Vec2(x: Float, y: Float) {
  def +(other: Vec2) = Vec2(this.x + other.x, this.y + other.y)
  
  def -(other: Vec2) = Vec2(this.x - other.x, this.y - other.y)
  
  def neg() = Vec2(-this.x, -this.y)
  
  def *(other: Vec2) = Vec2(this.x * other.x, this.y * other.y)
  
  def *(value: Float) = Vec2(this.x * value, this.y * value)
  
  def /(other: Vec2) = Vec2(this.x / other.x, this.y / other.y)
  
  def /(value: Float) = Vec2(this.x / value, this.y / value)
    
  def dot(other: Vec2): Float = this.x * other.x + this.y * other.y
  
  lazy val length = Math.pow(this.dot(this), 0.5).toFloat

  lazy val lengthSquared = this.dot(this)
  
  def normalize(): Vec2 = this / length
  
  def cross(other: Vec2): Float = this.x * other.y - this.y * other.x
  
  override def toString = {
    "x: " + x + " y: " + y
  }
}