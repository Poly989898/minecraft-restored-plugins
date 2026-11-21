package io.lumine.mythic.bukkit.utils.storage.sql;

public class SqlCredentials {
   private String address = "localhost";
   private int port = 3306;
   private String username = "";
   private String password = "";
   private String database = "database";

   public static SqlCredentials.SqlCredentialsBuilder create() {
      return new SqlCredentials.SqlCredentialsBuilder();
   }

   SqlCredentials(String address, int port, String username, String password, String database) {
      this.address = address;
      this.port = port;
      this.username = username;
      this.password = password;
      this.database = database;
   }

   public static SqlCredentials.SqlCredentialsBuilder builder() {
      return new SqlCredentials.SqlCredentialsBuilder();
   }

   public String getAddress() {
      return this.address;
   }

   public int getPort() {
      return this.port;
   }

   public String getUsername() {
      return this.username;
   }

   public String getPassword() {
      return this.password;
   }

   public String getDatabase() {
      return this.database;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public void setPort(int port) {
      this.port = port;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public void setDatabase(String database) {
      this.database = database;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SqlCredentials other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.getPort() != other.getPort()) {
         return false;
      } else {
         Object this$address = this.getAddress();
         Object other$address = other.getAddress();
         if (this$address == null ? other$address == null : this$address.equals(other$address)) {
            Object this$username = this.getUsername();
            Object other$username = other.getUsername();
            if (this$username == null ? other$username == null : this$username.equals(other$username)) {
               Object this$password = this.getPassword();
               Object other$password = other.getPassword();
               if (this$password == null ? other$password == null : this$password.equals(other$password)) {
                  Object this$database = this.getDatabase();
                  Object other$database = other.getDatabase();
                  return this$database == null ? other$database == null : this$database.equals(other$database);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof SqlCredentials;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getPort();
      Object $address = this.getAddress();
      result = result * 59 + ($address == null ? 43 : $address.hashCode());
      Object $username = this.getUsername();
      result = result * 59 + ($username == null ? 43 : $username.hashCode());
      Object $password = this.getPassword();
      result = result * 59 + ($password == null ? 43 : $password.hashCode());
      Object $database = this.getDatabase();
      return result * 59 + ($database == null ? 43 : $database.hashCode());
   }

   @Override
   public String toString() {
      return "SqlCredentials(address="
         + this.getAddress()
         + ", port="
         + this.getPort()
         + ", username="
         + this.getUsername()
         + ", password="
         + this.getPassword()
         + ", database="
         + this.getDatabase()
         + ")";
   }

   public static class SqlCredentialsBuilder {
      private String address;
      private int port;
      private String username;
      private String password;
      private String database;

      SqlCredentialsBuilder() {
      }

      public SqlCredentials.SqlCredentialsBuilder address(String address) {
         this.address = address;
         return this;
      }

      public SqlCredentials.SqlCredentialsBuilder port(int port) {
         this.port = port;
         return this;
      }

      public SqlCredentials.SqlCredentialsBuilder username(String username) {
         this.username = username;
         return this;
      }

      public SqlCredentials.SqlCredentialsBuilder password(String password) {
         this.password = password;
         return this;
      }

      public SqlCredentials.SqlCredentialsBuilder database(String database) {
         this.database = database;
         return this;
      }

      public SqlCredentials build() {
         return new SqlCredentials(this.address, this.port, this.username, this.password, this.database);
      }

      @Override
      public String toString() {
         return "SqlCredentials.SqlCredentialsBuilder(address="
            + this.address
            + ", port="
            + this.port
            + ", username="
            + this.username
            + ", password="
            + this.password
            + ", database="
            + this.database
            + ")";
      }
   }
}
