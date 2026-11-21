package litebans;

import java.sql.SQLClientInfoException;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Wrapper;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public final class HikariProxyConnection extends ProxyConnection implements Wrapper, AutoCloseable, Connection {
   @Override
   public Statement createStatement() {
      try {
         try {
            return super.createStatement();
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String text) {
      try {
         try {
            return super.prepareStatement(text);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public CallableStatement prepareCall(String text) {
      try {
         try {
            return super.prepareCall(text);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String nativeSQL(String text) {
      try {
         try {
            return super.connection.nativeSQL(text);
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setAutoCommit(boolean flag) {
      try {
         try {
            super.setAutoCommit(flag);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean getAutoCommit() {
      try {
         try {
            return super.connection.getAutoCommit();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void commit() {
      try {
         try {
            super.commit();
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void rollback() {
      try {
         try {
            super.rollback();
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isClosed() {
      try {
         try {
            return super.isClosed();
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public DatabaseMetaData getMetaData() {
      try {
         try {
            return super.getMetaData();
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setReadOnly(boolean flag) {
      try {
         try {
            super.setReadOnly(flag);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isReadOnly() {
      try {
         try {
            return super.connection.isReadOnly();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setCatalog(String text) {
      try {
         try {
            super.setCatalog(text);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getCatalog() {
      try {
         try {
            return super.connection.getCatalog();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setTransactionIsolation(int value) {
      try {
         try {
            super.setTransactionIsolation(value);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getTransactionIsolation() {
      try {
         try {
            return super.connection.getTransactionIsolation();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public SQLWarning getWarnings() {
      try {
         try {
            return super.connection.getWarnings();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void clearWarnings() {
      try {
         try {
            super.connection.clearWarnings();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Statement createStatement(int value, int value2) {
      try {
         try {
            return super.createStatement(value, value2);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String text, int value, int value2) {
      try {
         try {
            return super.prepareStatement(text, value, value2);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public CallableStatement prepareCall(String text, int value, int value2) {
      try {
         try {
            return super.prepareCall(text, value, value2);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Map getTypeMap() {
      try {
         try {
            return super.connection.getTypeMap();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setTypeMap(Map map) {
      try {
         try {
            super.connection.setTypeMap(map);
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setHoldability(int value) {
      try {
         try {
            super.connection.setHoldability(value);
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getHoldability() {
      try {
         try {
            return super.connection.getHoldability();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Savepoint setSavepoint() {
      try {
         try {
            return super.connection.setSavepoint();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Savepoint setSavepoint(String text) {
      try {
         try {
            return super.connection.setSavepoint(text);
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void rollback(Savepoint savepoint) {
      try {
         try {
            super.rollback(savepoint);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void releaseSavepoint(Savepoint savepoint) {
      try {
         try {
            super.connection.releaseSavepoint(savepoint);
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Statement createStatement(int value, int value2, int value3) {
      try {
         try {
            return super.createStatement(value, value2, value3);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String text, int value, int value2, int value3) {
      try {
         try {
            return super.prepareStatement(text, value, value2, value3);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public CallableStatement prepareCall(String text, int value, int value2, int value3) {
      try {
         try {
            return super.prepareCall(text, value, value2, value3);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String text, int value) {
      try {
         try {
            return super.prepareStatement(text, value);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String text, int[] ints) {
      try {
         try {
            return super.prepareStatement(text, ints);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String text, String[] texts) {
      try {
         try {
            return super.prepareStatement(text, texts);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Clob createClob() {
      try {
         try {
            return super.connection.createClob();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Blob createBlob() {
      try {
         try {
            return super.connection.createBlob();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public NClob createNClob() {
      try {
         try {
            return super.connection.createNClob();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public SQLXML createSQLXML() {
      try {
         try {
            return super.connection.createSQLXML();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isValid(int value) {
      try {
         try {
            return super.connection.isValid(value);
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setClientInfo(String text, String text2) {
      try {
         super.connection.setClientInfo(text, text2);
      } catch (SQLClientInfoException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setClientInfo(Properties properties) {
      try {
         super.connection.setClientInfo(properties);
      } catch (SQLClientInfoException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getClientInfo(String text) {
      try {
         try {
            return super.connection.getClientInfo(text);
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Properties getClientInfo() {
      try {
         try {
            return super.connection.getClientInfo();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Array createArrayOf(String text, Object[] objs) {
      try {
         try {
            return super.connection.createArrayOf(text, objs);
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Struct createStruct(String text, Object[] objs) {
      try {
         try {
            return super.connection.createStruct(text, objs);
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setSchema(String text) {
      try {
         try {
            super.setSchema(text);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getSchema() {
      try {
         try {
            return super.connection.getSchema();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void abort(Executor executor) {
      try {
         try {
            super.connection.abort(executor);
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNetworkTimeout(Executor executor, int value) {
      try {
         try {
            super.setNetworkTimeout(executor, value);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.checkException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getNetworkTimeout() {
      try {
         try {
            return super.connection.getNetworkTimeout();
         } catch (SQLException e2) {
            throw this.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   protected HikariProxyConnection(PoolEntry poolEntry, Connection connection, FastList fastList, LeakTask leakTask, long value, boolean flag, boolean flag2) {
      super(poolEntry, connection, fastList, leakTask, value, flag, flag2);
   }
}
