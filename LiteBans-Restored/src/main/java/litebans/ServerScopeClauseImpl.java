package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ServerScopeClauseImpl extends ServerScopeClause {
   private final boolean flag2;

   public ServerScopeClauseImpl(@NotNull Platform platform, @Nullable String text, boolean flag) {
      super(platform, text);
      this.flag2 = flag;
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
         return this.flag2 ? " AND(server_scope=?)" : "server_scope=?";
      }
   }
}
