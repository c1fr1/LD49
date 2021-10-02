in vec2 tc;

out vec4 color;

uniform sampler2D texSampler;

void main() {
	color = texture(texSampler, tc);
}