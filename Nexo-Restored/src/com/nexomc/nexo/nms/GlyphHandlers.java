package com.nexomc.nexo.nms;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.glyphs.Glyph;
import com.nexomc.nexo.glyphs.RequiredGlyph;
import com.nexomc.nexo.glyphs.ShiftTag;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.font.Font;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000f2\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012J\u0018\u0010\u0013\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000f2\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012J\u0018\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000f2\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012J\u000e\u0010\u0015\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000fJ\u000e\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000fJ\u000e\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000fJ\u001c\u0010\u0018\u001a\u00020\u000f*\u00020\u000f2\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0007b\u0002\b\u001bR1\u0010\u0004\u001a\u0018\u0012\u0004\u0012\u00020\u0006\u0012\u000e\u0012\f0\u0007¢\u0006\u0002\b\b¢\u0006\u0002\b\t0\u00058BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000b¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/nms/GlyphHandlers;", "", "<init>", "()V", "defaultEmoteReplacementConfigs", "Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "Lcom/nexomc/nexo/glyphs/Glyph;", "Lnet/kyori/adventure/text/TextReplacementConfig;", "Lorg/jetbrains/annotations/NotNull;", "Lkotlin/jvm/internal/EnhancedNullability;", "getDefaultEmoteReplacementConfigs", "()Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "defaultEmoteReplacementConfigs$delegate", "Lkotlin/Lazy;", "escapeGlyphs", "Lnet/kyori/adventure/text/Component;", "component", "player", "Lorg/bukkit/entity/Player;", "escapePlaceholders", "escapeGlyphTags", "unescapeGlyphs", "unescapePlaceholders", "unescapeGlyphTags", "transformGlyphs", "locale", "Ljava/util/Locale;", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
)
public final class GlyphHandlers {
   @NotNull
   public static final GlyphHandlers INSTANCE = new GlyphHandlers();
   @NotNull
   private static final Lazy defaultEmoteReplacementConfigs$delegate = LazyKt.lazy(GlyphHandlers::defaultEmoteReplacementConfigs_delegate$lambda$0);

   private GlyphHandlers() {
   }

   private final Object2ObjectOpenHashMap<Glyph, TextReplacementConfig> getDefaultEmoteReplacementConfigs() {
      Lazy lazy = defaultEmoteReplacementConfigs$delegate;
      return (Object2ObjectOpenHashMap)lazy.getValue();
   }

   @NotNull
   public final Component escapeGlyphs(@NotNull Component component, @Nullable Player player) {
      return this.escapePlaceholders(this.escapeGlyphTags(component, player), player);
   }

   @NotNull
   public final Component escapePlaceholders(@NotNull Component component, @Nullable Player player) {
      Iterable glyphs = (Iterable)NexoPlugin.Companion.instance().fontManager().glyphs();
      Object accumulator = component;

      for(Object element : glyphs) {
         Object obj;
         block: {
            Glyph glyph = (Glyph)element;
            int value = 0;
            TextReplacementConfig escapePlaceholderConfig = glyph.getEscapePlaceholderConfig();
            if (escapePlaceholderConfig != null) {
               TextReplacementConfig textReplacementConfig = escapePlaceholderConfig;
               int value2 = 0;
               escapePlaceholderConfig = !glyph.hasPermission(player) ? textReplacementConfig : null;
               if (escapePlaceholderConfig != null) {
                  TextReplacementConfig config = escapePlaceholderConfig;
                  obj = ((Component)accumulator).replaceText(config);
                  break block;
               }
            }

            obj = accumulator;
         }

         accumulator = obj;
      }

      return (Component)accumulator;
   }

   @NotNull
   public final Component escapeGlyphTags(@NotNull Component component, @Nullable Player player) {
      boolean flag;
      net.kyori.adventure.text.Component component2;
      String serialized = AdventureUtils.INSTANCE.asFlatTextContent(component);
      Set entrySet = this.getDefaultEmoteReplacementConfigs().entrySet();
      Iterable iterable = (Iterable)entrySet;
      Object accumulator = component;

      for(Object element : iterable) {
         block: {
            Map.Entry entry = (Map.Entry)element;
            int value = 0;
            Glyph glyph = (Glyph)entry.getKey();
            TextReplacementConfig config = (TextReplacementConfig)entry.getValue();
            if (!glyph.hasPermission(player)) {
               char[] chars = glyph.getChars();
               int value2 = 0;
               int value3 = chars.length;

               while(true) {
                  if (value2 >= value3) {
                     flag = true;
                     break;
                  }

                  char ch = chars[value2];
                  int value4 = 0;
                  if (StringsKt.contains((CharSequence)serialized, ch, false)) {
                     flag = false;
                     break;
                  }

                  ++value2;
               }

               if (!flag) {
                  component2 = ((Component)accumulator).replaceText(config);
                  break block;
               }
            }

            component2 = (Component)accumulator;
         }

         accumulator = component2;
      }

      Iterable initial = (Iterable)NexoPlugin.Companion.instance().fontManager().glyphs();
      Object obj3 = accumulator;

      for(Object obj4 : initial) {
         Glyph curr = (Glyph)obj4;
         int value5 = 0;
         Object obj;
         if (curr.hasPermission(player)) {
            obj = obj3;
         } else {
            obj = ((Component)obj3).replaceText(curr.getEscapeTagConfig());
         }

         obj3 = obj;
      }

      Object obj2;
      if (ShiftTag.INSTANCE.containsTag(serialized)) {
         obj2 = ((Component)obj3).replaceText(ShiftTag.INSTANCE.getESCAPE_REPLACEMENT_CONFIG());
      } else {
         obj2 = obj3;
      }

      return (Component)obj2;
   }

   @NotNull
   public final Component unescapeGlyphs(@NotNull Component component) {
      return this.unescapePlaceholders(this.unescapeGlyphTags(component));
   }

   @NotNull
   public final Component unescapePlaceholders(@NotNull Component component) {
        Intrinsics.checkNotNullParameter((Object)component, (String)"component");
        Component component2 = null;
        component2 = component;
        Iterable glyphs = NexoPlugin.Companion.instance().fontManager().glyphs();
        for (Object element : glyphs) {
            Glyph glyph = (Glyph)element;
            boolean bl = false;
            TextReplacementConfig textReplacementConfig = glyph.getUnescapePlaceholderConfig();
            if (textReplacementConfig == null) continue;
            Intrinsics.checkNotNullExpressionValue((Object)component2.replaceText(textReplacementConfig), (String)"replaceText(...)");
        }
        return component2;
    }

   @NotNull
   public final Component unescapeGlyphTags(@NotNull Component component) {
        Intrinsics.checkNotNullParameter((Object)component, (String)"component");
        Component component2 = null;
        component2 = component;
        String serialized = AdventureUtils.INSTANCE.asFlatTextContent(component2);
        if (Glyph.Companion.containsTagOrPlaceholder(serialized)) {
            Iterable glyphs = NexoPlugin.Companion.instance().fontManager().glyphs();
            for (Object element : glyphs) {
                Glyph glyph = (Glyph)element;
                boolean bl = false;
                Intrinsics.checkNotNullExpressionValue((Object)component2.replaceText(glyph.getUnescapeTagConfig()), (String)"replaceText(...)");
            }
        }
        if (ShiftTag.INSTANCE.containsTag(serialized)) {
            Component component3 = component2.replaceText(ShiftTag.INSTANCE.getESCAPE_REPLACEMENT_CONFIG());
            Intrinsics.checkNotNullExpressionValue((Object)component3, (String)"replaceText(...)");
            component2 = component3;
        }
        return component2;
    }

   @JvmStatic
   @NotNull
   public static final Component transformGlyphs(@NotNull Component component6, @Nullable Locale locale) {
      Object component = null;
      Locale locale2 = locale;
      if (locale == null) {
         locale2 = Locale.US;
      }

      Component component2 = GlobalTranslator.render(component6, locale2);
      Component component3 = component2;
      String serialized = AdventureUtils.INSTANCE.asFlatTextContent(component3);
      if (Glyph.Companion.containsTagOrPlaceholder(serialized)) {
         Object obj;
         try {
            int value = 0;
            obj = ResultShim.ok(NexoPlugin.Companion.instance().fontManager().glyphs());
         } catch (Throwable e) {
            obj = ResultShim.ok(ResultKt.createFailure(e));
         }

         Collection collection = (Collection)(ResultShim.isFailure(obj) ? null : obj);
         if (collection != null) {
            Iterable iterable = (Iterable)collection;

            for(Object element : iterable) {
               Glyph glyph = (Glyph)element;
               int value2 = 0;
               if (StringsKt.contains((CharSequence)serialized, (CharSequence)glyph.getId(), false) && glyph.getBaseRegex().containsMatchIn((CharSequence)serialized)) {
                  Component component4 = component3.replaceText(glyph.getTagConfig());
                  component3 = component4;
               }

               Iterable placeholders = (Iterable)glyph.getPlaceholders();
               boolean flag;
               if (placeholders instanceof Collection && ((Collection)placeholders).isEmpty()) {
                  flag = false;
               } else {
                  Iterator iterator = placeholders.iterator();

                  while(true) {
                     if (!iterator.hasNext()) {
                        flag = false;
                        break;
                     }

                     Object obj2 = iterator.next();
                     CharSequence p0 = (CharSequence)obj2;
                     int value3 = 0;
                     if (StringsKt.contains((CharSequence)serialized, p0, false)) {
                        flag = true;
                        break;
                     }
                  }
               }

               if (flag) {
                  TextReplacementConfig placeholderConfig = glyph.getPlaceholderConfig();
                  if (placeholderConfig != null) {
                     TextReplacementConfig it = placeholderConfig;
                     int value4 = 0;
                     Component component5 = component3.replaceText(it);
                     component3 = component5;
                  }
               }
            }
         }
      }

      if (ShiftTag.INSTANCE.containsTag(serialized)) {
         component2 = component3.replaceText(ShiftTag.INSTANCE.getREPLACEMENT_CONFIG());
         component3 = component2;
      }

      return component3;
   }

   private static final Object2ObjectOpenHashMap defaultEmoteReplacementConfigs_delegate$lambda$0() {
      Iterable glyphs = (Iterable)NexoPlugin.Companion.instance().getFontManager$com_nexomc_core().glyphs();
      Collection destination = (Collection)(new ArrayList());

      for(Object element : glyphs) {
         Glyph it = (Glyph)element;
         int value = 0;
         if (Intrinsics.areEqual(it.getFont(), Font.MINECRAFT_DEFAULT)) {
            destination.add(element);
         }
      }

      glyphs = (Iterable)((List)destination);
      Object2ObjectOpenHashMap result = new Object2ObjectOpenHashMap(RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity((glyphs instanceof Collection ? (Collection)glyphs : null) != null ? (glyphs instanceof Collection ? (Collection)glyphs : null).size() : 10), 16));
      Map map = (Map)result;

      for(Object obj : glyphs) {
         Glyph it = (Glyph)obj;
         int value2 = 0;
         TextReplacementConfig.Builder builder = TextReplacementConfig.builder();
         int value3 = 0;
         if (it.getUnicodes().size() == 1) {
            builder.matchLiteral((String)CollectionsKt.first(it.getUnicodes()));
         } else {
            String str = CollectionsKt.joinToString((Iterable)it.getUnicodes(), (CharSequence)"|", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null);
            builder.match("(" + StringsKt.removeSuffix(str, (CharSequence)"|") + ")");
         }

         TextReplacementConfig textReplacementConfig = (TextReplacementConfig)builder.replacement((ComponentLike)RequiredGlyph.INSTANCE.getComponent()).build();
         map.put(obj, textReplacementConfig);
      }

      return (Object2ObjectOpenHashMap)map;
   }

   /** Kotlin default-argument bridge, kept for binary compatibility with classes that were not recompiled. */
   public static net.kyori.adventure.text.Component transformGlyphs$default(net.kyori.adventure.text.Component component, java.util.Locale locale, int mask, Object marker) {
      if ((mask & 1) != 0) locale = null;
      return transformGlyphs(component, locale);
   }
}
