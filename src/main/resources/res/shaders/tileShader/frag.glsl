#import <gnoise>

in vec2 tc;

out vec4 color;

uniform float strength;
uniform vec2 pos;
uniform sampler2D texSampler;

void main() {
	color = texture(texSampler, tc);
	vec2 realPos = pos;
	realPos.x += tc.x;
	realPos.y -= tc.y;
	float noise = gnoise(vec4(2 * realPos, 0, 0)) / 2 + 0.5;
	color.w *= noise;
}