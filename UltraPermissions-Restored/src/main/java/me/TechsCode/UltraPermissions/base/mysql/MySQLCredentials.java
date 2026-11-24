package me.TechsCode.UltraPermissions.base.mysql;

public class MySQLCredentials {
   private String hostname;
   private String port;
   private String database;
   private String username;
   private String password;

   public MySQLCredentials(String text, String port, String database, String username, String password) {
      this.hostname = text;
      this.port = port;
      this.database = database;
      this.username = username;
      this.password = password;
   }

   public String getHostname() {
      return this.hostname;
   }

   public void setHostname(String text) {
      this.hostname = text;
   }

   public String getPort() {
      return this.port;
   }

   public void setPort(String text) {
      this.port = text;
   }

   public String getDatabase() {
      return this.database;
   }

   public void setDatabase(String text) {
      this.database = text;
   }

   public String getUsername() {
      return this.username;
   }

   public void setUsername(String text) {
      this.username = text;
   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(String text) {
      this.password = text;
   }
}
