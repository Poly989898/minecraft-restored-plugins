package io.lumine.mythic.bukkit.utils.storage.sql.hikari.pool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Wrapper;

public abstract class ProxyResultSet implements ResultSet {
   protected final ProxyConnection connection;
   protected final ProxyStatement statement;
   protected final ResultSet delegate;

   protected ProxyResultSet(ProxyConnection connection, ProxyStatement statement, ResultSet resultSet) {
      this.connection = connection;
      this.statement = statement;
      this.delegate = resultSet;
   }

   final SQLException checkException(SQLException e) {
      return this.connection.checkException(e);
   }

   @Override
   public String toString() {
      return new StringBuilder(64)
         .append(this.getClass().getSimpleName())
         .append('@')
         .append(System.identityHashCode(this))
         .append(" wrapping ")
         .append(this.delegate)
         .toString();
   }

   @Override
   public final Statement getStatement() throws SQLException {
      return this.statement;
   }

   @Override
   public void updateRow() throws SQLException {
      this.connection.markCommitStateDirty();
      this.delegate.updateRow();
   }

   @Override
   public void insertRow() throws SQLException {
      this.connection.markCommitStateDirty();
      this.delegate.insertRow();
   }

   @Override
   public void deleteRow() throws SQLException {
      this.connection.markCommitStateDirty();
      this.delegate.deleteRow();
   }

   @Override
   public final <T> T unwrap(Class<T> iface) throws SQLException {
      if (iface.isInstance(this.delegate)) {
         return (T)this.delegate;
      } else if (this.delegate instanceof Wrapper) {
         return this.delegate.unwrap(iface);
      } else {
         throw new SQLException("Wrapped ResultSet is not an instance of " + iface);
      }
   }
}
