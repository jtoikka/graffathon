package main

import math._
class Entity(
    var position: Vec3, 
    var rotation: Vec3, 
    var scale: Vec3, 
    val model: String,
    val texture: Option[String]) {
  
  def copy() = {
    new Entity(position,rotation,scale,model,texture)
  }

}