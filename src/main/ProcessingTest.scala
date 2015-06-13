package main

import java.awt.Dimension

import scala.collection.mutable.Map

import javax.media.opengl.GL
import javax.media.opengl.GL2
import math._
import processing.core._
import processing.core.PConstants._
import processing.opengl._
import processing.opengl.PGL._
import processing.opengl.PJOGL._
import processing.opengl.PGraphics3D._
import processing.opengl.PShader._
import java.awt.Dimension
import math._
import javax.media.opengl.GL2
import javax.media.opengl.GL
import scala.collection.mutable.Map
import java.nio.FloatBuffer
import javax.media.opengl.GL2GL3
import util.ValueManager
import moonlander.library.Moonlander

object ProcessingTest extends PApplet {
  
  lazy val vMan = ValueManager(
      Moonlander.initWithSoundtrack(this, "sound/sound.mp3", 125, 8), 
      "camera_pos_x",
      "camera_pos_y",
      "camera_pos_z"
      )
  
  val shapes =  Map[String, PShape]()
  val shaders = Map[String, PShader]()
  val framebuffers = Map[String, Framebuffer]()
  
  val cow = new Entity(Vec3(0, 0, 0), Vec3(toRadians(180), 0, 0), Vec3(1, 1, 1), "cow")
  val corridor = new Entity(Vec3(0, 0, 0), Vec3(toRadians(180), 0, 0), Vec3(1, 1, 1), "corridor")
  val corridorFull = Vector.tabulate(10)(f => new Entity(Vec3(0, -1, f*4), Vec3(toRadians(180), 0, 0), Vec3(1, 1, 1), "corridor"))

  
  var cameraPos = Vec3(10, 0, 10)
  var cameraLookAt = Vec3(0, 0, 0)
  var cameraUp = Vec3(0, 1, 0)
  
  var fov = 45.0f
  var zNear = 0.3f
  var zFar = 1000.0f
  
  var specIntensity = 1.0f
  var specExponent = 60.0f
  var exposure = 1.0f
  var diffuseIntensity = 0.8f
  var specColour = Vec4(1, 1, 1, 1)
  var roughness = 0.8f;
  var directionalLight = Vec4(0.0f, -0.2f, 1.0f, 0).normalize()
  
  var gl2: Option[GL2] = None
    
  override def setup() = {
    
    size(640, 480, OPENGL)
    background(0)
    lights()
    shapes("teapot") = loadShape("data/teapot.obj")
    shapes("cow") = loadShape("data/cow.obj")
    shapes("quad") = loadShape("data/quad.obj")
    shapes("corridor") = loadShape("data/corridor.obj")
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
    
//    gl2.get.glDisable(GL.GL_BLEND)
    
    // position, diffuse and normals
    framebuffers("test") = 
      new Framebuffer(
          width, height, 
          Vector(
              (GL.GL_COLOR_ATTACHMENT0, GL.GL_RGBA),
              (GL.GL_COLOR_ATTACHMENT0 + 1, GL.GL_RGBA),
              (GL.GL_COLOR_ATTACHMENT0 + 2, GL.GL_RGB)), gl2.get, true)
  }
  
  def toRadians(degrees: Float): Float = {
    degrees/180.0f * scala.math.Pi.toFloat
  }
  
  def calcFrustumScale(fovDegrees: Float): Float = {
    val degToRadians = scala.math.Pi * 2.0f / 360.0f
    var fovRad = fovDegrees * degToRadians
    1.0f / scala.math.tan(fovRad / 2.0f).toFloat;
  }
  
  def setPerspective(g: PGraphics) {
    g.perspective(fov, width.toFloat/height.toFloat, zNear, zFar)
  }
  
  override def draw() = {
    update()
    var tex = drawEntitiesToTexture(corridorFull, shaders("test"))
    var fbo = framebuffers("test")
    shader(shaders("screen"))
    drawTextureToScreen(fbo.textures, shaders("screen"))
    resetShader
    
  }
  
  // For updating logic
  def update() = {
    vMan.update()
    cameraPos = new Vec3(vMan("camera_pos_x"), vMan("camera_pos_y"),vMan("camera_pos_z"))
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
  
  def screenQuad() = {
    val gl = gl2.get
    var screenVBO = Array[Int](0)
    gl.glGenBuffers(1, screenVBO, 0)
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, screenVBO(0))
    val verticesBuffer = FloatBuffer.allocate(12)
    verticesBuffer.put(Array(
      -1.0f,  1.0f,
      -1.0f, -1.0f,
       1.0f,  1.0f,
      -1.0f, -1.0f,
       1.0f, -1.0f,
       1.0f,  1.0f
    ))
    verticesBuffer.flip()
    gl.glBufferData(GL.GL_ARRAY_BUFFER, 12 * 4, verticesBuffer, GL.GL_STATIC_DRAW)
  }
  
  def setLightingParameters(shader: PShader) = {
    val frustumScale = calcFrustumScale(fov);
    val m00 = frustumScale / (width.toFloat / height.toFloat)
    val m11 = frustumScale
    shader.set("m00", m00)
    shader.set("m11", m11)
    shader.set("specularExponent", specExponent)
    shader.set("specularIntensity", specIntensity)
    shader.set("exposure", exposure)
    shader.set("roughness", roughness)
    shader.set("diffuseIntensity", diffuseIntensity)
    shader.set("specularColour", specColour.x, specColour.y, specColour.z, specColour.w)
    shader.set("directionalLight", directionalLight.x, directionalLight.y, directionalLight.z, directionalLight.w)
  }
  
  def drawEntitiesToTexture(entities: Vector[Entity], shader: PShader): Unit = {
    var gl = gl2.get
    gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, framebuffers("test").id)
    gl.glDisable(GL.GL_BLEND)
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT)
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
    gl.glEnable(GL.GL_BLEND)
//    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA)
    gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0)
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT)
    resetShader
  }
  
  def drawTextureToTexture(textures: Vector[Int], shader: PShader): PImage = {
    ???
  }
  
  def drawTextureToScreen(textures: Vector[Int], s: PShader): Unit = {
    setLightingParameters(s)
    val gl = gl2.get
    for (i <- 0 until textures.size) {
      gl.glActiveTexture(GL.GL_TEXTURE0 + i)
      gl.glBindTexture(GL.GL_TEXTURE_2D, textures(i))
    }
    shape(shapes("quad"))
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