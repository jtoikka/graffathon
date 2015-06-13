#define NUM_LIGHTS 8

varying vec2 UV;

uniform sampler2D screen;

void main() {
	vec4 diffuse = Texture2D(screen, UV);
	gl_FragColor = diffuse;
}