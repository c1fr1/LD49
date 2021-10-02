in vec2 distCenter;

out vec4 color;

layout (binding = 3) buffer Color { float colour[]; };

void main() {
	color = vec4(colour[gl_InstanceID], 1 - length(distCenter));
}