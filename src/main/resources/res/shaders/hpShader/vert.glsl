layout (location = 0) in vec3 vertices;

uniform mat4 matrix;

out float x;

void main() {
	x = (vertices.x + 1) / 2;
	gl_Position = matrix * vec4(vertices, 1);
}