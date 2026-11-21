package io.lumine.mythic.bukkit.utils.storage.sql.hikari.pool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
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
import java.util.Map;

public class HikariProxyCallableStatement extends ProxyCallableStatement implements AutoCloseable, Wrapper, PreparedStatement, CallableStatement, Statement {
   @Override
   public boolean isWrapperFor(Class clazz) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).isWrapperFor(clazz);
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
         ((CallableStatement)super.delegate).setNull(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBoolean(int value, boolean flag) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBoolean(value, flag);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setByte(int value, byte b) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setByte(value, b);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setShort(int value, short value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setShort(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setInt(int value, int value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setInt(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setLong(int value, long value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setLong(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setFloat(int value, float value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setFloat(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setDouble(int value, double value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setDouble(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBigDecimal(int value, BigDecimal bigDecimal) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBigDecimal(value, bigDecimal);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setString(int value, String str) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setString(value, str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBytes(int value, byte[] bs) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBytes(value, bs);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setDate(int value, Date date) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setDate(value, date);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setTime(int value, Time time) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTime(value, time);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setTimestamp(int value, Timestamp timestamp) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTimestamp(value, timestamp);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setAsciiStream(int value, InputStream inputStream, int value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setAsciiStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setUnicodeStream(int value, InputStream inputStream, int value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setUnicodeStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBinaryStream(int value, InputStream inputStream, int value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBinaryStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void clearParameters() throws SQLException {
      try {
         ((CallableStatement)super.delegate).clearParameters();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, int value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(value, obj, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setObject(int value, Object obj) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(value, obj);
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
         ((CallableStatement)super.delegate).addBatch();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setCharacterStream(int value, Reader reader, int value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setCharacterStream(value, reader, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setRef(int value, Ref ref) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setRef(value, ref);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBlob(int value, Blob blob) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBlob(value, blob);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setClob(int value, Clob clob) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setClob(value, clob);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setArray(int value, Array array) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setArray(value, array);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public ResultSetMetaData getMetaData() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getMetaData();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setDate(int value, Date date, Calendar calendar) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setDate(value, date, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setTime(int value, Time time, Calendar calendar) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTime(value, time, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setTimestamp(int value, Timestamp timestamp, Calendar calendar) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTimestamp(value, timestamp, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNull(int value, int value2, String str) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNull(value, value2, str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setURL(int value, URL url) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setURL(value, url);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public ParameterMetaData getParameterMetaData() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getParameterMetaData();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setRowId(int value, RowId rowId) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setRowId(value, rowId);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNString(int value, String str) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNString(value, str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNCharacterStream(int value, Reader reader, long value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNCharacterStream(value, reader, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNClob(int value, NClob nClob) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNClob(value, nClob);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setClob(int value, Reader reader, long value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setClob(value, reader, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBlob(int value, InputStream inputStream, long value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBlob(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNClob(int value, Reader reader, long value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNClob(value, reader, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setSQLXML(int value, SQLXML sqlxml) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setSQLXML(value, sqlxml);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, int value2, int value3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(value, obj, value2, value3);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setAsciiStream(int value, InputStream inputStream, long value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setAsciiStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBinaryStream(int value, InputStream inputStream, long value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBinaryStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setCharacterStream(int value, Reader reader, long value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setCharacterStream(value, reader, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setAsciiStream(int value, InputStream inputStream) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setAsciiStream(value, inputStream);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBinaryStream(int value, InputStream inputStream) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBinaryStream(value, inputStream);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setCharacterStream(int value, Reader reader) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setCharacterStream(value, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNCharacterStream(int value, Reader reader) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNCharacterStream(value, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setClob(int value, Reader reader) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setClob(value, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBlob(int value, InputStream inputStream) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBlob(value, inputStream);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNClob(int value, Reader reader) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNClob(value, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, SQLType sqlType, int value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(value, obj, sqlType, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, SQLType sqlType) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(value, obj, sqlType);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long executeLargeUpdate() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).executeLargeUpdate();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void registerOutParameter(int value, int value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void registerOutParameter(int value, int value2, int value3) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(value, value2, value3);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean wasNull() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).wasNull();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public String getString(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getString(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean getBoolean(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBoolean(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public byte getByte(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getByte(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public short getShort(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getShort(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getInt(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getInt(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long getLong(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getLong(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public float getFloat(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getFloat(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public double getDouble(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getDouble(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public BigDecimal getBigDecimal(int value, int value2) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBigDecimal(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public byte[] getBytes(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBytes(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Date getDate(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getDate(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Time getTime(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTime(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Timestamp getTimestamp(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTimestamp(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Object getObject(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getObject(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public BigDecimal getBigDecimal(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBigDecimal(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Object getObject(int value, Map map) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getObject(value, map);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Ref getRef(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getRef(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Blob getBlob(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBlob(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Clob getClob(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getClob(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Array getArray(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getArray(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Date getDate(int value, Calendar calendar) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getDate(value, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Time getTime(int value, Calendar calendar) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTime(value, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Timestamp getTimestamp(int value, Calendar calendar) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTimestamp(value, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void registerOutParameter(int value, int value2, String str) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(value, value2, str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void registerOutParameter(String str, int value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void registerOutParameter(String str, int value, int value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(str, value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void registerOutParameter(String str, int value, String str2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(str, value, str2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public URL getURL(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getURL(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setURL(String str, URL url) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setURL(str, url);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNull(String str, int value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNull(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBoolean(String str, boolean flag) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBoolean(str, flag);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setByte(String str, byte b) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setByte(str, b);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setShort(String str, short value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setShort(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setInt(String str, int value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setInt(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setLong(String str, long value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setLong(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setFloat(String str, float value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setFloat(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setDouble(String str, double value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setDouble(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBigDecimal(String str, BigDecimal bigDecimal) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBigDecimal(str, bigDecimal);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setString(String str, String str2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setString(str, str2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBytes(String str, byte[] bs) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBytes(str, bs);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setDate(String str, Date date) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setDate(str, date);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setTime(String str, Time time) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTime(str, time);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setTimestamp(String str, Timestamp timestamp) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTimestamp(str, timestamp);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setAsciiStream(String str, InputStream inputStream, int value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setAsciiStream(str, inputStream, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBinaryStream(String str, InputStream inputStream, int value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBinaryStream(str, inputStream, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setObject(String str, Object obj, int value, int value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(str, obj, value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setObject(String str, Object obj, int value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(str, obj, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setObject(String str, Object obj) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(str, obj);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setCharacterStream(String str, Reader reader, int value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setCharacterStream(str, reader, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setDate(String str, Date date, Calendar calendar) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setDate(str, date, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setTime(String str, Time time, Calendar calendar) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTime(str, time, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setTimestamp(String str, Timestamp timestamp, Calendar calendar) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setTimestamp(str, timestamp, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNull(String str, int value, String str2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNull(str, value, str2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public String getString(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getString(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean getBoolean(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBoolean(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public byte getByte(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getByte(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public short getShort(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getShort(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getInt(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getInt(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long getLong(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getLong(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public float getFloat(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getFloat(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public double getDouble(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getDouble(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public byte[] getBytes(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBytes(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Date getDate(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getDate(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Time getTime(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTime(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Timestamp getTimestamp(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTimestamp(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Object getObject(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getObject(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public BigDecimal getBigDecimal(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBigDecimal(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Object getObject(String str, Map map) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getObject(str, map);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Ref getRef(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getRef(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Blob getBlob(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getBlob(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Clob getClob(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getClob(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Array getArray(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getArray(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Date getDate(String str, Calendar calendar) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getDate(str, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Time getTime(String str, Calendar calendar) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTime(str, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Timestamp getTimestamp(String str, Calendar calendar) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getTimestamp(str, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public URL getURL(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getURL(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public RowId getRowId(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getRowId(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public RowId getRowId(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getRowId(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setRowId(String str, RowId rowId) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setRowId(str, rowId);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNString(String str, String str2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNString(str, str2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNCharacterStream(String str, Reader reader, long value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNCharacterStream(str, reader, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNClob(String str, NClob nClob) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNClob(str, nClob);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setClob(String str, Reader reader, long value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setClob(str, reader, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBlob(String str, InputStream inputStream, long value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBlob(str, inputStream, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNClob(String str, Reader reader, long value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNClob(str, reader, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public NClob getNClob(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getNClob(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public NClob getNClob(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getNClob(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setSQLXML(String str, SQLXML sqlxml) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setSQLXML(str, sqlxml);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public SQLXML getSQLXML(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getSQLXML(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public SQLXML getSQLXML(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getSQLXML(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public String getNString(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getNString(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public String getNString(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getNString(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Reader getNCharacterStream(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getNCharacterStream(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Reader getNCharacterStream(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getNCharacterStream(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Reader getCharacterStream(int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getCharacterStream(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Reader getCharacterStream(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getCharacterStream(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBlob(String str, Blob blob) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBlob(str, blob);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setClob(String str, Clob clob) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setClob(str, clob);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setAsciiStream(String str, InputStream inputStream, long value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setAsciiStream(str, inputStream, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBinaryStream(String str, InputStream inputStream, long value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBinaryStream(str, inputStream, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setCharacterStream(String str, Reader reader, long value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setCharacterStream(str, reader, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setAsciiStream(String str, InputStream inputStream) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setAsciiStream(str, inputStream);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBinaryStream(String str, InputStream inputStream) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBinaryStream(str, inputStream);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setCharacterStream(String str, Reader reader) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setCharacterStream(str, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNCharacterStream(String str, Reader reader) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNCharacterStream(str, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setClob(String str, Reader reader) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setClob(str, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setBlob(String str, InputStream inputStream) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setBlob(str, inputStream);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setNClob(String str, Reader reader) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setNClob(str, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Object getObject(int value, Class clazz) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getObject(value, clazz);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Object getObject(String str, Class clazz) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getObject(str, clazz);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setObject(String str, Object obj, SQLType sqlType, int value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(str, obj, sqlType, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setObject(String str, Object obj, SQLType sqlType) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setObject(str, obj, sqlType);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void registerOutParameter(int value, SQLType sqlType) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(value, sqlType);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void registerOutParameter(int value, SQLType sqlType, int value2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(value, sqlType, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void registerOutParameter(int value, SQLType sqlType, String str) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(value, sqlType, str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void registerOutParameter(String str, SQLType sqlType) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(str, sqlType);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void registerOutParameter(String str, SQLType sqlType, int value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(str, sqlType, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void registerOutParameter(String str, SQLType sqlType, String str2) throws SQLException {
      try {
         ((CallableStatement)super.delegate).registerOutParameter(str, sqlType, str2);
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
         return ((CallableStatement)super.delegate).getMaxFieldSize();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setMaxFieldSize(int value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setMaxFieldSize(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getMaxRows() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getMaxRows();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setMaxRows(int value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setMaxRows(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setEscapeProcessing(boolean flag) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setEscapeProcessing(flag);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getQueryTimeout() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getQueryTimeout();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setQueryTimeout(int value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setQueryTimeout(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void cancel() throws SQLException {
      try {
         ((CallableStatement)super.delegate).cancel();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public SQLWarning getWarnings() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getWarnings();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void clearWarnings() throws SQLException {
      try {
         ((CallableStatement)super.delegate).clearWarnings();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setCursorName(String str) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setCursorName(str);
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
         return ((CallableStatement)super.delegate).getUpdateCount();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean getMoreResults() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getMoreResults();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setFetchDirection(int value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setFetchDirection(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getFetchDirection() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getFetchDirection();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setFetchSize(int value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setFetchSize(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getFetchSize() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getFetchSize();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getResultSetConcurrency() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getResultSetConcurrency();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getResultSetType() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getResultSetType();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void addBatch(String str) throws SQLException {
      try {
         ((CallableStatement)super.delegate).addBatch(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void clearBatch() throws SQLException {
      try {
         ((CallableStatement)super.delegate).clearBatch();
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
         return ((CallableStatement)super.delegate).getMoreResults(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public ResultSet getGeneratedKeys() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getGeneratedKeys();
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
         return ((CallableStatement)super.delegate).getResultSetHoldability();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isClosed() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).isClosed();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setPoolable(boolean flag) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setPoolable(flag);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isPoolable() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).isPoolable();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void closeOnCompletion() throws SQLException {
      try {
         ((CallableStatement)super.delegate).closeOnCompletion();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isCloseOnCompletion() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).isCloseOnCompletion();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long getLargeUpdateCount() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getLargeUpdateCount();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void setLargeMaxRows(long value) throws SQLException {
      try {
         ((CallableStatement)super.delegate).setLargeMaxRows(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long getLargeMaxRows() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).getLargeMaxRows();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long[] executeLargeBatch() throws SQLException {
      try {
         return ((CallableStatement)super.delegate).executeLargeBatch();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long executeLargeUpdate(String str) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).executeLargeUpdate(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long executeLargeUpdate(String str, int value) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).executeLargeUpdate(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long executeLargeUpdate(String str, int[] ints) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).executeLargeUpdate(str, ints);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long executeLargeUpdate(String str, String[] strs) throws SQLException {
      try {
         return ((CallableStatement)super.delegate).executeLargeUpdate(str, strs);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   protected HikariProxyCallableStatement(ProxyConnection proxyConnection, CallableStatement callableStatement) {
      super(proxyConnection, callableStatement);
   }
}
