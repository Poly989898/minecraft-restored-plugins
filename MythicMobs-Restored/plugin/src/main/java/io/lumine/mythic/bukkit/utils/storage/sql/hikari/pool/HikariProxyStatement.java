package io.lumine.mythic.bukkit.utils.storage.sql.hikari.pool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Wrapper;

public class HikariProxyStatement extends ProxyStatement implements AutoCloseable, Wrapper, Statement {
   @Override
   public boolean isWrapperFor(Class clazz) throws SQLException {
      try {
         return super.delegate.isWrapperFor(clazz);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public ResultSet executeQuery(String str) throws SQLException {
      try {
         return super.executeQuery(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int executeUpdate(String str) throws SQLException {
      try {
         return super.executeUpdate(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getMaxFieldSize() throws SQLException {
      try {
         return super.delegate.getMaxFieldSize();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setMaxFieldSize(int value) throws SQLException {
      try {
         super.delegate.setMaxFieldSize(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getMaxRows() throws SQLException {
      try {
         return super.delegate.getMaxRows();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setMaxRows(int value) throws SQLException {
      try {
         super.delegate.setMaxRows(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setEscapeProcessing(boolean flag) throws SQLException {
      try {
         super.delegate.setEscapeProcessing(flag);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getQueryTimeout() throws SQLException {
      try {
         return super.delegate.getQueryTimeout();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setQueryTimeout(int value) throws SQLException {
      try {
         super.delegate.setQueryTimeout(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void cancel() throws SQLException {
      try {
         super.delegate.cancel();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public SQLWarning getWarnings() throws SQLException {
      try {
         return super.delegate.getWarnings();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void clearWarnings() throws SQLException {
      try {
         super.delegate.clearWarnings();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setCursorName(String str) throws SQLException {
      try {
         super.delegate.setCursorName(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean execute(String str) throws SQLException {
      try {
         return super.execute(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public ResultSet getResultSet() throws SQLException {
      try {
         return super.getResultSet();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getUpdateCount() throws SQLException {
      try {
         return super.delegate.getUpdateCount();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean getMoreResults() throws SQLException {
      try {
         return super.delegate.getMoreResults();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setFetchDirection(int value) throws SQLException {
      try {
         super.delegate.setFetchDirection(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getFetchDirection() throws SQLException {
      try {
         return super.delegate.getFetchDirection();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setFetchSize(int value) throws SQLException {
      try {
         super.delegate.setFetchSize(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getFetchSize() throws SQLException {
      try {
         return super.delegate.getFetchSize();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getResultSetConcurrency() throws SQLException {
      try {
         return super.delegate.getResultSetConcurrency();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getResultSetType() throws SQLException {
      try {
         return super.delegate.getResultSetType();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void addBatch(String str) throws SQLException {
      try {
         super.delegate.addBatch(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void clearBatch() throws SQLException {
      try {
         super.delegate.clearBatch();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int[] executeBatch() throws SQLException {
      try {
         return super.executeBatch();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Connection getConnection() throws SQLException {
      try {
         return super.getConnection();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean getMoreResults(int value) throws SQLException {
      try {
         return super.delegate.getMoreResults(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public ResultSet getGeneratedKeys() throws SQLException {
      try {
         return super.delegate.getGeneratedKeys();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int executeUpdate(String str, int value) throws SQLException {
      try {
         return super.executeUpdate(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int executeUpdate(String str, int[] ints) throws SQLException {
      try {
         return super.executeUpdate(str, ints);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int executeUpdate(String str, String[] strs) throws SQLException {
      try {
         return super.executeUpdate(str, strs);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean execute(String str, int value) throws SQLException {
      try {
         return super.execute(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean execute(String str, int[] ints) throws SQLException {
      try {
         return super.execute(str, ints);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean execute(String str, String[] strs) throws SQLException {
      try {
         return super.execute(str, strs);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getResultSetHoldability() throws SQLException {
      try {
         return super.delegate.getResultSetHoldability();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isClosed() throws SQLException {
      try {
         return super.delegate.isClosed();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setPoolable(boolean flag) throws SQLException {
      try {
         super.delegate.setPoolable(flag);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isPoolable() throws SQLException {
      try {
         return super.delegate.isPoolable();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void closeOnCompletion() throws SQLException {
      try {
         super.delegate.closeOnCompletion();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isCloseOnCompletion() throws SQLException {
      try {
         return super.delegate.isCloseOnCompletion();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long getLargeUpdateCount() throws SQLException {
      try {
         return super.delegate.getLargeUpdateCount();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setLargeMaxRows(long value) throws SQLException {
      try {
         super.delegate.setLargeMaxRows(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long getLargeMaxRows() throws SQLException {
      try {
         return super.delegate.getLargeMaxRows();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long[] executeLargeBatch() throws SQLException {
      try {
         return super.delegate.executeLargeBatch();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long executeLargeUpdate(String str) throws SQLException {
      try {
         return super.delegate.executeLargeUpdate(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long executeLargeUpdate(String str, int value) throws SQLException {
      try {
         return super.delegate.executeLargeUpdate(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long executeLargeUpdate(String str, int[] ints) throws SQLException {
      try {
         return super.delegate.executeLargeUpdate(str, ints);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long executeLargeUpdate(String str, String[] strs) throws SQLException {
      try {
         return super.delegate.executeLargeUpdate(str, strs);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   protected HikariProxyStatement(ProxyConnection proxyConnection, Statement statement) {
      super(proxyConnection, statement);
   }
}
