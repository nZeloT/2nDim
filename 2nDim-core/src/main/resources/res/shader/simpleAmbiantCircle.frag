#version 330 core

layout (location = 0) out vec4 color;

uniform vec4 col;

in DATA {
    vec2 tc;
} vert_in;

void main()
{

    vec2 coord = vert_in.tc - vec2(0.5);
    if(coord.x * coord.x + coord.y * coord.y > 0.25)
        discard;
    else
        color = col;
}