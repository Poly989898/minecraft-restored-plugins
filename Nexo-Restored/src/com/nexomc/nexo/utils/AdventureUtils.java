package com.nexomc.nexo.utils;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.compatibilities.placeholderapi.PapiTag;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.glyphs.GlyphTag;
import com.nexomc.nexo.glyphs.ShiftTag;
import java.util.Collection;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import kotlin.text.Regex;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010#\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0006\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\b\u0010\n\u001a\u00020\u000bH\u0016J\u000e\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eJ\u000e\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eJ\u0006\u0010\u0010\u001a\u00020\u000bJ\u001b\u0010\u0011\u001a\u00070\u0012¢\u0006\u0002\b\u0013*\u00020\u00122\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0015J\u001e\u0010&\u001a\u00020\t2\u0006\u0010'\u001a\u00020\t2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0007b\u0002\b(J\u0014\u0010)\u001a\u00020\t2\u0006\u0010'\u001a\u00020\tH\u0007b\u0002\b(J\u0014\u0010*\u001a\u00020\t2\u0006\u0010'\u001a\u00020\tH\u0007b\u0002\b(J\u001c\u0010\r\u001a\u00020\u000e2\u0006\u0010+\u001a\u00020\t2\u0006\u0010,\u001a\u00020\tH\u0007b\u0002\b(J\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010+\u001a\u00020\t2\u0006\u0010,\u001a\u00020\u0012J\n\u0010-\u001a\u00020\t*\u00020\u0012R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\u0016\u001a\u00070\u000e¢\u0006\u0002\b\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0017\u001a\u00070\u0018¢\u0006\u0002\b\u00138\u0006X\u0087\u0004\u0092\u0002\u0002\b\u0019¢\u0006\u0002\n\u0000R\u0016\u0010\u001a\u001a\u00070\u0018¢\u0006\u0002\b\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0019\u0010\u001d\u001a\r\u0012\t\u0012\u00070\u000e¢\u0006\u0002\b\u00130\u001eX\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u001f\u001a\u00070 ¢\u0006\u0002\b\u00138\u0006X\u0087\u0004\u0092\u0002\u0002\b\u0019¢\u0006\u0002\n\u0000R\u0016\u0010!\u001a\u00070\u0018¢\u0006\u0002\b\u00138F¢\u0006\u0006\u001a\u0004\b\"\u0010\u001cR\u0013\u0010#\u001a\u00070\u0018¢\u0006\u0002\b\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010$\u001a\u00070\u0018¢\u0006\u0002\b\u0013¢\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001c¨\u0006."},
   d2 = {"Lcom/nexomc/nexo/utils/AdventureUtils;", "Lcom/nexomc/nexo/utils/Reloadable;", "<init>", "()V", "legacyRegex", "Lkotlin/text/Regex;", "containsLegacyCodes", "", "value", "", "reload", "", "injectTagResolver", "tagResolver", "Lnet/kyori/adventure/text/minimessage/tag/resolver/TagResolver;", "uninjectTagResolver", "resetNexoTagResolver", "setDefaultStyle", "Lnet/kyori/adventure/text/Component;", "Lorg/jetbrains/annotations/NotNull;", "color", "Lnet/kyori/adventure/text/format/TextColor;", "standardResolvers", "STANDARD_MINI_MESSAGE", "Lnet/kyori/adventure/text/minimessage/MiniMessage;", "Lkotlin/jvm/JvmField;", "STRICT_MINI_MESSAGE", "getSTRICT_MINI_MESSAGE", "()Lnet/kyori/adventure/text/minimessage/MiniMessage;", "NexoTagResolver", "", "LEGACY_SERIALIZER", "Lnet/kyori/adventure/text/serializer/legacy/LegacyComponentSerializer;", "MINI_MESSAGE", "getMINI_MESSAGE", "MM", "NEXO_SERIALIZER", "getNEXO_SERIALIZER", "parseMiniMessage", "message", "Lkotlin/jvm/JvmStatic;", "parseLegacy", "parseLegacyThroughMiniMessage", "string", "tag", "asFlatTextContent", "com.nexomc:core"}
)
public final class AdventureUtils implements Reloadable {
   @NotNull
   public static final AdventureUtils INSTANCE = new AdventureUtils();
   @NotNull
   private static final Regex legacyRegex = new Regex("[&§]\\w");
   @NotNull
   private static final TagResolver standardResolvers;
   @JvmField
   @NotNull
   public static final MiniMessage STANDARD_MINI_MESSAGE;
   @NotNull
   private static final MiniMessage STRICT_MINI_MESSAGE;
   @NotNull
   private static final Set<TagResolver> NexoTagResolver;
   @JvmField
   @NotNull
   public static final LegacyComponentSerializer LEGACY_SERIALIZER;
   @NotNull
   private static MiniMessage MM;
   @NotNull
   private static final MiniMessage NEXO_SERIALIZER;

   private AdventureUtils() {
   }

   public final boolean containsLegacyCodes(@NotNull String value) {
      CharSequence charSequence = (CharSequence)value;
      return legacyRegex.containsMatchIn(charSequence);
   }

   public void reload() {
      ((Collection)NexoTagResolver).remove(Message.Companion.getPREFIX_TAG_RESOLVER());
      Message.Companion.rebuildPrefixTagResolver();
      ((Collection)NexoTagResolver).add(Message.Companion.getPREFIX_TAG_RESOLVER());
      MiniMessage miniMessage = MiniMessage.builder().tags(TagResolver.resolver((Iterable)NexoTagResolver)).build();
      MM = miniMessage;
   }

   public final void injectTagResolver(@NotNull TagResolver tagResolver) {
      ((Collection)NexoTagResolver).add(tagResolver);
   }

   public final void uninjectTagResolver(@NotNull TagResolver tagResolver) {
      ((Collection)NexoTagResolver).remove(tagResolver);
   }

   public final void resetNexoTagResolver() {
      NexoTagResolver.clear();
      Set set = NexoTagResolver;
      TagResolver standard = TagResolver.standard();
      set.add(standard);
      NexoTagResolver.add(GlyphTag.INSTANCE.getRESOLVER());
      NexoTagResolver.add(ShiftTag.INSTANCE.getRESOLVER());
      NexoTagResolver.add(Message.Companion.getPREFIX_TAG_RESOLVER());
      NexoTagResolver.add(PapiTag.INSTANCE.getRESOLVER());
   }

   @NotNull
   public final Component setDefaultStyle(@NotNull Component component2, @Nullable TextColor color) {
      Component component = component2.decorationIfAbsent(TextDecoration.ITALIC, State.FALSE).colorIfAbsent(color);
      return component;
   }

   @NotNull
   public final MiniMessage getSTRICT_MINI_MESSAGE() {
      return STRICT_MINI_MESSAGE;
   }

   @NotNull
   public final MiniMessage getMINI_MESSAGE() {
      return MM;
   }

   @NotNull
   public final MiniMessage getNEXO_SERIALIZER() {
      return NEXO_SERIALIZER;
   }

   @JvmStatic
   @NotNull
   public static final String parseMiniMessage(@NotNull String message, @Nullable TagResolver tagResolver) {
      Object obj = INSTANCE.getMINI_MESSAGE().serialize(tagResolver != null ? INSTANCE.getMINI_MESSAGE().deserialize(message, tagResolver) : INSTANCE.getMINI_MESSAGE().deserialize(message));
      CharSequence charSequence = (CharSequence)obj;
      Regex regex = new Regex("\\\\(?!u)(?!n)(?!\")");
      String str = "";
      return regex.replace(charSequence, str);
   }

   @JvmStatic
   @NotNull
   public static final String parseLegacy(@NotNull String message) {
      Object obj = INSTANCE.getMINI_MESSAGE().serialize((Component)LEGACY_SERIALIZER.deserialize(message));
      CharSequence charSequence = (CharSequence)obj;
      Regex regex = new Regex("\\\\(?!u)(?!n)(?!\")");
      String str = "";
      return regex.replace(charSequence, str);
   }

   @JvmStatic
   @NotNull
   public static final String parseLegacyThroughMiniMessage(@NotNull String message) {
      LegacyComponentSerializer legacyComponentSerializer = LEGACY_SERIALIZER;
      MiniMessage minI_MESSAGE = INSTANCE.getMINI_MESSAGE();
      Object obj = INSTANCE.getMINI_MESSAGE().serialize((Component)LEGACY_SERIALIZER.deserialize(message));
      CharSequence charSequence = (CharSequence)obj;
      Regex regex = new Regex("\\\\(?!u)(?!n)(?!\")");
      String str = "";
      String str2 = legacyComponentSerializer.serialize(minI_MESSAGE.deserialize(regex.replace(charSequence, str)));
      return str2;
   }

   @JvmStatic
   @NotNull
   public static final TagResolver tagResolver(@NotNull String string, @NotNull String tag) {
      TagResolver.Single single = TagResolver.resolver(string, Tag.selfClosingInserting(INSTANCE.getMINI_MESSAGE().deserialize(tag)));
      return (TagResolver)single;
   }

   @NotNull
   public final TagResolver tagResolver(@NotNull String string, @NotNull Component tag) {
      TagResolver.Single single = TagResolver.resolver(string, Tag.selfClosingInserting(tag));
      return (TagResolver)single;
   }

   @NotNull
   public final String asFlatTextContent(@NotNull Component component2) {
      TextComponent textComponent = component2 instanceof TextComponent ? (TextComponent)component2 : null;
      if ((component2 instanceof TextComponent ? (TextComponent)component2 : null) == null) {
         return "";
      } else {
         TextComponent flatText = textComponent;
         StringBuilder stringBuilder = new StringBuilder();
         int value = 0;
         stringBuilder.append(flatText.content());
         stringBuilder.append(CollectionsKt.joinToString((Iterable)flatText.children(), (CharSequence)"", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", AdventureUtils::asFlatTextContent$lambda$0$0));
         HoverEvent hoverEvent = flatText.hoverEvent();
         Object obj = hoverEvent != null ? hoverEvent.value() : null;
         Component component = obj instanceof Component ? (Component)obj : null;
         if ((obj instanceof Component ? (Component)obj : null) != null) {
            Component hover = component;
            int value2 = 0;
            TextComponent textComponent2 = hover instanceof TextComponent ? (TextComponent)hover : null;
            if ((hover instanceof TextComponent ? (TextComponent)hover : null) != null) {
               TextComponent hoverText = textComponent2;
               stringBuilder.append(hoverText.content());
               stringBuilder.append(CollectionsKt.joinToString((Iterable)hoverText.children(), (CharSequence)"", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", AdventureUtils::asFlatTextContent$lambda$0$1$0));
            }
         }

         return stringBuilder.toString();
      }
   }

   private static final CharSequence asFlatTextContent$lambda$0$0(Component it) {
      AdventureUtils adventureUtils = INSTANCE;
      return (CharSequence)adventureUtils.asFlatTextContent(it);
   }

   private static final CharSequence asFlatTextContent$lambda$0$1$0(Component it) {
      AdventureUtils adventureUtils = INSTANCE;
      return (CharSequence)adventureUtils.asFlatTextContent(it);
   }

   static {
      TagResolver[] tagResolvers = new TagResolver[12];
      tagResolvers[0] = StandardTags.font();
      tagResolvers[1] = StandardTags.color();
      tagResolvers[2] = StandardTags.gradient();
      tagResolvers[3] = StandardTags.rainbow();
      tagResolvers[4] = StandardTags.decorations();
      tagResolvers[5] = StandardTags.newline();
      tagResolvers[6] = StandardTags.translatable();
      tagResolvers[7] = StandardTags.reset();
      byte b = 8;

      Object obj;
      try {
         int value = 0;
         obj = ResultShim.ok(StandardTags.pride());
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      tagResolvers[b] = (TagResolver)(ResultShim.isFailure(obj) ? null : obj);
      byte b2 = 9;

      try {
         int value2 = 0;
         obj = ResultShim.ok(StandardTags.shadowColor());
      } catch (Throwable ex) {
         obj = ResultShim.ok(ResultKt.createFailure(ex));
      }

      tagResolvers[b2] = (TagResolver)(ResultShim.isFailure(obj) ? null : obj);
      byte b3 = 10;

      try {
         int value3 = 0;
         obj = ResultShim.ok(StandardTags.sequentialHead());
      } catch (Throwable ex2) {
         obj = ResultShim.ok(ResultKt.createFailure(ex2));
      }

      tagResolvers[b3] = (TagResolver)(ResultShim.isFailure(obj) ? null : obj);
      byte b4 = 11;

      try {
         int value4 = 0;
         obj = ResultShim.ok(StandardTags.sprite());
      } catch (Throwable ex3) {
         obj = ResultShim.ok(ResultKt.createFailure(ex3));
      }

      tagResolvers[b4] = (TagResolver)(ResultShim.isFailure(obj) ? null : obj);
      TagResolver tagResolver = TagResolver.resolver((Iterable)CollectionsKt.listOfNotNull(tagResolvers));
      standardResolvers = tagResolver;
      MiniMessage miniMessage = MiniMessage.builder().tags(standardResolvers).build();
      STANDARD_MINI_MESSAGE = miniMessage;
      miniMessage = MiniMessage.builder().strict(true).build();
      STRICT_MINI_MESSAGE = miniMessage;
      TagResolver[] tagResolvers2 = new TagResolver[]{standardResolvers, GlyphTag.INSTANCE.getRESOLVER(), ShiftTag.INSTANCE.getRESOLVER(), Message.Companion.getPREFIX_TAG_RESOLVER(), PapiTag.INSTANCE.getRESOLVER()};
      NexoTagResolver = SetsKt.mutableSetOf(tagResolvers2);
      LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
      LEGACY_SERIALIZER = legacyComponentSerializer;
      MiniMessage miniMessage2 = MiniMessage.builder().tags(TagResolver.resolver((Iterable)NexoTagResolver)).build();
      MM = miniMessage2;
      MiniMessage.Builder builder = MiniMessage.builder();
      tagResolvers = new TagResolver[]{GlyphTag.INSTANCE.getRESOLVER(), ShiftTag.INSTANCE.getRESOLVER(), PapiTag.INSTANCE.getRESOLVER()};
      MiniMessage miniMessage3 = builder.tags(TagResolver.resolver(tagResolvers)).build();
      NEXO_SERIALIZER = miniMessage3;
   }
}
