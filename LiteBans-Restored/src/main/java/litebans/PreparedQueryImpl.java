package litebans;

import java.sql.PreparedStatement;

public final class PreparedQueryImpl extends PreparedQuery {
   private PreparedQueryImpl(DatabaseSession databaseSession, PreparedStatement preparedStatement, String text) {
      super(databaseSession, preparedStatement, text, null);
   }

   public PreparedQueryImpl computePreparedQueryImpl(boolean flag) {
      PreparedQuery preparedQuery = this;
      int value = flag ? 49 : 48;
      preparedQuery.bindString(String.valueOf((char)value));
      return this;
   }

   @Override
   public PreparedQuery bindBoolean(boolean flag) {
      return this.computePreparedQueryImpl(flag);
   }

   public PreparedQueryImpl(DatabaseSession databaseSession, PreparedStatement preparedStatement, String text, DefaultConstructorMarker defaultConstructorMarker) {
      this(databaseSession, preparedStatement, text);
   }
}
