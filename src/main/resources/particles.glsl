#import <gnoise>

layout (local_size_x = 1) in;
layout (std430) buffer;

uniform vec2 playerPos;
uniform float dtime;

layout (binding = 0) buffer Pos { vec2 pos[]; };
layout (binding = 1) buffer Vel { vec2 vel[]; };
layout (binding = 2) buffer Size { float size[]; };
layout (binding = 3) buffer Color { vec3 color[]; };

void main() {

}