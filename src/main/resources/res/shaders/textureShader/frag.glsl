in vec2 tc;

out vec4 color;

uniform sampler2D texSampler;
uniform float strength;

void main() {
	color = texture(texSampler, tc);
}