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
	float leftFactor = clamp((1 - tc.x * 5), 0, 1) / 2;
	float rightFactor = clamp(tc.x * 5 - 4, 0, 1) / 2;
	float upFactor =  clamp((1 - tc.y * 5), 0, 1) / 2;
	float downFactor = clamp(tc.y * 5 - 4, 0, 1) / 2;

	float totalSideStrength = leftFactor + rightFactor + upFactor + downFactor;
	if (totalSideStrength < 0.0001) {
		return strength;
	}
	float maxSideStrength = max(max(leftFactor, rightFactor), max(upFactor, downFactor));
	leftFactor *= maxSideStrength / totalSideStrength;
	rightFactor *= maxSideStrength / totalSideStrength;
	upFactor *= maxSideStrength / totalSideStrength;
	downFactor *= maxSideStrength / totalSideStrength;
	float centerFactor = 1 - leftFactor - rightFactor - upFactor - downFactor;
	return centerFactor * strength +
		leftFactor * neighborStrengths.x +
		rightFactor * neighborStrengths.y +
		upFactor * neighborStrengths.z +
		downFactor * neighborStrengths.w;
}

float rand(vec2 co) {
	return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
	vec2 texid = floor(8 * vec2(rand(pos), rand(pos * 100)));
	color = texture(texSampler, (tc + texid) / 8);
	color.g = (color.r + color.b) / 2;

	vec2 realPos = calcRealPos();

	float realStr = realStrength();
	float noise = gnoise(vec4(2 * realPos, time / 15, 0)) / 2 + 0.5;
	if (noise > realStr) {
		color.w = 0;
	}
	vec3 emberColor = emberSlide((realStr - noise) * 8);
	float emberFactor = 1 - clamp(7 * (realStr - noise), 0, 1);
	color.xyz = mix(color.xyz, emberColor, emberFactor);
}