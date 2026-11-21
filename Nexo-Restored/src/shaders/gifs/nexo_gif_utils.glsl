#version 150
#define PACK_FORMAT %PACK_FORMAT%

uniform sampler2D Sampler0;

#if PACK_FORMAT < 63
uniform float GameTime; // Import here as for <63 it is not included in the .vsh shader calling this
#endif

void applyGifEffect() {
    vec2 dimensions = vec2(textureSize(Sampler0, 0));
    ivec2 quadrantUV = ivec2(UV0 * dimensions);
    vec4 quadrant = texelFetch(Sampler0, quadrantUV, 0);

    float z = 254.0 / 255.0, zs = floor(254.0 / 4.0) / 255.0, a = 149.0 / 255.0;
    bool isAnimated = quadrant.a == a && (Color.xyz == vec3(z) || Color.xyz == vec3(zs));

    if (!isAnimated) return;

    vec2 newUV0 = UV0;
    vec4 infoPix1, infoPix2;
    float r = quadrant.r * 255.0;

    if (r == 0.0) {
        infoPix1 = texelFetch(Sampler0, quadrantUV + ivec2(-1,  0), 0);
        infoPix2 = texelFetch(Sampler0, quadrantUV + ivec2( 0,  1), 0);
        newUV0 += (quadrant.gb * 255.0 - vec2(1.0, -1.0)) / dimensions;
    } else if (r == 1.0) {
        infoPix1 = texelFetch(Sampler0, quadrantUV + ivec2( 1,  0), 0);
        infoPix2 = texelFetch(Sampler0, quadrantUV + ivec2( 0,  1), 0);
        newUV0 += (quadrant.gb * 255.0 + 1.0) / dimensions;
    } else if (r == 3.0) {
        infoPix1 = texelFetch(Sampler0, quadrantUV + ivec2(-1,  0), 0);
        infoPix2 = texelFetch(Sampler0, quadrantUV + ivec2( 0, -1), 0);
        newUV0 += (quadrant.gb * 255.0 - 1.0) / dimensions;
    } else if (r == 2.0) {
        infoPix1 = texelFetch(Sampler0, quadrantUV + ivec2( 1,  0), 0);
        infoPix2 = texelFetch(Sampler0, quadrantUV + ivec2( 0, -1), 0);
        newUV0 += (quadrant.gb * 255.0 + vec2(1.0, -1.0)) / dimensions;
    } else {
#if PACK_FORMAT >= 84 // 26.1+
        vertexColor = Color * sample_lightmap(Sampler2, UV2);
#else
        vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);
#endif
        return;
    }

#if PACK_FORMAT >= 84 // 26.1+
    vec4 lightmap = sample_lightmap(Sampler2, UV2);
#else
    vec4 lightmap = texelFetch(Sampler2, UV2 / 16, 0);
#endif

    float totalTime = infoPix1.r * 256.0 + infoPix1.g;
    float startTime = infoPix1.b * 256.0 + infoPix1.a;
    float endTime   = infoPix2.r * 256.0 + infoPix2.g;
    float lower     = startTime / totalTime;
    float upper     = endTime   / totalTime;
    float time      = mod(GameTime / (totalTime / 4705.882352941176), 1.0);

    float visible   = float(time >= lower && time < upper);
    vertexColor     = vec4(Color.rgb, Color.a * visible) * lightmap;
    texCoord0       = newUV0;
}