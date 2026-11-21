package litebans;

import java.sql.CallableStatement;

public abstract class ProxyCallableStatement extends ProxyPreparedStatement implements CallableStatement {
   protected ProxyCallableStatement(ProxyConnection proxyConnection, CallableStatement callableStatement) {
      super(proxyConnection, callableStatement);
   }
}
