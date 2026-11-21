package litebans;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

final class DeadConnectionProxy {
   static final Connection CONNECTION = obtainConnection();

   private DeadConnectionProxy() {
   }

   private static final Connection obtainConnection() {
      InvocationHandler invocationHandler = (obj, method, objs) -> {
         try {
            String name = method.getName();
            if ("isClosed".equals(name)) {
               return Boolean.TRUE;
            } else if ("isValid".equals(name)) {
               return Boolean.FALSE;
            } else if ("abort".equals(name)) {
               return void.class;
            } else if ("close".equals(name)) {
               return void.class;
            } else if ("toString".equals(name)) {
               return DeadConnectionProxy.class.getCanonicalName();
            } else {
               throw new SQLException("Connection is closed");
            }
         } catch (SQLException e) {
            throw SneakyThrow.<RuntimeException>rethrow(e);
         }
      };
      return (Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[]{Connection.class}, invocationHandler);
   }

}
