#import <gnoise>

layout (local_size_x = 1) in;
layout (std430) buffer;

uniform vec2 playerPos;
uniform float dtime;
uniform float time;
uniform vec2 playerVel;
uniform float playerHP;

layout (binding = 0) buffer Pos { vec2 pos[]; };
layout (binding = 1) buffer Vel { vec2 vel[]; };
layout (binding = 2) buffer Size { float size[]; };

float PLAYER_PARTICLES = 100;

void main() {
	uint pID = gl_GlobalInvocationID.x;
	float noiseX = gnoise(vec4(5 * time, pID * 100, 0, 0));
	float noiseY = gnoise(vec4(5 * time, pID * 100, 0, 100));
	if (size[pID] < 0) {
		if (pID < PLAYER_PARTICLES * playerHP) {
			pos[pID] = playerPos;
			vel[pID] = vec2(noiseX, noiseY) * 10 + playerVel / (3 * dtime);
			size[pID] = mod(mod(time, 1) * (473 + pID), 1);
		} else {
			size[pID] = 0;
		}
	}
	vel[pID] += 100 * vec2(noiseX, noiseY) * dtime / size[pID];
	pos[pID] += vel[pID] * dtime;
	size[pID] -= dtime;
}