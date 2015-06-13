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
import scala.util.Random
import util.EntityFactory
import src.main.Corridor

object ProcessingTest extends PApplet {
  val vManVars = Vector[String](
      "specularExponent",
      "specularIntensity",
      "exposure",
      "diffuseIntensity",
      //"specColour_r",
      //"specColour_g",
      //"specColour_b",
      //"specColour_a",
      //"roughness",
      //"directionalLight_x",
      //"directionalLight_y",
      //"directionalLight_z",
      //"directionalLight_w",
      "camera_pos_x",
      "camera_pos_y",
      "camera_pos_z",
      "camera_look_x",
      "camera_look_y",
      "camera_look_z",
      "camera_up_x",
      "camera_up_y",
      "camera_up_z",
      "pe_00118h",
      "pe_00218h",
      "pe_00520h",
      "pe_00540h",
      "station_light_r",
      "station_light_b",
      "station_light_g",
      "station_light_a",
      "station_light_intensity",
      "station_light_radius",
      "ambient_light_r",
      "ambient_light_b",
      "ambient_light_g",
      "diffuseMultiplier_r",
      "diffuseMultiplier_g",
      "diffuseMultiplier_b",
      "diffuseMultiplier_a",
      "fov"
      ) ++ (for(i <- 5 to 8) yield{
           Vector(
               "light" + i + "_x",
               "light" + i + "_y",
               "light" + i + "_z",
               "light" + i + "_radius",
               "light" + i + "_intensity",
               "light" + i + "_r",
               "light" + i + "_g",
               "light" + i + "_b",
               "light" + i + "_a"
               )}).flatten
  
  lazy val vMan = ValueManager( Moonlander.initWithSoundtrack(this, "sound/sound.mp3", 125, 8), vManVars)
  val corridorModels = Vector[String](
      "cor_pipes_small",
      "cor_pilars",
      "cor_pipes_large",
      "cor_beams_horiz",
      "cor_roof_panel",
      "cor_floor",
      "cor_roof_fill"
      )
      
  val specColor = Vec4(0.3f,0.3f,0.4f,0f)
  val directionalLight = Vec4(0.6f,-0.2f,0.7f,1f)
  val roughness = 0.8f
  
  var rand = new Random(2)
  val shapes =  Map[String, PShape]()
  val shaders = Map[String, PShader]()
  val framebuffers = Map[String, Framebuffer]()
  val textures = Map[String, PImage]()
  
  val cow = new Entity(Vec3(0, 0, 0), Vec3(toRadians(180), 0, 0), Vec3(0.01f, 0.01f, 0.01f), "cow", None)
  val quad = new Entity(Vec3(0, 0, 0), Vec3(toRadians(180), 0, 0), Vec3(0.01f, 0.01f, 0.01f), "quad", None)
  val particle = new Entity(Vec3(0, 0, 0), Vec3(toRadians(180), 0, 0), Vec3(0.01f, 0.01f, 0.01f), "particle", None)
  
  val corridorEnts = EntityFactory.createCorridorEntities(corridorModels)
  val corridorSect = new Corridor(corridorEnts,Vec3(0,-1,0))
  val corridorFull =  Vector.tabulate(100)(f => corridorSect.clone(Vec3(0, -1, f*4)))
  
  //val corridorFull = Vector.tabulate(100)(f => new Entity(Vec3(0, -1, f*4), Vec3(toRadians(180), 0, 0), Vec3(1, 1, 1), "corridor", None))
  
  var explosions = Map[ParticleEmitter, String](
      (new ParticleEmitter(Vec3(0, 0, 15f), 1000, 100, rand, particle, Vec4(1, 1, 1, 1)), "pe_00118h"),
      (new ParticleEmitter(Vec3(0, 0, 35f), 1000, 100, rand, particle, Vec4(1, 1, 1, 1)), "pe_00218h"),
      (new ParticleEmitter(Vec3(0, 0, 95f), 1000, 100, rand, particle, Vec4(1, 1, 1, 1)), "pe_00520h"),
      (new ParticleEmitter(Vec3(-0.2f, 0, 100f), 1000, 100, rand, particle, Vec4(1, 1, 1, 1)), "pe_00540h"),
      (new ParticleEmitter(Vec3(0.2f, 0, 100f), 1000, 100, rand, particle, Vec4(1, 1, 1, 1)), "pe_00540h")
      )
  
  var cameraPos = Vec3(10, 0, 10)
  var cameraLookAt = Vec3(0, 0, 0)
  var cameraUp = Vec3(0, 1, 0)
  
  //var fov = 45.0f
  var zNear = 0.3f
  var zFar = 1000.0f
  
  var stationLightPos = Array.tabulate(4)(f => new Vec3(0,0,10))
  var stationLightColor = Vec4(0.2f, 0.0f, 0.2f, 1.0f)
  var stationLightRadius = 1.0f
  var stationLightInten = 1.0f
  
  var ambient = Vec3(0.2f, 0.0f, 0.2f)
  
  //var diffuseMultiplier = Vec4(1.0f, 1.0f, 1.0f, 1.0f)
  
  //val starfield = util.EntityFactory.createStarfield(
  // "quad", new Vec3(0.01f,0.01f,0.01f), 5, 1, zFar, fov, 1, rand)
    
  var gl2: Option[GL2] = None
    
  override def setup() = {
    
    size(640, 480, OPENGL)
    background(0)
    lights()
    //shapes("teapot") = loadShape("data/teapot.obj")
    //shapes("cow") = loadShape("data/cow.obj")
    shapes("quad") = loadShape("data/quad.obj")
    shapes("particle") = loadShape("data/particle.obj")
    //shapes("corridor") = loadShape("data/corridor.obj")
    
    corridorModels.foreach(s => {
      shapes(s) = loadShape("data/" + s + ".obj")
      textures(s) = loadImage("textures/" + s + ".png")
    })
    
    
    var test = loadShader("shaders/test.fsh", "shaders/test.vsh")
    var screen = loadShader("shaders/screen.fsh", "shaders/screen.vsh")
    var explosions = loadShader("shaders/explosions.fsh", "shaders/explosions.vsh")
    screen.set("positionTex", 0)
    screen.set("diffuseTex", 1)
    screen.set("normalTex", 2)
    shaders("screen") = screen
    test.set("fraction", 1.0f)
    shaders("test") = test
    shaders("explosions") = explosions
//    shader
    
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
    
    framebuffers("explosions") = 
      new Framebuffer(
          width, height,
          Vector((GL.GL_COLOR_ATTACHMENT0, GL.GL_RGBA)),
          gl2.get, false)
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
    g.perspective(toRadians(vMan("fov")), width.toFloat/height.toFloat, zNear, zFar)
  }
  
  override def draw() = {
    update()
    val corridorStuff = corridorFull.map(e => e.getEntities()).fold(Vector[Entity]())(_++_)
    var gl = gl2.get
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT)
    var tex = drawEntitiesToTexture(corridorStuff, shaders("test"))
    var fbo = framebuffers("test")
//    shader(shaders("screen"))
    
//    bindFramebuffer("explosions", gl)
    
    shader(shaders("screen"))
    drawTextureToScreen(fbo.textures, shaders("screen"))
    val explosionShader = shaders("explosions")
    explosionShader.set("depthTex", 0)
    shader(explosionShader)
    
    gl.glActiveTexture(GL.GL_TEXTURE0)
    gl.glBindTexture(GL.GL_TEXTURE_2D, fbo.textures(0))
    
    explosions.map(_._1).foreach(exp => {
      explosionShader.set("colour", exp.color.x, exp.color.y, exp.color.z, exp.color.w)
      drawEntities(exp.getEntities(), shaders("explosions"))
    })
//    drawEntities(explosions.map(f => f._1.getEntities()).foldLeft(Vector[Entity]())(_++_), shaders("explosions"))
//    unbindFramebuffer(gl)
//    drawTextureToScreen(framebuffers("explosions").textures, shaders("explosions"))
    resetShader
    
  }
  
  // For updating logic
  def update() = {
    
    vMan.update()
    explosions.foreach(e => e._1.updateTo(vMan(e._2)))
    
    // camera
    cameraPos = new Vec3(vMan("camera_pos_x"), vMan("camera_pos_y"),vMan("camera_pos_z"))
    cameraLookAt = new Vec3(vMan("camera_look_x"), vMan("camera_look_y"),vMan("camera_look_z"))
    cameraUp = new Vec3(vMan("camera_up_x"), vMan("camera_up_y"),vMan("camera_up_z"))
    updateStationLights()
    
    //starfield.position = cameraPos
  }
  
  def updateStationLights() = {
    stationLightColor = new Vec4(vMan("station_light_r"), vMan("station_light_g"),vMan("station_light_b"),vMan("station_light_a"))
    stationLightInten = vMan("station_light_intensity")
    stationLightRadius = vMan("station_light_radius")
    
    for(i <- 0 to 3){
      var pos = stationLightPos(i)
      var z1 = ((-cameraPos.z - 2 + 4*i)/ 4f).toInt * 4
      z1 = if(z1 > -1) 1000 else z1
      stationLightPos(i) = Vec3(0,0, z1)// + stationLightCameraOffset + Vec3(0,0,-sectionLength*i)

    }
    
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
    val frustumScale = calcFrustumScale( vMan("fov"));
    val m00 = frustumScale / (width.toFloat / height.toFloat)
    val m11 = frustumScale
    shader.set("m00", m00)
    shader.set("m11", m11)
    for(i <- 1 to 4){
      val e = i-1
      shader.set("light" + i, stationLightPos(e).x,stationLightPos(e).y,stationLightPos(e).z)
      shader.set("light" + i + "radius", stationLightRadius)
      shader.set("light" + i + "Colour", stationLightColor.x,stationLightColor.y,stationLightColor.z,stationLightColor.w )
      shader.set("light" + i + "intensity", stationLightInten)
    }
    for(i <- 5 to 8) {
      shader.set("light" + i, vMan("light" + i + "_x"), vMan("light" + i + "_y"), vMan("light" + i + "_z"))
      shader.set("light" + i + "radius", vMan("light" + i + "_radius"))
      shader.set("light" + i + "Colour", vMan("light" + i + "_r"), vMan("light" + i + "_g"), vMan("light" + i + "_b"), vMan("light" + i + "_a"))
      shader.set("light" + i + "intensity", vMan("light" + i + "_intensity"))
    }
   
    shader.set("cameraPos", cameraPos.x, cameraPos.y, cameraPos.z)
    shader.set("specularExponent", vMan("specularExponent"))
    shader.set("specularIntensity", vMan("specularIntensity"))
    shader.set("exposure", vMan("exposure"))
    shader.set("roughness", roughness)
    shader.set("diffuseIntensity", vMan("diffuseIntensity"))
    shader.set("specularColour", specColor.x,specColor.y,specColor.z,specColor.w)
    shader.set("directionalLight",directionalLight.x,directionalLight.y,directionalLight.z,directionalLight.w)
    shader.set("diffuseMultiplier", vMan("diffuseMultiplier_r"),vMan("diffuseMultiplier_g"),vMan("diffuseMultiplier_b"),vMan("diffuseMultiplier_a"))
    shader.set("ambient", vMan("ambient_light_r"), vMan("ambient_light_g"), vMan("ambient_light_b"))
  }
  
  def setPointLights(shader: PShader) = {
    
  }
  
  def drawEntitiesToTexture(entities: Vector[Entity], shader: PShader): Unit = {
    var gl = gl2.get
    bindFramebuffer("test", gl)
    gl.glDisable(GL.GL_BLEND)
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT)
    g.beginDraw()
    g.directionalLight(204, 204, 204, -0, -0, -1)
    setCamera(g)
    setPerspective(g)
    g.shader(shader)
    textureMode(NORMAL)
    drawEntities(entities) 
    g.endDraw
    gl.glDepthFunc(GL.GL_LEQUAL)
    gl.glEnable(GL.GL_BLEND)
    unbindFramebuffer(gl)
    resetShader
  }
  
  def drawEntities(entities: Vector[Entity], shader: PShader): Unit = {
    var gl = gl2.get
//    gl.glDisable(GL.GL_BLEND)
//    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT)
    g.beginDraw()
    g.directionalLight(204, 204, 204, -0, -0, -1)
    setCamera(g)
    setPerspective(g)
    g.shader(shader)
    textureMode(NORMAL)
    drawEntities(entities) 
    g.endDraw
//    gl.glDepthFunc(GL.GL_LEQUAL)
//    gl.glEnable(GL.GL_BLEND)
    resetShader
  }
  
  def bindFramebuffer(buffer: String, gl: GL2) = {
    gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, framebuffers(buffer).id)
  }
  
  def unbindFramebuffer(gl: GL2) = {
    gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0)
  }
  
  def drawEntities(entities: Vector[Entity]): Unit = {
    entities.foreach(ent => {
      val shape = shapes(ent.model)
      g.pushMatrix()
      g.translate(ent.position.x, -ent.position.y, ent.position.z)
      g.scale(ent.scale.x, ent.scale.y, ent.scale.z)
      g.rotateX(ent.rotation.x)
      g.rotateY(ent.rotation.y)
      g.rotateZ(ent.rotation.z)
      g.shape(shape)
      g.popMatrix()
    }) 
  }
  
  def drawTextureToTexture(textures: Vector[Int], shader: PShader): Unit = {
    val gl = gl2.get
    for (i <- 0 until textures.size) {
      gl.glActiveTexture(GL.GL_TEXTURE0 + i)
      gl.glBindTexture(GL.GL_TEXTURE_2D, textures(i))
    }
    shape(shapes("quad"))
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