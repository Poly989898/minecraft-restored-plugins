package litebans;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

public abstract class ProxyConnection implements Connection {
   static final int VALUE_1 = 1;
   static final int VALUE_2 = 2;
   static final int VALUE_4 = 4;
   static final int closed = 8;
   static final int VALUE_16 = 16;
   static final int VALUE_32 = 32;
   private static final PoolLogger POOL_LOGGER = PoolLogger.computePoolLogger(ProxyConnection.class);
   private static final Set SET = new HashSet();
   private static final Set SET_2 = new HashSet();
   protected Connection connection;
   private final PoolEntry poolEntry;
   private final LeakTask leakTask;
   private final FastList fastList;
   private int intValue;
   private long longValue;
   private boolean flag3;
   private boolean flag4;
   private boolean flag5;
   private int intValue2;
   private int intValue3;
   private String text3;
   private String text4;

   protected ProxyConnection(PoolEntry poolEntry, Connection connection, FastList fastList, LeakTask leakTask, long value, boolean flag, boolean flag2) {
      this.poolEntry = poolEntry;
      this.connection = connection;
      this.fastList = fastList;
      this.leakTask = leakTask;
      this.longValue = value;
      this.flag4 = flag;
      this.flag5 = flag2;
   }

   @Override
   public final String toString() {
      return this.getClass().getSimpleName() + '@' + System.identityHashCode(this) + " wrapping " + this.connection;
   }

   final boolean obtainFlag() {
      return this.flag5;
   }

   final String obtainText() {
      return this.text3;
   }

   final String obtainText2() {
      return this.text4;
   }

   final int obtainIntValue() {
      return this.intValue3;
   }

   final boolean obtainFlag2() {
      return this.flag4;
   }

   final int obtainIntValue2() {
      return this.intValue2;
   }

   final SQLException checkException(SQLException sqlException) {
      boolean flag = false;
      SQLException sqlException2 = sqlException;
      SQLExceptionOverride poolAInterface = this.poolEntry.getPoolBase().exceptionOverride;

      for (int i = 0; this.connection != DeadConnectionProxy.CONNECTION && sqlException2 != null && i < 10; i++) {
         String sqlState = sqlException2.getSQLState();
         if (sqlState != null && sqlState.startsWith("08") || sqlException2 instanceof SQLTimeoutException || SET.contains(sqlState) || SET_2.contains(sqlException2.getErrorCode())) {
            if (poolAInterface == null || poolAInterface.computeEvictionResult(sqlException2) != EvictionResult.DO_NOT_EVICT) {
               flag = true;
            }
            break;
         }

         sqlException2 = sqlException2.getNextException();
      }

      if (flag) {
         SQLException sqlException3 = sqlException2 != null ? sqlException2 : sqlException;
         POOL_LOGGER.handleText4(
            "%s - Connection %s marked as broken because of SQLSTATE(%s), ErrorCode(%d)",
            new Object[]{this.poolEntry.getPoolName(), this.connection.toString(), sqlException3.getSQLState(), sqlException3.getErrorCode(), sqlException3}
         );
         this.leakTask.perform();
         this.poolEntry.evict("(connection is broken)");
         this.connection = DeadConnectionProxy.CONNECTION;
      }

      return sqlException;
   }

   final synchronized void untrackStatement(Statement statement) {
      this.fastList.remove(statement);
   }

   final void perform() {
      if (this.flag5) {
         this.longValue = ClockSource.currentTime();
      } else {
         this.flag3 = true;
      }
   }

   private final synchronized Statement trackStatement(Statement statement) {
      this.fastList.add(statement);
      return statement;
   }

   private final synchronized void closeStatements() {
      int value = this.fastList.size();
      if (value > 0) {
         for (int i = 0; i < value && this.connection != DeadConnectionProxy.CONNECTION; i++) {
            try {
               Statement statement = (Statement)this.fastList.get(i);
               Throwable error = null;
               if (statement != null) {
                  if (error != null) {
                     try {
                        statement.close();
                     } catch (Throwable e) {
                        error.addSuppressed(e);
                     }
                  } else {
                     statement.close();
                  }
               }
            } catch (SQLException e2) {
               POOL_LOGGER.handleText4(
                  "%s - Connection %s marked as broken because of an exception closing open statements during Connection.close()",
                  new Object[]{this.poolEntry.getPoolName(), this.connection.toString()}
               );
               this.leakTask.perform();
               this.poolEntry.evict("(exception closing Statements during Connection.close())");
               this.connection = DeadConnectionProxy.CONNECTION;
            }
         }

         this.fastList.clear();
      }
   }

   @Override
   public final void close() {
      try {
         this.closeStatements();
         if (this.connection != DeadConnectionProxy.CONNECTION) {
            this.leakTask.perform();

            try {
               if (this.flag3 && !this.flag5) {
                  this.connection.rollback();
                  this.longValue = ClockSource.currentTime();
                  POOL_LOGGER.handleText("%s - Executed rollback on connection %s due to dirty commit state on close().", new Object[]{this.poolEntry.getPoolName(), this.connection.toString()});
               }

               if (this.intValue != 0) {
                  this.poolEntry.resetConnectionState(this, this.intValue);
                  this.longValue = ClockSource.currentTime();
               }

               this.connection.clearWarnings();
            } catch (SQLException e2) {
               if (!this.poolEntry.isMarkedEvicted()) {
                  throw this.checkException(e2);
               }
            } finally {
               this.connection = DeadConnectionProxy.CONNECTION;
               this.poolEntry.recycle(this.longValue);
            }
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isClosed() {
      return this.connection == DeadConnectionProxy.CONNECTION;
   }

   @Override
   public Statement createStatement() {
      try {
         return ProxyFactory.computeStatement(this, this.trackStatement(this.connection.createStatement()));
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Statement createStatement(int value, int value2) {
      try {
         return ProxyFactory.computeStatement(this, this.trackStatement(this.connection.createStatement(value, value2)));
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Statement createStatement(int value, int value2, int value3) {
      try {
         return ProxyFactory.computeStatement(this, this.trackStatement(this.connection.createStatement(value, value2, value3)));
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public CallableStatement prepareCall(String text) {
      try {
         return ProxyFactory.computeCallableStatement(this, (CallableStatement)this.trackStatement(this.connection.prepareCall(text)));
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public CallableStatement prepareCall(String text, int value, int value2) {
      try {
         return ProxyFactory.computeCallableStatement(this, (CallableStatement)this.trackStatement(this.connection.prepareCall(text, value, value2)));
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public CallableStatement prepareCall(String text, int value, int value2, int value3) {
      try {
         return ProxyFactory.computeCallableStatement(this, (CallableStatement)this.trackStatement(this.connection.prepareCall(text, value, value2, value3)));
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String text) {
      try {
         return ProxyFactory.computePreparedStatement(this, (PreparedStatement)this.trackStatement(this.connection.prepareStatement(text)));
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String text, int value) {
      try {
         return ProxyFactory.computePreparedStatement(this, (PreparedStatement)this.trackStatement(this.connection.prepareStatement(text, value)));
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String text, int value, int value2) {
      try {
         return ProxyFactory.computePreparedStatement(this, (PreparedStatement)this.trackStatement(this.connection.prepareStatement(text, value, value2)));
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String text, int value, int value2, int value3) {
      try {
         return ProxyFactory.computePreparedStatement(this, (PreparedStatement)this.trackStatement(this.connection.prepareStatement(text, value, value2, value3)));
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String text, int[] ints) {
      try {
         return ProxyFactory.computePreparedStatement(this, (PreparedStatement)this.trackStatement(this.connection.prepareStatement(text, ints)));
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String text, String[] texts) {
      try {
         return ProxyFactory.computePreparedStatement(this, (PreparedStatement)this.trackStatement(this.connection.prepareStatement(text, texts)));
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public DatabaseMetaData getMetaData() {
      try {
         this.perform();
         return ProxyFactory.computeDatabaseMetaData(this, this.connection.getMetaData());
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void commit() {
      try {
         this.connection.commit();
         this.flag3 = false;
         this.longValue = ClockSource.currentTime();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void rollback() {
      try {
         this.connection.rollback();
         this.flag3 = false;
         this.longValue = ClockSource.currentTime();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void rollback(Savepoint savepoint) {
      try {
         this.connection.rollback(savepoint);
         this.flag3 = false;
         this.longValue = ClockSource.currentTime();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setAutoCommit(boolean flag) {
      try {
         this.connection.setAutoCommit(flag);
         this.flag5 = flag;
         this.intValue |= 2;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setReadOnly(boolean flag) {
      try {
         this.connection.setReadOnly(flag);
         this.flag4 = flag;
         this.flag3 = false;
         this.intValue |= 1;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setTransactionIsolation(int value) {
      try {
         this.connection.setTransactionIsolation(value);
         this.intValue3 = value;
         this.intValue |= 4;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setCatalog(String text) {
      try {
         this.connection.setCatalog(text);
         this.text3 = text;
         this.intValue |= 8;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNetworkTimeout(Executor executor, int value) {
      try {
         this.connection.setNetworkTimeout(executor, value);
         this.intValue2 = value;
         this.intValue |= 16;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setSchema(String text) {
      try {
         this.connection.setSchema(text);
         this.text4 = text;
         this.intValue |= 32;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public final boolean isWrapperFor(Class class2) {
      try {
         return class2.isInstance(this.connection) || this.connection != null && this.connection.isWrapperFor(class2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public final Object unwrap(Class class2) {
      try {
         if (class2.isInstance(this.connection)) {
            return this.connection;
         } else if (this.connection != null) {
            return this.connection.unwrap(class2);
         } else {
            throw new SQLException("Wrapped connection is not an instance of " + class2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   static {
      SET.add("0A000");
      SET.add("57P01");
      SET.add("57P02");
      SET.add("57P03");
      SET.add("01002");
      SET.add("JZ0C0");
      SET.add("JZ0C1");
      SET_2.add(500150);
      SET_2.add(2399);
   }
}
