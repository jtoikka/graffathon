#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_LIGHT_SHADER

uniform float fraction;

varying vec4 vertColor;
varying vec3 vertNormal;
varying vec3 vertLightDir;
varying vec4 pos;

void main() {  
  float intensity;
  vec4 color;
  intensity = max(0.0, dot(vertLightDir, vertNormal));

  if (intensity > pow(0.95, fraction)) {
    color = vec4(vec3(1.0), 1.0);
  } else if (intensity > pow(0.5, fraction)) {
    color = vec4(vec3(0.6), 1.0);
  } else if (intensity > pow(0.25, fraction)) {
    color = vec4(vec3(0.4), 1.0);
  } else {
    color = vec4(vec3(0.2), 1.0);
  }

  gl_FragData[1] = (pos/16.0);  
  gl_FragData[2] = color;
  gl_FragData[3] = vec4(vertNormal, 1.0);
}