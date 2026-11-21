package io.lumine.mythic.bukkit.utils.storage.sql.hikari.pool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Wrapper;
import java.util.Calendar;

public class HikariProxyPreparedStatement extends ProxyPreparedStatement implements AutoCloseable, Wrapper, PreparedStatement, Statement {
   @Override
   public boolean isWrapperFor(Class clazz) throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).isWrapperFor(clazz);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public ResultSet executeQuery() throws SQLException {
      try {
         return super.executeQuery();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int executeUpdate() throws SQLException {
      try {
         return super.executeUpdate();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNull(int value, int value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNull(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBoolean(int value, boolean flag) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBoolean(value, flag);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setByte(int value, byte b) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setByte(value, b);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setShort(int value, short value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setShort(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setInt(int value, int value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setInt(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setLong(int value, long value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setLong(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setFloat(int value, float value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setFloat(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setDouble(int value, double value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setDouble(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBigDecimal(int value, BigDecimal bigDecimal) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBigDecimal(value, bigDecimal);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setString(int value, String str) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setString(value, str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBytes(int value, byte[] bs) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBytes(value, bs);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setDate(int value, Date date) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setDate(value, date);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setTime(int value, Time time) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setTime(value, time);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setTimestamp(int value, Timestamp timestamp) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setTimestamp(value, timestamp);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setAsciiStream(int value, InputStream inputStream, int value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setAsciiStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setUnicodeStream(int value, InputStream inputStream, int value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setUnicodeStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBinaryStream(int value, InputStream inputStream, int value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBinaryStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void clearParameters() throws SQLException {
      try {
         ((PreparedStatement)super.delegate).clearParameters();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, int value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setObject(value, obj, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setObject(int value, Object obj) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setObject(value, obj);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean execute() throws SQLException {
      try {
         return super.execute();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void addBatch() throws SQLException {
      try {
         ((PreparedStatement)super.delegate).addBatch();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setCharacterStream(int value, Reader reader, int value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setCharacterStream(value, reader, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setRef(int value, Ref ref) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setRef(value, ref);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBlob(int value, Blob blob) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBlob(value, blob);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setClob(int value, Clob clob) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setClob(value, clob);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setArray(int value, Array array) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setArray(value, array);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public ResultSetMetaData getMetaData() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getMetaData();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setDate(int value, Date date, Calendar calendar) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setDate(value, date, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setTime(int value, Time time, Calendar calendar) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setTime(value, time, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setTimestamp(int value, Timestamp timestamp, Calendar calendar) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setTimestamp(value, timestamp, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNull(int value, int value2, String str) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNull(value, value2, str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setURL(int value, URL url) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setURL(value, url);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public ParameterMetaData getParameterMetaData() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getParameterMetaData();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setRowId(int value, RowId rowId) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setRowId(value, rowId);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNString(int value, String str) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNString(value, str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNCharacterStream(int value, Reader reader, long value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNCharacterStream(value, reader, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNClob(int value, NClob nClob) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNClob(value, nClob);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setClob(int value, Reader reader, long value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setClob(value, reader, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBlob(int value, InputStream inputStream, long value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBlob(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNClob(int value, Reader reader, long value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNClob(value, reader, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setSQLXML(int value, SQLXML sqlxml) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setSQLXML(value, sqlxml);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, int value2, int value3) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setObject(value, obj, value2, value3);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setAsciiStream(int value, InputStream inputStream, long value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setAsciiStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBinaryStream(int value, InputStream inputStream, long value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBinaryStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setCharacterStream(int value, Reader reader, long value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setCharacterStream(value, reader, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setAsciiStream(int value, InputStream inputStream) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setAsciiStream(value, inputStream);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBinaryStream(int value, InputStream inputStream) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBinaryStream(value, inputStream);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setCharacterStream(int value, Reader reader) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setCharacterStream(value, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNCharacterStream(int value, Reader reader) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNCharacterStream(value, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setClob(int value, Reader reader) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setClob(value, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBlob(int value, InputStream inputStream) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setBlob(value, inputStream);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNClob(int value, Reader reader) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setNClob(value, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, SQLType sqlType, int value2) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setObject(value, obj, sqlType, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, SQLType sqlType) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setObject(value, obj, sqlType);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long executeLargeUpdate() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).executeLargeUpdate();
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
         return ((PreparedStatement)super.delegate).getMaxFieldSize();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setMaxFieldSize(int value) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setMaxFieldSize(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getMaxRows() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getMaxRows();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setMaxRows(int value) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setMaxRows(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setEscapeProcessing(boolean flag) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setEscapeProcessing(flag);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getQueryTimeout() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getQueryTimeout();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setQueryTimeout(int value) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setQueryTimeout(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void cancel() throws SQLException {
      try {
         ((PreparedStatement)super.delegate).cancel();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public SQLWarning getWarnings() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getWarnings();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void clearWarnings() throws SQLException {
      try {
         ((PreparedStatement)super.delegate).clearWarnings();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setCursorName(String str) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setCursorName(str);
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
         return ((PreparedStatement)super.delegate).getUpdateCount();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean getMoreResults() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getMoreResults();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setFetchDirection(int value) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setFetchDirection(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getFetchDirection() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getFetchDirection();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setFetchSize(int value) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setFetchSize(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getFetchSize() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getFetchSize();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getResultSetConcurrency() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getResultSetConcurrency();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getResultSetType() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getResultSetType();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void addBatch(String str) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).addBatch(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void clearBatch() throws SQLException {
      try {
         ((PreparedStatement)super.delegate).clearBatch();
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
         return ((PreparedStatement)super.delegate).getMoreResults(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public ResultSet getGeneratedKeys() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getGeneratedKeys();
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
         return ((PreparedStatement)super.delegate).getResultSetHoldability();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isClosed() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).isClosed();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setPoolable(boolean flag) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setPoolable(flag);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isPoolable() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).isPoolable();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void closeOnCompletion() throws SQLException {
      try {
         ((PreparedStatement)super.delegate).closeOnCompletion();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isCloseOnCompletion() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).isCloseOnCompletion();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long getLargeUpdateCount() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getLargeUpdateCount();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setLargeMaxRows(long value) throws SQLException {
      try {
         ((PreparedStatement)super.delegate).setLargeMaxRows(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long getLargeMaxRows() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).getLargeMaxRows();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long[] executeLargeBatch() throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).executeLargeBatch();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long executeLargeUpdate(String str) throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).executeLargeUpdate(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long executeLargeUpdate(String str, int value) throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).executeLargeUpdate(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long executeLargeUpdate(String str, int[] ints) throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).executeLargeUpdate(str, ints);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long executeLargeUpdate(String str, String[] strs) throws SQLException {
      try {
         return ((PreparedStatement)super.delegate).executeLargeUpdate(str, strs);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   protected HikariProxyPreparedStatement(ProxyConnection proxyConnection, PreparedStatement preparedStatement) {
      super(proxyConnection, preparedStatement);
   }
}
