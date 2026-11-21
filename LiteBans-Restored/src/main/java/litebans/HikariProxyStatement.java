package litebans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Wrapper;

public final class HikariProxyStatement extends ProxyStatement implements Wrapper, AutoCloseable, Statement {
   @Override
   public boolean isWrapperFor(Class class2) {
      try {
         try {
            return super.statement.isWrapperFor(class2);
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
            return super.statement.getMaxFieldSize();
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
            super.statement.setMaxFieldSize(value);
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
            return super.statement.getMaxRows();
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
            super.statement.setMaxRows(value);
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
            super.statement.setEscapeProcessing(flag);
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
            return super.statement.getQueryTimeout();
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
            super.statement.setQueryTimeout(value);
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
            super.statement.cancel();
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
            return super.statement.getWarnings();
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
            super.statement.clearWarnings();
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
            super.statement.setCursorName(text);
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
            return super.statement.getUpdateCount();
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
            return super.statement.getMoreResults();
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
            super.statement.setFetchDirection(value);
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
            return super.statement.getFetchDirection();
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
            super.statement.setFetchSize(value);
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
            return super.statement.getFetchSize();
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
            return super.statement.getResultSetConcurrency();
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
            return super.statement.getResultSetType();
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
            super.statement.addBatch(text);
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
            super.statement.clearBatch();
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
            return super.statement.getMoreResults(value);
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
            return super.statement.getResultSetHoldability();
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
            super.statement.setPoolable(flag);
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
            return super.statement.isPoolable();
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
            super.statement.closeOnCompletion();
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
            return super.statement.isCloseOnCompletion();
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
            return super.statement.getLargeUpdateCount();
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
            super.statement.setLargeMaxRows(value);
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
            return super.statement.getLargeMaxRows();
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
            return super.statement.executeLargeBatch();
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
            return super.statement.executeLargeUpdate(text);
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
            return super.statement.executeLargeUpdate(text, value);
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
            return super.statement.executeLargeUpdate(text, ints);
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
            return super.statement.executeLargeUpdate(text, texts);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   HikariProxyStatement(ProxyConnection proxyConnection, Statement statement) {
      super(proxyConnection, statement);
   }
}
