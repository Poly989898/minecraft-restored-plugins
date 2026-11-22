package com.Zrips.CMI.Modules.DataBase_NEW;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import net.Zrips.CMILib.Logs.CMIDebug;

public class DBConnectionPool_NEW {
  private final String url;
  private final String username;
  private final String password;
  private final int maxPoolSize = 25;
  private final Queue<Connection> pool = new ArrayDeque<>();
  private final Set<Connection> created = new HashSet<>();

  public DBConnectionPool_NEW(String string, String userNameValue, String dbPassword)
      throws SQLException {
    this.url = string;
    this.username = userNameValue;
    this.password = dbPassword;

    for (int value = 0; value < Math.min(2, 25); value++) {
      this.pool.add(this.createNewConnection());
    }
  }

  private Connection createNewConnection() throws SQLException {
    Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
    this.created.add(connection);
    return connection;
  }

  public synchronized Connection getConnection() throws SQLException {
    for (Connection connection = this.pool.poll();
        connection != null;
        connection = this.pool.poll()) {
      if (connection.isValid(1) && !connection.isClosed()) {
        CMIDebug.c(
            new Object[] {
              "Getting pooled Connection, remains: ",
              this.getTotalConnections() + " / " + this.created.size()
            });
        return connection;
      }

      try {
        connection.close();
      } catch (SQLException sqlException) {
      }
    }

    if (this.getTotalConnections() < 25) {
      Connection newConnection = this.createNewConnection();
      CMIDebug.c(
          new Object[] {
            "Getting pooled Connection, remains: ",
            this.getTotalConnections() + " / " + this.created.size()
          });
      return newConnection;
    } else {
      throw new SQLException("All database connections are in use");
    }
  }

  private int getTotalConnections() {
    return this.pool.size();
  }

  public synchronized void releaseConnection(Connection connection) {
    if (connection != null) {
      this.pool.offer(connection);
      this.notify();
      CMIDebug.c(
          new Object[] {
            "Returned pooled Connection: ", this.getTotalConnections() + " / " + this.created.size()
          });
    }
  }

  public synchronized void closeAll() {
    for (Connection connection : this.created) {
      try {
        connection.close();
      } catch (SQLException sqlException) {
        sqlException.printStackTrace();
      }
    }

    this.pool.clear();
    this.created.clear();
  }

  public synchronized boolean hasValidConnection() {
    for (Connection connection : this.pool) {
      try {
        if (connection != null && !connection.isClosed() && connection.isValid(1)) {
          return true;
        }
      } catch (SQLException sqlException) {
      }
    }

    return false;
  }
}
