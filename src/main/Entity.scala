package main

import math._
class Entity(
    var position: Vec3, 
    var rotation: Vec3, 
    var scale: Vec3, 
    val model: String) {
  
  def copy() = {
    new Entity(position,rotation,scale,model)
  }

}