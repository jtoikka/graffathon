#define NUM_LIGHTS 8

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

const float pointLightRadius = 1.0;

uniform int lightCount;
uniform vec3 light1;
uniform vec3 light2;
uniform vec3 light3;
uniform vec3 light4;
uniform vec3 light5;
uniform vec3 light6;
uniform vec3 light7;
uniform vec3 light8;

uniform float light1radius;
uniform float light2radius;
uniform float light3radius;
uniform float light4radius;
uniform float light5radius;
uniform float light6radius;
uniform float light7radius;
uniform float light8radius;

uniform vec4 light1Colour;
uniform vec4 light2Colour;
uniform vec4 light3Colour;
uniform vec4 light4Colour;
uniform vec4 light5Colour;
uniform vec4 light6Colour;
uniform vec4 light7Colour;
uniform vec4 light8Colour;

uniform float light1intensity;
uniform float light2intensity;
uniform float light3intensity;
uniform float light4intensity;
uniform float light5intensity;
uniform float light6intensity;
uniform float light7intensity;
uniform float light8intensity;

uniform vec3 cameraPos;

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
float calcDiffuse(vec4 lightDirection, vec4 normal) {
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

// //Blinn-Phong
// vec4 calcDiffuse(vec4 lightDirection, vec4 normal, float intensity, vec4 colour) {
// 	vec3 halfAngle = normalize(lightDirection.xyz + viewRay);
// 	float b = dot(halfAngle, normal.xyz);
//   b = clamp(b, 0.0, 1.0);

//   float lightIntensity = intensity * b;
// 	vec4 col = lightIntensity * colour;
//   return col;
// }

float luma(vec3 rgb) {
    return rgb.g * 0.7152 + rgb.r * 0.2198;
}

float calcExposure(vec4 diffuse, float exposure) {
    float luminance = luma(diffuse.rgb);
    float brightness = 1.0 - (exp((exposure) * -luma(diffuse.xyz)));

    return brightness;
}

float attenuation(float r, float dist) {
	float d = max(dist - r, 0.0);

	float denom = d/r + 1.0;
	float att = 1.0 / (denom * denom);

	// att = 1.0/d;

	return att;
}

float calcLight(vec3 light, vec3 fragPosition, vec4 normal, float radius) {
	vec3 lpos = light + cameraPos;
	// vec3 direction = fragPosition - lpos;
	vec3 direction = normalize(-fragPosition + lpos);
	float l = length(fragPosition - lpos);
	float diff = calcDiffuse(vec4(direction, 0.0), normal);
	float att = attenuation(radius, l);
	return att;
}

void main() {
	vec4 pos = texture2D(positionTex, UV);
	float depth = unpackFloatFromVec4i(pos);
	if (depth == 1.0) discard;
	vec4 diffuseColour = texture2D(diffuseTex, UV);
	vec4 normal = texture2D(normalTex, UV);
	vec3 fragPosition = viewRay * depth * 1000.0;
	// fragPosition.z = depth;
	vec4 fixedNormal = vec4(normal.x * 2.0 - 1.0, normal.y * 2.0 - 1.0, normal.z * 2.0 - 1.0, 1.0);
	float depthShifted = depth * 100.0;

	vec4 totalDiff = vec4(0.0, 0.0, 0.0, 1.0);

	// // for (int i = 0; i < lightCount; i++) {
	// 	vec3 lpos = light1 + cameraPos;
	// 	// vec3 direction = fragPosition - lpos;
	// 	vec3 direction = normalize(-fragPosition + lpos);
	// 	float l = length(fragPosition - lpos);
	// 	float diff = calcDiffuse(vec4(direction, 0.0), normal);
	// 	float att = attenuation(pointLightRadius, l);
	totalDiff += calcLight(light1, fragPosition, normal, light1radius) * light1Colour * light1intensity;
	totalDiff += calcLight(light2, fragPosition, normal, light2radius) * light2Colour * light2intensity;
	totalDiff += calcLight(light3, fragPosition, normal, light3radius) * light3Colour * light3intensity;
	totalDiff += calcLight(light4, fragPosition, normal, light4radius) * light4Colour * light4intensity;
	totalDiff += calcLight(light5, fragPosition, normal, light5radius) * light5Colour * light5intensity;
	totalDiff += calcLight(light6, fragPosition, normal, light6radius) * light6Colour * light6intensity;
	totalDiff += calcLight(light7, fragPosition, normal, light7radius) * light7Colour * light7intensity;
	totalDiff += calcLight(light8, fragPosition, normal, light8radius) * light8Colour * light8intensity;
	// }

	// vec3 lightDir1 = light1.xyz - fragPosition;
	// float rough1 = calcRoughness(vec4(lightDir1, 0.0), normal);
	// float att1 = attenuation(pointLightRadius, length(lightDir1));
	// float diff1 = rough1 * att1 * 0.1;

	// vec3 lightDir2 = light2.xyz - fragPosition;
	// float rough2 = calcRoughness(vec4(lightDir2, 0.0), normal);
	// float att2 = attenuation(pointLightRadius, length(lightDir2));
	// float diff2 = rough1 * att2 * 0.1;

	// vec3 lightDir3 = light3.xyz - fragPosition;
	// float rough3 = calcRoughness(vec4(lightDir3, 0.0), normal);
	// float att3 = attenuation(pointLightRadius, length(lightDir3));
	// float diff3 = rough3 * att3 * 0.1;

	// vec3 lightDir4 = light4.xyz - fragPosition;
	// float rough4 = calcRoughness(vec4(lightDir4, 0.0), normal);
	// float att4 = attenuation(pointLightRadius, length(lightDir1));
	// float diff4 = rough1 * att4 * 0.1;

	vec4 roughDirect = calcDiffuse(directionalLight, normal) * diffuseColour;

	// float totalDiff = diff1 + diff2 + diff3 + diff4 + roughDirect;
	totalDiff += roughDirect;
	vec4 spec = calculateSpecular(directionalLight, fixedNormal, specularIntensity);

	vec4 shaded = ((diffuseIntensity * totalDiff) + spec);// * depthShifted;
	float exp = calcExposure(shaded, exposure);
	shaded *= exp;

	gl_FragColor = vec4(shaded.xyz, diffuseColour.a); //vec4(shaded.xyz, 1.0);
}