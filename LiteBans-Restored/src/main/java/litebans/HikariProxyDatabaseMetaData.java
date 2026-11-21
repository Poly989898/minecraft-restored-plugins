package litebans;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Wrapper;

public final class HikariProxyDatabaseMetaData extends ProxyDatabaseMetaData implements Wrapper, DatabaseMetaData {
   @Override
   public boolean isWrapperFor(Class class2) {
      try {
         try {
            return super.databaseMetaData.isWrapperFor(class2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean allProceduresAreCallable() {
      try {
         try {
            return super.databaseMetaData.allProceduresAreCallable();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean allTablesAreSelectable() {
      try {
         try {
            return super.databaseMetaData.allTablesAreSelectable();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getURL() {
      try {
         try {
            return super.databaseMetaData.getURL();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getUserName() {
      try {
         try {
            return super.databaseMetaData.getUserName();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isReadOnly() {
      try {
         try {
            return super.databaseMetaData.isReadOnly();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean nullsAreSortedHigh() {
      try {
         try {
            return super.databaseMetaData.nullsAreSortedHigh();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean nullsAreSortedLow() {
      try {
         try {
            return super.databaseMetaData.nullsAreSortedLow();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean nullsAreSortedAtStart() {
      try {
         try {
            return super.databaseMetaData.nullsAreSortedAtStart();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean nullsAreSortedAtEnd() {
      try {
         try {
            return super.databaseMetaData.nullsAreSortedAtEnd();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getDatabaseProductName() {
      try {
         try {
            return super.databaseMetaData.getDatabaseProductName();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getDatabaseProductVersion() {
      try {
         try {
            return super.databaseMetaData.getDatabaseProductVersion();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getDriverName() {
      try {
         try {
            return super.databaseMetaData.getDriverName();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getDriverVersion() {
      try {
         try {
            return super.databaseMetaData.getDriverVersion();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getDriverMajorVersion() {
      return super.databaseMetaData.getDriverMajorVersion();
   }

   @Override
   public int getDriverMinorVersion() {
      return super.databaseMetaData.getDriverMinorVersion();
   }

   @Override
   public boolean usesLocalFiles() {
      try {
         try {
            return super.databaseMetaData.usesLocalFiles();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean usesLocalFilePerTable() {
      try {
         try {
            return super.databaseMetaData.usesLocalFilePerTable();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsMixedCaseIdentifiers() {
      try {
         try {
            return super.databaseMetaData.supportsMixedCaseIdentifiers();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean storesUpperCaseIdentifiers() {
      try {
         try {
            return super.databaseMetaData.storesUpperCaseIdentifiers();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean storesLowerCaseIdentifiers() {
      try {
         try {
            return super.databaseMetaData.storesLowerCaseIdentifiers();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean storesMixedCaseIdentifiers() {
      try {
         try {
            return super.databaseMetaData.storesMixedCaseIdentifiers();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsMixedCaseQuotedIdentifiers() {
      try {
         try {
            return super.databaseMetaData.supportsMixedCaseQuotedIdentifiers();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean storesUpperCaseQuotedIdentifiers() {
      try {
         try {
            return super.databaseMetaData.storesUpperCaseQuotedIdentifiers();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean storesLowerCaseQuotedIdentifiers() {
      try {
         try {
            return super.databaseMetaData.storesLowerCaseQuotedIdentifiers();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean storesMixedCaseQuotedIdentifiers() {
      try {
         try {
            return super.databaseMetaData.storesMixedCaseQuotedIdentifiers();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getIdentifierQuoteString() {
      try {
         try {
            return super.databaseMetaData.getIdentifierQuoteString();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getSQLKeywords() {
      try {
         try {
            return super.databaseMetaData.getSQLKeywords();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getNumericFunctions() {
      try {
         try {
            return super.databaseMetaData.getNumericFunctions();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getStringFunctions() {
      try {
         try {
            return super.databaseMetaData.getStringFunctions();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getSystemFunctions() {
      try {
         try {
            return super.databaseMetaData.getSystemFunctions();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getTimeDateFunctions() {
      try {
         try {
            return super.databaseMetaData.getTimeDateFunctions();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getSearchStringEscape() {
      try {
         try {
            return super.databaseMetaData.getSearchStringEscape();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getExtraNameCharacters() {
      try {
         try {
            return super.databaseMetaData.getExtraNameCharacters();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsAlterTableWithAddColumn() {
      try {
         try {
            return super.databaseMetaData.supportsAlterTableWithAddColumn();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsAlterTableWithDropColumn() {
      try {
         try {
            return super.databaseMetaData.supportsAlterTableWithDropColumn();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsColumnAliasing() {
      try {
         try {
            return super.databaseMetaData.supportsColumnAliasing();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean nullPlusNonNullIsNull() {
      try {
         try {
            return super.databaseMetaData.nullPlusNonNullIsNull();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsConvert() {
      try {
         try {
            return super.databaseMetaData.supportsConvert();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsConvert(int value, int value2) {
      try {
         try {
            return super.databaseMetaData.supportsConvert(value, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsTableCorrelationNames() {
      try {
         try {
            return super.databaseMetaData.supportsTableCorrelationNames();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsDifferentTableCorrelationNames() {
      try {
         try {
            return super.databaseMetaData.supportsDifferentTableCorrelationNames();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsExpressionsInOrderBy() {
      try {
         try {
            return super.databaseMetaData.supportsExpressionsInOrderBy();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsOrderByUnrelated() {
      try {
         try {
            return super.databaseMetaData.supportsOrderByUnrelated();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsGroupBy() {
      try {
         try {
            return super.databaseMetaData.supportsGroupBy();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsGroupByUnrelated() {
      try {
         try {
            return super.databaseMetaData.supportsGroupByUnrelated();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsGroupByBeyondSelect() {
      try {
         try {
            return super.databaseMetaData.supportsGroupByBeyondSelect();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsLikeEscapeClause() {
      try {
         try {
            return super.databaseMetaData.supportsLikeEscapeClause();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsMultipleResultSets() {
      try {
         try {
            return super.databaseMetaData.supportsMultipleResultSets();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsMultipleTransactions() {
      try {
         try {
            return super.databaseMetaData.supportsMultipleTransactions();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsNonNullableColumns() {
      try {
         try {
            return super.databaseMetaData.supportsNonNullableColumns();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsMinimumSQLGrammar() {
      try {
         try {
            return super.databaseMetaData.supportsMinimumSQLGrammar();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsCoreSQLGrammar() {
      try {
         try {
            return super.databaseMetaData.supportsCoreSQLGrammar();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsExtendedSQLGrammar() {
      try {
         try {
            return super.databaseMetaData.supportsExtendedSQLGrammar();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsANSI92EntryLevelSQL() {
      try {
         try {
            return super.databaseMetaData.supportsANSI92EntryLevelSQL();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsANSI92IntermediateSQL() {
      try {
         try {
            return super.databaseMetaData.supportsANSI92IntermediateSQL();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsANSI92FullSQL() {
      try {
         try {
            return super.databaseMetaData.supportsANSI92FullSQL();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsIntegrityEnhancementFacility() {
      try {
         try {
            return super.databaseMetaData.supportsIntegrityEnhancementFacility();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsOuterJoins() {
      try {
         try {
            return super.databaseMetaData.supportsOuterJoins();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsFullOuterJoins() {
      try {
         try {
            return super.databaseMetaData.supportsFullOuterJoins();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsLimitedOuterJoins() {
      try {
         try {
            return super.databaseMetaData.supportsLimitedOuterJoins();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getSchemaTerm() {
      try {
         try {
            return super.databaseMetaData.getSchemaTerm();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getProcedureTerm() {
      try {
         try {
            return super.databaseMetaData.getProcedureTerm();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getCatalogTerm() {
      try {
         try {
            return super.databaseMetaData.getCatalogTerm();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isCatalogAtStart() {
      try {
         try {
            return super.databaseMetaData.isCatalogAtStart();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public String getCatalogSeparator() {
      try {
         try {
            return super.databaseMetaData.getCatalogSeparator();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsSchemasInDataManipulation() {
      try {
         try {
            return super.databaseMetaData.supportsSchemasInDataManipulation();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsSchemasInProcedureCalls() {
      try {
         try {
            return super.databaseMetaData.supportsSchemasInProcedureCalls();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsSchemasInTableDefinitions() {
      try {
         try {
            return super.databaseMetaData.supportsSchemasInTableDefinitions();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsSchemasInIndexDefinitions() {
      try {
         try {
            return super.databaseMetaData.supportsSchemasInIndexDefinitions();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsSchemasInPrivilegeDefinitions() {
      try {
         try {
            return super.databaseMetaData.supportsSchemasInPrivilegeDefinitions();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsCatalogsInDataManipulation() {
      try {
         try {
            return super.databaseMetaData.supportsCatalogsInDataManipulation();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsCatalogsInProcedureCalls() {
      try {
         try {
            return super.databaseMetaData.supportsCatalogsInProcedureCalls();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsCatalogsInTableDefinitions() {
      try {
         try {
            return super.databaseMetaData.supportsCatalogsInTableDefinitions();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsCatalogsInIndexDefinitions() {
      try {
         try {
            return super.databaseMetaData.supportsCatalogsInIndexDefinitions();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsCatalogsInPrivilegeDefinitions() {
      try {
         try {
            return super.databaseMetaData.supportsCatalogsInPrivilegeDefinitions();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsPositionedDelete() {
      try {
         try {
            return super.databaseMetaData.supportsPositionedDelete();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsPositionedUpdate() {
      try {
         try {
            return super.databaseMetaData.supportsPositionedUpdate();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsSelectForUpdate() {
      try {
         try {
            return super.databaseMetaData.supportsSelectForUpdate();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsStoredProcedures() {
      try {
         try {
            return super.databaseMetaData.supportsStoredProcedures();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsSubqueriesInComparisons() {
      try {
         try {
            return super.databaseMetaData.supportsSubqueriesInComparisons();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsSubqueriesInExists() {
      try {
         try {
            return super.databaseMetaData.supportsSubqueriesInExists();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsSubqueriesInIns() {
      try {
         try {
            return super.databaseMetaData.supportsSubqueriesInIns();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsSubqueriesInQuantifieds() {
      try {
         try {
            return super.databaseMetaData.supportsSubqueriesInQuantifieds();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsCorrelatedSubqueries() {
      try {
         try {
            return super.databaseMetaData.supportsCorrelatedSubqueries();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsUnion() {
      try {
         try {
            return super.databaseMetaData.supportsUnion();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsUnionAll() {
      try {
         try {
            return super.databaseMetaData.supportsUnionAll();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsOpenCursorsAcrossCommit() {
      try {
         try {
            return super.databaseMetaData.supportsOpenCursorsAcrossCommit();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsOpenCursorsAcrossRollback() {
      try {
         try {
            return super.databaseMetaData.supportsOpenCursorsAcrossRollback();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsOpenStatementsAcrossCommit() {
      try {
         try {
            return super.databaseMetaData.supportsOpenStatementsAcrossCommit();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsOpenStatementsAcrossRollback() {
      try {
         try {
            return super.databaseMetaData.supportsOpenStatementsAcrossRollback();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxBinaryLiteralLength() {
      try {
         try {
            return super.databaseMetaData.getMaxBinaryLiteralLength();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxCharLiteralLength() {
      try {
         try {
            return super.databaseMetaData.getMaxCharLiteralLength();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxColumnNameLength() {
      try {
         try {
            return super.databaseMetaData.getMaxColumnNameLength();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxColumnsInGroupBy() {
      try {
         try {
            return super.databaseMetaData.getMaxColumnsInGroupBy();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxColumnsInIndex() {
      try {
         try {
            return super.databaseMetaData.getMaxColumnsInIndex();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxColumnsInOrderBy() {
      try {
         try {
            return super.databaseMetaData.getMaxColumnsInOrderBy();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxColumnsInSelect() {
      try {
         try {
            return super.databaseMetaData.getMaxColumnsInSelect();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxColumnsInTable() {
      try {
         try {
            return super.databaseMetaData.getMaxColumnsInTable();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxConnections() {
      try {
         try {
            return super.databaseMetaData.getMaxConnections();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxCursorNameLength() {
      try {
         try {
            return super.databaseMetaData.getMaxCursorNameLength();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxIndexLength() {
      try {
         try {
            return super.databaseMetaData.getMaxIndexLength();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxSchemaNameLength() {
      try {
         try {
            return super.databaseMetaData.getMaxSchemaNameLength();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxProcedureNameLength() {
      try {
         try {
            return super.databaseMetaData.getMaxProcedureNameLength();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxCatalogNameLength() {
      try {
         try {
            return super.databaseMetaData.getMaxCatalogNameLength();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxRowSize() {
      try {
         try {
            return super.databaseMetaData.getMaxRowSize();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean doesMaxRowSizeIncludeBlobs() {
      try {
         try {
            return super.databaseMetaData.doesMaxRowSizeIncludeBlobs();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxStatementLength() {
      try {
         try {
            return super.databaseMetaData.getMaxStatementLength();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxStatements() {
      try {
         try {
            return super.databaseMetaData.getMaxStatements();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxTableNameLength() {
      try {
         try {
            return super.databaseMetaData.getMaxTableNameLength();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxTablesInSelect() {
      try {
         try {
            return super.databaseMetaData.getMaxTablesInSelect();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getMaxUserNameLength() {
      try {
         try {
            return super.databaseMetaData.getMaxUserNameLength();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getDefaultTransactionIsolation() {
      try {
         try {
            return super.databaseMetaData.getDefaultTransactionIsolation();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsTransactions() {
      try {
         try {
            return super.databaseMetaData.supportsTransactions();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsTransactionIsolationLevel(int value) {
      try {
         try {
            return super.databaseMetaData.supportsTransactionIsolationLevel(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsDataDefinitionAndDataManipulationTransactions() {
      try {
         try {
            return super.databaseMetaData.supportsDataDefinitionAndDataManipulationTransactions();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsDataManipulationTransactionsOnly() {
      try {
         try {
            return super.databaseMetaData.supportsDataManipulationTransactionsOnly();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean dataDefinitionCausesTransactionCommit() {
      try {
         try {
            return super.databaseMetaData.dataDefinitionCausesTransactionCommit();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean dataDefinitionIgnoredInTransactions() {
      try {
         try {
            return super.databaseMetaData.dataDefinitionIgnoredInTransactions();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getProcedures(String text, String text2, String text3) {
      try {
         try {
            return super.getProcedures(text, text2, text3);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getProcedureColumns(String text, String text2, String text3, String text4) {
      try {
         try {
            return super.getProcedureColumns(text, text2, text3, text4);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getTables(String text, String text2, String text3, String[] texts) {
      try {
         try {
            return super.getTables(text, text2, text3, texts);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getSchemas() {
      try {
         try {
            return super.getSchemas();
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getCatalogs() {
      try {
         try {
            return super.getCatalogs();
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getTableTypes() {
      try {
         try {
            return super.getTableTypes();
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getColumns(String text, String text2, String text3, String text4) {
      try {
         try {
            return super.getColumns(text, text2, text3, text4);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getColumnPrivileges(String text, String text2, String text3, String text4) {
      try {
         try {
            return super.getColumnPrivileges(text, text2, text3, text4);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getTablePrivileges(String text, String text2, String text3) {
      try {
         try {
            return super.getTablePrivileges(text, text2, text3);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getBestRowIdentifier(String text, String text2, String text3, int value, boolean flag) {
      try {
         try {
            return super.getBestRowIdentifier(text, text2, text3, value, flag);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getVersionColumns(String text, String text2, String text3) {
      try {
         try {
            return super.getVersionColumns(text, text2, text3);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getPrimaryKeys(String text, String text2, String text3) {
      try {
         try {
            return super.getPrimaryKeys(text, text2, text3);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getImportedKeys(String text, String text2, String text3) {
      try {
         try {
            return super.getImportedKeys(text, text2, text3);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getExportedKeys(String text, String text2, String text3) {
      try {
         try {
            return super.getExportedKeys(text, text2, text3);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getCrossReference(String text, String text2, String text3, String text4, String text5, String text6) {
      try {
         try {
            return super.getCrossReference(text, text2, text3, text4, text5, text6);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getTypeInfo() {
      try {
         try {
            return super.getTypeInfo();
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getIndexInfo(String text, String text2, String text3, boolean flag, boolean flag2) {
      try {
         try {
            return super.getIndexInfo(text, text2, text3, flag, flag2);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsResultSetType(int value) {
      try {
         try {
            return super.databaseMetaData.supportsResultSetType(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsResultSetConcurrency(int value, int value2) {
      try {
         try {
            return super.databaseMetaData.supportsResultSetConcurrency(value, value2);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean ownUpdatesAreVisible(int value) {
      try {
         try {
            return super.databaseMetaData.ownUpdatesAreVisible(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean ownDeletesAreVisible(int value) {
      try {
         try {
            return super.databaseMetaData.ownDeletesAreVisible(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean ownInsertsAreVisible(int value) {
      try {
         try {
            return super.databaseMetaData.ownInsertsAreVisible(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean othersUpdatesAreVisible(int value) {
      try {
         try {
            return super.databaseMetaData.othersUpdatesAreVisible(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean othersDeletesAreVisible(int value) {
      try {
         try {
            return super.databaseMetaData.othersDeletesAreVisible(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean othersInsertsAreVisible(int value) {
      try {
         try {
            return super.databaseMetaData.othersInsertsAreVisible(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean updatesAreDetected(int value) {
      try {
         try {
            return super.databaseMetaData.updatesAreDetected(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean deletesAreDetected(int value) {
      try {
         try {
            return super.databaseMetaData.deletesAreDetected(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean insertsAreDetected(int value) {
      try {
         try {
            return super.databaseMetaData.insertsAreDetected(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsBatchUpdates() {
      try {
         try {
            return super.databaseMetaData.supportsBatchUpdates();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getUDTs(String text, String text2, String text3, int[] ints) {
      try {
         try {
            return super.getUDTs(text, text2, text3, ints);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsSavepoints() {
      try {
         try {
            return super.databaseMetaData.supportsSavepoints();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsNamedParameters() {
      try {
         try {
            return super.databaseMetaData.supportsNamedParameters();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsMultipleOpenResults() {
      try {
         try {
            return super.databaseMetaData.supportsMultipleOpenResults();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsGetGeneratedKeys() {
      try {
         try {
            return super.databaseMetaData.supportsGetGeneratedKeys();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getSuperTypes(String text, String text2, String text3) {
      try {
         try {
            return super.getSuperTypes(text, text2, text3);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getSuperTables(String text, String text2, String text3) {
      try {
         try {
            return super.getSuperTables(text, text2, text3);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getAttributes(String text, String text2, String text3, String text4) {
      try {
         try {
            return super.getAttributes(text, text2, text3, text4);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsResultSetHoldability(int value) {
      try {
         try {
            return super.databaseMetaData.supportsResultSetHoldability(value);
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getResultSetHoldability() {
      try {
         try {
            return super.databaseMetaData.getResultSetHoldability();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getDatabaseMajorVersion() {
      try {
         try {
            return super.databaseMetaData.getDatabaseMajorVersion();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getDatabaseMinorVersion() {
      try {
         try {
            return super.databaseMetaData.getDatabaseMinorVersion();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getJDBCMajorVersion() {
      try {
         try {
            return super.databaseMetaData.getJDBCMajorVersion();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getJDBCMinorVersion() {
      try {
         try {
            return super.databaseMetaData.getJDBCMinorVersion();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int getSQLStateType() {
      try {
         try {
            return super.databaseMetaData.getSQLStateType();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean locatorsUpdateCopy() {
      try {
         try {
            return super.databaseMetaData.locatorsUpdateCopy();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsStatementPooling() {
      try {
         try {
            return super.databaseMetaData.supportsStatementPooling();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public RowIdLifetime getRowIdLifetime() {
      try {
         try {
            return super.databaseMetaData.getRowIdLifetime();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getSchemas(String text, String text2) {
      try {
         try {
            return super.getSchemas(text, text2);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsStoredFunctionsUsingCallSyntax() {
      try {
         try {
            return super.databaseMetaData.supportsStoredFunctionsUsingCallSyntax();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean autoCommitFailureClosesAllResultSets() {
      try {
         try {
            return super.databaseMetaData.autoCommitFailureClosesAllResultSets();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getClientInfoProperties() {
      try {
         try {
            return super.getClientInfoProperties();
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getFunctions(String text, String text2, String text3) {
      try {
         try {
            return super.getFunctions(text, text2, text3);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getFunctionColumns(String text, String text2, String text3, String text4) {
      try {
         try {
            return super.getFunctionColumns(text, text2, text3, text4);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getPseudoColumns(String text, String text2, String text3, String text4) {
      try {
         try {
            return super.getPseudoColumns(text, text2, text3, text4);
         } catch (Throwable e2) {
            if (!(e2 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            SQLException sqlException = (SQLException) e2;
            throw this.computeSqlException(sqlException);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean generatedKeyAlwaysReturned() {
      try {
         try {
            return super.databaseMetaData.generatedKeyAlwaysReturned();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long getMaxLogicalLobSize() {
      try {
         try {
            return super.databaseMetaData.getMaxLogicalLobSize();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean supportsRefCursors() {
      try {
         try {
            return super.databaseMetaData.supportsRefCursors();
         } catch (SQLException e2) {
            throw this.computeSqlException(e2);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   HikariProxyDatabaseMetaData(ProxyConnection proxyConnection, DatabaseMetaData databaseMetaData) {
      super(proxyConnection, databaseMetaData);
   }
}
