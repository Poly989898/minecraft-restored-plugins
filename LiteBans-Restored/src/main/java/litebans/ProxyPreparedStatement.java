package litebans;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class ProxyPreparedStatement extends ProxyStatement implements PreparedStatement {
   ProxyPreparedStatement(ProxyConnection proxyConnection, PreparedStatement preparedStatement) {
      super(proxyConnection, preparedStatement);
   }

   @Override
   public boolean execute() {
      try {
         this.proxyConnection.perform();
         return ((PreparedStatement)this.statement).execute();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet executeQuery() {
      try {
         this.proxyConnection.perform();
         ResultSet executeQuery2 = ((PreparedStatement)this.statement).executeQuery();
         return ProxyFactory.computeResultSet(this.proxyConnection, this, executeQuery2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int executeUpdate() {
      try {
         this.proxyConnection.perform();
         return ((PreparedStatement)this.statement).executeUpdate();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long executeLargeUpdate() {
      try {
         this.proxyConnection.perform();
         return ((PreparedStatement)this.statement).executeLargeUpdate();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
