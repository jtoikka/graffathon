package main

import processing.core._
import processing.core.PConstants._
import java.awt.Dimension

object ProcessingTest extends PApplet {
  
  val shapes = scala.collection.mutable.Map[String, PShape]()
  val shaders = scala.collection.mutable.Map[String, processing.opengl.PShader]()
  
  override def setup() = {
    size(640, 480, OPENGL)
    background(0)
    lights()
    shapes("teapot") = loadShape("data/teapot.obj")
    shapes("cow") = loadShape("data/cow.obj")
//    shaders("test") = 
    var test = loadShader("shaders/test.fsh", "shaders/test.vsh")
    test.set("fraction", 1.0f);
    shaders("test") = test
  }
  
  def degreesToRadians(degrees: Float): Float = {
    degrees/360.0f * 2 * scala.math.Pi.toFloat
  }
  
  override def draw() = {
    var dirY = (mouseY / height.toFloat - 0.5f) * 2
    var dirX = (mouseX / width.toFloat - 0.5f) * 2
    shader(shaders("test"))
    directionalLight(204, 204, 204, -dirX, -dirY, -1)
    pushMatrix()
    translate(width/2, height/2)
    scale(50, 50, 50)
    rotateZ(degreesToRadians(180.0f))
    shape(shapes("cow"), 0, 0)
    popMatrix()
//    pushMatrix()
//    translate(130, height/2, 0)
//    rotateY(1.25f)
//    rotateX(-0.4f)
////    noFill()
////    stroke(255)
//    box(100)
//    popMatrix()
  }
  
  def createShape(vertexData: Vector[Vertex]): PShape = {
    var shape = createShape()
    beginShape()
    vertexData.foreach(vert => {
      vertex(vert.position.x, vert.position.y, vert.position.z, vert.uv.x, vert.uv.y)
    })
    endShape(CLOSE)
    shape
  }

  def main(args: Array[String]) {
    val frame = new javax.swing.JFrame("Graffathon")
    init
    val preferredSize = new Dimension(640, 480)
    frame.getContentPane().setPreferredSize(preferredSize)
    frame.getContentPane().setMinimumSize(preferredSize)
    frame.getContentPane().add(this)
    frame.pack
    frame.setLocationRelativeTo(null)
    frame.setVisible(true)
    frame.setResizable(false)
    frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
  }
}