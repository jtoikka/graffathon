#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXLIGHT_SHADER

uniform float fraction;

uniform vec4 colour;

float unpackFloatFromVec4i(const vec4 value) {
  const vec4 bitSh = vec4(1.0/(256.0*256.0*256.0), 1.0/(256.0*256.0), 1.0/256.0, 1.0);
  return(dot(value, bitSh));
}


void main() {  
  gl_FragColor = colour;
}