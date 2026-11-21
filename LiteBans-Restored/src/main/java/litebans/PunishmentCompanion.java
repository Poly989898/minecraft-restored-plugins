package litebans;

import org.jetbrains.annotations.Nullable;

public final class PunishmentCompanion {
   private PunishmentCompanion() {
   }

   public final boolean checkText(@Nullable String text, @Nullable String text2) {
      return text != null && Intrinsics.areEqual(text, text2);
   }

   public final boolean checkFlag(boolean flag, @Nullable String text, @Nullable String text2, boolean flag2) {
      if (flag && text2 != null && text != null) {
         if (Intrinsics.areEqual(text2, text)) {
            return true;
         }

         if (flag2) {
            CharSequence message = text2;
            if (new Regex(CacheModule.CACHE_MODULE_COMPANION.computeText(text, "%", ".*")).matches(message)) {
               return true;
            }
         }
      }

      return false;
   }

   public final boolean checkText(@Nullable String text) {
      return text != null && (StringsKt.containsWithDefaults(text, '%', false, 2, null) || StringsKt.containsWithDefaults(text, '_', false, 2, null));
   }

   public PunishmentCompanion(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
