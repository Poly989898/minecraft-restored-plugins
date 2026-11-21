package com.nexomc.nexo.glyphs;

import com.nexomc.nexo.decomp.ResultShim;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.util.ARGBLike;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\u0013\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\f"},
   d2 = {"Lcom/nexomc/nexo/glyphs/GlyphShadow;", "", "color", "Lorg/bukkit/Color;", "<init>", "(Lorg/bukkit/Color;)V", "getColor", "()Lorg/bukkit/Color;", "applyTo", "Lnet/kyori/adventure/text/Component;", "component", "Companion", "com.nexomc:core"}
)
public final class GlyphShadow {
   @NotNull
   public static final Companion Companion = new Companion();
   @Nullable
   private final Color color;

   public GlyphShadow(@Nullable Color color) {
      this.color = color;
   }

   @Nullable
   public final Color getColor() {
      return this.color;
   }

   @NotNull
   public final Component applyTo(@NotNull Component component) {
      GlyphShadow glyphShadow = this;

      Object obj;
      try {
         GlyphShadow glyphShadow2 = glyphShadow;
         int value = 0;
         Component component2 = component;
         Color color2 = glyphShadow2.color;
         ShadowColor shadowColor;
         if (color2 != null) {
            int p0 = color2.asARGB();
            int value2 = 0;
            shadowColor = ShadowColor.shadowColor(p0);
            component2 = component;
         } else {
            shadowColor = null;
         }

         obj = ResultShim.ok(component2.shadowColor((ARGBLike)shadowColor));
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      return (Component)(ResultShim.isFailure(obj) ? component : obj);
   }

   public GlyphShadow() {
      this(com.nexomc.nexo.commands.DyeCommandKt.toColor(com.nexomc.nexo.configs.Settings.GLYPH_DEFAULT_SHADOW_COLOR.toString()));
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\u00020\u0005*\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/glyphs/GlyphShadow$Companion;", "", "<init>", "()V", "glyphShadow", "Lnet/kyori/adventure/text/Component;", "shadow", "Lcom/nexomc/nexo/glyphs/GlyphShadow;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Component glyphShadow(@NotNull Component component, @Nullable GlyphShadow shadow) {
         GlyphShadow glyphShadow = shadow;
         if (shadow == null) {
            glyphShadow = new GlyphShadow(com.nexomc.nexo.commands.DyeCommandKt.toColor(com.nexomc.nexo.configs.Settings.GLYPH_DEFAULT_SHADOW_COLOR.toString()));
         }

         return glyphShadow.applyTo(component);
      }
   }
}
