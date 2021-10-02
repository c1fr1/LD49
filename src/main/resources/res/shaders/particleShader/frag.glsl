in vec2 distCenter;
flat in int instanceID;

out vec4 color;

layout (binding = 3) buffer Color { vec3 colour[]; };

void main() {
	color = vec4(colour[instanceID], 1 - length(distCenter));
}