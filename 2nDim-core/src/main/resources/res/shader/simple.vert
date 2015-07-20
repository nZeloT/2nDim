#version 330 core

layout (location = 0) in vec4 position;

uniform mat4 pr_matrix;
uniform mat4 cm_matrix;
uniform mat4 mv_matrix = mat4(1.0);


void main()
{
	gl_Position = pr_matrix * cm_matrix * mv_matrix * position;
}