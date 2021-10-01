layout (location = 0) in vec3 vertices;
layout (location = 1) in vec2 inTC;

uniform mat4 matrix;

out vec2 tc;

void main() {
	gl_Position = matrix * vec4(vertices, 1);
	tc = inTC;
}