#import <gnoise>

in vec2 tc;

out vec4 color;

uniform mat3 strength;
uniform vec2 pos;
uniform float time;
uniform int eID;

uniform sampler2D texSampler;

layout (binding = 0) buffer Pos { vec2 ePos[]; };
layout (binding = 1) buffer Vel { vec2 eVel[]; };
layout (binding = 2) buffer Size { float eSize[]; };
layout (binding = 3) buffer EmberColor { vec3 eColor[]; };

float ROW_WIDTH = 20;

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
	vec2 loc = tc;
	loc = tc - vec2(0.5, 0.5);
	loc.x = loc.x * loc.x * loc.x;
	loc.y = loc.y * loc.y * loc.y;
	loc *= 2.0;
	loc += vec2(0.5, 0.5);
	float xWeight = 2 * loc.x - floor(2 * loc.x);
	float yWeight = 2 * loc.y - floor(2 * loc.y);

	int lowerXIndex = int(floor(loc.x * 2));
	int upperXIndex = lowerXIndex + 1;
	int lowerYIndex = int(floor(loc.y * 2));
	int upperYIndex = lowerYIndex + 1;

	float llStrength = strength[lowerXIndex][2 - lowerYIndex];
	float luStrength = strength[lowerXIndex][2 - upperYIndex];
	float ulStrength = strength[upperXIndex][2 - lowerYIndex];
	float uuStrength = strength[upperXIndex][2 - upperYIndex];

	float lowerXStrength = mix(llStrength, ulStrength, xWeight);
	float upperXStrength = mix(luStrength, uuStrength, xWeight);

	return mix(lowerXStrength, upperXStrength, yWeight);
}

float rand(vec2 co) {
	return fract(sin(dot(vec2(mod(co.x, 112.1591038), mod(co.y, 141.19024)), vec2(12.9898, 78.233))) * 43758.5453);
}

void spawnEmber(vec2 rpos) {
	float r = rand(rpos * mod(time, 0.5456354));
	if (r < 0.001) {
		r *= 500;
		if (eSize[eID] < 0) {
			ePos[eID] = 5 * rpos;
			eVel[eID] = vec2(rand(rpos * 51) - 0.5, 2 * r - 0.5) * 10;
			eSize[eID] = 1.5 * r + 0.15;
			eColor[eID] = emberSlide(r);
		}
	}
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
	} else if ((realStr - noise) < 0.05) {
		spawnEmber(realPos);
	}
	vec3 emberColor = emberSlide((realStr - noise) * 8);
	float emberFactor = 1 - clamp(7 * (realStr - noise), 0, 1);
	color.xyz = mix(color.xyz, emberColor, emberFactor);
}