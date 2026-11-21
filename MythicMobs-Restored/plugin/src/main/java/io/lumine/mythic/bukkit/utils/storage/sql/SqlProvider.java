package io.lumine.mythic.bukkit.utils.storage.sql;

import io.lumine.mythic.bukkit.utils.lib.jooq.SQLDialect;

public enum SqlProvider {
   MYSQL(SQLDialect.MYSQL, "jdbc:mysql://"),
   MARIADB(SQLDialect.MARIADB, "jdbc:mariadb://"),
   POSTGRES(SQLDialect.POSTGRES, "jdbc:postgresql://");

   private SQLDialect dialect;
   private String jdbcString;

   private SqlProvider(SQLDialect dialect, String jdbcString) {
      this.dialect = dialect;
      this.jdbcString = jdbcString;
   }

   public String formatAddress(SqlCredentials credentials) {
      return this.jdbcString + credentials.getAddress() + ":" + credentials.getPort() + "/" + credentials.getDatabase() + "?useSSL=false";
   }

   public SQLDialect getDialect() {
      return this.dialect;
   }
}
