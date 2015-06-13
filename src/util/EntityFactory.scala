package util
import util.mathUtil._
import scala.util.Random
import math.Vec3
import main.Vertex
import main.Vertex
import scala.collection.mutable.Buffer
import main.Entity
import processing.core.PShape

/**
 * @author Toni
 */
object EntityFactory{
  def createStarfield(model: String, scale: Vec3, starCount: Int, maxDepth: Float, farPlaneDist: Float, fov: Float, aspect: Float, rand: Random) = {
    var width:Float = 1 // farPlaneDist * scala.math.tan(degToRad(fov)).toFloat
    var heigth:Float = 1 // width*aspect.toFloat
    
    var points = Vector.tabulate[Vec3](starCount)(f => 
        new Vec3(rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f ,rand.nextFloat() - 0.5f).normalize
        ).map(v => Vec3(v.x*width, v.y*heigth, -1 + maxDepth*v.z))
        points.map(e => new Entity(e,new Vec3(0,0,0),scale, model, None))
  }
  
  private def moveVertexData(vd: Vector[Vertex], pos: Vec3): Vector[Vertex] = {
    var a = vd.map(e => {
       var instPos = new Vec3(e.position.x + pos.x, e.position.y + pos.y, e.position.z + pos.z)
       new Vertex(instPos,e.normal,e.colour,e.uv)
    })
    return a
  }
  
  def getVertexData(p: PShape) = {  
    val a = (for (i <- 0 until p.getVertexCount) yield (p.getVertex(i), p.getNormal(i), p.getTextureU(i),p.getTextureV(i)))
      //a.map(e => mathUtil.pVecToVec(e._1)).toVector
    a.map(e => new Vertex(mathUtil.pVecToVec(e._1), mathUtil.pVecToVec(e._2),new Vec3(1,1,1), new Vec3(e._3,e._4,1)))
  }
  
  def createCorridorEntities(models: Vector[String]) = {
    models.map(a => new Entity(Vec3(0,0,0), Vec3(mathUtil.degToRad(180),mathUtil.degToRad(180),0), Vec3(1,1,1), a, Some(a)))
  }
  
}