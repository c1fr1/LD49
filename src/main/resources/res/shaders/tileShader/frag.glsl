#import <gnoise>

in vec2 tc;

out vec4 color;

uniform float strength;
uniform vec2 pos;
uniform float time;
uniform vec4 neighborStrengths;

uniform sampler2D texSampler;

vec3 emberSlide(float val) {
	return vec3(clamp(3 - val * 3, 0, 1), 1 - val, 0);
}

vec2 calcRealPos() {
	vec2 realPos = pos;
	realPos.x += tc.x;
	realPos.y -= tc.y;
	return realPos;
}

float realStrength() {
	float leftFactor = 1 - clamp(tc.x * 5, 0, 1);
	float rightFactor = 1 - clamp((1 - tc.x) * 5, 0, 1);
	float upFactor = 1 - clamp(tc.y * 5, 0, 1);
	float downFactor = 1 - clamp((1 - tc.y) * 5, 0, 1);
	float centerFactor = 1 - leftFactor + rightFactor + upFactor + downFactor;
	return leftFactor;/*centerFactor * strength +
		leftFactor * neighborStrengths.x +
		rightFactor * neighborStrengths.y +
		upFactor * neighborStrengths.z +
		downFactor * neighborStrengths.w;*/
}

void main() {
	color = texture(texSampler, tc);
	vec2 realPos = calcRealPos();

	float realStr = realStrength();
	float noise = gnoise(vec4(2 * realPos, time / 15, 0)) / 2 + 0.5;
	if (noise > realStr) {
		color.w = 0;
	}
	vec3 emberColor = emberSlide((realStr - noise) * 10);
	float emberFactor = 1 - clamp(7 * (realStr - noise), 0, 1);
	color.xyz = mix(color.xyz, emberColor, emberFactor);
}