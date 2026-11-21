package litebans;

import java.sql.ResultSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HistoryQuery implements CharSequence {
   public static final HistoryQueryFactory HISTORY_QUERY_FACTORY = new HistoryQueryFactory(null);
   private final DatabaseSession databaseSession;
   private final String target;
   private final ExecutorSource executorSource;
   private final boolean activeOnly;
   private final ServerScopeClause serverScopeClause;
   private final String orderColumn;
   private final boolean descending;
   private final Number limit;
   private final Number offset;
   private Function1 function;

   public HistoryQuery(
      @NotNull DatabaseSession databaseSession,
   @Nullable String text,
   @NotNull ExecutorSource executorSource,
   boolean flag,
   @NotNull ServerScopeClause serverScopeClause,
   @Nullable String text2,
   boolean flag2,
   @NotNull Number number,
   @NotNull Number number2
   ) {
      this.databaseSession = databaseSession;
      this.target = text;
      this.executorSource = executorSource;
      this.activeOnly = flag;
      this.serverScopeClause = serverScopeClause;
      this.orderColumn = text2;
      this.descending = flag2;
      this.limit = number;
      this.offset = number2;
      this.function = DatabaseSessionImpl.DATABASE_SESSION_IMPL_COMPANION.obtainFunction2();
   }

   public HistoryQuery(DatabaseSession databaseSession, String text, ExecutorSource executorSource, boolean flag, ServerScopeClause serverScopeClause, String text2, boolean flag2, Number number, Number number2, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 4) != 0) {
         executorSource = ExecutorSource.UUID;
      }

      if ((value & 8) != 0) {
         flag = false;
      }

      if ((value & 32) != 0) {
         text2 = null;
      }

      if ((value & 64) != 0) {
         flag2 = true;
      }

      if ((value & 128) != 0) {
         number = 0;
      }

      if ((value & 256) != 0) {
         number2 = 0;
      }

      this(databaseSession, text, executorSource, flag, serverScopeClause, text2, flag2, number, number2);
   }

   public final String getTarget() {
      return this.target;
   }

   public final Function1 obtainFunction() {
      return this.function;
   }

   public final void handleFunction(@NotNull Function1 callback) {
      this.function = callback;
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
      this.perform();
      CharSequence message = "";
      CharSequence message2 = this.computeMessage2(this.computeMessage(this.computeMessage3(message)));
      if (this.orderColumn != null) {
         String text = this.descending ? "DESC" : "ASC";
         CharSequence message3 = message2;
         CharSequence message4 = " ORDER BY " + this.orderColumn + ' ' + text;
         message2 = "" + message3 + message4;
      }

      Number number = this.limit;
      byte b2 = 0;
      if (Intrinsics.compare(number.intValue(), b2) > 0) {
         CharSequence message5 = message2;
         CharSequence message6 = " LIMIT ? OFFSET ?";
         message2 = "" + message5 + message6;
         this.function = DatabaseSessionImpl.DATABASE_SESSION_IMPL_COMPANION.computeFunction2(this.function, obj -> {
            PreparedQuery preparedQuery = (PreparedQuery) obj;
            Number[] numbers = new Number[]{this.limit, this.offset};
            int value = 0;

            for (int i = numbers.length; value < i; value++) {
               Number number2 = numbers[value];
               preparedQuery.bindLong(number2);
            }

            return preparedQuery;
         });
      }

      return message2.toString();
   }

   private final CharSequence computeMessage(CharSequence message) {
      if (this.activeOnly) {
         this.function = DatabaseSessionImpl.DATABASE_SESSION_IMPL_COMPANION.computeFunction2(this.function, obj -> SqlUtil.access$bindLong(((PreparedQuery) obj)));
         return this.computeMessage(message, "(active={true} AND(until<1 OR until>?))");
      } else {
         return message;
      }
   }

   private final CharSequence computeMessage2(CharSequence message) {
      this.function = DatabaseSessionImpl.DATABASE_SESSION_IMPL_COMPANION.computeFunction2(this.function, obj -> {
         PreparedQuery preparedQuery = (PreparedQuery) obj;
         ServerScopeClause serverScopeClause = this.serverScopeClause;
         serverScopeClause.handlePreparedQuery(preparedQuery);
         return preparedQuery;
      });
      CharSequence message2 = this.serverScopeClause;
      return "" + message + message2;
   }

   public void perform() {
      if (!Intrinsics.areEqual(this.function, DatabaseSessionImpl.DATABASE_SESSION_IMPL_COMPANION.obtainFunction())) {
         this.function = DatabaseSessionImpl.DATABASE_SESSION_IMPL_COMPANION.obtainFunction2();
      }
   }

   public CharSequence computeMessage3(@NotNull CharSequence message) {
      if (this.target == null) {
         return message;
      }

      switch (HistoryQuerySwitchMap.value[this.executorSource.ordinal()]) {
         case 1:
            return this.computeMessage(this.target, "uuid", message);
         case 2:
            return this.computeMessage(this.target, "banned_by_uuid", message);
         case 3:
            String text = this.getTarget();
            if (text != null) {
               Long boxed = StringsKt.computeLong(text);
               if (boxed != null) {
                  return this.computeMessage(boxed, "id", message);
               }
            }

            return this.computeMessage(-1L, "id", message);
         default:
            return this.computeMessage(message, '(' + this.target + ')');
      }
   }

   private final CharSequence computeMessage(String text, String text2, CharSequence message) {
      this.function = DatabaseSessionImpl.DATABASE_SESSION_IMPL_COMPANION.computeFunction2(this.function, obj -> ((PreparedQuery) obj).bindString(text));
      return this.computeMessage(message, text2 + "=?");
   }

   private final CharSequence computeMessage(long value, String text, CharSequence message) {
      this.function = DatabaseSessionImpl.DATABASE_SESSION_IMPL_COMPANION.computeFunction2(this.function, obj -> ((PreparedQuery) obj).bindLong(value));
      return this.computeMessage(message, text + "=?");
   }

   public final CharSequence computeMessage(@NotNull CharSequence message, @NotNull CharSequence message2) {
      return message.length() == 0 ? message2 : (message2.length() == 0 ? message : message + " AND " + message2);
   }

   public final ResultSet computeResultSet(@NotNull PunishmentType punishmentType, @NotNull CharSequence message) {
      return this.computeResultSet(punishmentType.getPunishmentTable(), message);
   }

   public static ResultSet computeResultSet(HistoryQuery historyQuery, PunishmentType punishmentType, CharSequence message, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 2) != 0) {
         message = "*";
      }

      return historyQuery.computeResultSet(punishmentType, message);
   }

   public final ResultSet computeResultSet(@NotNull PunishmentTable punishmentTable, @NotNull CharSequence message) {
      if (this.databaseSession instanceof KotlinDatabaseSessionImpl) {
         return ResultSetUtil.obtainProxyResultSet();
      }

      DatabaseSessionImpl databaseSession = (DatabaseSessionImpl) this.databaseSession;
      Intrinsics.checkNotNullExpressionValue(this.databaseSession, "");
      databaseSession = databaseSession;
      PunishmentTable punishmentTable2 = punishmentTable;
      return SqlUtil.computeResultSet(databaseSession.prepare(SqlText.box(SqlText.constructorImpl("SELECT " + message + " FROM " + punishmentTable2 + " WHERE " + this))), this);
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
