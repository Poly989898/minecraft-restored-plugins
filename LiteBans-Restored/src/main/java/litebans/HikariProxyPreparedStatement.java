package litebans;

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

public final class HikariProxyPreparedStatement extends ProxyPreparedStatement implements Wrapper, AutoCloseable, Statement, PreparedStatement {
   @Override
   public boolean isWrapperFor(Class class2) {
      try {
         try {
            return ((PreparedStatement)super.statement).isWrapperFor(class2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet executeQuery(String text) {
      try {
         try {
            return super.executeQuery(text);
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
   public int executeUpdate(String text) {
      try {
         try {
            return super.executeUpdate(text);
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
   public int getMaxFieldSize() {
      try {
         try {
            return ((PreparedStatement)super.statement).getMaxFieldSize();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setMaxFieldSize(int value) {
      try {
         try {
            ((PreparedStatement)super.statement).setMaxFieldSize(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxRows() {
      try {
         try {
            return ((PreparedStatement)super.statement).getMaxRows();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setMaxRows(int value) {
      try {
         try {
            ((PreparedStatement)super.statement).setMaxRows(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setEscapeProcessing(boolean flag) {
      try {
         try {
            ((PreparedStatement)super.statement).setEscapeProcessing(flag);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getQueryTimeout() {
      try {
         try {
            return ((PreparedStatement)super.statement).getQueryTimeout();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setQueryTimeout(int value) {
      try {
         try {
            ((PreparedStatement)super.statement).setQueryTimeout(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void cancel() {
      try {
         try {
            ((PreparedStatement)super.statement).cancel();
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
            return ((PreparedStatement)super.statement).getWarnings();
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
            ((PreparedStatement)super.statement).clearWarnings();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setCursorName(String text) {
      try {
         try {
            ((PreparedStatement)super.statement).setCursorName(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean execute(String text) {
      try {
         try {
            return super.execute(text);
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
   public ResultSet getResultSet() {
      try {
         try {
            return super.getResultSet();
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
   public int getUpdateCount() {
      try {
         try {
            return ((PreparedStatement)super.statement).getUpdateCount();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean getMoreResults() {
      try {
         try {
            return ((PreparedStatement)super.statement).getMoreResults();
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
            ((PreparedStatement)super.statement).setFetchDirection(value);
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
            return ((PreparedStatement)super.statement).getFetchDirection();
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
            ((PreparedStatement)super.statement).setFetchSize(value);
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
            return ((PreparedStatement)super.statement).getFetchSize();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getResultSetConcurrency() {
      try {
         try {
            return ((PreparedStatement)super.statement).getResultSetConcurrency();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getResultSetType() {
      try {
         try {
            return ((PreparedStatement)super.statement).getResultSetType();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void addBatch(String text) {
      try {
         try {
            ((PreparedStatement)super.statement).addBatch(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void clearBatch() {
      try {
         try {
            ((PreparedStatement)super.statement).clearBatch();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int[] executeBatch() {
      try {
         try {
            return super.executeBatch();
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
   public Connection getConnection() {
      try {
         try {
            return super.getConnection();
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
   public boolean getMoreResults(int value) {
      try {
         try {
            return ((PreparedStatement)super.statement).getMoreResults(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getGeneratedKeys() {
      try {
         try {
            return super.getGeneratedKeys();
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
   public int executeUpdate(String text, int value) {
      try {
         try {
            return super.executeUpdate(text, value);
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
   public int executeUpdate(String text, int[] ints) {
      try {
         try {
            return super.executeUpdate(text, ints);
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
   public int executeUpdate(String text, String[] texts) {
      try {
         try {
            return super.executeUpdate(text, texts);
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
   public boolean execute(String text, int value) {
      try {
         try {
            return super.execute(text, value);
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
   public boolean execute(String text, int[] ints) {
      try {
         try {
            return super.execute(text, ints);
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
   public boolean execute(String text, String[] texts) {
      try {
         try {
            return super.execute(text, texts);
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
   public int getResultSetHoldability() {
      try {
         try {
            return ((PreparedStatement)super.statement).getResultSetHoldability();
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
            return super.isClosed();
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
   public void setPoolable(boolean flag) {
      try {
         try {
            ((PreparedStatement)super.statement).setPoolable(flag);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isPoolable() {
      try {
         try {
            return ((PreparedStatement)super.statement).isPoolable();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void closeOnCompletion() {
      try {
         try {
            ((PreparedStatement)super.statement).closeOnCompletion();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isCloseOnCompletion() {
      try {
         try {
            return ((PreparedStatement)super.statement).isCloseOnCompletion();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long getLargeUpdateCount() {
      try {
         try {
            return ((PreparedStatement)super.statement).getLargeUpdateCount();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setLargeMaxRows(long value) {
      try {
         try {
            ((PreparedStatement)super.statement).setLargeMaxRows(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long getLargeMaxRows() {
      try {
         try {
            return ((PreparedStatement)super.statement).getLargeMaxRows();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long[] executeLargeBatch() {
      try {
         try {
            return ((PreparedStatement)super.statement).executeLargeBatch();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long executeLargeUpdate(String text) {
      try {
         try {
            return ((PreparedStatement)super.statement).executeLargeUpdate(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long executeLargeUpdate(String text, int value) {
      try {
         try {
            return ((PreparedStatement)super.statement).executeLargeUpdate(text, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long executeLargeUpdate(String text, int[] ints) {
      try {
         try {
            return ((PreparedStatement)super.statement).executeLargeUpdate(text, ints);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long executeLargeUpdate(String text, String[] texts) {
      try {
         try {
            return ((PreparedStatement)super.statement).executeLargeUpdate(text, texts);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet executeQuery() {
      try {
         try {
            return super.executeQuery();
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
   public int executeUpdate() {
      try {
         try {
            return super.executeUpdate();
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
   public void setNull(int value, int value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setNull(value, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBoolean(int value, boolean flag) {
      try {
         try {
            ((PreparedStatement)super.statement).setBoolean(value, flag);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setByte(int value, byte b) {
      try {
         try {
            ((PreparedStatement)super.statement).setByte(value, b);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setShort(int value, short value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setShort(value, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setInt(int value, int value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setInt(value, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setLong(int value, long value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setLong(value, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setFloat(int value, float number) {
      try {
         try {
            ((PreparedStatement)super.statement).setFloat(value, number);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setDouble(int value, double number) {
      try {
         try {
            ((PreparedStatement)super.statement).setDouble(value, number);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBigDecimal(int value, BigDecimal bigDecimal) {
      try {
         try {
            ((PreparedStatement)super.statement).setBigDecimal(value, bigDecimal);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setString(int value, String text) {
      try {
         try {
            ((PreparedStatement)super.statement).setString(value, text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBytes(int value, byte[] bytes) {
      try {
         try {
            ((PreparedStatement)super.statement).setBytes(value, bytes);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setDate(int value, Date date) {
      try {
         try {
            ((PreparedStatement)super.statement).setDate(value, date);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setTime(int value, Time time) {
      try {
         try {
            ((PreparedStatement)super.statement).setTime(value, time);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setTimestamp(int value, Timestamp timestamp) {
      try {
         try {
            ((PreparedStatement)super.statement).setTimestamp(value, timestamp);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setAsciiStream(int value, InputStream inputStream, int value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setAsciiStream(value, inputStream, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setUnicodeStream(int value, InputStream inputStream, int value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setUnicodeStream(value, inputStream, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBinaryStream(int value, InputStream inputStream, int value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setBinaryStream(value, inputStream, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void clearParameters() {
      try {
         try {
            ((PreparedStatement)super.statement).clearParameters();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, int value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setObject(value, obj, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setObject(int value, Object obj) {
      try {
         try {
            ((PreparedStatement)super.statement).setObject(value, obj);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean execute() {
      try {
         try {
            return super.execute();
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
   public void addBatch() {
      try {
         try {
            ((PreparedStatement)super.statement).addBatch();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setCharacterStream(int value, Reader reader, int value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setCharacterStream(value, reader, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setRef(int value, Ref ref) {
      try {
         try {
            ((PreparedStatement)super.statement).setRef(value, ref);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBlob(int value, Blob blob) {
      try {
         try {
            ((PreparedStatement)super.statement).setBlob(value, blob);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setClob(int value, Clob clob) {
      try {
         try {
            ((PreparedStatement)super.statement).setClob(value, clob);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setArray(int value, Array array) {
      try {
         try {
            ((PreparedStatement)super.statement).setArray(value, array);
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
            return ((PreparedStatement)super.statement).getMetaData();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setDate(int value, Date date, Calendar calendar) {
      try {
         try {
            ((PreparedStatement)super.statement).setDate(value, date, calendar);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setTime(int value, Time time, Calendar calendar) {
      try {
         try {
            ((PreparedStatement)super.statement).setTime(value, time, calendar);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setTimestamp(int value, Timestamp timestamp, Calendar calendar) {
      try {
         try {
            ((PreparedStatement)super.statement).setTimestamp(value, timestamp, calendar);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNull(int value, int value2, String text) {
      try {
         try {
            ((PreparedStatement)super.statement).setNull(value, value2, text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setURL(int value, URL url) {
      try {
         try {
            ((PreparedStatement)super.statement).setURL(value, url);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ParameterMetaData getParameterMetaData() {
      try {
         try {
            return ((PreparedStatement)super.statement).getParameterMetaData();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setRowId(int value, RowId rowId) {
      try {
         try {
            ((PreparedStatement)super.statement).setRowId(value, rowId);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNString(int value, String text) {
      try {
         try {
            ((PreparedStatement)super.statement).setNString(value, text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNCharacterStream(int value, Reader reader, long value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setNCharacterStream(value, reader, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNClob(int value, NClob nClob) {
      try {
         try {
            ((PreparedStatement)super.statement).setNClob(value, nClob);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setClob(int value, Reader reader, long value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setClob(value, reader, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBlob(int value, InputStream inputStream, long value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setBlob(value, inputStream, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNClob(int value, Reader reader, long value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setNClob(value, reader, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setSQLXML(int value, SQLXML sqlxml) {
      try {
         try {
            ((PreparedStatement)super.statement).setSQLXML(value, sqlxml);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, int value2, int value3) {
      try {
         try {
            ((PreparedStatement)super.statement).setObject(value, obj, value2, value3);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setAsciiStream(int value, InputStream inputStream, long value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setAsciiStream(value, inputStream, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBinaryStream(int value, InputStream inputStream, long value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setBinaryStream(value, inputStream, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setCharacterStream(int value, Reader reader, long value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setCharacterStream(value, reader, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setAsciiStream(int value, InputStream inputStream) {
      try {
         try {
            ((PreparedStatement)super.statement).setAsciiStream(value, inputStream);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBinaryStream(int value, InputStream inputStream) {
      try {
         try {
            ((PreparedStatement)super.statement).setBinaryStream(value, inputStream);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setCharacterStream(int value, Reader reader) {
      try {
         try {
            ((PreparedStatement)super.statement).setCharacterStream(value, reader);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNCharacterStream(int value, Reader reader) {
      try {
         try {
            ((PreparedStatement)super.statement).setNCharacterStream(value, reader);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setClob(int value, Reader reader) {
      try {
         try {
            ((PreparedStatement)super.statement).setClob(value, reader);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBlob(int value, InputStream inputStream) {
      try {
         try {
            ((PreparedStatement)super.statement).setBlob(value, inputStream);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNClob(int value, Reader reader) {
      try {
         try {
            ((PreparedStatement)super.statement).setNClob(value, reader);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, SQLType sqlType, int value2) {
      try {
         try {
            ((PreparedStatement)super.statement).setObject(value, obj, sqlType, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setObject(int value, Object obj, SQLType sqlType) {
      try {
         try {
            ((PreparedStatement)super.statement).setObject(value, obj, sqlType);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long executeLargeUpdate() {
      try {
         try {
            return ((PreparedStatement)super.statement).executeLargeUpdate();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   HikariProxyPreparedStatement(ProxyConnection proxyConnection, PreparedStatement preparedStatement) {
      super(proxyConnection, preparedStatement);
   }
}
