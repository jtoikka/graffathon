uniform mat4 transform;
uniform mat3 normalMatrix;
uniform vec3 lightNormal;

attribute vec4 vertex;
attribute vec4 color;
attribute vec3 normal;

attribute vec2 texCoord;

varying vec4 vertTexCoord;

void main() {
  gl_Position = transform * vertex;  
  vertTexCoord = vec4(texCoord, 1.0, 1.0);
}