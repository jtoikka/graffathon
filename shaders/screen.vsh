#version 120
attribute vec2 position;

uniform float m00;
uniform float m11;
uniform mat4 transform;
uniform mat4 modelview;
uniform mat4 projection;

varying vec2 UV;
varying vec3 viewRay;

void main() {
	gl_Position = vec4(position, 0.0, 1.0);
	UV = (position + vec2(1.0, 1.0)) / 2.0;
	viewRay = vec3(
		position.x/ m00,
		position.y / m11,
		1.0);
}