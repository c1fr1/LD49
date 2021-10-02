in float x;

out vec4 color;

uniform float hp;

void main() {
	color = vec4(1, 1, 1, strength);
	color.xy -= tc / 10;
}