layout (location = 0) in vec3 vertices;

uniform mat4 matrix;

out vec2 distCenter;
flat out int instanceID;

layout (binding = 0) buffer Pos { vec2 pos[]; };
layout (binding = 2) buffer Size { float size[]; };

void main() {
	vec3 verts = vertices;
	distCenter = verts.xy;
	verts.xy *= size[gl_InstanceID];
	verts.xy += pos[gl_InstanceID];
	gl_Position = matrix * vec4(verts, 1);
	instanceID = gl_InstanceID;
}