uniform mat4 transform;

attribute vec4 vertex;

varying vec4 pos;

void main() {
	pos = transform * vertex;
  gl_Position = pos;  
}