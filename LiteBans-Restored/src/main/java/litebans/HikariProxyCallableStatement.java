package litebans;

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

public final class HikariProxyCallableStatement extends ProxyCallableStatement implements Wrapper, AutoCloseable, Statement, PreparedStatement, CallableStatement {
   @Override
   public boolean isWrapperFor(Class class2) {
      try {
         try {
            return ((CallableStatement)super.statement).isWrapperFor(class2);
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
            return ((CallableStatement)super.statement).getMaxFieldSize();
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
            ((CallableStatement)super.statement).setMaxFieldSize(value);
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
            return ((CallableStatement)super.statement).getMaxRows();
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
            ((CallableStatement)super.statement).setMaxRows(value);
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
            ((CallableStatement)super.statement).setEscapeProcessing(flag);
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
            return ((CallableStatement)super.statement).getQueryTimeout();
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
            ((CallableStatement)super.statement).setQueryTimeout(value);
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
            ((CallableStatement)super.statement).cancel();
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
            return ((CallableStatement)super.statement).getWarnings();
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
            ((CallableStatement)super.statement).clearWarnings();
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
            ((CallableStatement)super.statement).setCursorName(text);
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
            return ((CallableStatement)super.statement).getUpdateCount();
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
            return ((CallableStatement)super.statement).getMoreResults();
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
            ((CallableStatement)super.statement).setFetchDirection(value);
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
            return ((CallableStatement)super.statement).getFetchDirection();
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
            ((CallableStatement)super.statement).setFetchSize(value);
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
            return ((CallableStatement)super.statement).getFetchSize();
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
            return ((CallableStatement)super.statement).getResultSetConcurrency();
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
            return ((CallableStatement)super.statement).getResultSetType();
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
            ((CallableStatement)super.statement).addBatch(text);
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
            ((CallableStatement)super.statement).clearBatch();
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
            return ((CallableStatement)super.statement).getMoreResults(value);
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
            return ((CallableStatement)super.statement).getResultSetHoldability();
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
            ((CallableStatement)super.statement).setPoolable(flag);
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
            return ((CallableStatement)super.statement).isPoolable();
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
            ((CallableStatement)super.statement).closeOnCompletion();
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
            return ((CallableStatement)super.statement).isCloseOnCompletion();
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
            return ((CallableStatement)super.statement).getLargeUpdateCount();
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
            ((CallableStatement)super.statement).setLargeMaxRows(value);
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
            return ((CallableStatement)super.statement).getLargeMaxRows();
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
            return ((CallableStatement)super.statement).executeLargeBatch();
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
            return ((CallableStatement)super.statement).executeLargeUpdate(text);
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
            return ((CallableStatement)super.statement).executeLargeUpdate(text, value);
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
            return ((CallableStatement)super.statement).executeLargeUpdate(text, ints);
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
            return ((CallableStatement)super.statement).executeLargeUpdate(text, texts);
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
            ((CallableStatement)super.statement).setNull(value, value2);
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
            ((CallableStatement)super.statement).setBoolean(value, flag);
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
            ((CallableStatement)super.statement).setByte(value, b);
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
            ((CallableStatement)super.statement).setShort(value, value2);
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
            ((CallableStatement)super.statement).setInt(value, value2);
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
            ((CallableStatement)super.statement).setLong(value, value2);
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
            ((CallableStatement)super.statement).setFloat(value, number);
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
            ((CallableStatement)super.statement).setDouble(value, number);
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
            ((CallableStatement)super.statement).setBigDecimal(value, bigDecimal);
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
            ((CallableStatement)super.statement).setString(value, text);
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
            ((CallableStatement)super.statement).setBytes(value, bytes);
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
            ((CallableStatement)super.statement).setDate(value, date);
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
            ((CallableStatement)super.statement).setTime(value, time);
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
            ((CallableStatement)super.statement).setTimestamp(value, timestamp);
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
            ((CallableStatement)super.statement).setAsciiStream(value, inputStream, value2);
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
            ((CallableStatement)super.statement).setUnicodeStream(value, inputStream, value2);
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
            ((CallableStatement)super.statement).setBinaryStream(value, inputStream, value2);
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
            ((CallableStatement)super.statement).clearParameters();
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
            ((CallableStatement)super.statement).setObject(value, obj, value2);
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
            ((CallableStatement)super.statement).setObject(value, obj);
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
            ((CallableStatement)super.statement).addBatch();
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
            ((CallableStatement)super.statement).setCharacterStream(value, reader, value2);
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
            ((CallableStatement)super.statement).setRef(value, ref);
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
            ((CallableStatement)super.statement).setBlob(value, blob);
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
            ((CallableStatement)super.statement).setClob(value, clob);
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
            ((CallableStatement)super.statement).setArray(value, array);
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
            return ((CallableStatement)super.statement).getMetaData();
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
            ((CallableStatement)super.statement).setDate(value, date, calendar);
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
            ((CallableStatement)super.statement).setTime(value, time, calendar);
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
            ((CallableStatement)super.statement).setTimestamp(value, timestamp, calendar);
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
            ((CallableStatement)super.statement).setNull(value, value2, text);
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
            ((CallableStatement)super.statement).setURL(value, url);
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
            return ((CallableStatement)super.statement).getParameterMetaData();
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
            ((CallableStatement)super.statement).setRowId(value, rowId);
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
            ((CallableStatement)super.statement).setNString(value, text);
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
            ((CallableStatement)super.statement).setNCharacterStream(value, reader, value2);
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
            ((CallableStatement)super.statement).setNClob(value, nClob);
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
            ((CallableStatement)super.statement).setClob(value, reader, value2);
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
            ((CallableStatement)super.statement).setBlob(value, inputStream, value2);
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
            ((CallableStatement)super.statement).setNClob(value, reader, value2);
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
            ((CallableStatement)super.statement).setSQLXML(value, sqlxml);
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
            ((CallableStatement)super.statement).setObject(value, obj, value2, value3);
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
            ((CallableStatement)super.statement).setAsciiStream(value, inputStream, value2);
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
            ((CallableStatement)super.statement).setBinaryStream(value, inputStream, value2);
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
            ((CallableStatement)super.statement).setCharacterStream(value, reader, value2);
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
            ((CallableStatement)super.statement).setAsciiStream(value, inputStream);
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
            ((CallableStatement)super.statement).setBinaryStream(value, inputStream);
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
            ((CallableStatement)super.statement).setCharacterStream(value, reader);
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
            ((CallableStatement)super.statement).setNCharacterStream(value, reader);
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
            ((CallableStatement)super.statement).setClob(value, reader);
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
            ((CallableStatement)super.statement).setBlob(value, inputStream);
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
            ((CallableStatement)super.statement).setNClob(value, reader);
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
            ((CallableStatement)super.statement).setObject(value, obj, sqlType, value2);
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
            ((CallableStatement)super.statement).setObject(value, obj, sqlType);
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
            return ((CallableStatement)super.statement).executeLargeUpdate();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void registerOutParameter(int value, int value2) {
      try {
         try {
            ((CallableStatement)super.statement).registerOutParameter(value, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void registerOutParameter(int value, int value2, int value3) {
      try {
         try {
            ((CallableStatement)super.statement).registerOutParameter(value, value2, value3);
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
            return ((CallableStatement)super.statement).wasNull();
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
            return ((CallableStatement)super.statement).getString(value);
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
            return ((CallableStatement)super.statement).getBoolean(value);
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
            return ((CallableStatement)super.statement).getByte(value);
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
            return ((CallableStatement)super.statement).getShort(value);
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
            return ((CallableStatement)super.statement).getInt(value);
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
            return ((CallableStatement)super.statement).getLong(value);
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
            return ((CallableStatement)super.statement).getFloat(value);
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
            return ((CallableStatement)super.statement).getDouble(value);
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
            return ((CallableStatement)super.statement).getBigDecimal(value, value2);
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
            return ((CallableStatement)super.statement).getBytes(value);
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
            return ((CallableStatement)super.statement).getDate(value);
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
            return ((CallableStatement)super.statement).getTime(value);
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
            return ((CallableStatement)super.statement).getTimestamp(value);
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
            return ((CallableStatement)super.statement).getObject(value);
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
            return ((CallableStatement)super.statement).getBigDecimal(value);
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
            return ((CallableStatement)super.statement).getObject(value, map);
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
            return ((CallableStatement)super.statement).getRef(value);
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
            return ((CallableStatement)super.statement).getBlob(value);
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
            return ((CallableStatement)super.statement).getClob(value);
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
            return ((CallableStatement)super.statement).getArray(value);
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
            return ((CallableStatement)super.statement).getDate(value, calendar);
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
            return ((CallableStatement)super.statement).getTime(value, calendar);
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
            return ((CallableStatement)super.statement).getTimestamp(value, calendar);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void registerOutParameter(int value, int value2, String text) {
      try {
         try {
            ((CallableStatement)super.statement).registerOutParameter(value, value2, text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void registerOutParameter(String text, int value) {
      try {
         try {
            ((CallableStatement)super.statement).registerOutParameter(text, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void registerOutParameter(String text, int value, int value2) {
      try {
         try {
            ((CallableStatement)super.statement).registerOutParameter(text, value, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void registerOutParameter(String text, int value, String text2) {
      try {
         try {
            ((CallableStatement)super.statement).registerOutParameter(text, value, text2);
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
            return ((CallableStatement)super.statement).getURL(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setURL(String text, URL url) {
      try {
         try {
            ((CallableStatement)super.statement).setURL(text, url);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNull(String text, int value) {
      try {
         try {
            ((CallableStatement)super.statement).setNull(text, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBoolean(String text, boolean flag) {
      try {
         try {
            ((CallableStatement)super.statement).setBoolean(text, flag);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setByte(String text, byte b) {
      try {
         try {
            ((CallableStatement)super.statement).setByte(text, b);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setShort(String text, short value) {
      try {
         try {
            ((CallableStatement)super.statement).setShort(text, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setInt(String text, int value) {
      try {
         try {
            ((CallableStatement)super.statement).setInt(text, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setLong(String text, long value) {
      try {
         try {
            ((CallableStatement)super.statement).setLong(text, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setFloat(String text, float number) {
      try {
         try {
            ((CallableStatement)super.statement).setFloat(text, number);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setDouble(String text, double number) {
      try {
         try {
            ((CallableStatement)super.statement).setDouble(text, number);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBigDecimal(String text, BigDecimal bigDecimal) {
      try {
         try {
            ((CallableStatement)super.statement).setBigDecimal(text, bigDecimal);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setString(String text, String text2) {
      try {
         try {
            ((CallableStatement)super.statement).setString(text, text2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBytes(String text, byte[] bytes) {
      try {
         try {
            ((CallableStatement)super.statement).setBytes(text, bytes);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setDate(String text, Date date) {
      try {
         try {
            ((CallableStatement)super.statement).setDate(text, date);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setTime(String text, Time time) {
      try {
         try {
            ((CallableStatement)super.statement).setTime(text, time);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setTimestamp(String text, Timestamp timestamp) {
      try {
         try {
            ((CallableStatement)super.statement).setTimestamp(text, timestamp);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setAsciiStream(String text, InputStream inputStream, int value) {
      try {
         try {
            ((CallableStatement)super.statement).setAsciiStream(text, inputStream, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBinaryStream(String text, InputStream inputStream, int value) {
      try {
         try {
            ((CallableStatement)super.statement).setBinaryStream(text, inputStream, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setObject(String text, Object obj, int value, int value2) {
      try {
         try {
            ((CallableStatement)super.statement).setObject(text, obj, value, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setObject(String text, Object obj, int value) {
      try {
         try {
            ((CallableStatement)super.statement).setObject(text, obj, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setObject(String text, Object obj) {
      try {
         try {
            ((CallableStatement)super.statement).setObject(text, obj);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setCharacterStream(String text, Reader reader, int value) {
      try {
         try {
            ((CallableStatement)super.statement).setCharacterStream(text, reader, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setDate(String text, Date date, Calendar calendar) {
      try {
         try {
            ((CallableStatement)super.statement).setDate(text, date, calendar);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setTime(String text, Time time, Calendar calendar) {
      try {
         try {
            ((CallableStatement)super.statement).setTime(text, time, calendar);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setTimestamp(String text, Timestamp timestamp, Calendar calendar) {
      try {
         try {
            ((CallableStatement)super.statement).setTimestamp(text, timestamp, calendar);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNull(String text, int value, String text2) {
      try {
         try {
            ((CallableStatement)super.statement).setNull(text, value, text2);
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
            return ((CallableStatement)super.statement).getString(text);
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
            return ((CallableStatement)super.statement).getBoolean(text);
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
            return ((CallableStatement)super.statement).getByte(text);
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
            return ((CallableStatement)super.statement).getShort(text);
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
            return ((CallableStatement)super.statement).getInt(text);
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
            return ((CallableStatement)super.statement).getLong(text);
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
            return ((CallableStatement)super.statement).getFloat(text);
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
            return ((CallableStatement)super.statement).getDouble(text);
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
            return ((CallableStatement)super.statement).getBytes(text);
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
            return ((CallableStatement)super.statement).getDate(text);
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
            return ((CallableStatement)super.statement).getTime(text);
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
            return ((CallableStatement)super.statement).getTimestamp(text);
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
            return ((CallableStatement)super.statement).getObject(text);
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
            return ((CallableStatement)super.statement).getBigDecimal(text);
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
            return ((CallableStatement)super.statement).getObject(text, map);
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
            return ((CallableStatement)super.statement).getRef(text);
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
            return ((CallableStatement)super.statement).getBlob(text);
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
            return ((CallableStatement)super.statement).getClob(text);
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
            return ((CallableStatement)super.statement).getArray(text);
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
            return ((CallableStatement)super.statement).getDate(text, calendar);
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
            return ((CallableStatement)super.statement).getTime(text, calendar);
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
            return ((CallableStatement)super.statement).getTimestamp(text, calendar);
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
            return ((CallableStatement)super.statement).getURL(text);
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
            return ((CallableStatement)super.statement).getRowId(value);
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
            return ((CallableStatement)super.statement).getRowId(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setRowId(String text, RowId rowId) {
      try {
         try {
            ((CallableStatement)super.statement).setRowId(text, rowId);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNString(String text, String text2) {
      try {
         try {
            ((CallableStatement)super.statement).setNString(text, text2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNCharacterStream(String text, Reader reader, long value) {
      try {
         try {
            ((CallableStatement)super.statement).setNCharacterStream(text, reader, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNClob(String text, NClob nClob) {
      try {
         try {
            ((CallableStatement)super.statement).setNClob(text, nClob);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setClob(String text, Reader reader, long value) {
      try {
         try {
            ((CallableStatement)super.statement).setClob(text, reader, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBlob(String text, InputStream inputStream, long value) {
      try {
         try {
            ((CallableStatement)super.statement).setBlob(text, inputStream, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNClob(String text, Reader reader, long value) {
      try {
         try {
            ((CallableStatement)super.statement).setNClob(text, reader, value);
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
            return ((CallableStatement)super.statement).getNClob(value);
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
            return ((CallableStatement)super.statement).getNClob(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setSQLXML(String text, SQLXML sqlxml) {
      try {
         try {
            ((CallableStatement)super.statement).setSQLXML(text, sqlxml);
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
            return ((CallableStatement)super.statement).getSQLXML(value);
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
            return ((CallableStatement)super.statement).getSQLXML(text);
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
            return ((CallableStatement)super.statement).getNString(value);
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
            return ((CallableStatement)super.statement).getNString(text);
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
            return ((CallableStatement)super.statement).getNCharacterStream(value);
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
            return ((CallableStatement)super.statement).getNCharacterStream(text);
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
            return ((CallableStatement)super.statement).getCharacterStream(value);
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
            return ((CallableStatement)super.statement).getCharacterStream(text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBlob(String text, Blob blob) {
      try {
         try {
            ((CallableStatement)super.statement).setBlob(text, blob);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setClob(String text, Clob clob) {
      try {
         try {
            ((CallableStatement)super.statement).setClob(text, clob);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setAsciiStream(String text, InputStream inputStream, long value) {
      try {
         try {
            ((CallableStatement)super.statement).setAsciiStream(text, inputStream, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBinaryStream(String text, InputStream inputStream, long value) {
      try {
         try {
            ((CallableStatement)super.statement).setBinaryStream(text, inputStream, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setCharacterStream(String text, Reader reader, long value) {
      try {
         try {
            ((CallableStatement)super.statement).setCharacterStream(text, reader, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setAsciiStream(String text, InputStream inputStream) {
      try {
         try {
            ((CallableStatement)super.statement).setAsciiStream(text, inputStream);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBinaryStream(String text, InputStream inputStream) {
      try {
         try {
            ((CallableStatement)super.statement).setBinaryStream(text, inputStream);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setCharacterStream(String text, Reader reader) {
      try {
         try {
            ((CallableStatement)super.statement).setCharacterStream(text, reader);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNCharacterStream(String text, Reader reader) {
      try {
         try {
            ((CallableStatement)super.statement).setNCharacterStream(text, reader);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setClob(String text, Reader reader) {
      try {
         try {
            ((CallableStatement)super.statement).setClob(text, reader);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setBlob(String text, InputStream inputStream) {
      try {
         try {
            ((CallableStatement)super.statement).setBlob(text, inputStream);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setNClob(String text, Reader reader) {
      try {
         try {
            ((CallableStatement)super.statement).setNClob(text, reader);
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
            return ((CallableStatement)super.statement).getObject(value, class2);
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
            return ((CallableStatement)super.statement).getObject(text, class2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setObject(String text, Object obj, SQLType sqlType, int value) {
      try {
         try {
            ((CallableStatement)super.statement).setObject(text, obj, sqlType, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setObject(String text, Object obj, SQLType sqlType) {
      try {
         try {
            ((CallableStatement)super.statement).setObject(text, obj, sqlType);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void registerOutParameter(int value, SQLType sqlType) {
      try {
         try {
            ((CallableStatement)super.statement).registerOutParameter(value, sqlType);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void registerOutParameter(int value, SQLType sqlType, int value2) {
      try {
         try {
            ((CallableStatement)super.statement).registerOutParameter(value, sqlType, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void registerOutParameter(int value, SQLType sqlType, String text) {
      try {
         try {
            ((CallableStatement)super.statement).registerOutParameter(value, sqlType, text);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void registerOutParameter(String text, SQLType sqlType) {
      try {
         try {
            ((CallableStatement)super.statement).registerOutParameter(text, sqlType);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void registerOutParameter(String text, SQLType sqlType, int value) {
      try {
         try {
            ((CallableStatement)super.statement).registerOutParameter(text, sqlType, value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void registerOutParameter(String text, SQLType sqlType, String text2) {
      try {
         try {
            ((CallableStatement)super.statement).registerOutParameter(text, sqlType, text2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   protected HikariProxyCallableStatement(ProxyConnection proxyConnection, CallableStatement callableStatement) {
      super(proxyConnection, callableStatement);
   }
}
