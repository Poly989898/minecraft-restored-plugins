package me.TechsCode.UltraPermissions.base.storage.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import me.TechsCode.UltraPermissions.base.TechPlugin;

public abstract class MySQLTableRefactor {
   public MySQLTableRefactor(TechPlugin plugin) {
      if (plugin.getMySQLManager().isEnabled()) {
         try {
            Connection connection = plugin.getMySQLManager().getConnection();

            for (MySQLTableRefactor.RefactorTable refactorTable : this.getRefactors()) {
               if (this.tableExist(connection, refactorTable.from)) {
                  if (this.tableExist(connection, refactorTable.to)) {
                     plugin.log("Archiving Table " + refactorTable.to);
                     this.tableRename(connection, refactorTable.to, "OLD_" + refactorTable.to);
                  }

                  plugin.log("Renaming Table " + refactorTable.from + " to " + refactorTable.to);
                  this.tableRename(connection, refactorTable.from, refactorTable.to);
               }
            }

            connection.close();
         } catch (SQLException ex) {
            ex.printStackTrace();
         }
      }
   }

   private boolean tableExist(Connection connection, String text) throws SQLException {
      boolean flag = false;

      try (ResultSet resultSet = connection.getMetaData().getTables(null, null, text, null)) {
         while (resultSet.next()) {
            String string = resultSet.getString("TABLE_NAME");
            if (string != null && string.equals(text)) {
               flag = true;
               break;
            }
         }
      }

      return flag;
   }

   private void tableDelete(Connection connection, String text) throws SQLException {
      Statement statement = connection.createStatement();
      statement.executeUpdate("DROP TABLE " + text + ";");
      statement.close();
   }

   private void tableRename(Connection connection, String oldName, String newName) throws SQLException {
      Statement statement = connection.createStatement();
      statement.executeUpdate("RENAME TABLE " + oldName + " TO " + newName + ";");
      statement.close();
   }

   public abstract MySQLTableRefactor.RefactorTable[] getRefactors();

   public class RefactorTable {
      private String from;
      private String to;

      public RefactorTable(String from, String to) {
         this.from = from;
         this.to = to;
      }
   }
}
