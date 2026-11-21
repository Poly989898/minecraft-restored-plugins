package io.lumine.mythic.bukkit.utils.storage.sql.hikari.pool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Wrapper;
import java.util.Calendar;
import java.util.Map;

public class HikariProxyResultSet extends ProxyResultSet implements ResultSet, AutoCloseable, Wrapper {
   @Override
   public boolean next() throws SQLException {
      try {
         return super.delegate.next();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void close() throws SQLException {
      try {
         super.delegate.close();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean wasNull() throws SQLException {
      try {
         return super.delegate.wasNull();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public String getString(int value) throws SQLException {
      try {
         return super.delegate.getString(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean getBoolean(int value) throws SQLException {
      try {
         return super.delegate.getBoolean(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public byte getByte(int value) throws SQLException {
      try {
         return super.delegate.getByte(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public short getShort(int value) throws SQLException {
      try {
         return super.delegate.getShort(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getInt(int value) throws SQLException {
      try {
         return super.delegate.getInt(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long getLong(int value) throws SQLException {
      try {
         return super.delegate.getLong(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public float getFloat(int value) throws SQLException {
      try {
         return super.delegate.getFloat(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public double getDouble(int value) throws SQLException {
      try {
         return super.delegate.getDouble(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public BigDecimal getBigDecimal(int value, int value2) throws SQLException {
      try {
         return super.delegate.getBigDecimal(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public byte[] getBytes(int value) throws SQLException {
      try {
         return super.delegate.getBytes(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Date getDate(int value) throws SQLException {
      try {
         return super.delegate.getDate(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Time getTime(int value) throws SQLException {
      try {
         return super.delegate.getTime(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Timestamp getTimestamp(int value) throws SQLException {
      try {
         return super.delegate.getTimestamp(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public InputStream getAsciiStream(int value) throws SQLException {
      try {
         return super.delegate.getAsciiStream(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public InputStream getUnicodeStream(int value) throws SQLException {
      try {
         return super.delegate.getUnicodeStream(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public InputStream getBinaryStream(int value) throws SQLException {
      try {
         return super.delegate.getBinaryStream(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public String getString(String str) throws SQLException {
      try {
         return super.delegate.getString(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean getBoolean(String str) throws SQLException {
      try {
         return super.delegate.getBoolean(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public byte getByte(String str) throws SQLException {
      try {
         return super.delegate.getByte(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public short getShort(String str) throws SQLException {
      try {
         return super.delegate.getShort(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getInt(String str) throws SQLException {
      try {
         return super.delegate.getInt(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public long getLong(String str) throws SQLException {
      try {
         return super.delegate.getLong(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public float getFloat(String str) throws SQLException {
      try {
         return super.delegate.getFloat(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public double getDouble(String str) throws SQLException {
      try {
         return super.delegate.getDouble(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public BigDecimal getBigDecimal(String str, int value) throws SQLException {
      try {
         return super.delegate.getBigDecimal(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public byte[] getBytes(String str) throws SQLException {
      try {
         return super.delegate.getBytes(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Date getDate(String str) throws SQLException {
      try {
         return super.delegate.getDate(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Time getTime(String str) throws SQLException {
      try {
         return super.delegate.getTime(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Timestamp getTimestamp(String str) throws SQLException {
      try {
         return super.delegate.getTimestamp(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public InputStream getAsciiStream(String str) throws SQLException {
      try {
         return super.delegate.getAsciiStream(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public InputStream getUnicodeStream(String str) throws SQLException {
      try {
         return super.delegate.getUnicodeStream(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public InputStream getBinaryStream(String str) throws SQLException {
      try {
         return super.delegate.getBinaryStream(str);
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
   public String getCursorName() throws SQLException {
      try {
         return super.delegate.getCursorName();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public ResultSetMetaData getMetaData() throws SQLException {
      try {
         return super.delegate.getMetaData();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Object getObject(int value) throws SQLException {
      try {
         return super.delegate.getObject(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Object getObject(String str) throws SQLException {
      try {
         return super.delegate.getObject(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int findColumn(String str) throws SQLException {
      try {
         return super.delegate.findColumn(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Reader getCharacterStream(int value) throws SQLException {
      try {
         return super.delegate.getCharacterStream(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Reader getCharacterStream(String str) throws SQLException {
      try {
         return super.delegate.getCharacterStream(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public BigDecimal getBigDecimal(int value) throws SQLException {
      try {
         return super.delegate.getBigDecimal(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public BigDecimal getBigDecimal(String str) throws SQLException {
      try {
         return super.delegate.getBigDecimal(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isBeforeFirst() throws SQLException {
      try {
         return super.delegate.isBeforeFirst();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isAfterLast() throws SQLException {
      try {
         return super.delegate.isAfterLast();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isFirst() throws SQLException {
      try {
         return super.delegate.isFirst();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isLast() throws SQLException {
      try {
         return super.delegate.isLast();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void beforeFirst() throws SQLException {
      try {
         super.delegate.beforeFirst();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void afterLast() throws SQLException {
      try {
         super.delegate.afterLast();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean first() throws SQLException {
      try {
         return super.delegate.first();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean last() throws SQLException {
      try {
         return super.delegate.last();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getRow() throws SQLException {
      try {
         return super.delegate.getRow();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean absolute(int value) throws SQLException {
      try {
         return super.delegate.absolute(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean relative(int value) throws SQLException {
      try {
         return super.delegate.relative(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean previous() throws SQLException {
      try {
         return super.delegate.previous();
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
   public int getType() throws SQLException {
      try {
         return super.delegate.getType();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public int getConcurrency() throws SQLException {
      try {
         return super.delegate.getConcurrency();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean rowUpdated() throws SQLException {
      try {
         return super.delegate.rowUpdated();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean rowInserted() throws SQLException {
      try {
         return super.delegate.rowInserted();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean rowDeleted() throws SQLException {
      try {
         return super.delegate.rowDeleted();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateNull(int value) throws SQLException {
      try {
         super.delegate.updateNull(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBoolean(int value, boolean flag) throws SQLException {
      try {
         super.delegate.updateBoolean(value, flag);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateByte(int value, byte b) throws SQLException {
      try {
         super.delegate.updateByte(value, b);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateShort(int value, short value2) throws SQLException {
      try {
         super.delegate.updateShort(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateInt(int value, int value2) throws SQLException {
      try {
         super.delegate.updateInt(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateLong(int value, long value2) throws SQLException {
      try {
         super.delegate.updateLong(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateFloat(int value, float value2) throws SQLException {
      try {
         super.delegate.updateFloat(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateDouble(int value, double value2) throws SQLException {
      try {
         super.delegate.updateDouble(value, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBigDecimal(int value, BigDecimal bigDecimal) throws SQLException {
      try {
         super.delegate.updateBigDecimal(value, bigDecimal);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateString(int value, String str) throws SQLException {
      try {
         super.delegate.updateString(value, str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBytes(int value, byte[] bs) throws SQLException {
      try {
         super.delegate.updateBytes(value, bs);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateDate(int value, Date date) throws SQLException {
      try {
         super.delegate.updateDate(value, date);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateTime(int value, Time time) throws SQLException {
      try {
         super.delegate.updateTime(value, time);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateTimestamp(int value, Timestamp timestamp) throws SQLException {
      try {
         super.delegate.updateTimestamp(value, timestamp);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateAsciiStream(int value, InputStream inputStream, int value2) throws SQLException {
      try {
         super.delegate.updateAsciiStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBinaryStream(int value, InputStream inputStream, int value2) throws SQLException {
      try {
         super.delegate.updateBinaryStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateCharacterStream(int value, Reader reader, int value2) throws SQLException {
      try {
         super.delegate.updateCharacterStream(value, reader, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateObject(int value, Object obj, int value2) throws SQLException {
      try {
         super.delegate.updateObject(value, obj, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateObject(int value, Object obj) throws SQLException {
      try {
         super.delegate.updateObject(value, obj);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateNull(String str) throws SQLException {
      try {
         super.delegate.updateNull(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBoolean(String str, boolean flag) throws SQLException {
      try {
         super.delegate.updateBoolean(str, flag);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateByte(String str, byte b) throws SQLException {
      try {
         super.delegate.updateByte(str, b);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateShort(String str, short value) throws SQLException {
      try {
         super.delegate.updateShort(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateInt(String str, int value) throws SQLException {
      try {
         super.delegate.updateInt(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateLong(String str, long value) throws SQLException {
      try {
         super.delegate.updateLong(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateFloat(String str, float value) throws SQLException {
      try {
         super.delegate.updateFloat(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateDouble(String str, double value) throws SQLException {
      try {
         super.delegate.updateDouble(str, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBigDecimal(String str, BigDecimal bigDecimal) throws SQLException {
      try {
         super.delegate.updateBigDecimal(str, bigDecimal);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateString(String str, String str2) throws SQLException {
      try {
         super.delegate.updateString(str, str2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBytes(String str, byte[] bs) throws SQLException {
      try {
         super.delegate.updateBytes(str, bs);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateDate(String str, Date date) throws SQLException {
      try {
         super.delegate.updateDate(str, date);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateTime(String str, Time time) throws SQLException {
      try {
         super.delegate.updateTime(str, time);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateTimestamp(String str, Timestamp timestamp) throws SQLException {
      try {
         super.delegate.updateTimestamp(str, timestamp);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateAsciiStream(String str, InputStream inputStream, int value) throws SQLException {
      try {
         super.delegate.updateAsciiStream(str, inputStream, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBinaryStream(String str, InputStream inputStream, int value) throws SQLException {
      try {
         super.delegate.updateBinaryStream(str, inputStream, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateCharacterStream(String str, Reader reader, int value) throws SQLException {
      try {
         super.delegate.updateCharacterStream(str, reader, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateObject(String str, Object obj, int value) throws SQLException {
      try {
         super.delegate.updateObject(str, obj, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateObject(String str, Object obj) throws SQLException {
      try {
         super.delegate.updateObject(str, obj);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void insertRow() throws SQLException {
      try {
         super.insertRow();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateRow() throws SQLException {
      try {
         super.updateRow();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void deleteRow() throws SQLException {
      try {
         super.deleteRow();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void refreshRow() throws SQLException {
      try {
         super.delegate.refreshRow();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void cancelRowUpdates() throws SQLException {
      try {
         super.delegate.cancelRowUpdates();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void moveToInsertRow() throws SQLException {
      try {
         super.delegate.moveToInsertRow();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void moveToCurrentRow() throws SQLException {
      try {
         super.delegate.moveToCurrentRow();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Object getObject(int value, Map map) throws SQLException {
      try {
         return super.delegate.getObject(value, map);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Ref getRef(int value) throws SQLException {
      try {
         return super.delegate.getRef(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Blob getBlob(int value) throws SQLException {
      try {
         return super.delegate.getBlob(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Clob getClob(int value) throws SQLException {
      try {
         return super.delegate.getClob(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Array getArray(int value) throws SQLException {
      try {
         return super.delegate.getArray(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Object getObject(String str, Map map) throws SQLException {
      try {
         return super.delegate.getObject(str, map);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Ref getRef(String str) throws SQLException {
      try {
         return super.delegate.getRef(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Blob getBlob(String str) throws SQLException {
      try {
         return super.delegate.getBlob(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Clob getClob(String str) throws SQLException {
      try {
         return super.delegate.getClob(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Array getArray(String str) throws SQLException {
      try {
         return super.delegate.getArray(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Date getDate(int value, Calendar calendar) throws SQLException {
      try {
         return super.delegate.getDate(value, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Date getDate(String str, Calendar calendar) throws SQLException {
      try {
         return super.delegate.getDate(str, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Time getTime(int value, Calendar calendar) throws SQLException {
      try {
         return super.delegate.getTime(value, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Time getTime(String str, Calendar calendar) throws SQLException {
      try {
         return super.delegate.getTime(str, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Timestamp getTimestamp(int value, Calendar calendar) throws SQLException {
      try {
         return super.delegate.getTimestamp(value, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Timestamp getTimestamp(String str, Calendar calendar) throws SQLException {
      try {
         return super.delegate.getTimestamp(str, calendar);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public URL getURL(int value) throws SQLException {
      try {
         return super.delegate.getURL(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public URL getURL(String str) throws SQLException {
      try {
         return super.delegate.getURL(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateRef(int value, Ref ref) throws SQLException {
      try {
         super.delegate.updateRef(value, ref);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateRef(String str, Ref ref) throws SQLException {
      try {
         super.delegate.updateRef(str, ref);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBlob(int value, Blob blob) throws SQLException {
      try {
         super.delegate.updateBlob(value, blob);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBlob(String str, Blob blob) throws SQLException {
      try {
         super.delegate.updateBlob(str, blob);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateClob(int value, Clob clob) throws SQLException {
      try {
         super.delegate.updateClob(value, clob);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateClob(String str, Clob clob) throws SQLException {
      try {
         super.delegate.updateClob(str, clob);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateArray(int value, Array array) throws SQLException {
      try {
         super.delegate.updateArray(value, array);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateArray(String str, Array array) throws SQLException {
      try {
         super.delegate.updateArray(str, array);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public RowId getRowId(int value) throws SQLException {
      try {
         return super.delegate.getRowId(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public RowId getRowId(String str) throws SQLException {
      try {
         return super.delegate.getRowId(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateRowId(int value, RowId rowId) throws SQLException {
      try {
         super.delegate.updateRowId(value, rowId);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateRowId(String str, RowId rowId) throws SQLException {
      try {
         super.delegate.updateRowId(str, rowId);
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
   public boolean isClosed() throws SQLException {
      try {
         return super.delegate.isClosed();
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateNString(int value, String str) throws SQLException {
      try {
         super.delegate.updateNString(value, str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateNString(String str, String str2) throws SQLException {
      try {
         super.delegate.updateNString(str, str2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateNClob(int value, NClob nClob) throws SQLException {
      try {
         super.delegate.updateNClob(value, nClob);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateNClob(String str, NClob nClob) throws SQLException {
      try {
         super.delegate.updateNClob(str, nClob);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public NClob getNClob(int value) throws SQLException {
      try {
         return super.delegate.getNClob(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public NClob getNClob(String str) throws SQLException {
      try {
         return super.delegate.getNClob(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public SQLXML getSQLXML(int value) throws SQLException {
      try {
         return super.delegate.getSQLXML(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public SQLXML getSQLXML(String str) throws SQLException {
      try {
         return super.delegate.getSQLXML(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateSQLXML(int value, SQLXML sqlxml) throws SQLException {
      try {
         super.delegate.updateSQLXML(value, sqlxml);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateSQLXML(String str, SQLXML sqlxml) throws SQLException {
      try {
         super.delegate.updateSQLXML(str, sqlxml);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public String getNString(int value) throws SQLException {
      try {
         return super.delegate.getNString(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public String getNString(String str) throws SQLException {
      try {
         return super.delegate.getNString(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Reader getNCharacterStream(int value) throws SQLException {
      try {
         return super.delegate.getNCharacterStream(value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Reader getNCharacterStream(String str) throws SQLException {
      try {
         return super.delegate.getNCharacterStream(str);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateNCharacterStream(int value, Reader reader, long value2) throws SQLException {
      try {
         super.delegate.updateNCharacterStream(value, reader, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateNCharacterStream(String str, Reader reader, long value) throws SQLException {
      try {
         super.delegate.updateNCharacterStream(str, reader, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateAsciiStream(int value, InputStream inputStream, long value2) throws SQLException {
      try {
         super.delegate.updateAsciiStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBinaryStream(int value, InputStream inputStream, long value2) throws SQLException {
      try {
         super.delegate.updateBinaryStream(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateCharacterStream(int value, Reader reader, long value2) throws SQLException {
      try {
         super.delegate.updateCharacterStream(value, reader, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateAsciiStream(String str, InputStream inputStream, long value) throws SQLException {
      try {
         super.delegate.updateAsciiStream(str, inputStream, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBinaryStream(String str, InputStream inputStream, long value) throws SQLException {
      try {
         super.delegate.updateBinaryStream(str, inputStream, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateCharacterStream(String str, Reader reader, long value) throws SQLException {
      try {
         super.delegate.updateCharacterStream(str, reader, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBlob(int value, InputStream inputStream, long value2) throws SQLException {
      try {
         super.delegate.updateBlob(value, inputStream, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBlob(String str, InputStream inputStream, long value) throws SQLException {
      try {
         super.delegate.updateBlob(str, inputStream, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateClob(int value, Reader reader, long value2) throws SQLException {
      try {
         super.delegate.updateClob(value, reader, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateClob(String str, Reader reader, long value) throws SQLException {
      try {
         super.delegate.updateClob(str, reader, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateNClob(int value, Reader reader, long value2) throws SQLException {
      try {
         super.delegate.updateNClob(value, reader, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateNClob(String str, Reader reader, long value) throws SQLException {
      try {
         super.delegate.updateNClob(str, reader, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateNCharacterStream(int value, Reader reader) throws SQLException {
      try {
         super.delegate.updateNCharacterStream(value, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateNCharacterStream(String str, Reader reader) throws SQLException {
      try {
         super.delegate.updateNCharacterStream(str, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateAsciiStream(int value, InputStream inputStream) throws SQLException {
      try {
         super.delegate.updateAsciiStream(value, inputStream);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBinaryStream(int value, InputStream inputStream) throws SQLException {
      try {
         super.delegate.updateBinaryStream(value, inputStream);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateCharacterStream(int value, Reader reader) throws SQLException {
      try {
         super.delegate.updateCharacterStream(value, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateAsciiStream(String str, InputStream inputStream) throws SQLException {
      try {
         super.delegate.updateAsciiStream(str, inputStream);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBinaryStream(String str, InputStream inputStream) throws SQLException {
      try {
         super.delegate.updateBinaryStream(str, inputStream);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateCharacterStream(String str, Reader reader) throws SQLException {
      try {
         super.delegate.updateCharacterStream(str, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBlob(int value, InputStream inputStream) throws SQLException {
      try {
         super.delegate.updateBlob(value, inputStream);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateBlob(String str, InputStream inputStream) throws SQLException {
      try {
         super.delegate.updateBlob(str, inputStream);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateClob(int value, Reader reader) throws SQLException {
      try {
         super.delegate.updateClob(value, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateClob(String str, Reader reader) throws SQLException {
      try {
         super.delegate.updateClob(str, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateNClob(int value, Reader reader) throws SQLException {
      try {
         super.delegate.updateNClob(value, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateNClob(String str, Reader reader) throws SQLException {
      try {
         super.delegate.updateNClob(str, reader);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Object getObject(int value, Class clazz) throws SQLException {
      try {
         return super.delegate.getObject(value, clazz);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public Object getObject(String str, Class clazz) throws SQLException {
      try {
         return super.delegate.getObject(str, clazz);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateObject(int value, Object obj, SQLType sqlType, int value2) throws SQLException {
      try {
         super.delegate.updateObject(value, obj, sqlType, value2);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateObject(String str, Object obj, SQLType sqlType, int value) throws SQLException {
      try {
         super.delegate.updateObject(str, obj, sqlType, value);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateObject(int value, Object obj, SQLType sqlType) throws SQLException {
      try {
         super.delegate.updateObject(value, obj, sqlType);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public void updateObject(String str, Object obj, SQLType sqlType) throws SQLException {
      try {
         super.delegate.updateObject(str, obj, sqlType);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   @Override
   public boolean isWrapperFor(Class clazz) throws SQLException {
      try {
         return super.delegate.isWrapperFor(clazz);
      } catch (SQLException e) {
         throw this.checkException(e);
      }
   }

   protected HikariProxyResultSet(ProxyConnection proxyConnection, ProxyStatement proxyStatement, ResultSet resultSet) {
      super(proxyConnection, proxyStatement, resultSet);
   }
}
