package io.lumine.mythic.bukkit.utils.storage.sql.hikari.pool;

import io.lumine.mythic.bukkit.utils.storage.sql.hikari.util.FastList;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public final class ProxyFactory {
   private ProxyFactory() {
   }

   static ProxyConnection getProxyConnection(
      PoolEntry poolEntry, Connection connection, FastList<Statement> fastList, ProxyLeakTask proxyLeakTask, long value, boolean flag, boolean flag2
   ) {
      return new HikariProxyConnection(poolEntry, connection, fastList, proxyLeakTask, value, flag, flag2);
   }

   static Statement getProxyStatement(ProxyConnection proxyConnection, Statement statement) {
      return new HikariProxyStatement(proxyConnection, statement);
   }

   static CallableStatement getProxyCallableStatement(ProxyConnection proxyConnection, CallableStatement callableStatement) {
      return new HikariProxyCallableStatement(proxyConnection, callableStatement);
   }

   static PreparedStatement getProxyPreparedStatement(ProxyConnection proxyConnection, PreparedStatement preparedStatement) {
      return new HikariProxyPreparedStatement(proxyConnection, preparedStatement);
   }

   static ResultSet getProxyResultSet(ProxyConnection proxyConnection, ProxyStatement proxyStatement, ResultSet resultSet) {
      return new HikariProxyResultSet(proxyConnection, proxyStatement, resultSet);
   }
}
