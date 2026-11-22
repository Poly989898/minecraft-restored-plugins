package com.Zrips.CMI.Modules.DataBase_NEW;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class CMIDbBatch {
  private static Connection batchConnection = null;
  private PreparedStatement batch = null;

  public PreparedStatement getBatch() {
    return this.batch;
  }

  public PreparedStatement prepareBatch(String string) {
    if (this.getBatch() != null) {
      return this.getBatch();
    }

    if (batchConnection == null) {
      batchConnection = DBDAO_NEW.getPooledConnection();

      try {
        batchConnection.setAutoCommit(false);
      } catch (SQLException sqlException) {
        sqlException.printStackTrace();
      }
    }

    if (batchConnection == null) {
      return null;
    }

    try {
      this.batch = batchConnection.prepareStatement(string);
    } catch (SQLException sqlException2) {
      sqlException2.printStackTrace();
    }

    return this.getBatch();
  }

  public static boolean commitBatch() {
    if (batchConnection == null) {
      return false;
    }

    try {
      batchConnection.commit();
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
    }

    return true;
  }

  public boolean commit() {
    return this.commit(this.getBatch());
  }

  public void addBatch() {
    if (this.getBatch() != null) {
      try {
        this.getBatch().addBatch();
      } catch (SQLException sqlException) {
        sqlException.printStackTrace();
      }
    }
  }

  private boolean commit(PreparedStatement preparedStatement) {
    if (preparedStatement == null) {
      return true;
    }

    boolean flag = true;

    try {
      preparedStatement.executeBatch();
    } catch (SQLException sqlException) {
      flag = false;
    }

    return this.close() ? true : flag;
  }

  public boolean close() {
    this.close(this.getBatch());
    this.batch = null;
    return true;
  }

  private void close(Statement statement) {
    try {
      statement.close();
    } catch (Error | Exception value) {
      value.printStackTrace();
    }
  }
}
