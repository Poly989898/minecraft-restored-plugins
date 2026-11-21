package com.nexomc.nexo.glyphs;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.font.FontProvider;
import team.unnamed.creative.font.SpaceFontProvider;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u0000 &2\u00020\u0001:\u0001&B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0016\u001a\u00020\u0017H\u0016J\u0010\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020\tH\u0014R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u001b\u0010\b\u001a\u00020\t8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\u000e\u001a\u00020\u000fX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0012\u001a\u00020\u0013X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u001b\u0010\u0018\u001a\u00020\u00198BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u001c\u0010\r\u001a\u0004\b\u001a\u0010\u001bR!\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001e8VX\u0096\u0084\u0002¢\u0006\f\n\u0004\b\"\u0010\r\u001a\u0004\b \u0010!¨\u0006'"},
   d2 = {"Lcom/nexomc/nexo/glyphs/ShaderAnimatedGlyph;", "Lcom/nexomc/nexo/glyphs/AnimatedGlyph;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getSection", "()Lorg/bukkit/configuration/ConfigurationSection;", "offset", "", "getOffset", "()I", "offset$delegate", "Lkotlin/Lazy;", "font", "Lnet/kyori/adventure/key/Key;", "getFont", "()Lnet/kyori/adventure/key/Key;", "defaultColor", "Lnet/kyori/adventure/text/format/TextColor;", "getDefaultColor", "()Lnet/kyori/adventure/text/format/TextColor;", "registerComponent", "Lnet/kyori/adventure/text/Component;", "fontProvider", "Lteam/unnamed/creative/font/SpaceFontProvider;", "getFontProvider", "()Lteam/unnamed/creative/font/SpaceFontProvider;", "fontProvider$delegate", "fontProviders", "", "Lteam/unnamed/creative/font/FontProvider;", "getFontProviders", "()[Lteam/unnamed/creative/font/FontProvider;", "fontProviders$delegate", "onFrameCountReady", "", "frameCount", "Companion", "com.nexomc:core"}
)
public final class ShaderAnimatedGlyph extends AnimatedGlyph {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ConfigurationSection section;
   @NotNull
   private final Lazy offset$delegate;
   @NotNull
   private final Key font;
   @NotNull
   private final TextColor defaultColor;
   @NotNull
   private final Lazy fontProvider$delegate;
   @NotNull
   private final Lazy fontProviders$delegate;
   public static final int PRIVATE_USE_FIRST = 57344;
   @NotNull
   private static final TextColor GIF_COLOR;

   public ShaderAnimatedGlyph(@NotNull ConfigurationSection section) {
      super(section);
      this.section = section;
      this.offset$delegate = LazyKt.lazy((() -> ShaderAnimatedGlyph.offset_delegate$lambda$0(this)));
      Key key = Key.key("nexo", "gifs/" + this.getId());
      this.font = key;
      this.defaultColor = GIF_COLOR;
      this.fontProvider$delegate = LazyKt.lazy((() -> ShaderAnimatedGlyph.fontProvider_delegate$lambda$0(this)));
      this.fontProviders$delegate = LazyKt.lazy((() -> ShaderAnimatedGlyph.fontProviders_delegate$lambda$0(this)));
      NexoYamlKt.remove(NexoYamlKt.remove(this.section, "char"), "chars");
      Glyph.Companion.calculateGlyphUnicodes(this.section);
   }

   @NotNull
   public final ConfigurationSection getSection() {
      return this.section;
   }

   public final int getOffset() {
      Lazy lazy = this.offset$delegate;
      return ((Number)lazy.getValue()).intValue();
   }

   @NotNull
   public Key getFont() {
      return this.font;
   }

   @NotNull
   public TextColor getDefaultColor() {
      return this.defaultColor;
   }

   @NotNull
   public Component registerComponent() {
      net.kyori.adventure.text.event.HoverEvent<net.kyori.adventure.text.Component> hoverEvent;
      String str;
      block: {
         String it = Settings.GLYPH_HOVER_TEXT.toString();
         int value = 0;
         str = (String)CollectionsKt.firstOrNull(this.getPlaceholders());
         if (str != null) {
            String p = str;
            int value2 = 0;
            str = StringsKt.replace(it, "<glyph_placeholder>", p, false);
            if (str != null) {
               break block;
            }
         }

         str = it;
      }

      CharSequence it = (CharSequence)StringsKt.replace(str, "<glyph_id>", this.getId(), false);
      CharSequence charSequence;
      if (it.length() == 0) {
         int value3 = 0;
         charSequence = null;
      } else {
         charSequence = it;
      }

      block: {
         String str2 = (String)charSequence;
         if (str2 != null) {
            Component it_1 = AdventureUtilsKt.deserialize(str2);
            if (it_1 != null) {
               int value4 = 0;
               hoverEvent = HoverEvent.showText(it_1);
               break block;
            }
         }

         hoverEvent = null;
      }

      HoverEvent hoverText = hoverEvent;
      Component component = ((TextComponent)((TextComponent)Component.text(CollectionsKt.joinToString((Iterable)RangesKt.until(0, this.getFrameCount()), (CharSequence)Companion.unicode(this.getFrameCount()), (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1<Integer, String>)(p0 -> Companion.unicode(p0)))).font(this.getFont())).color(GIF_COLOR)).hoverEvent((HoverEventSource)hoverText);
      return component;
   }

   private final SpaceFontProvider getFontProvider() {
      Lazy lazy = this.fontProvider$delegate;
      Object value = lazy.getValue();
      return (SpaceFontProvider)value;
   }

   @NotNull
   public FontProvider[] getFontProviders() {
      Lazy lazy = this.fontProviders$delegate;
      return (FontProvider[])lazy.getValue();
   }

   protected void onFrameCountReady(int frameCount) {
      if (this.getUnicodes().isEmpty()) {
         Collection unicodes = (Collection)this.getUnicodes();
         Companion companion = Companion;
         ArrayList arrayList = new ArrayList(frameCount);

         for(int i = 0; i < frameCount; ++i) {
            int value = 0;
            arrayList.add(companion.unicode(i));
         }

         Iterable iterable = (Iterable)((List)arrayList);
         CollectionsKt.addAll(unicodes, iterable);
         NexoPlugin.Companion.instance().fontManager().registerGlyph(this);
      }

   }

   private static final int offset_delegate$lambda$0(ShaderAnimatedGlyph shaderAnimatedGlyph) {
      return shaderAnimatedGlyph.section.getInt("offset", MathKt.roundToInt(-((float)shaderAnimatedGlyph.getHeight() * shaderAnimatedGlyph.getAspectRatio()) - (float)1));
   }

   private static final SpaceFontProvider fontProvider_delegate$lambda$0(ShaderAnimatedGlyph shaderAnimatedGlyph) {
      return FontProvider.space().advance(Companion.unicode(shaderAnimatedGlyph.getFrameCount()), shaderAnimatedGlyph.getOffset()).build();
   }

   private static final FontProvider[] fontProviders_delegate$lambda$0(ShaderAnimatedGlyph shaderAnimatedGlyph) {
      return (FontProvider[])ArraysKt.plus(shaderAnimatedGlyph.getFontProviders(), shaderAnimatedGlyph.getFontProvider());
   }

   static {
      TextColor textColor = TextColor.color(16711422);
      GIF_COLOR = textColor;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\u00070\u0007¢\u0006\u0002\b\b¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\u000e"},
      d2 = {"Lcom/nexomc/nexo/glyphs/ShaderAnimatedGlyph$Companion;", "", "<init>", "()V", "PRIVATE_USE_FIRST", "", "GIF_COLOR", "Lnet/kyori/adventure/text/format/TextColor;", "Lorg/jetbrains/annotations/NotNull;", "getGIF_COLOR", "()Lnet/kyori/adventure/text/format/TextColor;", "unicode", "", "index", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final TextColor getGIF_COLOR() {
         return ShaderAnimatedGlyph.GIF_COLOR;
      }

      @NotNull
      public final String unicode(int index) {
         char[] ches = Character.toChars('\ue000' + index);
         return String.valueOf(ArraysKt.first(ches));
      }
   }
}
