#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXLIGHT_SHADER

uniform float fraction;

uniform sampler2D depthTex;

uniform vec4 colour;

varying vec4 pos;

float unpackFloatFromVec4i(const vec4 value) {
  const vec4 bitSh = vec4(1.0/(256.0*256.0*256.0), 1.0/(256.0*256.0), 1.0/256.0, 1.0);
  return(dot(value, bitSh));
}


void main() {  
	vec4 posdiv = pos / pos.w;
	vec2 uv = vec2((posdiv.x + 1.0) * 0.5, (posdiv.y + 1.0) * 0.5);
	float baseDepth = unpackFloatFromVec4i(texture2D(depthTex, uv)) * 1000.0;
	if (pos.z > baseDepth) discard;
  gl_FragColor = colour;
}