package main

import scala.util.Random
import math.Vec3

case class EmitterData(dir: Vec3, rotation: Vec3, distMultiplier: Float){
  
}


/**
 * @author Toni
 */
class ParticleEmitter(loc: Vec3, count: Int, maxDistance: Float, rand: Random, particle: Entity){
  val parts = Vector.tabulate[Entity](count)(f => particle.copy)
  //val vectors = Vector.tabulate[Vec3](count)(f => new Vec3(rand.nextFloat() - 0.5f,rand.nextFloat() - 0.5f ,rand.nextFloat() - 0.5f).normalize)
  var currentTime = 0f
  val emmitData = Vector.tabulate[EmitterData](count)(f => 
    new EmitterData(
      new Vec3(rand.nextFloat() - 0.5f,rand.nextFloat() - 0.5f ,rand.nextFloat() - 0.5f).normalize,
      new Vec3(rand.nextFloat()*100,rand.nextFloat()*100 ,rand.nextFloat()*100),
      rand.nextFloat() 
    ))
    
  def updateTo(time: Float){
    currentTime = time
    //parts.indices.foreach(x => parts(x).position = loc + vectors(x).normalize * (time * maxDistance))
    parts.indices.foreach(x => {
      parts(x).position = loc + (emmitData(x).dir * (currentTime * maxDistance) * emmitData(x).distMultiplier)
      parts(x).rotation = emmitData(x).rotation * currentTime
    })
  }
  
  def getEntities() = {
    if(currentTime <= 0.0001)
      Vector()
    else parts
    
  }
  
}