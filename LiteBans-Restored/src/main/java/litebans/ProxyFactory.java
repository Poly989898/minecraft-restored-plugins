package litebans;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public final class ProxyFactory {
   static ProxyConnection computeProxyConnection(PoolEntry poolEntry, Connection connection, FastList fastList, LeakTask leakTask, long value, boolean flag, boolean flag2) {
      return new HikariProxyConnection(poolEntry, connection, fastList, leakTask, value, flag, flag2);
   }

   static Statement computeStatement(ProxyConnection proxyConnection, Statement statement) {
      return new HikariProxyStatement(proxyConnection, statement);
   }

   static CallableStatement computeCallableStatement(ProxyConnection proxyConnection, CallableStatement callableStatement) {
      return new HikariProxyCallableStatement(proxyConnection, callableStatement);
   }

   static PreparedStatement computePreparedStatement(ProxyConnection proxyConnection, PreparedStatement preparedStatement) {
      return new HikariProxyPreparedStatement(proxyConnection, preparedStatement);
   }

   static ResultSet computeResultSet(ProxyConnection proxyConnection, ProxyStatement proxyStatement, ResultSet resultSet) {
      return new HikariProxyResultSet(proxyConnection, proxyStatement, resultSet);
   }

   static DatabaseMetaData computeDatabaseMetaData(ProxyConnection proxyConnection, DatabaseMetaData databaseMetaData) {
      return new HikariProxyDatabaseMetaData(proxyConnection, databaseMetaData);
   }
}
