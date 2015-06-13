#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXLIGHT_SHADER

uniform float fraction;

uniform sampler2D texture;

// varying vec4 vertColor;
// varying vec4 vertTexCoord;
// varying vec3 vertNormal;
// varying vec3 vertLightDir;
// varying vec4 pos;


void main() {  
  vec4 color = vec4(1, 1, 1, 1);

  gl_FragColor = color;
}