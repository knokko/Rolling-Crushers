#version 400 core

in vec2 passTextureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 lightColour;
uniform float shineDamper;
uniform float reflectivity;

void main(void){
	vec4 textureColor = texture(textureSampler,passTextureCoords);
	if(textureColor.a < 0.01)
		discard;
	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitLightVector = normalize(toLightVector);
	vec3 unitCameraVector = normalize(toCameraVector);
	vec3 lightDirection = -unitLightVector;
	vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);
	float specularFactor = dot(reflectedLightDirection,unitCameraVector);
	specularFactor = max(specularFactor,0.0);
	float dampedFactor = pow(specularFactor,shineDamper);
	vec3 specular = dampedFactor * lightColour * reflectivity;
	float nDotl = dot(unitNormal,unitLightVector);
	float brightness = max(nDotl,0.5);
	vec3 diffuse = brightness * lightColour;
	out_Color = vec4(diffuse,1.0) * textureColor + vec4(specular,1.0);
}