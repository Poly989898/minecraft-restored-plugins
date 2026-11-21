package litebans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class ProxyStatement implements Statement {
   protected final ProxyConnection proxyConnection;
   final Statement statement;
   private boolean flag2;
   private ResultSet resultSet2;

   ProxyStatement(ProxyConnection proxyConnection, Statement statement) {
      this.proxyConnection = proxyConnection;
      this.statement = statement;
   }

   final SQLException computeSqlException(SQLException sqlException) {
      return this.proxyConnection.checkException(sqlException);
   }

   @Override
   public final String toString() {
      String string = this.statement.toString();
      return this.getClass().getSimpleName() + '@' + System.identityHashCode(this) + " wrapping " + string;
   }

   @Override
   public final void close() {
      try {
         synchronized (this) {
            if (this.flag2) {
               return;
            }

            this.flag2 = true;
         }

         this.proxyConnection.untrackStatement(this.statement);

         try {
            this.statement.close();
         } catch (SQLException e2) {
            throw this.proxyConnection.checkException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isClosed() {
      try {
         return this.statement.isClosed();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Connection getConnection() {
      return this.proxyConnection;
   }

   @Override
   public boolean execute(String text) {
      try {
         this.proxyConnection.perform();
         return this.statement.execute(text);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean execute(String text, int value) {
      try {
         this.proxyConnection.perform();
         return this.statement.execute(text, value);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet executeQuery(String text) {
      try {
         this.proxyConnection.perform();
         ResultSet executeQuery2 = this.statement.executeQuery(text);
         return ProxyFactory.computeResultSet(this.proxyConnection, this, executeQuery2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int executeUpdate(String text) {
      try {
         this.proxyConnection.perform();
         return this.statement.executeUpdate(text);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int[] executeBatch() {
      try {
         this.proxyConnection.perform();
         return this.statement.executeBatch();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int executeUpdate(String text, int value) {
      try {
         this.proxyConnection.perform();
         return this.statement.executeUpdate(text, value);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int executeUpdate(String text, int[] ints) {
      try {
         this.proxyConnection.perform();
         return this.statement.executeUpdate(text, ints);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int executeUpdate(String text, String[] texts) {
      try {
         this.proxyConnection.perform();
         return this.statement.executeUpdate(text, texts);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean execute(String text, int[] ints) {
      try {
         this.proxyConnection.perform();
         return this.statement.execute(text, ints);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean execute(String text, String[] texts) {
      try {
         this.proxyConnection.perform();
         return this.statement.execute(text, texts);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long[] executeLargeBatch() {
      try {
         this.proxyConnection.perform();
         return this.statement.executeLargeBatch();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long executeLargeUpdate(String text) {
      try {
         this.proxyConnection.perform();
         return this.statement.executeLargeUpdate(text);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long executeLargeUpdate(String text, int value) {
      try {
         this.proxyConnection.perform();
         return this.statement.executeLargeUpdate(text, value);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long executeLargeUpdate(String text, int[] ints) {
      try {
         this.proxyConnection.perform();
         return this.statement.executeLargeUpdate(text, ints);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long executeLargeUpdate(String text, String[] texts) {
      try {
         this.proxyConnection.perform();
         return this.statement.executeLargeUpdate(text, texts);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getResultSet() {
      try {
         ResultSet resultSet = this.statement.getResultSet();
         if (resultSet != null) {
            if (this.resultSet2 == null || ((ResultSetWrapper)this.resultSet2).resultSet != resultSet) {
               this.resultSet2 = ProxyFactory.computeResultSet(this.proxyConnection, this, resultSet);
            }
         } else {
            this.resultSet2 = null;
         }

         return this.resultSet2;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getGeneratedKeys() {
      try {
         ResultSet generatedKeys = this.statement.getGeneratedKeys();
         if (this.resultSet2 == null || ((ResultSetWrapper)this.resultSet2).resultSet != generatedKeys) {
            this.resultSet2 = ProxyFactory.computeResultSet(this.proxyConnection, this, generatedKeys);
         }

         return this.resultSet2;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public final Object unwrap(Class class2) {
      try {
         if (class2.isInstance(this.statement)) {
            return this.statement;
         } else if (this.statement != null) {
            return this.statement.unwrap(class2);
         } else {
            throw new SQLException("Wrapped statement is not an instance of " + class2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
