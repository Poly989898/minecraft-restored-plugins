package litebans;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import org.jetbrains.annotations.NotNull;

public class QueryExecutor {
   public static final PoolHelperCompanion POOL_HELPER_COMPANION = new PoolHelperCompanion(null);
   private final Platform platform;
   public static final int VALUE_5 = 5;

   public QueryExecutor(@NotNull Platform platform) {
      this.platform = platform;
   }

   public ResultSet computeResultSet(@NotNull PreparedQuery preparedQuery) {
      try {
         if (preparedQuery.getSession().isClosed()) {
            return ResultSetUtil.obtainProxyResultSet();
         }

         ResultSet resultSet;
         label22: {
            PreparedQuery preparedQuery2 = preparedQuery;
            if (!preparedQuery2.getStatement().isClosed()) {
               PreparedQuery preparedQuery3 = preparedQuery;
               if (preparedQuery3.getStatement().execute()) {
                  PreparedQuery preparedQuery4 = preparedQuery;
                  resultSet = preparedQuery4.getStatement().getResultSet();
                  break label22;
               }
            }

            resultSet = null;
         }

         ResultSet resultSet2 = resultSet;
         resultSet = resultSet2;
         if (resultSet2 == null) {
            resultSet = ResultSetUtil.obtainProxyResultSet();
         }

         return resultSet;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public ResultSet computeResultSet2(@NotNull PreparedQuery preparedQuery) {
      try {
         if (preparedQuery.getSession().isClosed()) {
            return ResultSetUtil.obtainProxyResultSet();
         }

         PreparedQuery preparedQuery2 = preparedQuery;
         ResultSet resultSet;
         if (!preparedQuery2.getStatement().isClosed()) {
            PreparedQuery preparedQuery3 = preparedQuery;
            ResultSet executeQuery2 = preparedQuery3.getStatement().executeQuery();
            resultSet = executeQuery2;
            if (executeQuery2 == null) {
               resultSet = ResultSetUtil.obtainProxyResultSet();
            }
         } else {
            resultSet = null;
         }

         ResultSet resultSet2 = resultSet;
         resultSet = resultSet2;
         if (resultSet2 == null) {
            resultSet = ResultSetUtil.obtainProxyResultSet();
         }

         return resultSet;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public int computeIntValue(@NotNull PreparedQuery preparedQuery) {
      try {
         if (preparedQuery.getSession().isClosed()) {
            return -1;
         }

         int value = 0;

         while (true) {
            PreparedQuery preparedQuery2 = preparedQuery;
            if (!preparedQuery2.getStatement().isClosed()) {
               try {
                  PreparedQuery preparedQuery3 = preparedQuery;
                  return preparedQuery3.getStatement().executeUpdate();
               } catch (Exception e2) {
                  value++;
                  if (value <= 5 && this.checkIntValue(value, e2)) {
                     continue;
                  }

                  throw e2;
               }
            }

            return -1;
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public int[] computeInts(@NotNull Statement statement) {
      try {
         Statement statement2 = statement;
         int[] ints;
         if (!statement2.isClosed()) {
            int[] executeBatch2 = statement.executeBatch();
            Intrinsics.checkNotNull(executeBatch2);
            ints = executeBatch2;
         } else {
            ints = new int[0];
         }

         return ints;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public String computeText(@NotNull String text) {
      return text;
   }

   private final boolean checkIntValue(int value, Throwable error) {
      try {
         ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
         String message = error.getMessage();
         if (message == null) {
            message = "";
         }

         boolean flag;
         if (StringsKt.containsWithDefaults((CharSequence)message, (CharSequence)"Deadlock found when trying to get lock; try restarting transaction", false, 2, null)) {
            int value2 = value * value * 101;
            module.logDebug("executeUpdate() failed due to deadlock. Wait " + value2 + " ms before retrying... (" + value + "/5)");
            Thread.sleep(value2);
            flag = true;
         } else {
            flag = false;
         }

         return flag;
      } catch (InterruptedException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
