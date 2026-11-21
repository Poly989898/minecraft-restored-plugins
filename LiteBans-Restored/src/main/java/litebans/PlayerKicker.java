package litebans;

import org.jetbrains.annotations.NotNull;

public final class PlayerKicker {
   public static void disallowLoginWithDefaults(PlatformBridge platformBridge, Object obj, CharSequence message, String text, boolean flag, String text2, int value, Object obj2) {
      if (obj2 != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 2) != 0) {
         message = "";
      }

      if ((value & 4) != 0) {
         text = "[event cancelled]";
      }

      if ((value & 8) != 0) {
         flag = true;
      }

      if ((value & 16) != 0) {
         text2 = "KICK_BANNED";
      }

      platformBridge.disallowLogin(obj, message, text, flag, text2);
   }

   public static boolean access$getPlugin(@NotNull PlatformBridge platformBridge, @NotNull String text) {
      return platformBridge.getPlugin(text) != null;
   }
}
