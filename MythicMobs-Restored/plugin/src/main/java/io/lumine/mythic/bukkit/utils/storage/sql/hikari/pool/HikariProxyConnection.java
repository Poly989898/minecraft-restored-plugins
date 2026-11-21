package io.lumine.mythic.bukkit.utils.storage.sql.hikari.pool;

import io.lumine.mythic.bukkit.utils.storage.sql.hikari.util.FastList;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
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

public class HikariProxyConnection extends ProxyConnection implements AutoCloseable, Wrapper, Connection {
   @Override
   public Statement createStatement() throws SQLException {
      try {
         return super.createStatement();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String str) throws SQLException {
      try {
         return super.prepareStatement(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public CallableStatement prepareCall(String str) throws SQLException {
      try {
         return super.prepareCall(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public String nativeSQL(String str) throws SQLException {
      try {
         return super.delegate.nativeSQL(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setAutoCommit(boolean flag) throws SQLException {
      try {
         super.setAutoCommit(flag);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean getAutoCommit() throws SQLException {
      try {
         return super.delegate.getAutoCommit();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void commit() throws SQLException {
      try {
         super.commit();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void rollback() throws SQLException {
      try {
         super.rollback();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isClosed() throws SQLException {
      try {
         return super.isClosed();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public DatabaseMetaData getMetaData() throws SQLException {
      try {
         return super.delegate.getMetaData();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setReadOnly(boolean flag) throws SQLException {
      try {
         super.setReadOnly(flag);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isReadOnly() throws SQLException {
      try {
         return super.delegate.isReadOnly();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setCatalog(String str) throws SQLException {
      try {
         super.setCatalog(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public String getCatalog() throws SQLException {
      try {
         return super.delegate.getCatalog();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setTransactionIsolation(int value) throws SQLException {
      try {
         super.setTransactionIsolation(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getTransactionIsolation() throws SQLException {
      try {
         return super.delegate.getTransactionIsolation();
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
   public Statement createStatement(int value, int value2) throws SQLException {
      try {
         return super.createStatement(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String str, int value, int value2) throws SQLException {
      try {
         return super.prepareStatement(str, value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public CallableStatement prepareCall(String str, int value, int value2) throws SQLException {
      try {
         return super.prepareCall(str, value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Map getTypeMap() throws SQLException {
      try {
         return super.delegate.getTypeMap();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setTypeMap(Map map) throws SQLException {
      try {
         super.delegate.setTypeMap(map);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setHoldability(int value) throws SQLException {
      try {
         super.delegate.setHoldability(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getHoldability() throws SQLException {
      try {
         return super.delegate.getHoldability();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Savepoint setSavepoint() throws SQLException {
      try {
         return super.delegate.setSavepoint();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Savepoint setSavepoint(String str) throws SQLException {
      try {
         return super.delegate.setSavepoint(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void rollback(Savepoint savepoint) throws SQLException {
      try {
         super.rollback(savepoint);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void releaseSavepoint(Savepoint savepoint) throws SQLException {
      try {
         super.delegate.releaseSavepoint(savepoint);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Statement createStatement(int value, int value2, int value3) throws SQLException {
      try {
         return super.createStatement(value, value2, value3);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String str, int value, int value2, int value3) throws SQLException {
      try {
         return super.prepareStatement(str, value, value2, value3);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public CallableStatement prepareCall(String str, int value, int value2, int value3) throws SQLException {
      try {
         return super.prepareCall(str, value, value2, value3);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String str, int value) throws SQLException {
      try {
         return super.prepareStatement(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String str, int[] ints) throws SQLException {
      try {
         return super.prepareStatement(str, ints);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public PreparedStatement prepareStatement(String str, String[] strs) throws SQLException {
      try {
         return super.prepareStatement(str, strs);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Clob createClob() throws SQLException {
      try {
         return super.delegate.createClob();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Blob createBlob() throws SQLException {
      try {
         return super.delegate.createBlob();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public NClob createNClob() throws SQLException {
      try {
         return super.delegate.createNClob();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public SQLXML createSQLXML() throws SQLException {
      try {
         return super.delegate.createSQLXML();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isValid(int value) throws SQLException {
      try {
         return super.delegate.isValid(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setClientInfo(String str, String str2) throws SQLClientInfoException {
      super.delegate.setClientInfo(str, str2);
   }

   @Override
   public void setClientInfo(Properties properties) throws SQLClientInfoException {
      super.delegate.setClientInfo(properties);
   }

   @Override
   public String getClientInfo(String str) throws SQLException {
      try {
         return super.delegate.getClientInfo(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Properties getClientInfo() throws SQLException {
      try {
         return super.delegate.getClientInfo();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Array createArrayOf(String str, Object[] objs) throws SQLException {
      try {
         return super.delegate.createArrayOf(str, objs);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Struct createStruct(String str, Object[] objs) throws SQLException {
      try {
         return super.delegate.createStruct(str, objs);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setSchema(String str) throws SQLException {
      try {
         super.delegate.setSchema(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public String getSchema() throws SQLException {
      try {
         return super.delegate.getSchema();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void abort(Executor executor) throws SQLException {
      try {
         super.delegate.abort(executor);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNetworkTimeout(Executor executor, int value) throws SQLException {
      try {
         super.setNetworkTimeout(executor, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getNetworkTimeout() throws SQLException {
      try {
         return super.delegate.getNetworkTimeout();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   protected HikariProxyConnection(PoolEntry poolEntry, Connection connection, FastList fastList, ProxyLeakTask proxyLeakTask, long value, boolean flag, boolean flag2) {
      super(poolEntry, connection, fastList, proxyLeakTask, value, flag, flag2);
   }
}
