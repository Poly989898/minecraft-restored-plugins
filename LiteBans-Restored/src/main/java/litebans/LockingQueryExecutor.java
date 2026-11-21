package litebans;

import java.sql.SQLException;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.jetbrains.annotations.NotNull;

public final class LockingQueryExecutor extends QueryExecutor {
   private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

   public LockingQueryExecutor(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public ResultSet computeResultSet(@NotNull PreparedQuery preparedQuery) {
      try {
         if (preparedQuery.getSession().isClosed()) {
            return ResultSetUtil.obtainProxyResultSet();
         }

         Lock writeLock2 = this.reentrantReadWriteLock.writeLock();
         writeLock2.lock();

         try {
            PreparedQuery preparedQuery2 = preparedQuery;
            ResultSet resultSet;
            if (preparedQuery2.getStatement().execute()) {
               PreparedQuery preparedQuery3 = preparedQuery;
               resultSet = preparedQuery3.getStatement().getResultSet();
            } else {
               resultSet = ResultSetUtil.obtainProxyResultSet();
            }

            return resultSet;
         } finally {
            writeLock2.unlock();
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int[] computeInts(@NotNull Statement statement) {
      try {
         Lock writeLock2 = this.reentrantReadWriteLock.writeLock();
         writeLock2.lock();

         try {
            return statement.executeBatch();
         } finally {
            writeLock2.unlock();
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet computeResultSet2(@NotNull PreparedQuery preparedQuery) {
      try {
         if (preparedQuery.getSession().isClosed()) {
            return ResultSetUtil.obtainProxyResultSet();
         }

         Lock lock2 = this.reentrantReadWriteLock.readLock();
         lock2.lock();

         try {
            PreparedQuery preparedQuery2 = preparedQuery;
            return preparedQuery2.getStatement().executeQuery();
         } finally {
            lock2.unlock();
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int computeIntValue(@NotNull PreparedQuery preparedQuery) {
      try {
         if (preparedQuery.getSession().isClosed()) {
            return -1;
         }

         Lock writeLock2 = this.reentrantReadWriteLock.writeLock();
         writeLock2.lock();

         try {
            PreparedQuery preparedQuery2 = preparedQuery;
            return preparedQuery2.getStatement().executeUpdate();
         } finally {
            writeLock2.unlock();
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String computeText(@NotNull String text) {
      return FileUtils.computeFile(new File(text)).getAbsolutePath();
   }
}
