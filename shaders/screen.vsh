#version 120
attribute vec2 position;

uniform float m00;
uniform float m11;
uniform mat4 transform;
uniform mat4 modelview;
uniform mat4 projection;

varying vec2 UV;
varying vec3 viewRay;

// uniform vec3 pointLight1;
// uniform vec3 pointLight2;
// uniform vec3 pointLight3;
// uniform vec3 pointLight4;

// varying vec4 light1;
// varying vec4 light2;
// varying vec4 light3;
// varying vec4 light4;

void main() {
	gl_Position = vec4(position, 0.0, 1.0);
	UV = (position + vec2(1.0, 1.0)) / 2.0;
	viewRay = vec3(
		position.x/ m00,
		position.y / m11,
		1.0);

	// for (int i = 0; i < lightCount; i++) {
	// 	lightPos[i] = modelview * lightPosition[i];
	// }

	// light1 = transform * vec4(pointLight1, 1.0);
	// light2 = transform * vec4(pointLight2, 1.0);
	// light3 = transform * vec4(pointLight3, 1.0);
	// light4 = transform * vec4(pointLight4, 1.0);
}