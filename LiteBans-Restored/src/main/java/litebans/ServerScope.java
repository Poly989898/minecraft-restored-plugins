package litebans;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ServerScope {
   private ServerScope() {
   }

   public final String getLocalScope() {
      return ServerScopes.getWildcard();
   }

   public final ServerScopes getScopes() {
      return ServerScopes.getEmpty();
   }

   public final boolean isGlobal(@Nullable String text) {
      return Intrinsics.areEqual(text, this.getLocalScope()) || StringsKt.checkText(text, "global", true) || StringsKt.checkText(text, "all", true);
   }

   public final boolean isLocal(@Nullable String text) {
      return StringsKt.checkText(text, "local", true) || StringsKt.checkText(text, "this", true);
   }

   public final ServerScopes parseScopes(@NotNull LiteSender liteSender, @NotNull List list, @NotNull String text, boolean flag) {
      Iterator iterator2 = list.iterator();

      while (iterator2.hasNext()) {
         String string = (String)iterator2.next();
         if (string.length() >= 7) {
            if (StringsKt.checkText(string.toLowerCase(Locale.ENGLISH), "server:", false, 2, null)) {
               String substring2 = string.substring(7);
               String scope = this.resolveScope(substring2, text, liteSender);
               if (flag) {
                  iterator2.remove();
               }

               return new ServerScopes(scope);
            }
         }

         if (Intrinsics.areEqual(string, "--")) {
            return this.getScopes();
         }

         if (StringsKt.startsWithWithDefaults(string, '-', false, 2, null) && StringsKt.containsWithDefaults(string, 'g', false, 2, null)) {
            if (flag) {
               iterator2.remove();
            }

            return new ServerScopes(this.getLocalScope());
         }
      }

      return this.getScopes();
   }

   public static ServerScopes parseScopesWithDefaults(ServerScope serverScope, LiteSender liteSender, List list, String text, boolean flag, int value, Object obj) {
      if ((value & 8) != 0) {
         flag = true;
      }

      return serverScope.parseScopes(liteSender, list, text, flag);
   }

   public final String normalize(@Nullable String text) {
      return text != null && !this.isGlobal(text) ? this.truncate(text) : this.getLocalScope();
   }

   public final String truncate(@NotNull String text) {
      return TextUtil.access$length(text, 32);
   }

   public final String resolveScope(@NotNull String text, @NotNull String text2, @NotNull LiteSender liteSender) {
      if (this.isGlobal(text)) {
         return "global";
      }

      if (this.isLocal(text)) {
         String serverName = liteSender.getServerName();
         String text3 = serverName;
         if (serverName == null) {
            text3 = text2;
         }

         return text3;
      } else {
         return this.truncate(text);
      }
   }

   public final boolean scopesMatch(@NotNull String text, @NotNull String text2) {
      return !this.isGlobal(text) && !this.isGlobal(text2) ? StringsKt.checkText(this.truncate(text), this.truncate(text2), true) : true;
   }

   public ServerScope(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
