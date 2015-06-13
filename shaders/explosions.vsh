#version 120
attribute vec2 position;

varying vec2 UV;

void main() {
	gl_Position = vec4(position, 0.0, 1.0);
	UV = (position + vec2(1.0, 1.0)) / 2.0;
}