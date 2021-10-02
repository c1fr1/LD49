in vec2 tc;

out vec4 color;

uniform vec3 ocolor;

void main() {
	color = vec4(ocolor, 1);
	color.xy -= tc / 10;
}