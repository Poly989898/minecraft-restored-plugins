package team.unnamed.creative.util;

import java.util.function.IntPredicate;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class Keys {
   private static final IntPredicate NAMESPACE_PREDICATE = (value) -> value == 95 || value == 45 || value >= 97 && value <= 122 || value >= 48 && value <= 57 || value == 46;

   private Keys() {
   }

   public static void validateNamespace(@Subst("minecraft") String namespace) {
      Key.key(namespace, "dummy");
   }

   public static boolean isValidNamespace(String namespace) {
      int i = 0;

      for(int length = namespace.length(); i < length; ++i) {
         if (!NAMESPACE_PREDICATE.test(namespace.charAt(i))) {
            return false;
         }
      }

      return true;
   }
}
