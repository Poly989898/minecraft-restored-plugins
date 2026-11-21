package com.nexomc.nexo.glyphs;

import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import kotlin.Metadata;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.atlas.AtlasSource;
import team.unnamed.creative.font.FontProvider;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0016\u001a\u00020\u0017H\u0016J\u000e\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u001c\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0096\u0004¢\u0006\n\n\u0002\u0010\u0015\u001a\u0004\b\u0013\u0010\u0014¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/glyphs/SpriteAnimatedGlyph;", "Lcom/nexomc/nexo/glyphs/AnimatedGlyph;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getSection", "()Lorg/bukkit/configuration/ConfigurationSection;", "atlas", "Lnet/kyori/adventure/key/Key;", "getAtlas", "()Lnet/kyori/adventure/key/Key;", "interpolate", "", "getInterpolate", "()Z", "fontProviders", "", "Lteam/unnamed/creative/font/FontProvider;", "getFontProviders", "()[Lteam/unnamed/creative/font/FontProvider;", "[Lteam/unnamed/creative/font/FontProvider;", "registerComponent", "Lnet/kyori/adventure/text/Component;", "handleSpriteAtlas", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "com.nexomc:core"}
)
public final class SpriteAnimatedGlyph extends AnimatedGlyph {
   @NotNull
   private final ConfigurationSection section;
   @NotNull
   private final Key atlas;
   private final boolean interpolate;
   @NotNull
   private final FontProvider[] fontProviders;

   public SpriteAnimatedGlyph(@NotNull ConfigurationSection section) {
      super(section);
      this.section = section;
      Key key = Atlas.GUI;
      this.atlas = key;
      this.interpolate = this.section.getBoolean("interpolate");
      this.fontProviders = new FontProvider[0];
      NexoYamlKt.remove(NexoYamlKt.remove(this.section, "char"), "chars");
   }

   @NotNull
   public final ConfigurationSection getSection() {
      return this.section;
   }

   @NotNull
   public final Key getAtlas() {
      return this.atlas;
   }

   public final boolean getInterpolate() {
      return this.interpolate;
   }

   @NotNull
   public FontProvider[] getFontProviders() {
      return this.fontProviders;
   }

   @NotNull
   public Component registerComponent() {
      ObjectComponent objectComponent = Component.object((ObjectContents)ObjectContents.sprite(this.atlas, KeyUtils.INSTANCE.removeSuffix(this.getTexture(), ".png")));
      return (Component)objectComponent;
   }

   public final void handleSpriteAtlas(@NotNull ResourcePack resourcePack) {
      Atlas.Builder builder;
      block: {
         Atlas atlas2 = resourcePack.atlas(this.atlas);
         if (atlas2 != null) {
            builder = atlas2.toBuilder();
            if (builder != null) {
               break block;
            }
         }

         builder = Atlas.atlas().key(this.atlas);
      }

      Atlas.Builder atlas = builder;
      atlas.addSource(AtlasSource.single(KeyUtils.INSTANCE.removeSuffix(this.getTexture(), ".png")));
      resourcePack.atlas(atlas.build());
   }
}
