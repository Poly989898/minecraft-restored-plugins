package io.lumine.mythic.bukkit.utils.storage.sql.hikari.pool;

import java.sql.CallableStatement;

public abstract class ProxyCallableStatement extends ProxyPreparedStatement implements CallableStatement {
   protected ProxyCallableStatement(ProxyConnection connection, CallableStatement statement) {
      super(connection, statement);
   }
}
