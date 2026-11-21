package com.nexomc.nexo.glyphs;
import com.nexomc.nexo.glyphs.GlyphShadow;
import com.nexomc.nexo.glyphs.GlyphTag;
import com.nexomc.nexo.glyphs.RequiredGlyph;
import com.nexomc.nexo.glyphs.Shift;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.commands.DyeCommandKt;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.RangeUtilsKt;
import com.nexomc.nexo.utils.StringUtilsKt;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.font.BitMapFontProvider;
import team.unnamed.creative.font.Font;
import team.unnamed.creative.font.FontProvider;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0098\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0019\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0016\u0018\u0000 q2\u00020\u0001:\u0001qBw\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00030\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00030\u000b\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u000f\u0012\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0012¢\u0006\u0004\b\u0013\u0010\u0014B9\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\u0015\u001a\u00020\u0003¢\u0006\u0004\b\u0013\u0010\u0016B\u0011\b\u0016\u0012\u0006\u0010\u0017\u001a\u00020\u0018¢\u0006\u0004\b\u0013\u0010\u0019J\b\u0010<\u001a\u000208H\u0016J5\u0010=\u001a\u000708¢\u0006\u0002\b>2\u0006\u0010?\u001a\u00020\b2\b\b\u0002\u0010@\u001a\u00020\u000f2\n\b\u0002\u0010A\u001a\u0004\u0018\u00010\u00122\b\b\u0002\u0010B\u001a\u00020\u0003H\u0002J+\u0010=\u001a\u00070C¢\u0006\u0002\b>2\u0006\u0010D\u001a\u00020E2\b\b\u0002\u0010@\u001a\u00020\u000f2\n\b\u0002\u0010A\u001a\u0004\u0018\u00010\u0012H\u0002J\u0082\u0001\u0010b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00030\u000b2\b\b\u0002\u0010\f\u001a\u00020\u00032\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00030\u000b2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u000f2\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0012J\u0010\u0010c\u001a\u00020\u000f2\b\u0010d\u001a\u0004\u0018\u00010eJ,\u0010h\u001a\u0002082\b\b\u0002\u0010@\u001a\u00020\u000f2\n\b\u0002\u0010A\u001a\u0004\u0018\u00010\u00122\b\b\u0002\u0010i\u001a\u00020EH\u0017b\u0002\bjR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0014\u0010\u0006\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001dR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\t\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b!\u0010 R\u001a\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00030\u000bX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0011\u0010\f\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001bR\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00030\u000b¢\u0006\b\n\u0000\u001a\u0004\b%\u0010#R\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b&\u0010'R\u0011\u0010\u0010\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010'R\u0013\u0010\u0011\u001a\u0004\u0018\u00010\u0012¢\u0006\b\n\u0000\u001a\u0004\b(\u0010)R\u0014\u0010*\u001a\u00020+X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b,\u0010-R\u001b\u0010.\u001a\u00020/8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b2\u00103\u001a\u0004\b0\u00101R\u001b\u00104\u001a\u00020\u00038FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b6\u00103\u001a\u0004\b5\u0010\u001bR\u001b\u00107\u001a\u0002088FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b;\u00103\u001a\u0004\b9\u0010:R\u000e\u0010F\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010G\u001a\u00020H¢\u0006\b\n\u0000\u001a\u0004\bI\u0010JR\u0011\u0010K\u001a\u00020H¢\u0006\b\n\u0000\u001a\u0004\bL\u0010JR\u000e\u0010M\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001b\u0010N\u001a\u00020O8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bR\u00103\u001a\u0004\bP\u0010QR\u001b\u0010S\u001a\u00020O8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bU\u00103\u001a\u0004\bT\u0010QR\u001b\u0010V\u001a\u00020O8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\bX\u00103\u001a\u0004\bW\u0010QR\u001d\u0010Y\u001a\u0004\u0018\u00010O8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b[\u00103\u001a\u0004\bZ\u0010QR\u001d\u0010\\\u001a\u0004\u0018\u00010O8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b^\u00103\u001a\u0004\b]\u0010QR\u001d\u0010_\u001a\u0004\u0018\u00010O8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\ba\u00103\u001a\u0004\b`\u0010QR\u0011\u0010f\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\bg\u0010\u001bR!\u0010k\u001a\b\u0012\u0004\u0012\u00020m0l8VX\u0096\u0084\u0002¢\u0006\f\n\u0004\bp\u00103\u001a\u0004\bn\u0010o¨\u0006r"},
   d2 = {"Lcom/nexomc/nexo/glyphs/Glyph;", "", "id", "", "font", "Lnet/kyori/adventure/key/Key;", "texture", "ascent", "", "height", "unicodes", "", "permission", "placeholders", "tabcomplete", "", "isEmoji", "defaultShadow", "Lcom/nexomc/nexo/glyphs/GlyphShadow;", "<init>", "(Ljava/lang/String;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;IILjava/util/List;Ljava/lang/String;Ljava/util/List;ZZLcom/nexomc/nexo/glyphs/GlyphShadow;)V", "unicode", "(Ljava/lang/String;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;IILjava/lang/String;)V", "glyphSection", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getId", "()Ljava/lang/String;", "getFont", "()Lnet/kyori/adventure/key/Key;", "getTexture", "getAscent", "()I", "getHeight", "getUnicodes", "()Ljava/util/List;", "getPermission", "getPlaceholders", "getTabcomplete", "()Z", "getDefaultShadow", "()Lcom/nexomc/nexo/glyphs/GlyphShadow;", "defaultColor", "Lnet/kyori/adventure/text/format/TextColor;", "getDefaultColor", "()Lnet/kyori/adventure/text/format/TextColor;", "chars", "", "getChars", "()[C", "chars$delegate", "Lkotlin/Lazy;", "formattedUnicodes", "getFormattedUnicodes", "formattedUnicodes$delegate", "component", "Lnet/kyori/adventure/text/Component;", "getComponent", "()Lnet/kyori/adventure/text/Component;", "component$delegate", "registerComponent", "bitmapComponent", "Lorg/jetbrains/annotations/NotNull;", "bitmapIndex", "colorable", "shadow", "shift", "Lnet/kyori/adventure/text/TextComponent;", "indexRange", "Lkotlin/ranges/IntRange;", "_baseRegex", "baseRegex", "Lkotlin/text/Regex;", "getBaseRegex", "()Lkotlin/text/Regex;", "escapedRegex", "getEscapedRegex", "placeholderMatch", "tagConfig", "Lnet/kyori/adventure/text/TextReplacementConfig;", "getTagConfig", "()Lnet/kyori/adventure/text/TextReplacementConfig;", "tagConfig$delegate", "escapeTagConfig", "getEscapeTagConfig", "escapeTagConfig$delegate", "unescapeTagConfig", "getUnescapeTagConfig", "unescapeTagConfig$delegate", "placeholderConfig", "getPlaceholderConfig", "placeholderConfig$delegate", "escapePlaceholderConfig", "getEscapePlaceholderConfig", "escapePlaceholderConfig$delegate", "unescapePlaceholderConfig", "getUnescapePlaceholderConfig", "unescapePlaceholderConfig$delegate", "copy", "hasPermission", "player", "Lorg/bukkit/entity/Player;", "glyphTag", "getGlyphTag", "glyphComponent", "bitmapIndexRange", "Lkotlin/jvm/JvmOverloads;", "fontProviders", "", "Lteam/unnamed/creative/font/FontProvider;", "getFontProviders", "()[Lteam/unnamed/creative/font/FontProvider;", "fontProviders$delegate", "Companion", "com.nexomc:core"}
)
public class Glyph {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final String id;
   @NotNull
   private final Key font;
   @NotNull
   private final Key texture;
   private final int ascent;
   private final int height;
   @NotNull
   private final List<String> unicodes;
   @NotNull
   private final String permission;
   @NotNull
   private final List<String> placeholders;
   private final boolean tabcomplete;
   private final boolean isEmoji;
   @Nullable
   private final GlyphShadow defaultShadow;
   @NotNull
   private final TextColor defaultColor;
   @NotNull
   private final Lazy chars$delegate;
   @NotNull
   private final Lazy formattedUnicodes$delegate;
   @NotNull
   private final Lazy component$delegate;
   @NotNull
   private final String _baseRegex;
   @NotNull
   private final Regex baseRegex;
   @NotNull
   private final Regex escapedRegex;
   @NotNull
   private final String placeholderMatch;
   @NotNull
   private final Lazy tagConfig$delegate;
   @NotNull
   private final Lazy escapeTagConfig$delegate;
   @NotNull
   private final Lazy unescapeTagConfig$delegate;
   @NotNull
   private final Lazy placeholderConfig$delegate;
   @NotNull
   private final Lazy escapePlaceholderConfig$delegate;
   @NotNull
   private final Lazy unescapePlaceholderConfig$delegate;
   @NotNull
   private final String glyphTag;
   @NotNull
   private final Lazy fontProviders$delegate;
   @NotNull
   private static final NamespacedKey ORIGINAL_ITEM_RENAME;
   @NotNull
   private static final String ORIGINAL_ITEM_RENAME_STRING;
   @NotNull
   private static final Key REQUIRED_GLYPH;
   @NotNull
   private static final String REQUIRED_CHAR;
   @NotNull
   private static final Map<String, List<String>> assignedGlyphUnicodes;

   public Glyph(@NotNull String id, @NotNull Key font, @NotNull Key texture, int ascent, int height, @NotNull List<String> unicodes, @NotNull String permission, @NotNull List<String> placeholders, boolean tabcomplete, boolean isEmoji, @Nullable GlyphShadow defaultShadow) {
      super();
      this.id = id;
      this.font = font;
      this.texture = texture;
      this.ascent = ascent;
      this.height = height;
      this.unicodes = unicodes;
      this.permission = permission;
      this.placeholders = placeholders;
      this.tabcomplete = tabcomplete;
      this.isEmoji = isEmoji;
      this.defaultShadow = defaultShadow;
      NamedTextColor namedTextColor = NamedTextColor.WHITE;
      this.defaultColor = (TextColor)namedTextColor;
      this.chars$delegate = LazyKt.lazy((() -> Glyph.chars_delegate$lambda$0(this)));
      this.formattedUnicodes$delegate = LazyKt.lazy((() -> Glyph.formattedUnicodes_delegate$lambda$0(this)));
      this.component$delegate = LazyKt.lazy((() -> Glyph.component_delegate$lambda$0(this)));
      this._baseRegex = "<(glyph|g):(" + this.id + "(?::(?:c|colorable|\\d+(?:\\.\\.\\d+)?|s|shadow)(?::[\\w#]+)?)*)>";
      Pattern pattern = Pattern.compile("(?<!\\\\)" + this._baseRegex);
      Pattern pattern2 = pattern;
      this.baseRegex = new Regex(pattern2);
      pattern = Pattern.compile("\\\\" + this._baseRegex);
      pattern2 = pattern;
      this.escapedRegex = new Regex(pattern2);
      Iterable iterable = (Iterable)this.placeholders;
      this.placeholderMatch = "(" + CollectionsKt.joinToString(iterable, (CharSequence)"|", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1<String, String>)(p0 -> Regex.Companion.escape(p0))) + ")";
      this.tagConfig$delegate = LazyKt.lazy((() -> Glyph.tagConfig_delegate$lambda$0(this)));
      this.escapeTagConfig$delegate = LazyKt.lazy((() -> Glyph.escapeTagConfig_delegate$lambda$0(this)));
      this.unescapeTagConfig$delegate = LazyKt.lazy((() -> Glyph.unescapeTagConfig_delegate$lambda$0(this)));
      this.placeholderConfig$delegate = LazyKt.lazy((() -> Glyph.placeholderConfig_delegate$lambda$0(this)));
      this.escapePlaceholderConfig$delegate = LazyKt.lazy((() -> Glyph.escapePlaceholderConfig_delegate$lambda$0(this)));
      this.unescapePlaceholderConfig$delegate = LazyKt.lazy((() -> Glyph.unescapePlaceholderConfig_delegate$lambda$0(this)));
      this.glyphTag = "<glyph:" + this.id + ">";
      this.fontProviders$delegate = LazyKt.lazy((() -> Glyph.fontProviders_delegate$lambda$0(this)));
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public Key getFont() {
      return this.font;
   }

   @NotNull
   public Key getTexture() {
      return this.texture;
   }

   public final int getAscent() {
      return this.ascent;
   }

   public final int getHeight() {
      return this.height;
   }

   @NotNull
   public List<String> getUnicodes() {
      return this.unicodes;
   }

   @NotNull
   public final String getPermission() {
      return this.permission;
   }

   @NotNull
   public final List<String> getPlaceholders() {
      return this.placeholders;
   }

   public final boolean getTabcomplete() {
      return this.tabcomplete;
   }

   public final boolean isEmoji() {
      return this.isEmoji;
   }

   @Nullable
   public final GlyphShadow getDefaultShadow() {
      return this.defaultShadow;
   }

   @NotNull
   public TextColor getDefaultColor() {
      return this.defaultColor;
   }

   @NotNull
   public final char[] getChars() {
      Lazy lazy = this.chars$delegate;
      return (char[])lazy.getValue();
   }

   @NotNull
   public final String getFormattedUnicodes() {
      Lazy lazy = this.formattedUnicodes$delegate;
      return (String)lazy.getValue();
   }

   @NotNull
   public final Component getComponent() {
      Lazy lazy = this.component$delegate;
      return (Component)lazy.getValue();
   }

   @NotNull
   public Component registerComponent() {
      net.kyori.adventure.text.event.HoverEvent<net.kyori.adventure.text.Component> hoverEvent;
      String placeholder = CollectionsKt.firstOrNull(this.placeholders);
      String it = Settings.GLYPH_HOVER_TEXT.toString();
      int value = 0;
      CharSequence it_11 = (CharSequence)StringsKt.replace(placeholder != null ? StringsKt.replace(it, "<glyph_placeholder>", placeholder, false) : it, "<glyph_id>", this.id, false);
      CharSequence charSequence;
      if (it_11.length() == 0) {
         int value2 = 0;
         charSequence = null;
      } else {
         charSequence = it_11;
      }

      block: {
         String str = (String)charSequence;
         if (str != null) {
            Component it_10 = AdventureUtilsKt.deserialize(str);
            if (it_10 != null) {
               int value3 = 0;
               hoverEvent = HoverEvent.showText(it_10);
               break block;
            }
         }

         hoverEvent = null;
      }

      HoverEvent hoverText = hoverEvent;
      Iterable it_9 = (Iterable)this.getUnicodes();
      Collection thisCollection = (Collection)(new ArrayList());
      int value4 = 0;

      for(Object obj : it_9) {
         int i = value4++;
         if (i < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         String row = (String)obj;
         int value5 = 0;
         String row_8 = StringUtilsKt.joinToString(row, Shift.INSTANCE.of(-1));
         TextComponent[] textComponents = new TextComponent[]{Component.text(row_8).font(this.getFont()), null};
         TextComponent newline = Component.newline();
         byte b = 1;
         int value6 = 0;
         boolean flag = this.getUnicodes().size() != i + 1;
         textComponents[b] = flag ? newline : null;
         CollectionsKt.addAll(thisCollection, (Iterable)CollectionsKt.listOfNotNull(textComponents));
      }

      Collection collection = (Collection)((List)thisCollection);
      TextComponent[] it_7 = (TextComponent[])collection.toArray(new TextComponent[0]);
      Component component = Component.textOfChildren((ComponentLike[])Arrays.copyOf(it_7, it_7.length)).hoverEvent((HoverEventSource)hoverText);
      return component;
   }

   private final Component bitmapComponent(int bitmapIndex, boolean colorable, GlyphShadow shadow, String shift) {
      GlyphShadow.Companion companion = GlyphShadow.Companion;
      Character orNull = ArraysKt.getOrNull(this.getChars(), bitmapIndex - 1);
      char ch = orNull != null ? orNull : ArraysKt.first(this.getChars());
      TextComponent textComponent = Component.text(ch + shift, colorable ? null : this.getDefaultColor());
      Component component = companion.glyphShadow((Component)textComponent, shadow).font(this.getFont());
      return component;
   }

   private final TextComponent bitmapComponent(IntRange indexRange, boolean colorable, GlyphShadow shadow) {
      Iterable iterable = (Iterable)indexRange;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      int item;
      String str;
      for(Iterator iterator = iterable.iterator(); iterator.hasNext(); destination.add(this.bitmapComponent(item, colorable, shadow, str))) {
         item = ((IntIterator)iterator).nextInt();
         int value = 0;
         String str2 = Shift.INSTANCE.of(-1);
         int value2 = 0;
         boolean flag = CollectionsKt.count((Iterable)indexRange) > 1;
         str = flag ? str2 : null;
         if ((flag ? str2 : null) == null) {
            str = "";
         }
      }

      Collection collection = (Collection)((List)destination);
      Component[] components = (Component[])collection.toArray(new Component[0]);
      TextComponent textComponent = Component.textOfChildren((ComponentLike[])Arrays.copyOf(components, components.length));
      return textComponent;
   }

   @NotNull
   public final Regex getBaseRegex() {
      return this.baseRegex;
   }

   @NotNull
   public final Regex getEscapedRegex() {
      return this.escapedRegex;
   }

   @NotNull
   public final TextReplacementConfig getTagConfig() {
      Lazy lazy = this.tagConfig$delegate;
      Object value = lazy.getValue();
      return (TextReplacementConfig)value;
   }

   @NotNull
   public final TextReplacementConfig getEscapeTagConfig() {
      Lazy lazy = this.escapeTagConfig$delegate;
      Object value = lazy.getValue();
      return (TextReplacementConfig)value;
   }

   @NotNull
   public final TextReplacementConfig getUnescapeTagConfig() {
      Lazy lazy = this.unescapeTagConfig$delegate;
      Object value = lazy.getValue();
      return (TextReplacementConfig)value;
   }

   @Nullable
   public final TextReplacementConfig getPlaceholderConfig() {
      Lazy lazy = this.placeholderConfig$delegate;
      return (TextReplacementConfig)lazy.getValue();
   }

   @Nullable
   public final TextReplacementConfig getEscapePlaceholderConfig() {
      Lazy lazy = this.escapePlaceholderConfig$delegate;
      return (TextReplacementConfig)lazy.getValue();
   }

   @Nullable
   public final TextReplacementConfig getUnescapePlaceholderConfig() {
      Lazy lazy = this.unescapePlaceholderConfig$delegate;
      return (TextReplacementConfig)lazy.getValue();
   }

   public Glyph(@NotNull String id, @NotNull Key font, @NotNull Key texture, int ascent, int height, @NotNull String unicode) {
      this(id, font, texture, ascent, height, CollectionsKt.listOf(unicode), defaultPermission(id), new java.util.ArrayList<String>(), false, false, null);
   }

   private static String defaultPermission(String id) {
      Object value = Settings.GLYPH_DEFAULT_PERMISSION.getValue();
      String text = value != null ? value.toString() : null;
      return text != null ? text.replace("<glyphid>", id) : "";
   }

   public Glyph(@NotNull ConfigurationSection glyphSection) {
      boolean flag;
      int value;
      int value2;
      String str;
      Glyph glyph;
      String str2;
      Key key;
      Key key2;
      List list;
      block: {
         /* this-alias removed */
         str2 = glyphSection.getName();
         Settings settings = Settings.GLYPH_DEFAULT_FONT;
         Key key3 = Font.MINECRAFT_DEFAULT;
         key = NexoYamlKt.key(glyphSection, "font", settings.toKey(key3));
         key2 = KeyUtils.INSTANCE.appendSuffix(NexoYamlKt.key(glyphSection, "texture", REQUIRED_GLYPH), ".png");
         value = glyphSection.getInt("ascent", 8);
         value2 = glyphSection.getInt("height", 8);
         list = Companion.calculateGlyphUnicodes(glyphSection);
         Object obj = Settings.GLYPH_DEFAULT_PERMISSION.getValue();
         if (obj != null) {
            String str3 = obj.toString();
            if (str3 != null) {
               String name = glyphSection.getName();
               str = StringsKt.replace(str3, "<glyphid>", name, false);
               if (str != null) {
                  break block;
               }
            }
         }

         str = "";
      }

      GlyphShadow glyphShadow;
      String str4;
      List list2;
      boolean flag2;
      block: {
         str4 = glyphSection.getString("permission", str);
         list2 = NexoYamlKt.stringListOrSingle(glyphSection, "placeholder", (String)null);
         flag = glyphSection.getBoolean("tabcomplete");
         flag2 = glyphSection.getBoolean("is_emoji");
         String string = glyphSection.getString("default_shadow_color");
         if (string != null) {
            Color color = DyeCommandKt.toColor(string);
            if (color != null) {
               Color p0 = color;
               boolean flag3 = flag2;
               boolean flag4 = flag;
               List list3 = list2;
               String str5 = str4;
               List list4 = list;
               int value3 = value2;
               int value4 = value;
               Key key4 = key2;
               Key key5 = key;
               String str6 = str2;
               int value5 = 0;
               GlyphShadow glyphShadow2 = new GlyphShadow(p0);
               /* this-alias removed */
               str2 = str6;
               key = key5;
               key2 = key4;
               value = value4;
               value2 = value3;
               list = list4;
               str4 = str5;
               list2 = list3;
               flag = flag4;
               flag2 = flag3;
               glyphShadow = glyphShadow2;
               break block;
            }
         }

         glyphShadow = null;
      }

      this(str2, key, key2, value, value2, list, str4, list2, flag, flag2, glyphShadow);
   }

   @NotNull
   public final Glyph copy(@NotNull String id, @NotNull Key font, @NotNull Key texture, int ascent, int height, @NotNull List<String> unicodes, @NotNull String permission, @NotNull List<String> placeholders, boolean tabcomplete, boolean isEmoji, @Nullable GlyphShadow defaultShadow) {
      return new Glyph(id, font, texture, ascent, height, unicodes, permission, placeholders, tabcomplete, isEmoji, defaultShadow);
   }

   public final boolean hasPermission(@Nullable Player player) {
      return player == null || ((CharSequence)this.permission).length() == 0 || player.hasPermission(this.permission);
   }

   @NotNull
   public final String getGlyphTag() {
      return this.glyphTag;
   }

   @JvmOverloads
   @NotNull
   public Component glyphComponent(boolean colorable, @Nullable GlyphShadow shadow, @NotNull IntRange bitmapIndexRange) {
      Component component;
      if (Intrinsics.areEqual(bitmapIndexRange, IntRange.Companion.getEMPTY())) {
         GlyphShadow.Companion companion = GlyphShadow.Companion;
         Component component4 = this.getComponent();
         GlyphShadow.Companion companion2 = companion;
         int value = 0;
         Component component2;
         if (colorable) {
            component2 = component4;
         } else {
            component2 = component4.color(this.getDefaultColor());
            Iterable children = (Iterable)component4.children();
            Component component3 = component2;
            Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(children, 10)));

            for(Object item : children) {
               Component it = (Component)item;
               int value2 = 0;
               destination.add(it.color(this.getDefaultColor()));
            }

            component2 = component3.children((List)destination);
         }

         component = companion2.glyphShadow(component2, shadow);
      } else {
         component = (Component)this.bitmapComponent(bitmapIndexRange, colorable, shadow);
      }

      return component;
   }

   @NotNull
   public FontProvider[] getFontProviders() {
      Lazy lazy = this.fontProviders$delegate;
      return (FontProvider[])lazy.getValue();
   }

   @JvmOverloads
   @NotNull
   public final Component glyphComponent(boolean colorable, @Nullable GlyphShadow shadow) {
      return this.glyphComponent(colorable, shadow, kotlin.ranges.IntRange.Companion.getEMPTY());
   }

   @JvmOverloads
   @NotNull
   public final Component glyphComponent(boolean colorable) {
      return this.glyphComponent(colorable, this.getDefaultShadow(), kotlin.ranges.IntRange.Companion.getEMPTY());
   }

   @JvmOverloads
   @NotNull
   public final Component glyphComponent() {
      return this.glyphComponent(false, this.getDefaultShadow(), kotlin.ranges.IntRange.Companion.getEMPTY());
   }

   private static final char[] chars_delegate$lambda$0(Glyph glyph) {
      Iterable iterable = (Iterable)glyph.getUnicodes();
      int value2 = 0;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         String it = (String)element;
         int value = 0;
         Iterable list = (Iterable)StringsKt.toList((CharSequence)it);
         CollectionsKt.addAll(destination, list);
      }

      return CollectionsKt.toCharArray((Collection)((List)destination));
   }

   private static final CharSequence formattedUnicodes_delegate$lambda$0$0(String it) {
      return (CharSequence)StringUtilsKt.joinToString(it, Shift.INSTANCE.of(-1));
   }

   private static final String formattedUnicodes_delegate$lambda$0(Glyph glyph) {
      return CollectionsKt.joinToString((Iterable)glyph.getUnicodes(), (CharSequence)"\n", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", Glyph::formattedUnicodes_delegate$lambda$0$0);
   }

   private static final Component component_delegate$lambda$0(Glyph glyph) {
      return glyph.registerComponent();
   }

   private static final ComponentLike tagConfig_delegate$lambda$0$0(Glyph glyph2, MatchResult match, TextComponent.Builder builder) {
      kotlin.ranges.IntRange intRange;
      String group = match.group();
      CharSequence charSequence = (CharSequence)StringsKt.substringBefore(StringsKt.substringAfter(StringsKt.substringAfter(group, "<glyph:", group), "<g:", StringsKt.substringAfter(group, "<glyph:", group)), ">", StringsKt.substringAfter(StringsKt.substringAfter(group, "<glyph:", group), "<g:", StringsKt.substringAfter(group, "<glyph:", group)));
      String[] strs = new String[]{":"};
      List args = StringsKt.split(charSequence, strs, false, 0);
      Iterable iterable = (Iterable)args;
      boolean flag;
      if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator = iterable.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Object element = iterator.next();
            String it = (String)element;
            int value = 0;
            if (Intrinsics.areEqual(it, "colorable") || Intrinsics.areEqual(it, "c")) {
               flag = true;
               break;
            }
         }
      }

      boolean colorable = flag;
      int idxFound;
      int index = 0;
      Iterator it = args.iterator();

      while(true) {
         if (!it.hasNext()) {
            idxFound = -1;
            break;
         }

         Object item = it.next();
         String it_6 = (String)item;
         int value2 = 0;
         if (Intrinsics.areEqual(it_6, "shadow") || Intrinsics.areEqual(it_6, "s")) {
            idxFound = index;
            break;
         }

         ++index;
      }

      int shadowIdx = idxFound + 1;
      String shadow = (String)CollectionsKt.getOrNull(args, shadowIdx);
      Iterator iterator2 = ((Iterable)args).iterator();

      while(true) {
         if (iterator2.hasNext()) {
            String it_5 = (String)iterator2.next();
            int value3 = 0;
            IntRange intRange2 = RangeUtilsKt.toIntRangeOrNull(it_5);
            if (intRange2 == null) {
               Integer number = StringsKt.toIntOrNull(it_5);
               if (number != null) {
                  int i = ((Number)number).intValue();
                  int value4 = 0;
                  intRange2 = new IntRange(i, i);
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

      IntRange bitmapIndex = intRange;
      Glyph glyph = glyph2;
      boolean flag2 = colorable;
      GlyphShadow glyphShadow;
      if (shadow != null) {
         Color color = DyeCommandKt.toColor(shadow);
         if (color != null) {
            Color color2 = color;
            int value5 = 0;
            GlyphShadow glyphShadow2 = new GlyphShadow(color2);
            glyph = glyph2;
            flag2 = colorable;
            glyphShadow = glyphShadow2;
            return (ComponentLike)glyph.glyphComponent(flag2, glyphShadow, bitmapIndex);
         }
      }

      glyphShadow = null;
      return (ComponentLike)glyph.glyphComponent(flag2, glyphShadow, bitmapIndex);
   }

   private static final TextReplacementConfig tagConfig_delegate$lambda$0(Glyph glyph) {
        return TextReplacementConfig.builder().match(glyph.baseRegex.getPattern()).replacement((arg_0, arg_1) -> Glyph.tagConfig_delegate$lambda$0$0(glyph, arg_0, arg_1)).build();
    }

   private static final ComponentLike escapeTagConfig_delegate$lambda$0$0(MatchResult match, TextComponent.Builder b) {
      return (ComponentLike)b.content("\\\\" + match.group(1));
   }

   private static final TextReplacementConfig escapeTagConfig_delegate$lambda$0(Glyph glyph) {
      return TextReplacementConfig.builder().match(glyph._baseRegex).replacement(Glyph::escapeTagConfig_delegate$lambda$0$0).build();
   }

   private static final ComponentLike unescapeTagConfig_delegate$lambda$0$0(MatchResult match, TextComponent.Builder b) {
      String str = match.group(1);
      return (ComponentLike)b.content(StringsKt.removePrefix(str, (CharSequence)"\\"));
   }

   private static final TextReplacementConfig unescapeTagConfig_delegate$lambda$0(Glyph glyph) {
      return TextReplacementConfig.builder().match(glyph.escapedRegex.getPattern()).replacement(Glyph::unescapeTagConfig_delegate$lambda$0$0).build();
   }

   private static final TextReplacementConfig placeholderConfig_delegate$lambda$0(Glyph glyph) {
      TextReplacementConfig.Builder builder = TextReplacementConfig.builder();
      int value = 0;
      TextReplacementConfig.Builder builder2 = !((Collection)glyph.placeholders).isEmpty() ? builder : null;
      TextReplacementConfig textReplacementConfig;
      if (builder2 != null) {
         builder = builder2.match("(?<!\\\\)" + glyph.placeholderMatch);
         if (builder != null) {
            TextReplacementConfig.Builder it = builder.replacement((ComponentLike)glyph.glyphComponent(false, glyph.getDefaultShadow(), kotlin.ranges.IntRange.Companion.getEMPTY()));
            if (it != null) {
               textReplacementConfig = it.build();
               return textReplacementConfig;
            }
         }
      }

      textReplacementConfig = null;
      return textReplacementConfig;
   }

   private static final ComponentLike escapePlaceholderConfig_delegate$lambda$0$1(MatchResult match, TextComponent.Builder b) {
      return (ComponentLike)b.content("\\\\" + match.group(1));
   }

   private static final TextReplacementConfig escapePlaceholderConfig_delegate$lambda$0(Glyph glyph) {
      TextReplacementConfig.Builder builder = TextReplacementConfig.builder();
      int value = 0;
      TextReplacementConfig.Builder builder2 = !((Collection)glyph.placeholders).isEmpty() ? builder : null;
      TextReplacementConfig textReplacementConfig;
      if (builder2 != null) {
         builder = builder2.match("(?<!\\\\)" + glyph.placeholderMatch);
         if (builder != null) {
            TextReplacementConfig.Builder it = builder.replacement(Glyph::escapePlaceholderConfig_delegate$lambda$0$1);
            if (it != null) {
               textReplacementConfig = it.build();
               return textReplacementConfig;
            }
         }
      }

      textReplacementConfig = null;
      return textReplacementConfig;
   }

   private static final ComponentLike unescapePlaceholderConfig_delegate$lambda$0$1(MatchResult match, TextComponent.Builder b) {
      String str = match.group(1);
      return (ComponentLike)b.content(StringsKt.removePrefix(str, (CharSequence)"\\"));
   }

   private static final TextReplacementConfig unescapePlaceholderConfig_delegate$lambda$0(Glyph glyph) {
      TextReplacementConfig.Builder builder = TextReplacementConfig.builder();
      int value = 0;
      TextReplacementConfig.Builder builder2 = !((Collection)glyph.placeholders).isEmpty() ? builder : null;
      TextReplacementConfig textReplacementConfig;
      if (builder2 != null) {
         builder = builder2.match("\\\\" + glyph.placeholderMatch);
         if (builder != null) {
            TextReplacementConfig.Builder it = builder.replacement(Glyph::unescapePlaceholderConfig_delegate$lambda$0$1);
            if (it != null) {
               textReplacementConfig = it.build();
               return textReplacementConfig;
            }
         }
      }

      textReplacementConfig = null;
      return textReplacementConfig;
   }

   private static final FontProvider[] fontProviders_delegate$lambda$0(Glyph glyph) {
      Collection collection = (Collection)glyph.getUnicodes();
      Object obj;
      if (collection.isEmpty()) {
         int value = 0;
         obj = RequiredGlyph.INSTANCE.getUnicodes();
      } else {
         obj = collection;
      }

      List unicodes = (List)obj;
      FontProvider[] fontProviders = new FontProvider[1];
      BitMapFontProvider bitMapFontProvider = FontProvider.bitMap(glyph.getTexture(), glyph.height, RangesKt.coerceAtMost(glyph.ascent, glyph.height), unicodes);
      fontProviders[0] = bitMapFontProvider;
      return fontProviders;
   }

   static {
      NamespacedKey namespacedKey = NamespacedKey.fromString("nexo:original_item_rename");
      ORIGINAL_ITEM_RENAME = namespacedKey;
      String asString = ORIGINAL_ITEM_RENAME.asString();
      ORIGINAL_ITEM_RENAME_STRING = asString;
      Key key = Key.key("minecraft:required/exit_icon.png");
      REQUIRED_GLYPH = key;
      char ch = 'ꐏ';
      REQUIRED_CHAR = String.valueOf(ch);
      assignedGlyphUnicodes = (Map)(new Object2ObjectOpenHashMap());
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010%\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\n0\u00162\u0006\u0010\u001a\u001a\u00020\u001bJ\u000e\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\nR\u0016\u0010\u0004\u001a\u00070\u0005¢\u0006\u0002\b\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0016\u0010\t\u001a\u00070\n¢\u0006\u0002\b\u000b¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\u000e\u001a\u00070\u000f¢\u0006\u0002\b\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0012\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\rR&\u0010\u0014\u001a\u0014\u0012\u0004\u0012\u00020\n\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00160\u0015X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018¨\u0006\u001f"},
      d2 = {"Lcom/nexomc/nexo/glyphs/Glyph$Companion;", "", "<init>", "()V", "ORIGINAL_ITEM_RENAME", "Lorg/bukkit/NamespacedKey;", "Lorg/jetbrains/annotations/Nullable;", "getORIGINAL_ITEM_RENAME", "()Lorg/bukkit/NamespacedKey;", "ORIGINAL_ITEM_RENAME_STRING", "", "Lorg/jetbrains/annotations/NotNull;", "getORIGINAL_ITEM_RENAME_STRING", "()Ljava/lang/String;", "REQUIRED_GLYPH", "Lnet/kyori/adventure/key/Key;", "getREQUIRED_GLYPH", "()Lnet/kyori/adventure/key/Key;", "REQUIRED_CHAR", "getREQUIRED_CHAR", "assignedGlyphUnicodes", "", "", "getAssignedGlyphUnicodes$com_nexomc_core", "()Ljava/util/Map;", "calculateGlyphUnicodes", "glyphSection", "Lorg/bukkit/configuration/ConfigurationSection;", "containsTagOrPlaceholder", "", "string", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NamespacedKey getORIGINAL_ITEM_RENAME() {
         return Glyph.ORIGINAL_ITEM_RENAME;
      }

      @NotNull
      public final String getORIGINAL_ITEM_RENAME_STRING() {
         return Glyph.ORIGINAL_ITEM_RENAME_STRING;
      }

      @NotNull
      public final Key getREQUIRED_GLYPH() {
         return Glyph.REQUIRED_GLYPH;
      }

      @NotNull
      public final String getREQUIRED_CHAR() {
         return Glyph.REQUIRED_CHAR;
      }

      @NotNull
      public final Map<String, List<String>> getAssignedGlyphUnicodes$com_nexomc_core() {
         return Glyph.assignedGlyphUnicodes;
      }

      @NotNull
      public final List<String> calculateGlyphUnicodes(@NotNull ConfigurationSection glyphSection) {
         Object obj = this.getAssignedGlyphUnicodes$com_nexomc_core().computeIfAbsent(glyphSection.getName(), (a0_ -> Companion.calculateGlyphUnicodes$lambda$1((b0_ -> Companion.calculateGlyphUnicodes$lambda$0(glyphSection, (String)b0_)), a0_)));
         return (List)obj;
      }

      public final boolean containsTagOrPlaceholder(@NotNull String string) {
         boolean flag;
         if (!GlyphTag.INSTANCE.containsTag(string)) {
            Set keySet = NexoPlugin.Companion.instance().fontManager().getPlaceholderGlyphMap().keySet();
            Iterable iterable = (Iterable)keySet;
            boolean flag2;
            if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
               flag2 = false;
            } else {
               Iterator iterator = iterable.iterator();

               while(true) {
                  if (!iterator.hasNext()) {
                     flag2 = false;
                     break;
                  }

                  Object element = iterator.next();
                  CharSequence p0 = (CharSequence)element;
                  int value = 0;
                  if (StringsKt.contains((CharSequence)string, p0, false)) {
                     flag2 = true;
                     break;
                  }
               }
            }

            if (!flag2) {
               flag = false;
               return flag;
            }
         }

         flag = true;
         return flag;
      }

      private static final List calculateGlyphUnicodes$lambda$0(ConfigurationSection glyphSection, String it) {
         Collection collection = (Collection)NexoYamlKt.stringListOrSingle(glyphSection, "char", (String)null);
         Object obj;
         if (collection.isEmpty()) {
            int value = 0;
            int min = 0;
            min = 42000;
            Iterable iterable = (Iterable)CollectionsKt.flatten((Iterable)Glyph.Companion.getAssignedGlyphUnicodes$com_nexomc_core().values());
            int value5 = 0;
            Collection destination = (Collection)(new ArrayList());

            for(Object element : iterable) {
               String it_4 = (String)element;
               int value2 = 0;
               CharSequence charSequence = (CharSequence)it_4;

               for(int i = 0; i < charSequence.length(); ++i) {
                  destination.add((int)charSequence.charAt(i));
               }
            }

            Set usedCharIds = CollectionsKt.toMutableSet((Iterable)CollectionsKt.sorted((Iterable)((List)destination)));
            Pair pair = TuplesKt.to(glyphSection.getInt("rows", 1), glyphSection.getInt("columns", 1));
            value5 = ((Number)pair.component1()).intValue();
            int columns = ((Number)pair.component2()).intValue();
            Iterable iterable2 = (Iterable)RangesKt.until(0, value5 * columns);
            Collection collection3 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));
            Iterator iterator = iterable2.iterator();

            while(iterator.hasNext()) {
               int nextInt = ((IntIterator)iterator).nextInt();

               for(int j = 0; usedCharIds.contains(min); ++min) {
               }

               ((Collection)usedCharIds).add(min);
               collection3.add((char)min);
            }

            iterable2 = (Iterable)CollectionsKt.chunked((Iterable)((List)collection3), columns);
            collection3 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

            for(Object obj2 : iterable2) {
               List it_1 = (List)obj2;
               int value4 = 0;
               collection3.add(CollectionsKt.joinToString((Iterable)it_1, (CharSequence)"", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null));
            }

            List unicodes = (List)collection3;
            int value6 = 0;
            if (!Settings.DISABLE_AUTOMATIC_GLYPH_CODE.toBool()) {
               glyphSection.set("char", unicodes.size() == 1 ? CollectionsKt.first(unicodes) : unicodes);
               NexoYamlKt.remove(NexoYamlKt.remove(glyphSection, "rows"), "columns");
            }

            obj = unicodes;
         } else {
            obj = collection;
         }

         return (List)obj;
      }

      private static final List calculateGlyphUnicodes$lambda$1(Function1 tmp0, Object p0) {
         return (List)tmp0.invoke(p0);
      }
   }

   /** Kotlin default-argument bridge, kept for binary compatibility with classes that were not recompiled. */
   public static Component glyphComponent$default(Glyph glyph, boolean colorable, GlyphShadow shadow, kotlin.ranges.IntRange range, int mask, Object marker) {
      if ((mask & 1) != 0) colorable = false;
      if ((mask & 2) != 0) shadow = glyph.defaultShadow;
      if ((mask & 4) != 0) range = kotlin.ranges.IntRange.Companion.getEMPTY();
      return glyph.glyphComponent(colorable, shadow, range);
   }
}
