package src.main

import src.math.Vec3


/**
 * @author Toni
 */
class Corridor(entities: Vector[Entity], pos: Vec3) {
  
  def clone(newPos: Vec3) = new Corridor(entities, newPos)
  
  def getEntities() = {
    val a = entities.map(e => {
      val c = e.copy()
      c.position += pos
      c
    })
    a
  }
}