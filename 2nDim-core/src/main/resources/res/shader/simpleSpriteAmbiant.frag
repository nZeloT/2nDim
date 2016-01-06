#version 330 core

layout (location = 0) out vec4 color;

in DATA {
    vec2 tc;
} vert_in;

//{0} = tex row
//{1} = tex col
//{2} = tex width
//{3} = tex heigth
uniform vec4 sprite;
uniform sampler2D tex;

void main()
{
    vec2 coord = vec2((sprite[1] + vert_in.tc[0]) * sprite[2], (sprite[0] + 1 - vert_in.tc[1]) * sprite[3]);
    color = texture(tex, coord);
}