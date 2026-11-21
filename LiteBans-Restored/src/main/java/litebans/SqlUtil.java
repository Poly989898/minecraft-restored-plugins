package litebans;

import java.sql.ResultSet;
import org.jetbrains.annotations.NotNull;

public final class SqlUtil {
   public static final ResultSet computeResultSet(@NotNull PreparedQuery preparedQuery) {
      DatabaseModule databaseModule = preparedQuery.getSession().getDatabaseModule();
      ResultSet resultSet;
      if (preparedQuery.getSql() == null) {
         PreparedQuery preparedQuery2 = preparedQuery;
         resultSet = databaseModule.executeQuery(preparedQuery2);
      } else {
         String sql = preparedQuery.getSql();
         if (StringsKt.checkText(sql, "SELECT ", false, 2, null)) {
            PreparedQuery preparedQuery3 = preparedQuery;
            resultSet = databaseModule.executeQueryKeepOpen(preparedQuery3);
         } else {
            String sql2 = preparedQuery.getSql();
            if (StringsKt.checkText(sql2, "INSERT ", false, 2, null)
               || StringsKt.checkText(sql2, "DELETE ", false, 2, null)
               || StringsKt.checkText(sql2, "UPDATE ", false, 2, null)
               || StringsKt.checkText(sql2, "ALTER ", false, 2, null)
               || StringsKt.checkText(sql2, "CREATE ", false, 2, null)) {
               PreparedQuery preparedQuery4 = preparedQuery;
               resultSet = databaseModule.execute(preparedQuery4);
            } else {
               PreparedQuery preparedQuery5 = preparedQuery;
               resultSet = databaseModule.executeQuery(preparedQuery5);
            }
         }
      }

      return resultSet;
   }

   public static final ResultSet computeResultSet(@NotNull PreparedQuery preparedQuery, @NotNull HistoryQuery historyQuery) {
      PreparedQuery preparedQuery2 = preparedQuery;
      Function1 callback = historyQuery.obtainFunction();
      return computeResultSet((PreparedQuery)callback.computeObj(preparedQuery2));
   }

   public static final ResultSet computeResultSet(@NotNull PreparedQuery preparedQuery, @NotNull Function1 callback) {
      PreparedQuery preparedQuery2 = preparedQuery;
      return computeResultSet((PreparedQuery)callback.computeObj(preparedQuery2));
   }

   public static final ResultSet computeResultSet2(@NotNull PreparedQuery preparedQuery) {
      DatabaseModule module = (DatabaseModule)PlatformHolder.PLATFORM_STATE.obtainPlatform().getModule(DatabaseModule.class);
      PreparedQuery preparedQuery2 = preparedQuery;
      return module.executeQueryKeepOpen(preparedQuery2);
   }

   public static final ResultSet computeResultSet2(@NotNull PreparedQuery preparedQuery, @NotNull Function1 callback) {
      PreparedQuery preparedQuery2 = preparedQuery;
      return computeResultSet2((PreparedQuery)callback.computeObj(preparedQuery2));
   }

   public static final int computeIntValue(@NotNull PreparedQuery preparedQuery) {
      DatabaseModule module = (DatabaseModule)PlatformHolder.PLATFORM_STATE.obtainPlatform().getModule(DatabaseModule.class);
      var preparedQuery2 = preparedQuery;
      Throwable error = null;

      try {
         PreparedQuery preparedQuery3 = preparedQuery;
         return module.executeUpdate(preparedQuery3);
      } catch (Throwable e) {
         error = e;
         throw SneakyThrow.<RuntimeException>rethrow(e);
      } finally {
         CloseableKt.handleCloseable(preparedQuery2, error);
      }
   }

   public static final PreparedQuery access$bindLong(@NotNull PreparedQuery preparedQuery) {
      return preparedQuery.bindLong(DatabaseModule.getCurrentTimeWithDefaults(preparedQuery.getSession().getDatabaseModule(), false, 1, null));
   }
}
