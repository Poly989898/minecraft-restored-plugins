package com.nexomc.nexo.proxy;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.glyphs.Glyph;
import com.nexomc.nexo.glyphs.GlyphShadow;
import com.nexomc.nexo.glyphs.ShaderAnimatedGlyph;
import com.nexomc.nexo.glyphs.Shift;
import com.nexomc.nexo.glyphs.ShiftTag;
import com.nexomc.nexo.glyphs.SpriteAnimatedGlyph;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.JsonBuilder;
import com.nexomc.nexo.utils.KeyUtils;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;
import net.kyori.adventure.key.Key;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.font.Font;
import team.unnamed.creative.font.FontProvider;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.serialize.minecraft.font.FontSerializer;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/proxy/ProxyGlyphInfo;", "", "<init>", "()V", "asBytes", "", "getAsBytes", "()[B", "com.nexomc:core"}
)
public final class ProxyGlyphInfo {
   @NotNull
   public final byte[] getAsBytes() {
      try {
      com.nexomc.nexo.utils.JsonBuilder jsonBuilder;
      Key key = ShiftTag.INSTANCE.getFONT();
      FontProvider[] fontProviders = new FontProvider[]{Shift.INSTANCE.getFontProvider()};
      Font font2 = Font.font(key, fontProviders);
      Font font = font2;
      PackFormat packFormat = PackFormat.format(NMSHandlers.handler().resourcepackFormat());
      PackFormat format = packFormat;
      JsonElement shiftFont = JsonParser.parseString(FontSerializer.INSTANCE.serializeToJsonString(font, format));
      JsonObject jsonObject = JsonBuilder.INSTANCE.plus(new JsonObject(), "__shift_font", JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(new JsonObject(), "key", font.key().asString()), "font", shiftFont));
      JsonObject jsonObject3 = jsonObject;
      int value = 0;
      Iterable glyphs = (Iterable)NexoPlugin.Companion.instance().getFontManager$com_nexomc_core().glyphs();

      for(Object element : glyphs) {
         Glyph glyph;
         Integer number;
         JsonBuilder jsonBuilder2;
         JsonBuilder jsonBuilder3;
         JsonObject jsonObject2;
         block: {
            glyph = (Glyph)element;
            int value2 = 0;
            jsonBuilder = JsonBuilder.INSTANCE;
            jsonBuilder2 = JsonBuilder.INSTANCE;
            jsonBuilder3 = JsonBuilder.INSTANCE;
            jsonObject2 = JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(new JsonObject(), "font", glyph.getFont().asString()), "color", glyph.getDefaultColor().value());
            GlyphShadow defaultShadow = glyph.getDefaultShadow();
            if (defaultShadow != null) {
               Color color = defaultShadow.getColor();
               if (color != null) {
                  number = color.asARGB();
                  break block;
               }
            }

            number = null;
         }

         JsonObject obj = jsonBuilder.plus(jsonBuilder2.plus(jsonBuilder3.plus(jsonObject2, "shadow", number), "permission", glyph.getPermission()), "placeholders", JsonBuilder.INSTANCE.plus(new JsonArray(), glyph.getPlaceholders()));
         if (glyph instanceof ShaderAnimatedGlyph) {
            String sep = ShaderAnimatedGlyph.Companion.unicode(((ShaderAnimatedGlyph)glyph).getFrameCount());
            String joined = CollectionsKt.joinToString((Iterable)RangesKt.until(0, ((ShaderAnimatedGlyph)glyph).getFrameCount()), (CharSequence)sep, (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", ProxyGlyphInfo::_get_asBytes_$lambda$0$0$0);
            JsonBuilder.INSTANCE.plus(obj, "type", "shader");
            JsonBuilder.INSTANCE.plus(obj, "unicodes", JsonBuilder.INSTANCE.plus(new JsonArray(), CollectionsKt.listOf(joined)));
         } else if (glyph instanceof SpriteAnimatedGlyph) {
            JsonBuilder.INSTANCE.plus(obj, "type", "sprite");
            JsonBuilder.INSTANCE.plus(obj, "texture", KeyUtils.INSTANCE.removeSuffix(((SpriteAnimatedGlyph)glyph).getTexture(), ".png").asString());
            JsonBuilder.INSTANCE.plus(obj, "atlas", ((SpriteAnimatedGlyph)glyph).getAtlas().asString());
            JsonBuilder.INSTANCE.plus(obj, "unicodes", new JsonArray());
         } else {
            JsonBuilder.INSTANCE.plus(obj, "unicodes", JsonBuilder.INSTANCE.plus(new JsonArray(), glyph.getUnicodes()));
         }

         JsonBuilder.INSTANCE.plus(jsonObject3, glyph.getId(), obj);
      }

      String str = jsonObject.toString();
      String str2 = str;
      byte[] bytes = str2.getBytes(Charsets.UTF_8);
      return bytes;
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final CharSequence _get_asBytes_$lambda$0$0$0(int it) {
      return (CharSequence)ShaderAnimatedGlyph.Companion.unicode(it);
   }
}
