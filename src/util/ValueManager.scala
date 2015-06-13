package util

import moonlander.library.Moonlander
import scala.collection.mutable.Map

object ValueManager{
  def apply(moonlander: Moonlander, tracks: String*) = {
    val t = tracks.map(s => (s, 0.0f)).toVector
    val m = Map.apply[String, Float]()
    t.foreach(f => m += f)
    
    val man = new ValueManager(moonlander, m)
    moonlander.start()
    man
    
  }
}
class ValueManager(moonlander: Moonlander, tracks: Map[String, Float]) {
  
  def update(){
    moonlander.update()
    tracks.foreach(f => tracks(f._1) = moonlander.getValue(f._1).toFloat)
    
  }
  def getVal(s: String): Float = {
    tracks(s)
  }
  def apply(s:String): Float = getVal(s)
}