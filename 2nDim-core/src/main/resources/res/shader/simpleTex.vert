#version 330 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 tc;

uniform mat4 pr_matrix;
uniform mat4 cm_matrix;
uniform mat4 mv_matrix = mat4(1.0);

out DATA {
    vec2 tc;
} frag_out;

void main()
{
	gl_Position = pr_matrix * cm_matrix * mv_matrix * position;
    frag_out.tc = tc;
}
