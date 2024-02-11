#version 330 core

out vec4 color;

uniform vec4 cellColor;

void main(){
    color = cellColor;
}