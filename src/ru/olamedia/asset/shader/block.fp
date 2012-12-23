

smooth in vec2 texCoord;
//vec4 frontColor;

uniform sampler2D mesh_ActiveTexture;

vec4 frontColor = vec4(1.0);
const vec4 texEnvColor = vec4(0.0);

const vec4 zerov4 = vec4(0.0);
const vec4 onev4 = vec4(1.0);

vec4 calcTexColor(in vec4 color, in vec4 texColor) {
    color.rgb = mix(color.rgb, texEnvColor.rgb, texColor.rgb);
    color.a *= texColor.a;
    color = clamp(color, zerov4, onev4);
    return color;
}

void main (void)
{
	vec4 texColor;
    texColor = texture2D(mesh_ActiveTexture, texCoord.st);
    if (texColor.a < 0.1f) discard;
	vec4 color = calcTexColor(frontColor, texColor);
    gl_FragColor = vec4(gl_FragColor.a) * gl_FragColor + vec4(1.0 - gl_FragColor.a) * texColor;
}