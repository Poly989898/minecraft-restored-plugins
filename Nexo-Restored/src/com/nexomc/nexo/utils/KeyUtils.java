package com.nexomc.nexo.utils;

import com.nexomc.nexo.utils.logs.Logs;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u0005*\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u0017\u0010\b\u001a\u00070\u0005¢\u0006\u0002\b\t*\u00020\u00052\u0006\u0010\n\u001a\u00020\u0007J\u0017\u0010\u000b\u001a\u00070\u0005¢\u0006\u0002\b\t*\u00020\u00052\u0006\u0010\n\u001a\u00020\u0007J\u0012\u0010\f\u001a\u00020\u0005*\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u001f\u0010\r\u001a\u00070\u0005¢\u0006\u0002\b\t*\u00020\u00052\u0006\u0010\r\u001a\u00020\u00072\u0006\u0010\u000e\u001a\u00020\u0007J\u000f\u0010\u000f\u001a\u00070\u0005¢\u0006\u0002\b\t*\u00020\u0005J\u0014\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u0007H\u0007b\u0002\b\u0014J\u0010\u0010\u0012\u001a\u00020\u0005*\u00020\u0005H\u0007b\u0002\b\u0014J\u001e\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u00072\b\b\u0002\u0010\u0018\u001a\u00020\u0019H\u0007b\u0002\b\u0014J2\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00072\u0006\u0010\u0013\u001a\u00020\u00072\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\u0018\u001a\u00020\u0019H\u0007b\u0002\b\u0014R\u001a\u0010\u0010\u001a\u00070\u0005¢\u0006\u0002\b\t8\u0006X\u0087\u0004\u0092\u0002\u0002\b\u0011¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001b"},
   d2 = {"Lcom/nexomc/nexo/utils/KeyUtils;", "", "<init>", "()V", "prependIfMissing", "Lnet/kyori/adventure/key/Key;", "prefix", "", "removeSuffix", "Lorg/jetbrains/annotations/NotNull;", "suffix", "appendSuffix", "prependPrefix", "replace", "with", "appendPng", "MALFORMED_KEY_PLACEHOLDER", "Lkotlin/jvm/JvmField;", "dropExtension", "key", "Lkotlin/jvm/JvmStatic;", "KEY_REGEX", "Lkotlin/text/Regex;", "parseKey", "silent", "", "namespace", "com.nexomc:core"}
)
public final class KeyUtils {
   @NotNull
   public static final KeyUtils INSTANCE = new KeyUtils();
   @JvmField
   @NotNull
   public static final Key MALFORMED_KEY_PLACEHOLDER;
   @NotNull
   private static final Regex KEY_REGEX;

   private KeyUtils() {
   }

   @NotNull
   public final Key prependIfMissing(@NotNull Key key2, @NotNull String prefix) {
      String str = key2.value();
      Key key;
      if (StringsKt.startsWith(str, prefix, false)) {
         key = key2;
      } else {
         key = Key.key(key2.namespace(), prefix + key2.value());
      }

      return key;
   }

   @NotNull
   public final Key removeSuffix(@NotNull Key key2, @NotNull String suffix) {
      String it = key2.asString();
      int value = 0;
      String str = StringsKt.endsWith(it, suffix, false) ? it : null;
      Key key;
      if (str != null) {
         String str2 = StringsKt.removeSuffix(str, (CharSequence)suffix);
         if (str2 != null) {
            String p0 = str2;
            int value2 = 0;
            key = Key.key(p0);
            if (key != null) {
               return key;
            }
         }
      }

      key = key2;
      return key;
   }

   @NotNull
   public final Key appendSuffix(@NotNull Key key2, @NotNull String suffix) {
      String it = key2.asString();
      int value = 0;
      String str = !StringsKt.endsWith(it, suffix, false) ? it : null;
      Key key;
      if (str != null) {
         String str2 = str + suffix;
         if (str2 != null) {
            String p0 = str2;
            int value2 = 0;
            key = Key.key(p0);
            if (key != null) {
               return key;
            }
         }
      }

      key = key2;
      return key;
   }

   @NotNull
   public final Key prependPrefix(@NotNull Key key2, @NotNull String prefix) {
      String str = key2.value();
      Key key;
      if (StringsKt.startsWith(str, prefix, false)) {
         key = key2;
      } else {
         key = Key.key(key2.namespace(), prefix + key2.value());
      }

      return key;
   }

   @NotNull
   public final Key replace(@NotNull Key key2, @NotNull String replace, @NotNull String with) {
      String it = key2.asString();
      int value = 0;
      String str = StringsKt.contains((CharSequence)it, (CharSequence)replace, false) ? it : null;
      Key key;
      if (str != null) {
         String str2 = StringsKt.replace(str, replace, with, false);
         if (str2 != null) {
            String p0 = str2;
            int value2 = 0;
            key = Key.key(p0);
            if (key != null) {
               return key;
            }
         }
      }

      key = key2;
      return key;
   }

   @NotNull
   public final Key appendPng(@NotNull Key key2) {
      String asString = key2.asString();
      Key key = Key.key(StringUtilsKt.appendIfMissing(asString, ".png"));
      return key;
   }

   @JvmStatic
   @NotNull
   public static final Key dropExtension(@NotNull String key) {
      if (!Key.parseable(key)) {
         return MALFORMED_KEY_PLACEHOLDER;
      } else {
         int i = StringsKt.lastIndexOf((CharSequence)key, ".", 0, false);
         Key key2;
         if (i == -1) {
            key2 = Key.key(key);
         } else {
            key2 = Key.key(StringsKt.take(key, i));
         }

         return key2;
      }
   }

   @JvmStatic
   @NotNull
   public static final Key dropExtension(@NotNull Key key2) {
      String namespace = key2.namespace();
      String str = key2.value();
      Key key = Key.key(namespace, StringsKt.substringBeforeLast(str, ".", str));
      return key;
   }

   @JvmStatic
   @NotNull
   public static final Key parseKey(@NotNull String key, boolean silent) {
      return parseKey(StringsKt.substringBefore(key, ":", "minecraft"), StringsKt.substringAfter(key, ":", key), (String)null, silent);
   }

   @JvmStatic
   @NotNull
   public static final Key parseKey(@NotNull String namespace, @NotNull String key, @Nullable String prefix, boolean silent) {
      Pair pair = kotlin.TuplesKt.to(namespace, key);
      String namespace_2 = (String)pair.component1();
      String key_1 = (String)pair.component2();
      Pair pair2 = kotlin.TuplesKt.to(namespace_2, key_1);
      String oldNamespace = (String)pair2.component1();
      String oldKey = (String)pair2.component2();
      if (!Key.parseable(namespace_2 + ":" + key_1)) {
         String str = namespace_2.toLowerCase(Locale.ROOT);
         CharSequence charSequence = (CharSequence)str;
         Regex regex = KEY_REGEX;
         String str2 = "_";
         namespace_2 = regex.replace(charSequence, str2);
         str = key_1.toLowerCase(Locale.ROOT);
         charSequence = (CharSequence)str;
         regex = KEY_REGEX;
         str2 = "_";
         key_1 = regex.replace(charSequence, str2);
         if (!silent) {
            Logs.logError("Invalid " + prefix + "-key: " + oldNamespace + ":" + oldKey, false);
            Logs.logWarn("Keys must be all lower-case, without spaces and most special characters", false);
            Logs.logWarn("Example: " + oldNamespace + ":" + oldKey + " -> " + namespace_2 + ":" + key_1, false);
         }
      }

      Key key2 = Key.key(namespace_2, key_1);
      return key2;
   }

   static {
      Key key = Key.key("item/barrier");
      MALFORMED_KEY_PLACEHOLDER = key;
      KEY_REGEX = new Regex("[^a-z0-9._/-]");
   }
}
