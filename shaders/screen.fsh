varying vec2 UV;

uniform sampler2D diffuseTex;
uniform sampler2D positionTex;
uniform sampler2D normalTex;

const float texelH = 1.0/480.0;
const float texelW = 1.0/640.0;

void main() {
	vec4 pos = texture2D(positionTex, UV);
	vec4 diffuseColour = texture2D(diffuseTex, UV);
	vec4 normal = texture2D(normalTex, UV);

	gl_FragColor = vec4(pos.zzz, 1.0);
}