package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ServerScopeClauseImpl extends ServerScopeClause {
   private final boolean prefixed;

   public ServerScopeClauseImpl(@NotNull Platform platform, @Nullable String text, boolean flag) {
      super(platform, text);
      this.prefixed = flag;
   }

   @Override
   public void handlePreparedQuery(@NotNull PreparedQuery preparedQuery) {
      if (!Intrinsics.areEqual(this.obtainText(), "__ALL__")) {
         preparedQuery.bindString(this.obtainText());
      }
   }

   @Override
   public String toString() {
      if (Intrinsics.areEqual(this.obtainText(), "__ALL__")) {
         return "";
      } else {
         return this.prefixed ? " AND(server_scope=?)" : "server_scope=?";
      }
   }
}
