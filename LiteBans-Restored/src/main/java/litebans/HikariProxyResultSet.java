package litebans;

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

public final class HikariProxyResultSet extends ResultSetWrapper implements Wrapper, AutoCloseable, ResultSet {
   @Override
   public boolean isWrapperFor(Class class2) {
      try {
         try {
            return super.resultSet.isWrapperFor(class2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void close() {
      try {
         super.resultSet.close();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean next() {
      try {
         try {
            return super.resultSet.next();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean wasNull() {
      try {
         try {
            return super.resultSet.wasNull();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getString(int value) {
      try {
         try {
            return super.resultSet.getString(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean getBoolean(int value) {
      try {
         try {
            return super.resultSet.getBoolean(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public byte getByte(int value) {
      try {
         try {
            return super.resultSet.getByte(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public short getShort(int value) {
      try {
         try {
            return super.resultSet.getShort(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getInt(int value) {
      try {
         try {
            return super.resultSet.getInt(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long getLong(int value) {
      try {
         try {
            return super.resultSet.getLong(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public float getFloat(int value) {
      try {
         try {
            return super.resultSet.getFloat(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public double getDouble(int value) {
      try {
         try {
            return super.resultSet.getDouble(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public BigDecimal getBigDecimal(int value, int value2) {
      try {
         try {
            return super.resultSet.getBigDecimal(value, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public byte[] getBytes(int value) {
      try {
         try {
            return super.resultSet.getBytes(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Date getDate(int value) {
      try {
         try {
            return super.resultSet.getDate(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Time getTime(int value) {
      try {
         try {
            return super.resultSet.getTime(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Timestamp getTimestamp(int value) {
      try {
         try {
            return super.resultSet.getTimestamp(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public InputStream getAsciiStream(int value) {
      try {
         try {
            return super.resultSet.getAsciiStream(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public InputStream getUnicodeStream(int value) {
      try {
         try {
            return super.resultSet.getUnicodeStream(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public InputStream getBinaryStream(int value) {
      try {
         try {
            return super.resultSet.getBinaryStream(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getString(String text) {
      try {
         try {
            return super.resultSet.getString(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean getBoolean(String text) {
      try {
         try {
            return super.resultSet.getBoolean(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public byte getByte(String text) {
      try {
         try {
            return super.resultSet.getByte(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public short getShort(String text) {
      try {
         try {
            return super.resultSet.getShort(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getInt(String text) {
      try {
         try {
            return super.resultSet.getInt(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long getLong(String text) {
      try {
         try {
            return super.resultSet.getLong(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public float getFloat(String text) {
      try {
         try {
            return super.resultSet.getFloat(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public double getDouble(String text) {
      try {
         try {
            return super.resultSet.getDouble(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public BigDecimal getBigDecimal(String text, int value) {
      try {
         try {
            return super.resultSet.getBigDecimal(text, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public byte[] getBytes(String text) {
      try {
         try {
            return super.resultSet.getBytes(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Date getDate(String text) {
      try {
         try {
            return super.resultSet.getDate(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Time getTime(String text) {
      try {
         try {
            return super.resultSet.getTime(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Timestamp getTimestamp(String text) {
      try {
         try {
            return super.resultSet.getTimestamp(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public InputStream getAsciiStream(String text) {
      try {
         try {
            return super.resultSet.getAsciiStream(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public InputStream getUnicodeStream(String text) {
      try {
         try {
            return super.resultSet.getUnicodeStream(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public InputStream getBinaryStream(String text) {
      try {
         try {
            return super.resultSet.getBinaryStream(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public SQLWarning getWarnings() {
      try {
         try {
            return super.resultSet.getWarnings();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void clearWarnings() {
      try {
         try {
            super.resultSet.clearWarnings();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getCursorName() {
      try {
         try {
            return super.resultSet.getCursorName();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSetMetaData getMetaData() {
      try {
         try {
            return super.resultSet.getMetaData();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Object getObject(int value) {
      try {
         try {
            return super.resultSet.getObject(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Object getObject(String text) {
      try {
         try {
            return super.resultSet.getObject(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int findColumn(String text) {
      try {
         try {
            return super.resultSet.findColumn(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Reader getCharacterStream(int value) {
      try {
         try {
            return super.resultSet.getCharacterStream(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Reader getCharacterStream(String text) {
      try {
         try {
            return super.resultSet.getCharacterStream(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public BigDecimal getBigDecimal(int value) {
      try {
         try {
            return super.resultSet.getBigDecimal(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public BigDecimal getBigDecimal(String text) {
      try {
         try {
            return super.resultSet.getBigDecimal(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isBeforeFirst() {
      try {
         try {
            return super.resultSet.isBeforeFirst();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isAfterLast() {
      try {
         try {
            return super.resultSet.isAfterLast();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isFirst() {
      try {
         try {
            return super.resultSet.isFirst();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isLast() {
      try {
         try {
            return super.resultSet.isLast();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void beforeFirst() {
      try {
         try {
            super.resultSet.beforeFirst();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void afterLast() {
      try {
         try {
            super.resultSet.afterLast();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean first() {
      try {
         try {
            return super.resultSet.first();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean last() {
      try {
         try {
            return super.resultSet.last();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getRow() {
      try {
         try {
            return super.resultSet.getRow();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean absolute(int value) {
      try {
         try {
            return super.resultSet.absolute(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean relative(int value) {
      try {
         try {
            return super.resultSet.relative(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean previous() {
      try {
         try {
            return super.resultSet.previous();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setFetchDirection(int value) {
      try {
         try {
            super.resultSet.setFetchDirection(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getFetchDirection() {
      try {
         try {
            return super.resultSet.getFetchDirection();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setFetchSize(int value) {
      try {
         try {
            super.resultSet.setFetchSize(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getFetchSize() {
      try {
         try {
            return super.resultSet.getFetchSize();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getType() {
      try {
         try {
            return super.resultSet.getType();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getConcurrency() {
      try {
         try {
            return super.resultSet.getConcurrency();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean rowUpdated() {
      try {
         try {
            return super.resultSet.rowUpdated();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean rowInserted() {
      try {
         try {
            return super.resultSet.rowInserted();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean rowDeleted() {
      try {
         try {
            return super.resultSet.rowDeleted();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateNull(int value) {
      try {
         try {
            super.resultSet.updateNull(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBoolean(int value, boolean flag) {
      try {
         try {
            super.resultSet.updateBoolean(value, flag);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateByte(int value, byte b) {
      try {
         try {
            super.resultSet.updateByte(value, b);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateShort(int value, short value2) {
      try {
         try {
            super.resultSet.updateShort(value, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateInt(int value, int value2) {
      try {
         try {
            super.resultSet.updateInt(value, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateLong(int value, long value2) {
      try {
         try {
            super.resultSet.updateLong(value, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateFloat(int value, float number) {
      try {
         try {
            super.resultSet.updateFloat(value, number);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateDouble(int value, double number) {
      try {
         try {
            super.resultSet.updateDouble(value, number);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBigDecimal(int value, BigDecimal bigDecimal) {
      try {
         try {
            super.resultSet.updateBigDecimal(value, bigDecimal);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateString(int value, String text) {
      try {
         try {
            super.resultSet.updateString(value, text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBytes(int value, byte[] bytes) {
      try {
         try {
            super.resultSet.updateBytes(value, bytes);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateDate(int value, Date date) {
      try {
         try {
            super.resultSet.updateDate(value, date);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateTime(int value, Time time) {
      try {
         try {
            super.resultSet.updateTime(value, time);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateTimestamp(int value, Timestamp timestamp) {
      try {
         try {
            super.resultSet.updateTimestamp(value, timestamp);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateAsciiStream(int value, InputStream inputStream, int value2) {
      try {
         try {
            super.resultSet.updateAsciiStream(value, inputStream, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBinaryStream(int value, InputStream inputStream, int value2) {
      try {
         try {
            super.resultSet.updateBinaryStream(value, inputStream, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateCharacterStream(int value, Reader reader, int value2) {
      try {
         try {
            super.resultSet.updateCharacterStream(value, reader, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateObject(int value, Object obj, int value2) {
      try {
         try {
            super.resultSet.updateObject(value, obj, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateObject(int value, Object obj) {
      try {
         try {
            super.resultSet.updateObject(value, obj);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateNull(String text) {
      try {
         try {
            super.resultSet.updateNull(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBoolean(String text, boolean flag) {
      try {
         try {
            super.resultSet.updateBoolean(text, flag);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateByte(String text, byte b) {
      try {
         try {
            super.resultSet.updateByte(text, b);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateShort(String text, short value) {
      try {
         try {
            super.resultSet.updateShort(text, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateInt(String text, int value) {
      try {
         try {
            super.resultSet.updateInt(text, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateLong(String text, long value) {
      try {
         try {
            super.resultSet.updateLong(text, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateFloat(String text, float number) {
      try {
         try {
            super.resultSet.updateFloat(text, number);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateDouble(String text, double number) {
      try {
         try {
            super.resultSet.updateDouble(text, number);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBigDecimal(String text, BigDecimal bigDecimal) {
      try {
         try {
            super.resultSet.updateBigDecimal(text, bigDecimal);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateString(String text, String text2) {
      try {
         try {
            super.resultSet.updateString(text, text2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBytes(String text, byte[] bytes) {
      try {
         try {
            super.resultSet.updateBytes(text, bytes);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateDate(String text, Date date) {
      try {
         try {
            super.resultSet.updateDate(text, date);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateTime(String text, Time time) {
      try {
         try {
            super.resultSet.updateTime(text, time);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateTimestamp(String text, Timestamp timestamp) {
      try {
         try {
            super.resultSet.updateTimestamp(text, timestamp);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateAsciiStream(String text, InputStream inputStream, int value) {
      try {
         try {
            super.resultSet.updateAsciiStream(text, inputStream, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBinaryStream(String text, InputStream inputStream, int value) {
      try {
         try {
            super.resultSet.updateBinaryStream(text, inputStream, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateCharacterStream(String text, Reader reader, int value) {
      try {
         try {
            super.resultSet.updateCharacterStream(text, reader, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateObject(String text, Object obj, int value) {
      try {
         try {
            super.resultSet.updateObject(text, obj, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateObject(String text, Object obj) {
      try {
         try {
            super.resultSet.updateObject(text, obj);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void insertRow() {
      try {
         try {
            super.insertRow();
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateRow() {
      try {
         try {
            super.updateRow();
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void deleteRow() {
      try {
         try {
            super.deleteRow();
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void refreshRow() {
      try {
         try {
            super.resultSet.refreshRow();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void cancelRowUpdates() {
      try {
         try {
            super.resultSet.cancelRowUpdates();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void moveToInsertRow() {
      try {
         try {
            super.resultSet.moveToInsertRow();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void moveToCurrentRow() {
      try {
         try {
            super.resultSet.moveToCurrentRow();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Object getObject(int value, Map map) {
      try {
         try {
            return super.resultSet.getObject(value, map);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Ref getRef(int value) {
      try {
         try {
            return super.resultSet.getRef(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Blob getBlob(int value) {
      try {
         try {
            return super.resultSet.getBlob(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Clob getClob(int value) {
      try {
         try {
            return super.resultSet.getClob(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Array getArray(int value) {
      try {
         try {
            return super.resultSet.getArray(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Object getObject(String text, Map map) {
      try {
         try {
            return super.resultSet.getObject(text, map);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Ref getRef(String text) {
      try {
         try {
            return super.resultSet.getRef(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Blob getBlob(String text) {
      try {
         try {
            return super.resultSet.getBlob(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Clob getClob(String text) {
      try {
         try {
            return super.resultSet.getClob(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Array getArray(String text) {
      try {
         try {
            return super.resultSet.getArray(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Date getDate(int value, Calendar calendar) {
      try {
         try {
            return super.resultSet.getDate(value, calendar);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Date getDate(String text, Calendar calendar) {
      try {
         try {
            return super.resultSet.getDate(text, calendar);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Time getTime(int value, Calendar calendar) {
      try {
         try {
            return super.resultSet.getTime(value, calendar);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Time getTime(String text, Calendar calendar) {
      try {
         try {
            return super.resultSet.getTime(text, calendar);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Timestamp getTimestamp(int value, Calendar calendar) {
      try {
         try {
            return super.resultSet.getTimestamp(value, calendar);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Timestamp getTimestamp(String text, Calendar calendar) {
      try {
         try {
            return super.resultSet.getTimestamp(text, calendar);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public URL getURL(int value) {
      try {
         try {
            return super.resultSet.getURL(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public URL getURL(String text) {
      try {
         try {
            return super.resultSet.getURL(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateRef(int value, Ref ref) {
      try {
         try {
            super.resultSet.updateRef(value, ref);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateRef(String text, Ref ref) {
      try {
         try {
            super.resultSet.updateRef(text, ref);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBlob(int value, Blob blob) {
      try {
         try {
            super.resultSet.updateBlob(value, blob);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBlob(String text, Blob blob) {
      try {
         try {
            super.resultSet.updateBlob(text, blob);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateClob(int value, Clob clob) {
      try {
         try {
            super.resultSet.updateClob(value, clob);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateClob(String text, Clob clob) {
      try {
         try {
            super.resultSet.updateClob(text, clob);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateArray(int value, Array array) {
      try {
         try {
            super.resultSet.updateArray(value, array);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateArray(String text, Array array) {
      try {
         try {
            super.resultSet.updateArray(text, array);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public RowId getRowId(int value) {
      try {
         try {
            return super.resultSet.getRowId(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public RowId getRowId(String text) {
      try {
         try {
            return super.resultSet.getRowId(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateRowId(int value, RowId rowId) {
      try {
         try {
            super.resultSet.updateRowId(value, rowId);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateRowId(String text, RowId rowId) {
      try {
         try {
            super.resultSet.updateRowId(text, rowId);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getHoldability() {
      try {
         try {
            return super.resultSet.getHoldability();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isClosed() {
      try {
         try {
            return super.resultSet.isClosed();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateNString(int value, String text) {
      try {
         try {
            super.resultSet.updateNString(value, text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateNString(String text, String text2) {
      try {
         try {
            super.resultSet.updateNString(text, text2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateNClob(int value, NClob nClob) {
      try {
         try {
            super.resultSet.updateNClob(value, nClob);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateNClob(String text, NClob nClob) {
      try {
         try {
            super.resultSet.updateNClob(text, nClob);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public NClob getNClob(int value) {
      try {
         try {
            return super.resultSet.getNClob(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public NClob getNClob(String text) {
      try {
         try {
            return super.resultSet.getNClob(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public SQLXML getSQLXML(int value) {
      try {
         try {
            return super.resultSet.getSQLXML(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public SQLXML getSQLXML(String text) {
      try {
         try {
            return super.resultSet.getSQLXML(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateSQLXML(int value, SQLXML sqlxml) {
      try {
         try {
            super.resultSet.updateSQLXML(value, sqlxml);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateSQLXML(String text, SQLXML sqlxml) {
      try {
         try {
            super.resultSet.updateSQLXML(text, sqlxml);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getNString(int value) {
      try {
         try {
            return super.resultSet.getNString(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getNString(String text) {
      try {
         try {
            return super.resultSet.getNString(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Reader getNCharacterStream(int value) {
      try {
         try {
            return super.resultSet.getNCharacterStream(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Reader getNCharacterStream(String text) {
      try {
         try {
            return super.resultSet.getNCharacterStream(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateNCharacterStream(int value, Reader reader, long value2) {
      try {
         try {
            super.resultSet.updateNCharacterStream(value, reader, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateNCharacterStream(String text, Reader reader, long value) {
      try {
         try {
            super.resultSet.updateNCharacterStream(text, reader, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateAsciiStream(int value, InputStream inputStream, long value2) {
      try {
         try {
            super.resultSet.updateAsciiStream(value, inputStream, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBinaryStream(int value, InputStream inputStream, long value2) {
      try {
         try {
            super.resultSet.updateBinaryStream(value, inputStream, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateCharacterStream(int value, Reader reader, long value2) {
      try {
         try {
            super.resultSet.updateCharacterStream(value, reader, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateAsciiStream(String text, InputStream inputStream, long value) {
      try {
         try {
            super.resultSet.updateAsciiStream(text, inputStream, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBinaryStream(String text, InputStream inputStream, long value) {
      try {
         try {
            super.resultSet.updateBinaryStream(text, inputStream, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateCharacterStream(String text, Reader reader, long value) {
      try {
         try {
            super.resultSet.updateCharacterStream(text, reader, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBlob(int value, InputStream inputStream, long value2) {
      try {
         try {
            super.resultSet.updateBlob(value, inputStream, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBlob(String text, InputStream inputStream, long value) {
      try {
         try {
            super.resultSet.updateBlob(text, inputStream, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateClob(int value, Reader reader, long value2) {
      try {
         try {
            super.resultSet.updateClob(value, reader, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateClob(String text, Reader reader, long value) {
      try {
         try {
            super.resultSet.updateClob(text, reader, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateNClob(int value, Reader reader, long value2) {
      try {
         try {
            super.resultSet.updateNClob(value, reader, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateNClob(String text, Reader reader, long value) {
      try {
         try {
            super.resultSet.updateNClob(text, reader, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateNCharacterStream(int value, Reader reader) {
      try {
         try {
            super.resultSet.updateNCharacterStream(value, reader);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateNCharacterStream(String text, Reader reader) {
      try {
         try {
            super.resultSet.updateNCharacterStream(text, reader);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateAsciiStream(int value, InputStream inputStream) {
      try {
         try {
            super.resultSet.updateAsciiStream(value, inputStream);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBinaryStream(int value, InputStream inputStream) {
      try {
         try {
            super.resultSet.updateBinaryStream(value, inputStream);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateCharacterStream(int value, Reader reader) {
      try {
         try {
            super.resultSet.updateCharacterStream(value, reader);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateAsciiStream(String text, InputStream inputStream) {
      try {
         try {
            super.resultSet.updateAsciiStream(text, inputStream);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBinaryStream(String text, InputStream inputStream) {
      try {
         try {
            super.resultSet.updateBinaryStream(text, inputStream);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateCharacterStream(String text, Reader reader) {
      try {
         try {
            super.resultSet.updateCharacterStream(text, reader);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBlob(int value, InputStream inputStream) {
      try {
         try {
            super.resultSet.updateBlob(value, inputStream);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateBlob(String text, InputStream inputStream) {
      try {
         try {
            super.resultSet.updateBlob(text, inputStream);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateClob(int value, Reader reader) {
      try {
         try {
            super.resultSet.updateClob(value, reader);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateClob(String text, Reader reader) {
      try {
         try {
            super.resultSet.updateClob(text, reader);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateNClob(int value, Reader reader) {
      try {
         try {
            super.resultSet.updateNClob(value, reader);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateNClob(String text, Reader reader) {
      try {
         try {
            super.resultSet.updateNClob(text, reader);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Object getObject(int value, Class class2) {
      try {
         try {
            return super.resultSet.getObject(value, class2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Object getObject(String text, Class class2) {
      try {
         try {
            return super.resultSet.getObject(text, class2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateObject(int value, Object obj, SQLType sqlType, int value2) {
      try {
         try {
            super.resultSet.updateObject(value, obj, sqlType, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateObject(String text, Object obj, SQLType sqlType, int value) {
      try {
         try {
            super.resultSet.updateObject(text, obj, sqlType, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateObject(int value, Object obj, SQLType sqlType) {
      try {
         try {
            super.resultSet.updateObject(value, obj, sqlType);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void updateObject(String text, Object obj, SQLType sqlType) {
      try {
         try {
            super.resultSet.updateObject(text, obj, sqlType);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   protected HikariProxyResultSet(ProxyConnection proxyConnection, ProxyStatement proxyStatement, ResultSet resultSet) {
      super(proxyConnection, proxyStatement, resultSet);
   }
}
