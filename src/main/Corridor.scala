package src.main

import math.Vec3
import main.Entity


/**
 * @author Toni
 */
class Corridor(entities: Vector[Entity], pos: Vec3) {
  
  def getEntities() = {
    val a = entities.map(e => {
      val c = e.copy()
      c.position += pos
      c
    })
    a
  }
}