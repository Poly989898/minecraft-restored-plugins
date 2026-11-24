package fr.phoenixdevt.mmoprofiles.bukkit.manager.data.sql;

import io.lumine.mythic.lib.data.sql.SQLDataSource;
import java.util.UUID;

public class MySQLTableEditor {
   private final MySQLTableEditor.Table table;
   private final UUID uuid;
   private final SQLDataSource dataSource;

   public MySQLTableEditor(MySQLTableEditor.Table table, UUID uuid, SQLDataSource dataSource) {
      this.table = table;
      this.uuid = uuid;
      this.dataSource = dataSource;
   }

   public void updateData(String key, Object value) {
      this.dataSource
         .executeUpdate(
            "INSERT INTO "
               + this.table
               + "(uuid, "
               + key
               + ") VALUES('"
               + this.uuid
               + "', '"
               + value
               + "') ON DUPLICATE KEY UPDATE "
               + key
               + "='"
               + value
               + "';"
         );
   }

   public void updateData(MySQLRequest request) {
      this.dataSource.executeUpdate("INSERT INTO " + this.table + request.getRequestString());
   }

   public enum Table {
      PLAYERDATA("mmoprofiles_playerdata");

      final String tableName;

      Table(String tN) {
         this.tableName = tN;
      }

      @Override
      public String toString() {
         return this.tableName;
      }
   }
}
