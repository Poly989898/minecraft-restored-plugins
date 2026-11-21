package litebans;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CacheModuleCompanion {
   private CacheModuleCompanion() {
   }

   public final String computeText(@NotNull Object obj, @NotNull Object obj2, @Nullable Object obj3) {
      return this.computeText(obj.toString(), obj2.toString(), obj3);
   }

   public final String computeText(@NotNull CharSequence message, @NotNull String text, @Nullable Object obj) {
      if (!StringsKt.containsWithDefaults((CharSequence)message, (CharSequence)text, false, 2, null)) {
         return message.toString();
      }

      Pattern pattern = (Pattern)CacheModule.obtainMap().get(text);
      if (pattern == null) {
         pattern = this.computePattern(text);
      }

      String replaceAll2 = pattern.matcher(message).replaceAll(Matcher.quoteReplacement(String.valueOf(obj)));
      Intrinsics.checkNotNull(replaceAll2);
      return replaceAll2;
   }

   private final Pattern computePattern(String text) {
      Pattern compile2 = Pattern.compile(text, 16);
      Map map = CacheModule.obtainMap();
      if (map.size() > 512) {
         map.clear();
      }

      map.put(text, compile2);
      Intrinsics.checkNotNull(compile2);
      return compile2;
   }

   public CacheModuleCompanion(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
