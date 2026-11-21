package com.nexomc.nexo.glyphs;

import kotlin.Metadata;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.font.Font;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0004"},
   d2 = {"Lcom/nexomc/nexo/glyphs/RequiredGlyph;", "Lcom/nexomc/nexo/glyphs/Glyph;", "<init>", "()V", "com.nexomc:core"}
)
public final class RequiredGlyph extends Glyph {
   @NotNull
   public static final RequiredGlyph INSTANCE = new RequiredGlyph();

   private RequiredGlyph() {
      Key key = Font.MINECRAFT_DEFAULT;
      super("required", key, Glyph.Companion.getREQUIRED_GLYPH(), 8, 8, Glyph.Companion.getREQUIRED_CHAR());
   }
}
