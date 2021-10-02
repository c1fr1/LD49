#import <gnoise>

in vec2 tc;

out vec4 color;

uniform float strength;
uniform sampler2D texSampler;

void main() {
	color = texture(texSampler, tc);
	color.xy -= tc / 10;
	color.w *= strength;
}