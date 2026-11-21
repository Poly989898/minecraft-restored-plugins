package litebans;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class ProxyDatabaseMetaData implements DatabaseMetaData {
   protected final ProxyConnection proxyConnection;
   protected final DatabaseMetaData databaseMetaData;

   ProxyDatabaseMetaData(ProxyConnection proxyConnection, DatabaseMetaData databaseMetaData) {
      this.proxyConnection = proxyConnection;
      this.databaseMetaData = databaseMetaData;
   }

   final SQLException computeSqlException(SQLException sqlException) {
      return this.proxyConnection.checkException(sqlException);
   }

   @Override
   public final String toString() {
      String string = this.databaseMetaData.toString();
      return this.getClass().getSimpleName() + '@' + System.identityHashCode(this) + " wrapping " + string;
   }

   @Override
   public final Connection getConnection() {
      return this.proxyConnection;
   }

   @Override
   public ResultSet getProcedures(String text, String text2, String text3) {
      try {
         ResultSet procedures = this.databaseMetaData.getProcedures(text, text2, text3);
         Statement statement = procedures.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, procedures);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getProcedureColumns(String text, String text2, String text3, String text4) {
      try {
         ResultSet procedureColumns = this.databaseMetaData.getProcedureColumns(text, text2, text3, text4);
         Statement statement = procedureColumns.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, procedureColumns);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getTables(String text, String text2, String text3, String[] texts) {
      try {
         ResultSet tables = this.databaseMetaData.getTables(text, text2, text3, texts);
         Statement statement = tables.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, tables);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getSchemas() {
      try {
         ResultSet schemas = this.databaseMetaData.getSchemas();
         Statement statement = schemas.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, schemas);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getCatalogs() {
      try {
         ResultSet catalogs = this.databaseMetaData.getCatalogs();
         Statement statement = catalogs.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, catalogs);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getTableTypes() {
      try {
         ResultSet tableTypes = this.databaseMetaData.getTableTypes();
         Statement statement = tableTypes.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, tableTypes);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getColumns(String text, String text2, String text3, String text4) {
      try {
         ResultSet columns = this.databaseMetaData.getColumns(text, text2, text3, text4);
         Statement statement = columns.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, columns);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getColumnPrivileges(String text, String text2, String text3, String text4) {
      try {
         ResultSet columnPrivileges = this.databaseMetaData.getColumnPrivileges(text, text2, text3, text4);
         Statement statement = columnPrivileges.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, columnPrivileges);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getTablePrivileges(String text, String text2, String text3) {
      try {
         ResultSet tablePrivileges = this.databaseMetaData.getTablePrivileges(text, text2, text3);
         Statement statement = tablePrivileges.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, tablePrivileges);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getBestRowIdentifier(String text, String text2, String text3, int value, boolean flag) {
      try {
         ResultSet bestRowIdentifier = this.databaseMetaData.getBestRowIdentifier(text, text2, text3, value, flag);
         Statement statement = bestRowIdentifier.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, bestRowIdentifier);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getVersionColumns(String text, String text2, String text3) {
      try {
         ResultSet versionColumns = this.databaseMetaData.getVersionColumns(text, text2, text3);
         Statement statement = versionColumns.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, versionColumns);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getPrimaryKeys(String text, String text2, String text3) {
      try {
         ResultSet primaryKeys = this.databaseMetaData.getPrimaryKeys(text, text2, text3);
         Statement statement = primaryKeys.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, primaryKeys);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getImportedKeys(String text, String text2, String text3) {
      try {
         ResultSet importedKeys = this.databaseMetaData.getImportedKeys(text, text2, text3);
         Statement statement = importedKeys.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, importedKeys);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getExportedKeys(String text, String text2, String text3) {
      try {
         ResultSet exportedKeys = this.databaseMetaData.getExportedKeys(text, text2, text3);
         Statement statement = exportedKeys.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, exportedKeys);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getCrossReference(String text, String text2, String text3, String text4, String text5, String text6) {
      try {
         ResultSet crossReference = this.databaseMetaData.getCrossReference(text, text2, text3, text4, text5, text6);
         Statement statement = crossReference.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, crossReference);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getTypeInfo() {
      try {
         ResultSet typeInfo = this.databaseMetaData.getTypeInfo();
         Statement statement = typeInfo.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, typeInfo);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getIndexInfo(String text, String text2, String text3, boolean flag, boolean flag2) {
      try {
         ResultSet indexInfo = this.databaseMetaData.getIndexInfo(text, text2, text3, flag, flag2);
         Statement statement = indexInfo.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, indexInfo);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getUDTs(String text, String text2, String text3, int[] ints) {
      try {
         ResultSet udTs = this.databaseMetaData.getUDTs(text, text2, text3, ints);
         Statement statement = udTs.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, udTs);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getSuperTypes(String text, String text2, String text3) {
      try {
         ResultSet superTypes = this.databaseMetaData.getSuperTypes(text, text2, text3);
         Statement statement = superTypes.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, superTypes);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getSuperTables(String text, String text2, String text3) {
      try {
         ResultSet superTables = this.databaseMetaData.getSuperTables(text, text2, text3);
         Statement statement = superTables.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, superTables);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getAttributes(String text, String text2, String text3, String text4) {
      try {
         ResultSet attributes = this.databaseMetaData.getAttributes(text, text2, text3, text4);
         Statement statement = attributes.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, attributes);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getSchemas(String text, String text2) {
      try {
         ResultSet schemas = this.databaseMetaData.getSchemas(text, text2);
         Statement statement = schemas.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, schemas);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getClientInfoProperties() {
      try {
         ResultSet clientInfoProperties = this.databaseMetaData.getClientInfoProperties();
         Statement statement = clientInfoProperties.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, clientInfoProperties);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getFunctions(String text, String text2, String text3) {
      try {
         ResultSet functions = this.databaseMetaData.getFunctions(text, text2, text3);
         Statement statement = functions.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, functions);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getFunctionColumns(String text, String text2, String text3, String text4) {
      try {
         ResultSet functionColumns = this.databaseMetaData.getFunctionColumns(text, text2, text3, text4);
         Statement statement = functionColumns.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, functionColumns);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getPseudoColumns(String text, String text2, String text3, String text4) {
      try {
         ResultSet pseudoColumns = this.databaseMetaData.getPseudoColumns(text, text2, text3, text4);
         Statement statement = pseudoColumns.getStatement();
         if (statement != null) {
            statement = ProxyFactory.computeStatement(this.proxyConnection, statement);
         }

         return ProxyFactory.computeResultSet(this.proxyConnection, (ProxyStatement)statement, pseudoColumns);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public final Object unwrap(Class class2) {
      try {
         if (class2.isInstance(this.databaseMetaData)) {
            return this.databaseMetaData;
         } else if (this.databaseMetaData != null) {
            return this.databaseMetaData.unwrap(class2);
         } else {
            throw new SQLException("Wrapped DatabaseMetaData is not an instance of " + class2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
