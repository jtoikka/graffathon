uniform mat4 transform;

attribute vec4 vertex;

void main() {
	vec4 pos = transform * vertex;
  gl_Position = pos;  
}