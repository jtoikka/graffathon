#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXLIGHT_SHADER

uniform vec4 colour;

void main() {  
	gl_FragColor = colour;
}