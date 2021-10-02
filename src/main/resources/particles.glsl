#import <gnoise>

layout (local_size_x = 1) in;
layout (std430) buffer;

uniform vec2 playerPos;
uniform float dtime;
uniform float time;

layout (binding = 0) buffer Pos { vec2 pos[]; };
layout (binding = 1) buffer Vel { vec2 vel[]; };
layout (binding = 2) buffer Size { float size[]; };

void main() {
	uint pID = gl_GlobalInvocationID.x;
	float noiseX = gnoise(vec4(5 * time, pID * 100, 0, 0));
	float noiseY = gnoise(vec4(5 * time, pID * 100, 0, 100));
	if (size[pID] < 0) {
		pos[pID] = playerPos;
		vel[pID] = vec2(noiseX, noiseY) * 10;
		size[pID] = mod(mod(time, 1) * (473 + pID), 1);
	}
	vel[pID] += 30 * vec2(noiseX, noiseY) * dtime;
	pos[pID] += vel[pID] * dtime;
	size[pID] -= dtime;
}