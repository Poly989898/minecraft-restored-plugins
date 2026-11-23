package com.nexomc.nexo.compatibilities.placeholderapi;
import com.nexomc.nexo.compatibilities.placeholderapi.PapiTag;
import java.util.Set;

import com.nexomc.nexo.compatibilities.CompatibilitiesManager;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.StringUtilsKt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function1;
import kotlin.text.StringsKt;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.identity.Identity;
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
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\"\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00142\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u0019J\u000e\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0014\u0010\u000b\u001a\u00020\u0005X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\u000e\u001a\u00070\u000f¢\u0006\u0002\b\u0010¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/placeholderapi/PapiTag;", "", "<init>", "()V", "PAPI", "", "PAPI_SHORT", "PATTERN", "Ljava/util/regex/Pattern;", "getPATTERN", "()Ljava/util/regex/Pattern;", "PERMISSION", "getPERMISSION", "()Ljava/lang/String;", "RESOLVER", "Lnet/kyori/adventure/text/minimessage/tag/resolver/TagResolver;", "Lorg/jetbrains/annotations/NotNull;", "getRESOLVER", "()Lnet/kyori/adventure/text/minimessage/tag/resolver/TagResolver;", "replacePlaceholders", "Lnet/kyori/adventure/text/Component;", "component", "player", "Lorg/bukkit/entity/Player;", "checkPermission", "", "containsTag", "string", "com.nexomc:core"}
)
public final class PapiTag {
   @NotNull
   public static final PapiTag INSTANCE = new PapiTag();
   @NotNull
   private static final String PAPI = "placeholderapi";
   @NotNull
   private static final String PAPI_SHORT = "papi";
   @NotNull
   private static final Pattern PATTERN;
   @NotNull
   private static final String PERMISSION;
   @NotNull
   private static final TagResolver RESOLVER;

   private PapiTag() {
   }

   @NotNull
   public final Pattern getPATTERN() {
      return PATTERN;
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
   public final Component replacePlaceholders(@NotNull Component component, @Nullable Player player, boolean checkPermission) {
      if (checkPermission && player != null && !player.hasPermission(PERMISSION)) {
         return component;
      } else {
         Component component2 = component.replaceText((a0_ -> PapiTag.replacePlaceholders$lambda$0(player, a0_)));
         return component2;
      }
   }

   public final boolean containsTag(@NotNull String string) {
      return StringsKt.contains((CharSequence)string, (CharSequence)"<placeholderapi:", false) || StringsKt.contains((CharSequence)string, (CharSequence)"<papi:", false);
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
            optional = optional.map((a0_ -> PapiTag.RESOLVER$lambda$0$1$0((kotlin.jvm.functions.Function1)((Object x) -> org.bukkit.Bukkit.getPlayer((String)x)), a0_)));
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
      Player player2;
      block: {
         Pointered target = ctx.target();
         if (target != null) {
            Optional optional = target.get(Identity.UUID);
            if (optional != null) {
               optional = optional.map((a0_ -> PapiTag.RESOLVER$lambda$0$0((kotlin.jvm.functions.Function1)((Object x) -> org.bukkit.Bukkit.getPlayer((java.util.UUID)x)), a0_)));
               if (optional != null) {
                  player2 = (Player)optional.orElseGet((() -> PapiTag.RESOLVER$lambda$0$1(ctx)));
                  break block;
               }
            }
         }

         player2 = null;
      }

      Player player = player2;
      List placeholders = (List)(new ArrayList());

      while(args.hasNext()) {
         Collection collection = (Collection)placeholders;
         String str = args.pop().value();
         collection.add(StringUtilsKt.appendIfMissing(StringUtilsKt.prependIfMissing(str, "%"), "%"));
      }

      if (placeholders.isEmpty()) {
         return null;
      } else {
         if (player != null) {
            PapiTag papiTag = INSTANCE;
            if (!player.hasPermission(PERMISSION)) {
               return null;
            }
         }

         if (!CompatibilitiesManager.INSTANCE.isCompatibilityEnabled("PlaceholderAPI")) {
            return null;
         } else {
            List list = PlaceholderAPI.setPlaceholders(player, placeholders);
            Iterable iterable = (Iterable)list;
            Object[] result = new Component[CollectionsKt.count(iterable)];
            int index = 0;

            for(Object item : iterable) {
               int value = index++;
               String it = (String)item;
               int value2 = value;
               int value3 = 0;
               Component component = AdventureUtilsKt.deserialize(it);
               result[value2] = component;
            }

            Component[] components = (Component[])(result);
            return Tag.selfClosingInserting((Component)Component.textOfChildren((ComponentLike[])Arrays.copyOf(components, components.length)));
         }
      }
   }

   private static final ComponentLike replacePlaceholders$lambda$0$0(Player player, MatchResult r, TextComponent.Builder builder) {
      return player != null ? (ComponentLike)AdventureUtils.INSTANCE.getMINI_MESSAGE().deserialize(r.group(), (Pointered)player) : (ComponentLike)AdventureUtils.INSTANCE.getMINI_MESSAGE().deserialize(r.group());
   }

   private static final void replacePlaceholders$lambda$0(Player player, TextReplacementConfig.Builder builder) {
        builder.match(PATTERN).replacement((arg_0, arg_1) -> PapiTag.replacePlaceholders$lambda$0$0(player, arg_0, arg_1)).build();
    }

   static {
      Pattern pattern = Pattern.compile("<(placeholderapi|papi):(.*)>");
      PATTERN = pattern;
      PERMISSION = "nexo.papi";
      String[] strs = new String[]{"placeholderapi", "papi"};
      TagResolver tagResolver = TagResolver.resolver(SetsKt.mutableSetOf(strs), PapiTag::RESOLVER$lambda$0);
      RESOLVER = tagResolver;
   }
}
