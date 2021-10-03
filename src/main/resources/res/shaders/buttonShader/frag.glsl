#import <gnoise>

in vec2 tc;

out vec4 color;

uniform float strength;
uniform vec2 pos;
uniform float time;
uniform float aspect;

uniform sampler2D texSampler;

vec3 emberSlide(float val) {
	return vec3(clamp(3 - val * 3, 0, 1), 1 - val, 0);
}

void main() {
	color = texture(texSampler, tc);
	color.g = (color.r + color.b) / 2;

	float noise = gnoise(vec4(2 * pos.x * aspect, 2 * pos.y, time / 15, 0)) / 2 + 0.5;
	if (noise > strength) {
		color.w = 0;
	}
	vec3 emberColor = emberSlide((strength - noise) * 8);
	float emberFactor = 1 - clamp(7 * (strength - noise), 0, 1);
	color.xyz = mix(color.xyz, emberColor, emberFactor);
}