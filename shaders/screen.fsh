varying vec2 UV;

uniform sampler2D depthTex;
uniform sampler2D screenTex;
uniform sampler2D debugTex;

const float texelH = 1.0/480.0;
const float texelW = 1.0/640.0;

void main() {
	vec4 diffuseColour = texture2D(screenTex, UV);
	// vec4 debugColour = texture2D(debugTex, UV);
	// vec4 depth = texture2D(depthTex, UV);

	float north = texture2D(debugTex, UV + vec2(0, texelH)).y;
	float east = texture2D(debugTex, UV + vec2(texelW, 0)).y;
	float south = texture2D(debugTex, UV + vec2(0, -texelH)).y;
	float west = texture2D(debugTex, UV + vec2(-texelW, 0)).y;
	float center = texture2D(debugTex, UV).y;

	float sum = north + east + south + west + center;

	// if (debugColour.a != 0.0) diffuseColour = debugColour;

	// diffuseColour = vec4(depth.r / 255.0, depth.r, depth.r, 1.0);

	gl_FragColor = diffuseColour;
}