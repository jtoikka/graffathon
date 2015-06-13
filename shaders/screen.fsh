varying vec2 UV;
varying vec3 viewRay;

uniform sampler2D diffuseTex;
uniform sampler2D positionTex;
uniform sampler2D normalTex;

uniform float specularExponent;
uniform float specularIntensity;
uniform vec4 specularColour;
uniform float roughness;
uniform float exposure;
uniform float diffuseIntensity;

uniform vec4 directionalLight;

const float texelH = 1.0/480.0;
const float texelW = 1.0/640.0;

float unpackFloatFromVec4i(const vec4 value)
{
  const vec4 bitSh = vec4(1.0/(256.0*256.0*256.0), 1.0/(256.0*256.0), 1.0/256.0, 1.0);
  return(dot(value, bitSh));
}

vec4 calculateSpecular(
			vec4 lightDirection,
	  	vec4 normal,
	  	float intensity) {
		vec3 halfAngle = normalize(lightDirection.xyz + normalize(viewRay));
		float blinnTerm = dot(normal.xyz, halfAngle);
	  blinnTerm = clamp(blinnTerm, 0.0, 1.0);
	  blinnTerm = pow(blinnTerm, specularExponent);

    float specIntensity = intensity * blinnTerm;
		vec4 specColour = specIntensity * specularColour;
    return specColour;
}

// Oren-Nayar
float calcRoughness(vec4 lightDirection, vec4 normal) {
    float roughness = roughness;
    vec4 viewDir = normalize(vec4(-viewRay, 0.0));

    float nDotL = dot(lightDirection.xyz, normal.xyz);
    float nDotV = dot(viewDir.xyz, normal.xyz);

    float angleLN = acos(nDotL);
    float angleVN = acos(nDotV);

    float alpha = max(angleLN, angleVN);
    float beta = min(angleLN, angleVN);
    float gamma = dot(
    	viewDir.xyz - normal.xyz * nDotV,
      lightDirection.xyz - normal.xyz * nDotL);

    float rSquared = roughness * roughness;

    float A = 1.0 - 0.5 * rSquared / (rSquared + 0.57);
    float B = 0.45 * rSquared / (rSquared + 0.09);
    float C = sin(alpha) / tan(beta);

    float L1 = max(0.0, nDotL) * (A + B * max(0.0, gamma) * C);

    return L1;
}

//Blinn-Phong
vec4 calcDiffuse(vec4 lightDirection, vec4 normal, float intensity, vec4 colour) {
	vec3 halfAngle = normalize(lightDirection.xyz + viewRay);
	float b = dot(halfAngle, normal.xyz);
  b = clamp(b, 0.0, 1.0);

  float lightIntensity = intensity * b;
	vec4 col = lightIntensity * colour;
  return col;
}

float luma(vec3 rgb) {
    return rgb.g * 0.7152 + rgb.r * 0.2198;
}

float calcExposure(vec4 diffuse, float exposure) {
    float luminance = luma(diffuse.rgb);
    float brightness = 1.0 - (exp((exposure) * -luma(diffuse.xyz)));

    return brightness;
}


void main() {
	vec4 pos = texture2D(positionTex, UV);
	float depth = unpackFloatFromVec4i(pos);
	if (depth == 1.0) discard;
	vec4 diffuseColour = texture2D(diffuseTex, UV);
	vec4 normal = texture2D(normalTex, UV);

	vec4 fixedNormal = vec4(normal.x * 2.0 - 1.0, normal.y * 2.0 - 1.0, normal.z * 2.0 - 1.0, 1.0);

	float depthShifted = depth * 100.0;

	// vec4 shaded = normal + depthShifted;

	float rough = calcRoughness(directionalLight, normal);
	// vec4 diffuse = calcDiffuse(directionalLight, fixedNormal, diffuseIntensity, diffuseColour);
	vec4 spec = calculateSpecular(directionalLight, fixedNormal, specularIntensity);
	float exposure = calcExposure(diffuseColour, exposure);

	vec4 shaded = (diffuseColour * diffuseIntensity * rough) + spec;

	// vec3 halfAngle = normalize(directionalLight.xyz + normalize(viewRay));
	// float blinnTerm = dot(fixedNormal.xyz, halfAngle);
	// blinnTerm = pow(blinnTerm, specularExponent);

	gl_FragColor = vec4(shaded.xyz, diffuseColour.a); //vec4(shaded.xyz, 1.0);
}