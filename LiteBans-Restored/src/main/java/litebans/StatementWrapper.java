package litebans;

import java.sql.SQLException;
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
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public abstract class StatementWrapper implements PreparedStatement {
   private final PreparedStatement preparedStatement;

   public StatementWrapper(PreparedStatement preparedStatement) {
      this.preparedStatement = preparedStatement;
   }

   @Override
   public ResultSet executeQuery() {
      try {
         return this.preparedStatement.executeQuery();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int executeUpdate() {
      try {
         return this.preparedStatement.executeUpdate();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNull(int value, int value2) {
      try {
         this.preparedStatement.setNull(value, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBoolean(int value, boolean flag) {
      try {
         this.preparedStatement.setBoolean(value, flag);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setByte(int value, byte b) {
      try {
         this.preparedStatement.setByte(value, b);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setShort(int value, short value2) {
      try {
         this.preparedStatement.setShort(value, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setInt(int value, int value2) {
      try {
         this.preparedStatement.setInt(value, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setLong(int value, long value2) {
      try {
         this.preparedStatement.setLong(value, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setFloat(int value, float number) {
      try {
         this.preparedStatement.setFloat(value, number);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setDouble(int value, double number) {
      try {
         this.preparedStatement.setDouble(value, number);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBigDecimal(int value, BigDecimal bigDecimal) {
      try {
         this.preparedStatement.setBigDecimal(value, bigDecimal);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setString(int value, String text) {
      try {
         this.preparedStatement.setString(value, text);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBytes(int value, byte[] bytes) {
      try {
         this.preparedStatement.setBytes(value, bytes);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setDate(int value, Date date) {
      try {
         this.preparedStatement.setDate(value, date);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setTime(int value, Time time) {
      try {
         this.preparedStatement.setTime(value, time);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setTimestamp(int value, Timestamp timestamp) {
      try {
         this.preparedStatement.setTimestamp(value, timestamp);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setAsciiStream(int value, InputStream inputStream, int value2) {
      try {
         this.preparedStatement.setAsciiStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setUnicodeStream(int value, InputStream inputStream, int value2) {
      try {
         this.preparedStatement.setUnicodeStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBinaryStream(int value, InputStream inputStream, int value2) {
      try {
         this.preparedStatement.setBinaryStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void clearParameters() {
      try {
         this.preparedStatement.clearParameters();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, int value2) {
      try {
         this.preparedStatement.setObject(value, obj, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setObject(int value, Object obj) {
      try {
         this.preparedStatement.setObject(value, obj);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean execute() {
      try {
         return this.preparedStatement.execute();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void addBatch() {
      try {
         this.preparedStatement.addBatch();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setCharacterStream(int value, Reader reader, int value2) {
      try {
         this.preparedStatement.setCharacterStream(value, reader, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setRef(int value, Ref ref) {
      try {
         this.preparedStatement.setRef(value, ref);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBlob(int value, Blob blob) {
      try {
         this.preparedStatement.setBlob(value, blob);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setClob(int value, Clob clob) {
      try {
         this.preparedStatement.setClob(value, clob);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setArray(int value, Array array) {
      try {
         this.preparedStatement.setArray(value, array);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSetMetaData getMetaData() {
      try {
         return this.preparedStatement.getMetaData();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setDate(int value, Date date, Calendar calendar) {
      try {
         this.preparedStatement.setDate(value, date, calendar);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setTime(int value, Time time, Calendar calendar) {
      try {
         this.preparedStatement.setTime(value, time, calendar);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setTimestamp(int value, Timestamp timestamp, Calendar calendar) {
      try {
         this.preparedStatement.setTimestamp(value, timestamp, calendar);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNull(int value, int value2, String text) {
      try {
         this.preparedStatement.setNull(value, value2, text);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setURL(int value, URL url) {
      try {
         this.preparedStatement.setURL(value, url);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ParameterMetaData getParameterMetaData() {
      try {
         return this.preparedStatement.getParameterMetaData();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setRowId(int value, RowId rowId) {
      try {
         this.preparedStatement.setRowId(value, rowId);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNString(int value, String text) {
      try {
         this.preparedStatement.setNString(value, text);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNCharacterStream(int value, Reader reader, long value2) {
      try {
         this.preparedStatement.setNCharacterStream(value, reader, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNClob(int value, NClob nClob) {
      try {
         this.preparedStatement.setNClob(value, nClob);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setClob(int value, Reader reader, long value2) {
      try {
         this.preparedStatement.setClob(value, reader, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBlob(int value, InputStream inputStream, long value2) {
      try {
         this.preparedStatement.setBlob(value, inputStream, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNClob(int value, Reader reader, long value2) {
      try {
         this.preparedStatement.setNClob(value, reader, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setSQLXML(int value, SQLXML sqlxml) {
      try {
         this.preparedStatement.setSQLXML(value, sqlxml);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, int value2, int value3) {
      try {
         this.preparedStatement.setObject(value, obj, value2, value3);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setAsciiStream(int value, InputStream inputStream, long value2) {
      try {
         this.preparedStatement.setAsciiStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBinaryStream(int value, InputStream inputStream, long value2) {
      try {
         this.preparedStatement.setBinaryStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setCharacterStream(int value, Reader reader, long value2) {
      try {
         this.preparedStatement.setCharacterStream(value, reader, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setAsciiStream(int value, InputStream inputStream) {
      try {
         this.preparedStatement.setAsciiStream(value, inputStream);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBinaryStream(int value, InputStream inputStream) {
      try {
         this.preparedStatement.setBinaryStream(value, inputStream);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setCharacterStream(int value, Reader reader) {
      try {
         this.preparedStatement.setCharacterStream(value, reader);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNCharacterStream(int value, Reader reader) {
      try {
         this.preparedStatement.setNCharacterStream(value, reader);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setClob(int value, Reader reader) {
      try {
         this.preparedStatement.setClob(value, reader);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBlob(int value, InputStream inputStream) {
      try {
         this.preparedStatement.setBlob(value, inputStream);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNClob(int value, Reader reader) {
      try {
         this.preparedStatement.setNClob(value, reader);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, SQLType sqlType, int value2) {
      try {
         this.preparedStatement.setObject(value, obj, sqlType, value2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, SQLType sqlType) {
      try {
         this.preparedStatement.setObject(value, obj, sqlType);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long executeLargeUpdate() {
      try {
         return this.preparedStatement.executeLargeUpdate();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet executeQuery(String text) {
      try {
         return this.preparedStatement.executeQuery(text);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int executeUpdate(String text) {
      try {
         return this.preparedStatement.executeUpdate(text);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void close() {
      try {
         this.preparedStatement.close();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxFieldSize() {
      try {
         return this.preparedStatement.getMaxFieldSize();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setMaxFieldSize(int value) {
      try {
         this.preparedStatement.setMaxFieldSize(value);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxRows() {
      try {
         return this.preparedStatement.getMaxRows();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setMaxRows(int value) {
      try {
         this.preparedStatement.setMaxRows(value);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setEscapeProcessing(boolean flag) {
      try {
         this.preparedStatement.setEscapeProcessing(flag);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getQueryTimeout() {
      try {
         return this.preparedStatement.getQueryTimeout();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setQueryTimeout(int value) {
      try {
         this.preparedStatement.setQueryTimeout(value);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void cancel() {
      try {
         this.preparedStatement.cancel();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public SQLWarning getWarnings() {
      try {
         return this.preparedStatement.getWarnings();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void clearWarnings() {
      try {
         this.preparedStatement.clearWarnings();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setCursorName(String text) {
      try {
         this.preparedStatement.setCursorName(text);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean execute(String text) {
      try {
         return this.preparedStatement.execute(text);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getResultSet() {
      try {
         return this.preparedStatement.getResultSet();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getUpdateCount() {
      try {
         return this.preparedStatement.getUpdateCount();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean getMoreResults() {
      try {
         return this.preparedStatement.getMoreResults();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setFetchDirection(int value) {
      try {
         this.preparedStatement.setFetchDirection(value);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getFetchDirection() {
      try {
         return this.preparedStatement.getFetchDirection();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setFetchSize(int value) {
      try {
         this.preparedStatement.setFetchSize(value);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getFetchSize() {
      try {
         return this.preparedStatement.getFetchSize();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getResultSetConcurrency() {
      try {
         return this.preparedStatement.getResultSetConcurrency();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getResultSetType() {
      try {
         return this.preparedStatement.getResultSetType();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void addBatch(String text) {
      try {
         this.preparedStatement.addBatch(text);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void clearBatch() {
      try {
         this.preparedStatement.clearBatch();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int[] executeBatch() {
      try {
         return this.preparedStatement.executeBatch();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Connection getConnection() {
      try {
         return this.preparedStatement.getConnection();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean getMoreResults(int value) {
      try {
         return this.preparedStatement.getMoreResults(value);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getGeneratedKeys() {
      try {
         return this.preparedStatement.getGeneratedKeys();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int executeUpdate(String text, int value) {
      try {
         return this.preparedStatement.executeUpdate(text, value);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int executeUpdate(String text, int[] ints) {
      try {
         return this.preparedStatement.executeUpdate(text, ints);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int executeUpdate(String text, String[] texts) {
      try {
         return this.preparedStatement.executeUpdate(text, texts);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean execute(String text, int value) {
      try {
         return this.preparedStatement.execute(text, value);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean execute(String text, int[] ints) {
      try {
         return this.preparedStatement.execute(text, ints);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean execute(String text, String[] texts) {
      try {
         return this.preparedStatement.execute(text, texts);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getResultSetHoldability() {
      try {
         return this.preparedStatement.getResultSetHoldability();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isClosed() {
      try {
         return this.preparedStatement.isClosed();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setPoolable(boolean flag) {
      try {
         this.preparedStatement.setPoolable(flag);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isPoolable() {
      try {
         return this.preparedStatement.isPoolable();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void closeOnCompletion() {
      try {
         this.preparedStatement.closeOnCompletion();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isCloseOnCompletion() {
      try {
         return this.preparedStatement.isCloseOnCompletion();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long getLargeUpdateCount() {
      try {
         return this.preparedStatement.getLargeUpdateCount();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Object unwrap(Class class2) {
      try {
         return this.preparedStatement.unwrap(class2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isWrapperFor(Class class2) {
      try {
         return this.preparedStatement.isWrapperFor(class2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
