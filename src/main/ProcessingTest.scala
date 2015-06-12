package main

import processing.core._
import processing.core.PConstants._
import java.awt.Dimension

object ProcessingTest extends PApplet {
  override def setup() = {
    size(640, 480, P3D)
    background(0)
    lights()
  }
  
  override def draw() = {
    pushMatrix()
    translate(130, height/2, 0)
    rotateY(1.25f)
    rotateX(-0.4f)
    box(100)
    popMatrix()
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