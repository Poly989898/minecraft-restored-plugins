package com.nexomc.nexo.utils;

import kotlin.Metadata;
import kotlin.text.Regex;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\n\u0010\u0002\u001a\u00020\u0003*\u00020\u0001\u001a\n\u0010\u0004\u001a\u00020\u0003*\u00020\u0001\u001a\n\u0010\u0005\u001a\u00020\u0001*\u00020\u0003\u001a\u0014\u0010\u0005\u001a\u00020\u0001*\u00020\u00032\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u001a\n\u0010\b\u001a\u00020\u0003*\u00020\u0001*\n\u0010\u0000\"\u00020\u00012\u00020\u0001¨\u0006\t"},
   d2 = {"KyoriComponent", "Lnet/kyori/adventure/text/Component;", "serializeStrict", "", "serialize", "deserialize", "pointered", "Lnet/kyori/adventure/pointer/Pointered;", "toSimpleString", "com.nexomc:core"}
)
public final class AdventureUtilsKt {
   @NotNull
   public static final String serializeStrict(@NotNull Component component) {
      Object obj = AdventureUtils.INSTANCE.getSTRICT_MINI_MESSAGE().serialize(component);
      return (String)obj;
   }

   @NotNull
   public static final String serialize(@NotNull Component component) {
      Object obj = AdventureUtils.INSTANCE.getMINI_MESSAGE().serialize(component);
      return (String)obj;
   }

   @NotNull
   public static final Component deserialize(@NotNull String str) {
      Component component = AdventureUtils.INSTANCE.getMINI_MESSAGE().deserialize(AdventureUtils.INSTANCE.containsLegacyCodes(str) ? AdventureUtils.parseLegacy(str) : str);
      return component;
   }

   @NotNull
   public static final Component deserialize(@NotNull String str, @Nullable Pointered pointered) {
      Component component;
      if (pointered != null) {
         Component component2 = AdventureUtils.INSTANCE.getMINI_MESSAGE().deserialize(AdventureUtils.INSTANCE.containsLegacyCodes(str) ? AdventureUtils.parseLegacy(str) : str, pointered);
         component = component2;
      } else {
         component = AdventureUtils.INSTANCE.getMINI_MESSAGE().deserialize(AdventureUtils.INSTANCE.containsLegacyCodes(str) ? AdventureUtils.parseLegacy(str) : str);
      }

      return component;
   }

   @NotNull
   public static final String toSimpleString(@NotNull Component component) {
      CharSequence charSequence = (CharSequence)component.toString();
      Regex regex = new Regex(", (obfuscated|bold|strikethrough|underlined|italic)=not_set");
      String str = "";
      charSequence = (CharSequence)regex.replace(charSequence, str);
      regex = new Regex(", (color|shadowColor|clickEvent|hoverEvent|insertion|font)=null");
      str = "";
      charSequence = (CharSequence)regex.replace(charSequence, str);
      regex = new Regex("StyleImpl\\{(\\s*)}");
      str = "{}";
      charSequence = (CharSequence)regex.replace(charSequence, str);
      regex = new Regex(",\\s*}");
      str = "}";
      charSequence = (CharSequence)regex.replace(charSequence, str);
      regex = new Regex(", children=\\[]");
      str = "";
      return regex.replace(charSequence, str);
   }
}
