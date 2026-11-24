package me.TechsCode.UltraPermissions.base.mysql;

import me.TechsCode.UltraPermissions.base.registry.RegistryStorable;
import com.google.gson.JsonObject;

public class MySQLRegistry extends RegistryStorable {
   private String hostname;
   private String serverName = "";
   private String port;
   private String database;
   private String username;
   private String password;
   private boolean ssl;
   private int minimumIdle;
   private int maximumPoolSize;

   public MySQLRegistry() {
      super("mysql");
      this.hostname = "";
      this.port = "";
      this.database = "";
      this.username = "";
      this.password = "";
      this.ssl = false;
      this.minimumIdle = 10;
      this.maximumPoolSize = 25;
   }

   @Override
   public void setState(JsonObject jsonObject) {
      if (!jsonObject.has("serverName")) {
         jsonObject.addProperty("serverName", "");
      }

      this.serverName = jsonObject.get("serverName").getAsString();
      this.hostname = jsonObject.get("hostname").getAsString();
      this.port = jsonObject.get("port").getAsString();
      this.database = jsonObject.get("database").getAsString();
      this.username = jsonObject.get("username").getAsString();
      this.password = jsonObject.get("password").getAsString();
      this.ssl = jsonObject.has("ssl") && jsonObject.get("ssl").getAsBoolean();
      this.minimumIdle = jsonObject.has("minimumIdle") ? jsonObject.get("minimumIdle").getAsInt() : 10;
      this.maximumPoolSize = jsonObject.has("maximumPoolSize") ? jsonObject.get("maximumPoolSize").getAsInt() : 25;
   }

   @Override
   public JsonObject getState() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("serverName", this.serverName);
      jsonObject.addProperty("hostname", this.hostname);
      jsonObject.addProperty("port", this.port);
      jsonObject.addProperty("database", this.database);
      jsonObject.addProperty("username", this.username);
      jsonObject.addProperty("password", this.password);
      jsonObject.addProperty("ssl", this.ssl);
      jsonObject.addProperty("minimumIdle", this.minimumIdle);
      jsonObject.addProperty("maximumPoolSize", this.maximumPoolSize);
      return jsonObject;
   }

   public void updateState(JsonObject jsonObject) {
      this.setState(jsonObject);
      this.sync();
   }

   public boolean hasCredentials() {
      return !this.hostname.isEmpty() && !this.port.isEmpty() && !this.database.isEmpty() && !this.username.isEmpty() && !this.password.isEmpty();
   }

   public void setCredentials(MySQLCredentials mySQLCredentials) {
      if (mySQLCredentials == null) {
         this.clearCredentials();
      } else {
         this.hostname = mySQLCredentials.getHostname();
         this.port = mySQLCredentials.getPort();
         this.database = mySQLCredentials.getDatabase();
         this.username = mySQLCredentials.getUsername();
         this.password = mySQLCredentials.getPassword();
         this.sync();
      }
   }

   public void clearCredentials() {
      this.hostname = "";
      this.port = "";
      this.database = "";
      this.username = "";
      this.password = "";
      this.sync();
   }

   public MySQLCredentials getCredentials() {
      return this.hasCredentials() ? new MySQLCredentials(this.hostname, this.port, this.database, this.username, this.password) : null;
   }

   public boolean hasSSL() {
      return this.ssl;
   }

   public void setSSL(boolean flag) {
      this.ssl = flag;
      this.sync();
   }

   public int getMinimumIdle() {
      return this.minimumIdle;
   }

   public void setMinimumIdle(int count) {
      this.minimumIdle = count;
      this.sync();
   }

   public int getMaximumPoolSize() {
      return this.maximumPoolSize;
   }

   public void setMaximumPoolSize(int count) {
      this.maximumPoolSize = count;
      this.sync();
   }

   public String getServerName() {
      return this.serverName.isEmpty() ? null : this.serverName;
   }

   public void setServerName(String text) {
      this.serverName = text;
      this.sync();
   }
}
