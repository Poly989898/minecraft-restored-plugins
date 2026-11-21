package com.nexomc.nexo.glyphs;

import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.StringUtilsKt;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function1;
import kotlin.text.StringsKt;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
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
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R \u0010\u0007\u001a\u00070\b¢\u0006\u0002\b\t8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0012\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u0014\u0010\u0014\u001a\u00020\u0005X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0016\u0010\u0017\u001a\u00070\u0018¢\u0006\u0002\b\t¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0016\u0010\u001b\u001a\u00070\u001c¢\u0006\u0002\b\t¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0016\u0010\u001f\u001a\u00070\u001c¢\u0006\u0002\b\t¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u001e¨\u0006$"},
   d2 = {"Lcom/nexomc/nexo/glyphs/ShiftTag;", "", "<init>", "()V", "SHIFT", "", "SHIFT_SHORT", "FONT", "Lnet/kyori/adventure/key/Key;", "Lorg/jetbrains/annotations/NotNull;", "getFONT", "()Lnet/kyori/adventure/key/Key;", "FONT$delegate", "Lkotlin/Lazy;", "PATTERN", "Ljava/util/regex/Pattern;", "getPATTERN", "()Ljava/util/regex/Pattern;", "ESCAPED_PATTERN", "getESCAPED_PATTERN", "PERMISSION", "getPERMISSION", "()Ljava/lang/String;", "RESOLVER", "Lnet/kyori/adventure/text/minimessage/tag/resolver/TagResolver;", "getRESOLVER", "()Lnet/kyori/adventure/text/minimessage/tag/resolver/TagResolver;", "REPLACEMENT_CONFIG", "Lnet/kyori/adventure/text/TextReplacementConfig;", "getREPLACEMENT_CONFIG", "()Lnet/kyori/adventure/text/TextReplacementConfig;", "ESCAPE_REPLACEMENT_CONFIG", "getESCAPE_REPLACEMENT_CONFIG", "containsTag", "", "string", "com.nexomc:core"}
)
public final class ShiftTag {
   @NotNull
   public static final ShiftTag INSTANCE = new ShiftTag();
   @NotNull
   private static final String SHIFT = "shift";
   @NotNull
   private static final String SHIFT_SHORT = "s";
   @NotNull
   private static final Lazy FONT$delegate = LazyKt.lazy(ShiftTag::FONT_delegate$lambda$0);
   @NotNull
   private static final Pattern PATTERN;
   @NotNull
   private static final Pattern ESCAPED_PATTERN;
   @NotNull
   private static final String PERMISSION;
   @NotNull
   private static final TagResolver RESOLVER;
   @NotNull
   private static final TextReplacementConfig REPLACEMENT_CONFIG;
   @NotNull
   private static final TextReplacementConfig ESCAPE_REPLACEMENT_CONFIG;

   private ShiftTag() {
   }

   @NotNull
   public final Key getFONT() {
      Lazy lazy = FONT$delegate;
      return (Key)lazy.getValue();
   }

   @NotNull
   public final Pattern getPATTERN() {
      return PATTERN;
   }

   @NotNull
   public final Pattern getESCAPED_PATTERN() {
      return ESCAPED_PATTERN;
   }

   @NotNull
   public final String getPERMISSION() {
      return PERMISSION;
   }

   @NotNull
   public final TagResolver getRESOLVER() {
      return RESOLVER;
   }

   @NotNull
   public final TextReplacementConfig getREPLACEMENT_CONFIG() {
      return REPLACEMENT_CONFIG;
   }

   @NotNull
   public final TextReplacementConfig getESCAPE_REPLACEMENT_CONFIG() {
      return ESCAPE_REPLACEMENT_CONFIG;
   }

   public final boolean containsTag(@NotNull String string) {
      return StringsKt.contains((CharSequence)string, (CharSequence)"<shift:", false) || StringsKt.contains((CharSequence)string, (CharSequence)"<s:", false);
   }

   private static final Key FONT_delegate$lambda$0() {
      Settings settings = Settings.SHIFT_FONT;
      Key key = Key.key("nexo:shift");
      return settings.toKey(key);
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
            optional = optional.map((a0_ -> ShiftTag.RESOLVER$lambda$0$1$0((kotlin.jvm.functions.Function1)((Object x) -> org.bukkit.Bukkit.getPlayer((String)x)), a0_)));
            if (optional != null) {
               player = (Player)optional.orElse((Object)null);
               return player;
            }
         }
      }

      player = null;
      return player;
   }

   private static final Tag RESOLVER$lambda$0(ArgumentQueue args, Context ctx) {
      int shift;
      Player player2;
      block: {
         String str = args.popOr("A shift value is required").value();
         Integer number = StringsKt.toIntOrNull(str);
         shift = number != null ? number : 0;
         Pointered target = ctx.target();
         if (target != null) {
            Optional optional = target.get(Identity.UUID);
            if (optional != null) {
               optional = optional.map((a0_ -> ShiftTag.RESOLVER$lambda$0$0((kotlin.jvm.functions.Function1)((Object x) -> org.bukkit.Bukkit.getPlayer((java.util.UUID)x)), a0_)));
               if (optional != null) {
                  player2 = (Player)optional.orElseGet((() -> ShiftTag.RESOLVER$lambda$0$1(ctx)));
                  break block;
               }
            }
         }

         player2 = null;
      }

      Player player = player2;
      Tag tag;
      if (player != null) {
         ShiftTag shiftTag = INSTANCE;
         if (!player.hasPermission(PERMISSION)) {
            tag = (Tag)Tag.preProcessParsed("<shift:" + shift + ">");
            return tag;
         }
      }

      tag = Tag.selfClosingInserting(Component.text(Shift.INSTANCE.of(shift)).font(INSTANCE.getFONT()));
      return tag;
   }

   private static final ComponentLike REPLACEMENT_CONFIG$lambda$0(MatchResult r, TextComponent.Builder builder) {
      Shift shift = Shift.INSTANCE;
      String str = r.group(1);
      Integer number = StringsKt.toIntOrNull(StringUtilsKt.substringBetween(str, "<shift:", ">"));
      return (ComponentLike)Component.text(shift.of(number != null ? number : 0));
   }

   private static final ComponentLike ESCAPE_REPLACEMENT_CONFIG$lambda$0(MatchResult r, TextComponent.Builder b) {
      String str = r.group(1);
      return (ComponentLike)b.content(StringsKt.removePrefix(str, (CharSequence)"\\\\"));
   }

   static {
      Pattern pattern = Pattern.compile("(<shift:(-?\\d+)>)");
      PATTERN = pattern;
      pattern = Pattern.compile("(\\\\<shift:(-?\\d+)>)");
      ESCAPED_PATTERN = pattern;
      PERMISSION = "nexo.shift";
      String[] strs = new String[]{"shift", "s"};
      TagResolver tagResolver = TagResolver.resolver(SetsKt.setOf(strs), ShiftTag::RESOLVER$lambda$0);
      RESOLVER = tagResolver;
      TextReplacementConfig.Builder builder = TextReplacementConfig.builder();
      ShiftTag shiftTag = INSTANCE;
      Object obj = builder.match(PATTERN).replacement(ShiftTag::REPLACEMENT_CONFIG$lambda$0).build();
      REPLACEMENT_CONFIG = (TextReplacementConfig)obj;
      TextReplacementConfig.Builder builder2 = TextReplacementConfig.builder();
      shiftTag = INSTANCE;
      Object obj2 = builder2.match(ESCAPED_PATTERN).replacement(ShiftTag::ESCAPE_REPLACEMENT_CONFIG$lambda$0).build();
      ESCAPE_REPLACEMENT_CONFIG = (TextReplacementConfig)obj2;
   }
}
