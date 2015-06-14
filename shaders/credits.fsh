#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXLIGHT_SHADER

// uniform float fraction;

uniform sampler2D texture;

varying vec4 vertTexCoord;

void main() {  
  // float intensity;
  // vec4 color = vec4(1, 1, 1, 1);
  vec4 color = texture2D(texture, vertTexCoord.st);

  if (color.r == 0.0) discard;
  gl_FragColor = color;
  // gl_FragColor.a = 1.0;
}