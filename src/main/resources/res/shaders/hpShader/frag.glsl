in float x;

out vec4 color;

uniform float hp;

void main() {
	if (hp < x) {
		color = vec4(1, 0, 0, 1);
	} else {
		color = vec4(0, 1, 0, 1);
	}
}