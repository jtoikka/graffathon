package main

import java.awt.Dimension

import scala.collection.mutable.Map

import javax.media.opengl.GL
import javax.media.opengl.GL2
import math._
import processing.core._
import processing.core.PConstants._
import processing.opengl._

object ProcessingTest extends PApplet {
  
  val shapes =  Map[String, PShape]()
  val shaders = Map[String, PShader]()
  val framebuffers = Map[String, Framebuffer]()
  val cow = new Entity(Vec3(0, 0, 0), Vec3(radians(180), 0, 0), Vec3(1, 1, 1), "cow")
  
  var cameraPos = Vec3(10, 0, 10)
  var cameraLookAt = Vec3(0, 0, 0)
  var cameraUp = Vec3(0, 1, 0)
  
  var fov = 45.0f;
  var zNear = 0.3f;
  var zFar = 1000.0f;
  
  var gl2: Option[GL2] = None
    
  override def setup() = {
    size(640, 480, OPENGL)
    background(0)
    lights()
    shapes("teapot") = loadShape("data/teapot.obj")
    shapes("cow") = loadShape("data/cow.obj")
    shapes("quad") = loadShape("data/quad.obj")
    var test = loadShader("shaders/test.fsh", "shaders/test.vsh")
    var screen = loadShader("shaders/screen.fsh", "shaders/screen.vsh")
    screen.set("positionTex", 0)
    screen.set("diffuseTex", 1)
    screen.set("normalTex", 2)
    shaders("screen") = screen
    test.set("fraction", 1.0f)
    shaders("test") = test
    
    var pgl = beginPGL().asInstanceOf[PJOGL]
    
    println(pgl.gl.getGL().glGetString(GL.GL_VERSION));
    
    gl2 = Some(pgl.gl.getGL2)
    
    gl2.get.glDepthFunc(GL.GL_EQUAL)
    
    // position, diffuse and normals
    framebuffers("test") = 
      new Framebuffer(
          width, height, 
          Vector(
              (GL.GL_COLOR_ATTACHMENT0, GL.GL_RGBA),
              (GL.GL_COLOR_ATTACHMENT0 + 1, GL.GL_RGBA),
              (GL.GL_COLOR_ATTACHMENT0 + 2, GL.GL_RGB)), gl2.get, true)
  }
  
  def radians(degrees: Float): Float = {
    degrees/180.0f * scala.math.Pi.toFloat
  }
  
  def setPerspective(g: PGraphics) {
    g.perspective(fov, width.toFloat/height.toFloat, zNear, zFar)
  }
  
  override def draw() = {
    var tex = drawEntitiesToTexture(Vector(cow), shaders("test"))
    var fbo = framebuffers("test")
    shader(shaders("screen"))
    drawTextureToScreen(fbo.textures, shaders("screen"))
  }
  
  def defaultCamera(): Unit = {
    camera(0, 0, 0, 0, 0, -1, 0, 1, 0)
  } 
  
  def setCamera(g: PGraphics): Unit = {
    g.camera(
      cameraPos.x, 
      cameraPos.y, 
      cameraPos.z, 
      cameraLookAt.x,
      -cameraLookAt.y,
      cameraLookAt.z,
      cameraUp.x,
      cameraUp.y,
      cameraUp.z)
  }
  
  def drawEntitiesToTexture(entities: Vector[Entity], shader: PShader): Unit = {
    var gl = gl2.get
//    gl.glDepthFunc(GL.GL_GEQUAL)
//    gl.glDisable(GL.GL_CULL_FACE)
    gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, framebuffers("test").id)
//    gl.glClearDepth(0)
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT)
//    gl.glClearDepth(zFar)
//    gl.glDisable(GL.GL_DEPTH_TEST)
//    gl.glDepthFunc(GL.GL_GEQUAL)
//    gl.glFrontFace(GL.GL_CCW)
    g.beginDraw()
    g.directionalLight(204, 204, 204, -0, -0, -1)
    setCamera(g)
    setPerspective(g)
    g.shader(shader)
    entities.foreach(ent => {
      g.pushMatrix()
      g.translate(ent.position.x, -ent.position.y, ent.position.z)
      g.scale(ent.scale.x, ent.scale.y, ent.scale.z)
      g.rotateX(ent.rotation.x)
      g.rotateY(ent.rotation.y)
      g.rotateZ(ent.rotation.z)
      g.shape(shapes(ent.model))
      g.popMatrix()
    }) 
    g.endDraw
    gl.glDepthFunc(GL.GL_LEQUAL)
    gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0)
    resetShader
  }
  
  def drawTextureToTexture(textures: Vector[Int], shader: PShader): PImage = {
    ???
  }
  
  def drawTextureToScreen(textures: Vector[Int], s: PShader): Unit = {
    var gl = gl2.get
    for (i <- 0 until textures.size) {
      gl.glActiveTexture(GL.GL_TEXTURE0 + i)
      gl.glBindTexture(GL.GL_TEXTURE_2D, textures(i))
    }
    shape(shapes("quad"))
  }
  
  def setPerspective() = {
    
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