package main

import javax.media.opengl.GL2
import javax.media.opengl.GL
import java.nio.IntBuffer

class Framebuffer(width: Int, height: Int, attachments: Vector[(Int, Int)], gl: GL2, depthTest: Boolean) {
  val textures = attachments.map(pair => {
    genEmptyTexture(width, height, pair._2, gl)
  })

  val id = {
    var fbo = Array[Int](1)
    gl.glGenFramebuffers(1, fbo, 0)
    gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fbo(0))
    
    for (i <- 0 until attachments.size) {
      println("i: " + i + " attachment: " + attachments(i)._1 + " texture: " + textures(i))
      gl.glFramebufferTexture2D(
        GL.GL_FRAMEBUFFER,
        attachments(i)._1,
        GL.GL_TEXTURE_2D, textures(i), 0)
    }
    
    // Attach depth buffer
    if (depthTest) {
      var depthBuffer = Array[Int](0)
      gl.glGenRenderbuffers(1, depthBuffer, 0)
      gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, depthBuffer(0))
      gl.glRenderbufferStorage(GL.GL_RENDERBUFFER, GL.GL_DEPTH_COMPONENT24, width, height)
      gl.glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, GL.GL_DEPTH_ATTACHMENT, GL.GL_RENDERBUFFER, depthBuffer(0))
    }

    val FBOstatus = gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER)
    FBOstatus match {
      case GL.GL_FRAMEBUFFER_COMPLETE =>
      case _ => throw new RuntimeException("Framebuffer incomplete!")
    }

    val drawAttachments = {
      if (depthTest)
        Vector((GL.GL_NONE, 0)) ++ attachments.filter(_._1 != GL.GL_DEPTH_ATTACHMENT)
      else
        attachments.filter(_._1 != GL.GL_DEPTH_ATTACHMENT)
    }
    val drawBuf = IntBuffer.allocate(drawAttachments.size)
    drawAttachments.foreach(pair => {
      drawBuf.put(pair._1)
    })
    drawBuf.flip()
    gl.glDrawBuffers(drawAttachments.length, drawBuf)
    gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0)
    fbo(0)
  }

  def genEmptyTexture(width: Int, height: Int, format: Int, gl: GL2): Int = {
    val texID = Array[Int](1)
    gl.glGenTextures(1, texID, 0)
    gl.glBindTexture(GL.GL_TEXTURE_2D, texID(0))
    gl.glTexImage2D(
      GL.GL_TEXTURE_2D, 0, format, width, height, 0, 
      format, GL.GL_FLOAT, null)
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
    texID(0)
  }
}