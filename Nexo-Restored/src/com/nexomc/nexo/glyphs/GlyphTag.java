package com.nexomc.nexo.glyphs;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.commands.DyeCommandKt;
import com.nexomc.nexo.utils.RangeUtilsKt;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.text.StringsKt;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\u00070\b¢\u0006\u0002\b\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u000f"},
   d2 = {"Lcom/nexomc/nexo/glyphs/GlyphTag;", "", "<init>", "()V", "GLYPH", "", "GLYPH_SHORT", "RESOLVER", "Lnet/kyori/adventure/text/minimessage/tag/resolver/TagResolver;", "Lorg/jetbrains/annotations/NotNull;", "getRESOLVER", "()Lnet/kyori/adventure/text/minimessage/tag/resolver/TagResolver;", "containsTag", "", "string", "com.nexomc:core"}
)
public final class GlyphTag {
   @NotNull
   public static final GlyphTag INSTANCE = new GlyphTag();
   @NotNull
   private static final String GLYPH = "glyph";
   @NotNull
   private static final String GLYPH_SHORT = "g";
   @NotNull
   private static final TagResolver RESOLVER;

   private GlyphTag() {
   }

   @NotNull
   public final TagResolver getRESOLVER() {
      return RESOLVER;
   }

   public final boolean containsTag(@NotNull String string) {
      return StringsKt.contains((CharSequence)string, (CharSequence)"<glyph:", false) || StringsKt.contains((CharSequence)string, (CharSequence)"<g:", false);
   }

   private static final Player RESOLVER$lambda$0$0(Function1 tmp0, Object p0) {
      return (Player)tmp0.invoke(p0);
   }

   private static final Player RESOLVER$lambda$0$1$0(Function1 tmp0, Object p0) {
      return (Player)tmp0.invoke(p0);
   }

   private static final Player RESOLVER$lambda$0$1(Context ctx) {
      Pointered target = ctx.target();
      Player player;
      if (target != null) {
         Optional optional = target.get(Identity.NAME);
         if (optional != null) {
            optional = optional.map((a0_ -> GlyphTag.RESOLVER$lambda$0$1$0((kotlin.jvm.functions.Function1)((Object x) -> org.bukkit.Bukkit.getPlayer((String)x)), a0_)));
            if (optional != null) {
               player = (Player)optional.orElse(null);
               return player;
            }
         }
      }

      player = null;
      return player;
   }

   private static final Tag RESOLVER$lambda$0(ArgumentQueue args, Context ctx) {
      kotlin.ranges.IntRange intRange;
      int value;
      Player player2;
      block: {
         Pointered target = ctx.target();
         if (target != null) {
            Optional optional = target.get(Identity.UUID);
            if (optional != null) {
               optional = optional.map((a0_ -> GlyphTag.RESOLVER$lambda$0$0((kotlin.jvm.functions.Function1)((Object x) -> org.bukkit.Bukkit.getPlayer((java.util.UUID)x)), a0_)));
               if (optional != null) {
                  player2 = (Player)optional.orElseGet((() -> GlyphTag.RESOLVER$lambda$0$1(ctx)));
                  break block;
               }
            }
         }

         player2 = null;
      }

      Player player = player2;
      String str = args.popOr("A glyph value is required").value();
      String glyphId = str;
      Glyph glyph = NexoPlugin.Companion.instance().fontManager().glyphFromName(glyphId);
      List arguments = (List)(new ArrayList());

      while(args.hasNext()) {
         String str2 = args.pop().value();
         arguments.add(str2);
      }

      boolean colorable = arguments.contains("colorable") || arguments.contains("c");
      int index = 0;
      Iterator iterator = arguments.iterator();

      while(true) {
         if (!iterator.hasNext()) {
            value = -1;
            break;
         }

         Object item = iterator.next();
         String it = (String)item;
         int value2 = 0;
         if (Intrinsics.areEqual(it, "shadow") || Intrinsics.areEqual(it, "s")) {
            value = index;
            break;
         }

         ++index;
      }

      int value5 = value + 1;
      String shadow = (String)CollectionsKt.getOrNull(arguments, value5);
      Iterator iterator2 = ((Iterable)arguments).iterator();

      while(true) {
         if (iterator2.hasNext()) {
            String it = (String)iterator2.next();
            int value3 = 0;
            IntRange intRange2 = RangeUtilsKt.toIntRangeOrNull(it);
            if (intRange2 == null) {
               Integer number = StringsKt.toIntOrNull(it);
               if (number != null) {
                  int it_1 = ((Number)number).intValue();
                  int value4 = 0;
                  intRange2 = new IntRange(it_1, it_1);
               } else {
                  intRange2 = null;
               }
            }

            IntRange intRange3 = intRange2;
            if (intRange3 == null) {
               continue;
            }

            intRange = intRange3;
            break;
         }

         intRange = null;
         break;
      }

      if (intRange == null) {
         intRange = IntRange.Companion.getEMPTY();
      }

      IntRange bitmapIndexRange = intRange;
      Component component;
      if (glyph.hasPermission(player)) {
         component = glyph.glyphComponent(colorable, new GlyphShadow(shadow != null ? DyeCommandKt.toColor(shadow) : null), bitmapIndexRange);
      } else {
         TextComponent textComponent = Component.text(glyph.getGlyphTag());
         component = (Component)textComponent;
      }

      Component glyphComponent = component;
      return Tag.selfClosingInserting(glyphComponent);
   }

   static {
      String[] strs = new String[]{"glyph", "g"};
      TagResolver tagResolver = TagResolver.resolver(SetsKt.mutableSetOf(strs), GlyphTag::RESOLVER$lambda$0);
      RESOLVER = tagResolver;
   }
}
