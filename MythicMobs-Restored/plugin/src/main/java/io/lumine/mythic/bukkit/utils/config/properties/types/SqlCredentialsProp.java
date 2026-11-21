package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.storage.sql.SqlCredentials;
import org.bukkit.configuration.ConfigurationSection;

public class SqlCredentialsProp extends PropertyType<SqlCredentials> {
   private final SqlCredentials defaultValue = SqlCredentials.create().build();
   private StringProp SQL_USERNAME;
   private StringProp SQL_PASSWORD;
   private StringProp SQL_DATABASE;
   private StringProp SQL_ADDRESS;
   private IntProp SQL_PORT;

   public SqlCredentialsProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      this.SQL_USERNAME = new StringProp(plugin, file, node + ".Username", "root");
      this.SQL_PASSWORD = new StringProp(plugin, file, node + ".Password", "password");
      this.SQL_DATABASE = new StringProp(plugin, file, node + ".Database", "database");
      this.SQL_ADDRESS = new StringProp(plugin, file, node + ".Address", "localhost");
      this.SQL_PORT = new IntProp(plugin, file, node + ".Port", 3306);
   }

   protected SqlCredentials compute(ConfigurationSection section, String node) {
      if (section == null) {
         return this.defaultValue;
      } else {
         String sqlUser = this.SQL_USERNAME.get(section);
         SqlCredentials.SqlCredentialsBuilder builder = SqlCredentials.create()
            .username(sqlUser)
            .password(this.SQL_PASSWORD.get(section))
            .database(this.SQL_DATABASE.get(section))
            .address(this.SQL_ADDRESS.get(section))
            .port(this.SQL_PORT.get(section));
         return builder.build();
      }
   }

   public SqlCredentials getDefaultValue() {
      return this.defaultValue;
   }
}
