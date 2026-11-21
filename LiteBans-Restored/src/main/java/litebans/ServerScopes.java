package litebans;

import org.jetbrains.annotations.Nullable;

public final class ServerScopes {
   public static final ServerScope SERVER_SCOPE = new ServerScope(null);
   private final String scope;
   private static final String WILDCARD = "*";
   private static final ServerScopes EMPTY = new ServerScopes("");
   private static final int VALUE_7 = 7;

   public ServerScopes(@Nullable String text) {
      this.scope = text;
   }

   public final String getScope() {
      return this.scope;
   }

   public static final String getWildcard() {
      return WILDCARD;
   }

   public static final ServerScopes getEmpty() {
      return EMPTY;
   }
}
