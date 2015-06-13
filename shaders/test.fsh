#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXLIGHT_SHADER

uniform float fraction;

uniform sampler2D texture;

varying vec4 vertColor;
varying vec4 vertTexCoord;
varying vec3 vertNormal;
varying vec3 vertLightDir;
varying vec4 pos;

// vec4 toComponents(float v) {
//     vec4 color;
//     color.r = floor(v / 16777216.0);
//     color.g = floor((v - color.r * 16777216.0) / 65536.0);
//     color.b = floor((v - color.r * 16777216.0 - color.g * 65536.0) / 256.0);
//     color.b = floor((v - color.r * 16777216.0 - color.g * 65536.0 - color.b * 256.0));
//     return color / 256.0;
// }

// vec4 EncodeFloatRGBA(float v) {
//   vec4 enc = vec4(1.0, 255.0, 65025.0, 160581375.0) * v;
//   enc = fract(enc);
//   enc -= enc.yzww * vec4(1.0/255.0,1.0/255.0,1.0/255.0,0.0);
//   return enc;
// }

vec4 packFloatToVec4i(const float value)
{
  const vec4 bitSh = vec4(256.0*256.0*256.0, 256.0*256.0, 256.0, 1.0);
  const vec4 bitMsk = vec4(0.0, 1.0/256.0, 1.0/256.0, 1.0/256.0);
  vec4 res = fract(value * bitSh);
  res -= res.xxyz * bitMsk;
  return res;
}


void main() {  
  // float intensity;
  vec4 color = vec4(1, 1, 1, 1);
  // intensity = max(0.0, dot(vertLightDir, vertNormal));

  // if (intensity > pow(0.95, fraction)) {
  //   color = vec4(vec3(1.0), 1.0);
  // } else if (intensity > pow(0.5, fraction)) {
  //   color = vec4(vec3(0.6), 1.0);
  // } else if (intensity > pow(0.25, fraction)) {
  //   color = vec4(vec3(0.4), 1.0);
  // } else {
  //   color = vec4(vec3(0.2), 1.0);
  // }

  vec4 enc = packFloatToVec4i(pos.z / 1000.0);

  gl_FragData[1] = enc;//vec4(pos.xyz, 0.0);//EncodeFloatRGBA(0.5);  
  gl_FragData[2] = texture2D(texture, vertTexCoord.st) * color;
  gl_FragData[3] = vec4((vertNormal.x + 1.0) / 2.0, (vertNormal.y + 1.0) / 2.0, (vertNormal.z + 1.0) / 2.0, 1.0);
}