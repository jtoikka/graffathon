varying vec2 UV;

uniform sampler2D diffuseTex;
uniform sampler2D positionTex;
uniform sampler2D normalTex;

const float texelH = 1.0/480.0;
const float texelW = 1.0/640.0;

float fromComponents(vec4 color) {
	vec4 s = color * 256.0;
	return s.r;
}

float DecodeFloatRGBA(vec4 rgba) {
	vec4 v = vec4(rgba.x, rgba.y, rgba.z, 1.0 - rgba.a);
  return dot(v, vec4(1.0, 1.0/255.0, 1.0/65025.0, 1.0/160581375.0));
}

float unpackFloatFromVec4i(const vec4 value)
{
  const vec4 bitSh = vec4(1.0/(256.0*256.0*256.0), 1.0/(256.0*256.0), 1.0/256.0, 1.0);
  return(dot(value, bitSh));
}


void main() {
	vec4 pos = texture2D(positionTex, UV);
	vec4 diffuseColour = texture2D(diffuseTex, UV);
	vec4 normal = texture2D(normalTex, UV);

	float depth = unpackFloatFromVec4i(pos);

	gl_FragColor = vec4(depth, depth, depth, 1.0);
}