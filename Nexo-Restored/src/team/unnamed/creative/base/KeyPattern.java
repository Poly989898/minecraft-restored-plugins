package team.unnamed.creative.base;

import java.util.regex.Pattern;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface KeyPattern {
   @Nullable Pattern namespace();

   @Nullable Pattern value();

   boolean test(@NotNull Key var1);

   static @NotNull KeyPattern of(@Nullable Pattern namespace, @Nullable Pattern value) {
      return new KeyPatternImpl(namespace, value);
   }

   static @NotNull KeyPattern of(@RegExp @Nullable String namespace, @RegExp @Nullable String value) {
      return of(namespace == null ? null : Pattern.compile(namespace), value == null ? null : Pattern.compile(value));
   }

   static @NotNull KeyPattern ofNamespace(@Nullable Pattern namespace) {
      return of((Pattern)namespace, (Pattern)null);
   }

   static @NotNull KeyPattern ofNamespace(@RegExp @Nullable String namespace) {
      return of((Pattern)(namespace == null ? null : Pattern.compile(namespace)), (Pattern)null);
   }

   static @NotNull KeyPattern ofValue(@Nullable Pattern value) {
      return of((Pattern)null, (Pattern)value);
   }

   static @NotNull KeyPattern ofValue(@RegExp @Nullable String value) {
      return of((Pattern)null, (Pattern)(value == null ? null : Pattern.compile(value)));
   }

   static @NotNull KeyPattern any() {
      return KeyPatternImpl.ANY;
   }
}
