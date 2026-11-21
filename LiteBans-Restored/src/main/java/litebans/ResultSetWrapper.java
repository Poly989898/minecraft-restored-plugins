package litebans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class ResultSetWrapper implements ResultSet {
   protected final ProxyConnection proxyConnection;
   protected final ProxyStatement proxyStatement;
   final ResultSet resultSet;

   protected ResultSetWrapper(ProxyConnection proxyConnection, ProxyStatement proxyStatement, ResultSet resultSet) {
      this.proxyConnection = proxyConnection;
      this.proxyStatement = proxyStatement;
      this.resultSet = resultSet;
   }

   final SQLException computeSqlException(SQLException sqlException) {
      return this.proxyConnection.checkException(sqlException);
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + '@' + System.identityHashCode(this) + " wrapping " + this.resultSet;
   }

   @Override
   public final Statement getStatement() {
      return this.proxyStatement;
   }

   @Override
   public void updateRow() {
      try {
         this.proxyConnection.perform();
         this.resultSet.updateRow();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void insertRow() {
      try {
         this.proxyConnection.perform();
         this.resultSet.insertRow();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void deleteRow() {
      try {
         this.proxyConnection.perform();
         this.resultSet.deleteRow();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public final Object unwrap(Class class2) {
      try {
         if (class2.isInstance(this.resultSet)) {
            return this.resultSet;
         } else if (this.resultSet != null) {
            return this.resultSet.unwrap(class2);
         } else {
            throw new SQLException("Wrapped ResultSet is not an instance of " + class2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
