package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerScopeClause implements CharSequence {
   private final Platform platform;
   private String serverScope;
   private final byte byteValue;

   public ServerScopeClause(@NotNull Platform platform, @Nullable String text) {
      this.platform = platform;
      this.serverScope = text;
      this.serverScope = ServerScopes.SERVER_SCOPE.normalize(this.serverScope);
      byte b;
      if (Intrinsics.areEqual(this.serverScope, "__ALL__")) {
         b = 0;
      } else {
         String serverName = this.obtainDatabaseModule().getServerName();
         byte b2 = 1;
         if (this.serverScope != null && !ServerScopes.SERVER_SCOPE.isGlobal(this.serverScope)) {
            b2++;
         }

         if (this.platform.obtainFlag4() && !Intrinsics.areEqual(serverName, this.serverScope)) {
            b2++;
         }

         b = b2;
      }

      this.byteValue = b;
   }

   public final String obtainText() {
      return this.serverScope;
   }

   public final DatabaseModule obtainDatabaseModule() {
      return (DatabaseModule)this.platform.getModule(DatabaseModule.class);
   }

   public void handlePreparedQuery(@NotNull PreparedQuery preparedQuery) {
      if (this.byteValue >= 2) {
         preparedQuery.bindString(this.serverScope);
      }

      if (this.byteValue >= 3) {
         preparedQuery.bindString(this.obtainDatabaseModule().getServerName());
      }
   }

   public int obtainIntValue() {
      return this.toString().length();
   }

   public char computeCharValue(int value) {
      return this.toString().charAt(value);
   }

   @Override
   public CharSequence subSequence(int value, int value2) {
      return this.toString().subSequence(value, value2);
   }

   @Override
   public String toString() {
      String text;
      switch (this.byteValue) {
         case 0:
            text = "";
            break;
         case 1:
            text = " AND(server_scope='*')";
            break;
         case 2:
            text = " AND(server_scope='*' OR server_scope=?)";
            break;
         case 3:
            text = " AND(server_scope='*' OR server_scope=? OR server_scope=?)";
            break;
         default:
            throw new NotImplementedError();
      }

      return text;
   }

   @Override
   public final int length() {
      return this.obtainIntValue();
   }

   @Override
   public final char charAt(int value) {
      return this.computeCharValue(value);
   }
}
