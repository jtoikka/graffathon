#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXLIGHT_SHADER

uniform vec4 colour;

uniform float intensity;

void main() {  
	gl_FragColor = vec4(intensity, intensity, intensity, 1.0);
}