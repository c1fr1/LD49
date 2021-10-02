in vec2 tc;

out vec4 color;

uniform float strength;

void main() {
	color = vec4(1, 1, 1, strength);
	color.xy -= tc / 10;
}